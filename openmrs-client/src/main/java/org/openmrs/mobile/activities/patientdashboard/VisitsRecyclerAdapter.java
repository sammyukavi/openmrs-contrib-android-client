package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.LOCATION_UUID_BUNDLE;
import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE;
import static org.openmrs.mobile.utilities.ApplicationConstants.ClinicalFormConcepts.ClinicFormUUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeEntity.CLINICAL_NOTE_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.FORM_UUIDS.CLINICAL_FORM_UUID;

public class VisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int VIEW_TYPE_ITEM = 0;
	private final int VIEW_TYPE_LOADING = 1;
	private final HashMap uuIds;
	private OnLoadMoreListener onLoadMoreListener;
	private boolean isLoading;
	private Context context;
	private List<Visit> visits;
	private CustomDialogBundle createEditVisitNoteDialog;
	private ImageView showVisitDetails;
	private Intent intent;
	private LayoutInflater layoutInflater;
	private TableLayout visitVitalsTableLayout;
	private TextView visitNote;
	private Handler handler = new Handler();
	long delay = 3000; // 1 seconds after user stops typing
	long last_text_edit = 0;
	private Encounter visitNoteEncounter;
	private LocalDateTime localDateTime;

	Runnable input_finish_checker = new Runnable() {
		public void run() {
			if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
				boolean isNewEncounter = false;
				if (visitNoteEncounter == null) {
					visitNoteEncounter = createVisitNoteEncounter();
					isNewEncounter = true;
				}
				Observation observation = null;

				if (clinicalNoteObs == null) {

					//create concept
					Concept concept = new Concept();
					concept.setUuid(ClinicFormUUID);

					Person person = new Person();
					person.setUuid(activeVisit.getPatient().getUuid());

					Provider provider = new Provider();
					provider.setUuid(OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys
							.USER_UUID));

					Location location = new Location();
					location.setUuid(uuIds.get(LOCATION_UUID_BUNDLE).toString());

					//create observation
					observation = new Observation();
					observation.setConcept(concept);
					observation.setPerson(person);
					observation.setObsDatetime(localDateTime.toString());
					observation.setProvider(provider);

					observation.setLocation(uuIds.get(LOCATION_UUID_BUNDLE).toString());

				} else {
					observation = clinicalNoteObs;
				}

				observation.setValue(visitNote.getText());

				List<Observation> observationList = new ArrayList<>();
				observationList.add(observation);

				visitNoteEncounter.setObs(observationList);

				PatientDashboardActivity patientDashboardActivity = (PatientDashboardActivity)context;
				patientDashboardActivity.mPresenter.saveEncounter(visitNoteEncounter, isNewEncounter);

			}
		}
	};

	private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			/*totalItemCount = layoutManager.getItemCount();
				lastVisibleItem = layoutManager.findLastVisibleItemPosition();
				if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
					if (onLoadMoreListener != null) {
						onLoadMoreListener.onLoadMore();
					}
					isLoading = true;
				}*/
			if (!isLoading && onLoadMoreListener != null) {
				isLoading = true;
				onLoadMoreListener.onLoadMore();
			}
		}
	};
	private TextWatcher clinicalNoteWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(final CharSequence s, int start, int before,
				int count) {
			//You need to remove this to run only once
			handler.removeCallbacks(input_finish_checker);
		}

		@Override
		public void afterTextChanged(final Editable s) {
			//avoid triggering event when text is empty
			if (s.length() > 0) {
				last_text_edit = System.currentTimeMillis();
				handler.postDelayed(input_finish_checker, delay);
			} else {

			}
		}
	};
	private Visit activeVisit;
	private Observation clinicalNoteObs;

	public VisitsRecyclerAdapter(RecyclerView recyclerView, List<Visit> visits, Context context, HashMap uuIds) {
		this.visits = visits;
		this.context = context;
		this.createEditVisitNoteDialog = new CustomDialogBundle();
		this.createEditVisitNoteDialog.setTitleViewMessage(context.getString(R.string.visit_note));
		this.createEditVisitNoteDialog.setRightButtonText(context.getString(R.string.label_save));
		this.layoutInflater = LayoutInflater.from(context);
		this.uuIds = uuIds;
		this.localDateTime = new LocalDateTime();
		recyclerView.addOnScrollListener(onScrollListener);
	}

	private void loadVisitDetails(String uuid) {
		intent = new Intent(context, VisitActivity.class);
		intent.putExtra(PATIENT_UUID_BUNDLE, OpenMRS.getInstance()
				.getPatientUuid());
		intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, uuid);
		context.startActivity(intent);
	}

	public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
		this.onLoadMoreListener = mOnLoadMoreListener;
	}

	@Override
	public int getItemViewType(int position) {
		return visits.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_ITEM) {
			View view = LayoutInflater.from(context).inflate(R.layout.container_visits_observations, parent, false);
			return new VisitViewHolder(view);
		} else if (viewType == VIEW_TYPE_LOADING) {
			View view = LayoutInflater.from(context).inflate(R.layout.past_visits_loading, parent, false);
			return new LoadingViewHolder(view);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

		//View patientContactInfo = layoutInflater.inflate(R.layout.container_patient_address_info, null);
		//viewHolder.observationsContainer.addView(patientContactInfo);

		if (holder instanceof VisitViewHolder) {
			boolean isActiveVisit = false;
			VisitViewHolder viewHolder = (VisitViewHolder)holder;
			Visit visit = visits.get(position);
			View singleVisitView = layoutInflater.inflate(R.layout.container_single_visit_observation, null);
			TextView visitStartDate = (TextView)singleVisitView.findViewById(R.id.startDate);

			//Let's set the visit title
			String startDate = DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.DATE_FORMAT);
			String stopDate = visit.getStopDatetime();
			if (!StringUtils.notNull(stopDate)) {
				activeVisit = visit;
				isActiveVisit = true;
				singleVisitView.findViewById(R.id.active_visit_badge).setVisibility(View.VISIBLE);
				startDate = context.getString(R.string.started) + " " + startDate;
			}

			visitStartDate.setText(startDate);

			((TextView)singleVisitView.findViewById(R.id.visitTimeago)).setText(DateUtils.calculateTimeDifference(
					(visit.getStartDatetime())));

			if (isActiveVisit) {
				((TextView)singleVisitView.findViewById(R.id.visitDuration))
						.setText(context.getString(R.string.duration,
								DateUtils.calculateTimeDifference(visit.getStartDatetime(), false)));
			} else {
				((TextView)singleVisitView.findViewById(R.id.visitDuration))
						.setText(context.getString(R.string.duration,
								DateUtils.calculateTimeDifference(visit.getStartDatetime(), visit.getStopDatetime())));
			}

			//Adding the link to the visit details page
			showVisitDetails = (ImageView)singleVisitView.findViewById(R.id.loadVisitDetails);
			showVisitDetails.setOnClickListener(v -> loadVisitDetails(visit.getUuid()));
			singleVisitView.setOnClickListener(v -> loadVisitDetails(visit.getUuid()));

			if (visit.getEncounters().size() == 0) {
				presentVisitNotes(new Encounter(), singleVisitView, isActiveVisit);
			} else {
				for (Encounter encounter : visit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case ApplicationConstants.EncounterTypeDisplays.VISIT_NOTE:
							presentVisitNotes(encounter, singleVisitView, isActiveVisit);
							break;
						default:
							presentVisitNotes(new Encounter(), singleVisitView, isActiveVisit);
							break;
					}
				}
			}

			viewHolder.observationsContainer.addView(singleVisitView);

		} else if (holder instanceof LoadingViewHolder) {
			LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
			loadingViewHolder.progressBar.setIndeterminate(true);
		}

	}

	private void presentVisitNotes(Encounter encounter, View view, boolean isActiveVisit) {
		visitNote = null;

		if (isActiveVisit) {
			view.findViewById(R.id.editVisitNoteContainer).setVisibility(View.VISIBLE);
			visitNote = (TextInputEditText)view.findViewById(R.id.editVisitNote);
			visitNote.addTextChangedListener(clinicalNoteWatcher);

		} else {
			visitNote = (TextView)view.findViewById(R.id.visitNoteText);
			view.findViewById(R.id.visitNoteTextContainer).setVisibility(View.VISIBLE);
		}

		TextView primaryDiagnosis = (TextView)view.findViewById(R.id.primaryDiagnosis);
		TextView secondaryDiagnosis = (TextView)view.findViewById(R.id.secondaryDiagnosis);

		String visitNoteStr = null;
		String primaryObsStr = "";
		String secondaryObsStr = "";
		String otherObsStr = "";
		boolean hasVisitNote = false;

		for (Observation observation : encounter.getObs()) {
			if (observation.getDisplay().startsWith("Text of")) {
				this.visitNoteEncounter = encounter;
				this.clinicalNoteObs = observation;
				visitNoteStr = observation.getDisplay();
				visitNoteStr = visitNoteStr.substring((visitNoteStr.indexOf(":") + 1), (visitNoteStr.length() - 1));
				hasVisitNote = true;
			} else if (observation.getDisplay().startsWith("Visit Diagnoses: Primary, ")) {
				primaryObsStr += (observation.getDisplay().replaceAll("Visit Diagnoses: Primary, ", ""));
			} else if (observation.getDisplay().startsWith("Visit Diagnoses: Secondary,")) {
				secondaryObsStr += observation.getDisplay().replaceAll("Visit Diagnoses: Secondary, ", "");
			} else {
				otherObsStr += observation.getDisplay();
				hasVisitNote = true;
			}
		}

		primaryDiagnosis.setText(primaryObsStr.replaceAll("Presumed diagnosis", "").replaceAll(", ", ", ").replace(",",
				"").trim());
		secondaryDiagnosis.setText(secondaryObsStr.replaceAll("Presumed diagnosis", "").replaceAll(", ", ", ").replace(","
				+ "", "").trim());
		if (visitNoteStr == null) {
			visitNote.setText(otherObsStr.trim());
		} else {
			visitNote.setText(visitNoteStr.trim());
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.primaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.secondaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (!hasVisitNote && !isActiveVisit) {
			view.findViewById(R.id.visitNoteTitle).setVisibility(View.GONE);
			visitNote.setText("No Diagnosis");
		}

	}

	private void presentVitals(Encounter encounter, View singleVisitView) {
		TableLayout visitVitalsTableLayout = (TableLayout)singleVisitView.findViewById(R.id.visitVitalsTable);

		if (encounter.getObs().size() != 0) {
			for (Observation observation : encounter.getObs()) {

				TableRow row = new TableRow(context);
				row.setPadding(0, 20, 0, 10);
				row.setGravity(Gravity.CENTER);

				ArrayList splitValues = StringUtils.splitStrings(observation.getDisplay());

				TextView label = new TextView(context);
				label.setText(splitValues.get(0) + " :");
				label.setTextSize(14);
				label.setGravity(Gravity.RIGHT | Gravity.END);
				label.setTextColor(context.getResources().getColor(R.color.black));
				row.addView(label, 0);

				TextView vitalValue = new TextView(context);
				vitalValue.setText(splitValues.get(1).toString());
				vitalValue.setTextSize(14);
				vitalValue.setTextColor(context.getResources().getColor(R.color.dark_grey));
				row.addView(vitalValue, 1);

				visitVitalsTableLayout.addView(row);
			}
		} else {

		}
	}

	@Override
	public int getItemCount() {
		return visits == null ? 0 : visits.size();
	}

	public void setLoaded() {
		isLoading = false;
	}

	private Encounter createVisitNoteEncounter() {
		OpenMRS openMRS = new OpenMRS();

		SharedPreferences prefs = openMRS.getOpenMRSSharedPreferences();
		prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);

		//create encountertType
		EncounterType mEncountertype = new EncounterType();
		mEncountertype.setUuid(CLINICAL_NOTE_UUID);

		LocalDateTime localDateTime = new LocalDateTime();

		Patient patient = new Patient();
		patient.setUuid(uuIds.get(PATIENT_UUID_BUNDLE).toString());

		Form form = new Form();
		form.setUuid(CLINICAL_FORM_UUID);

		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setForm(form);
		encounter.setLocation(activeVisit.getLocation());
		encounter.setVisit(new Visit(activeVisit.getUuid()));
		encounter.setProvider(openMRS.getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID));
		encounter.setEncounterType(mEncountertype);

		return encounter;
	}

	private class LoadingViewHolder extends RecyclerView.ViewHolder {
		public ProgressBar progressBar;

		public LoadingViewHolder(View view) {
			super(view);
			progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
		}
	}

	private class VisitViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout observationsContainer;

		public VisitViewHolder(View view) {
			super(view);
			observationsContainer = (LinearLayout)view.findViewById(R.id.observationsContainer);
		}
	}

}