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
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.CHIEF_COMPLAINT;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.CLINICAL_NOTE;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.CONFIRMED_DIAGNOSIS;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.DIAGNOSES;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.PRESUMED_DIAGNOSIS;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.SECONDARY_DIAGNOSIS;

public class VisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int VIEW_TYPE_ITEM = 0;
	private final int VIEW_TYPE_LOADING = 1;
	private final HashMap uuIds;
	private OnLoadMoreListener onLoadMoreListener;
	private boolean isLoading;
	private Context context;
	private List<Visit> visits;
	private ImageView showVisitDetails;
	private Intent intent;
	private LayoutInflater layoutInflater;
	private TableLayout visitVitalsTableLayout;
	private OpenMRS instance = OpenMRS.getInstance();
	private TextView visitNote;
	private Handler handler = new Handler();
	private long delay = 3000; //seconds after user stops typing
	private long last_text_edit = 0;
	private Encounter visitNoteEncounter;
	private LocalDateTime localDateTime;
	private PatientDashboardActivity patientDashboardActivity;

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

				observation.setValueString(visitNote.getText().toString());

				List<Observation> observationList;

				if (isNewEncounter) {
					observationList = new ArrayList<>();
				} else {
					observationList = visitNoteEncounter.getObs();
				}

				observationList.add(observation);

				visitNoteEncounter.setObs(observationList);

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

	public VisitsRecyclerAdapter(RecyclerView recyclerView, List<Visit> visits, Context context, HashMap uuidsHashmap) {
		this.visits = visits;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.uuIds = uuidsHashmap;
		this.localDateTime = new LocalDateTime();
		recyclerView.addOnScrollListener(onScrollListener);
		this.patientDashboardActivity = (PatientDashboardActivity)context;
	}

	private void loadVisitDetails(Visit visit) {
		setVisitStopDate(visit);
		intent = new Intent(context, VisitActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, OpenMRS.getInstance()
				.getPatientUuid());
		intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visit.getUuid());
		intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visit.getStopDatetime());
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
			}

			visitStartDate.setText(startDate);
			((TextView)singleVisitView.findViewById(R.id.visitTimeago))
					.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime(), true));

			if (!isActiveVisit) {
				((TextView)singleVisitView.findViewById(R.id.visitDuration))
						.setText(context.getString(R.string.visit_duration,
								DateUtils.calculateTimeDifference(visit.getStartDatetime(), visit.getStopDatetime())));
			}

			//Adding the link to the visit details page
			showVisitDetails = (ImageView)singleVisitView.findViewById(R.id.loadVisitDetails);

			if (isActiveVisit) {
				showVisitDetails.setVisibility(View.VISIBLE);
				showVisitDetails.setOnClickListener(v -> loadVisitDetails(visit));
			} else {
				showVisitDetails.setVisibility(View.GONE);
				singleVisitView.setOnClickListener(v -> loadVisitDetails(visit));
			}

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
		visitNote = (TextInputEditText)view.findViewById(R.id.editVisitNote);
		visitNote.addTextChangedListener(clinicalNoteWatcher);

		if (isActiveVisit) {
			view.findViewById(R.id.editVisitNoteContainer).setVisibility(View.VISIBLE);

		} else {
			view.findViewById(R.id.visitNoteTextContainer).setVisibility(View.VISIBLE);
		}

		TextView primaryDiagnosis = (TextView)view.findViewById(R.id.primaryDiagnosis);
		TextView secondaryDiagnosis = (TextView)view.findViewById(R.id.secondaryDiagnosis);

		String visitNoteStr = "";
		String primaryObsStr = "";
		String secondaryObsStr = "";
		String chiefComplaintObsStr = "";
		boolean hasVisitNote = false;

		for (Observation observation : encounter.getObs()) {

			String displayStr = observation.getDisplay();

			if (displayStr.contains(PRIMARY_DIAGNOSIS)) {
				if (primaryObsStr != "") {
					primaryObsStr += ", ";
				}
				primaryObsStr += ((((displayStr.replace(CONFIRMED_DIAGNOSIS, "")).replace(PRIMARY_DIAGNOSIS, ""))
						.replace(PRESUMED_DIAGNOSIS, "")).replace(DIAGNOSES, "")).replace(":", " ").replaceAll("( +)", " ")
						.replaceAll("^\\W+|\\W+$", "");
				primaryDiagnosis.setText(primaryObsStr);
			} else if (displayStr.contains(SECONDARY_DIAGNOSIS)) {
				if (secondaryObsStr != "") {
					secondaryObsStr += ", ";
				}
				secondaryObsStr += ((((displayStr.replace(CONFIRMED_DIAGNOSIS, "")).replace(SECONDARY_DIAGNOSIS, ""))
						.replace(PRESUMED_DIAGNOSIS, "")).replace(DIAGNOSES, "")).replace(":", " ").replaceAll("( +)", " ")
						.replaceAll("^\\W+|\\W+$", "");
				secondaryDiagnosis.setText(secondaryObsStr);
			} else if (displayStr.contains(CLINICAL_NOTE)) {
				hasVisitNote = true;
				this.visitNoteEncounter = encounter;
				this.clinicalNoteObs = observation;

				visitNoteStr = (((((displayStr.replace(CONFIRMED_DIAGNOSIS, "")).replace(CLINICAL_NOTE, ""))
						.replace(PRESUMED_DIAGNOSIS, "")).replace(DIAGNOSES, "")).replace("(text)", "")).replace(":", " ")
						.replaceAll("" + "( +)", " ").replaceAll("^\\W+|\\W+$", "");
				visitNote.setText(visitNoteStr);
				view.findViewById(R.id.visitNoteTitle).setVisibility(View.VISIBLE);
			} else if (displayStr.contains(CHIEF_COMPLAINT)) {
				chiefComplaintObsStr = (((((displayStr.replace(CONFIRMED_DIAGNOSIS, "")).replace(CHIEF_COMPLAINT, ""))
						.replace(PRESUMED_DIAGNOSIS, "")).replace(DIAGNOSES, "")).replace("(text)", "")).replace(":", " ")
						.replaceAll("" + "( +)", " ").replaceAll("^\\W+|\\W+$", "");
				if (visitNote.getText().length() == 0) {
					visitNote.setText(chiefComplaintObsStr);
				}
			}
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.primaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.secondaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (!hasVisitNote && !isActiveVisit) {
			((TextView)view.findViewById(R.id.visitNoteText)).setText(context.getString(R.string.no_diagnoses_label));
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

		SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
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
		encounter.setProvider(
				OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID));
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

	private void setVisitStopDate(Visit visit) {
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visit.getStopDatetime());
		editor.commit();
	}
}