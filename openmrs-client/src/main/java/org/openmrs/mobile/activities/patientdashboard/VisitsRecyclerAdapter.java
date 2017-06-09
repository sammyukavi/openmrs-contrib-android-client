package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import org.openmrs.mobile.models.PersonAttribute;
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
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.COUNTY;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.SUBCOUNTY;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.TELEPHONE;

public class VisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int VIEW_TYPE_HEADER = 0;
	private final int VIEW_TYPE_ITEM = 1;
	private HashMap<String, String> uuids;
	private boolean allowUserNavigation, editIsAllowed;
	private Context context;
	private List<Visit> visits;
	private LayoutInflater layoutInflater;
	private TableLayout visitVitalsTableLayout;
	private OpenMRS instance = OpenMRS.getInstance();
	private TextView clinicalNote;
	private Handler handler = new Handler();
	private long delay = 3000; //seconds after user stops typing
	private long lastTextEdit = 0;
	private LocalDateTime localDateTime;
	private PatientDashboardActivity patientDashboardActivity;
	private Visit activeVisit;
	private Observation clinicalNoteObs;
	private View activeVisitView;
	private int startIndex, limit;

	private Runnable inputFinishChecker = () -> {

		if (System.currentTimeMillis() > (lastTextEdit + delay - 500)) {
			if (clinicalNoteObs != null) {
				clinicalNoteObs.setValue(clinicalNote.getText().toString());
				clinicalNoteObs.setObsDatetime(localDateTime.toString());
				patientDashboardActivity.mPresenter.saveObservation(clinicalNoteObs, false);
			} else {

				//create concept
				Concept concept = new Concept();
				concept.setUuid(ClinicFormUUID);

				Person person = new Person();
				person.setUuid(uuids.get(PATIENT_UUID_BUNDLE).toString());

				Provider provider = new Provider();
				provider.setUuid(OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys
						.USER_UUID));

				Location location = new Location();
				location.setUuid(uuids.get(LOCATION_UUID_BUNDLE).toString());

				//create observation
				Observation observation = new Observation();
				observation.setConcept(concept);
				observation.setPerson(person);
				observation.setObsDatetime(localDateTime.toString());
				observation.setProvider(provider);
				observation.setLocation(uuids.get(LOCATION_UUID_BUNDLE).toString());
				observation.setValue(clinicalNote.getText().toString());

				List<Observation> observationList = new ArrayList<>();

				observationList.add(observation);

				Encounter clinicNoteEncounter = createClinicalNoteEncounter();
				clinicNoteEncounter.setObs(observationList);

				patientDashboardActivity.mPresenter.saveEncounter(clinicNoteEncounter, true);
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
			//Remove this to run only once
			handler.removeCallbacks(inputFinishChecker);
		}

		@Override
		public void afterTextChanged(final Editable s) {

			/*Prevent sending of data back to sever after setting the value of the
			 clinical note from the server for the first time after data load*/
			if (editIsAllowed) {
				//Text has changed, stop navigaion until save is complete
				patientDashboardActivity.setHasPendingTransaction(true);

				allowUserNavigation = false;

				//avoid triggering event when text is empty
				if (s.length() > 0) {
					lastTextEdit = System.currentTimeMillis();
					handler.postDelayed(inputFinishChecker, delay);
				} else {
					patientDashboardActivity.setHasPendingTransaction(false);
					allowUserNavigation = true;
				}
			} else {
				editIsAllowed = true;
			}
		}
	};

	public VisitsRecyclerAdapter(RecyclerView visitsRecyclerView, List<Visit> visits, Context context) {

		this.visits = visits;

		this.context = context;

		this.layoutInflater = LayoutInflater.from(context);

		this.localDateTime = new LocalDateTime();

		this.patientDashboardActivity = (PatientDashboardActivity)context;

		this.allowUserNavigation = true;

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

		visitsRecyclerView.setLayoutManager(linearLayoutManager);

		visitsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				//Contact address header
				View patientContactInfo = recyclerView.findViewById(R.id.container_patient_address_info);
				if (patientContactInfo == null) {
					patientDashboardActivity.updateHeaderShadowLine(true);
				} else {
					patientDashboardActivity.updateHeaderShadowLine(false);
				}

				if (!patientDashboardActivity.mPresenter.isLoading()) {

					startIndex = patientDashboardActivity.mPresenter.getStartIndex();

					limit = patientDashboardActivity.mPresenter.getLimit();

					if (!recyclerView.canScrollVertically(1)) {
						// load next page
						patientDashboardActivity.mPresenter.fetchVisits(true);
					}

					if (!recyclerView.canScrollVertically(-1) && dy < 0) {
						// load previous page
						patientDashboardActivity.mPresenter.fetchVisits(false);
					}
				}

			}
		});
	}

	public void setUuids(HashMap<String, String> uuids) {
		this.uuids = uuids;
	}

	public void updateVisits(List<Visit> results) {
		visits.clear();
		visits.addAll(results);
		notifyDataSetChanged();
	}

	public void updateSavingClinicalNoteProgressBar(boolean show) {
		if (activeVisitView != null) {
			activeVisitView.findViewById(R.id.savingProgressBarView).setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	public void allowUserNavigation(boolean allowNavigation) {
		this.allowUserNavigation = allowNavigation;
	}

	public void updateClinicalNoteObs(Observation obs) {
		this.clinicalNoteObs = obs;
	}

	private void loadVisitDetails(Visit visit) {
		if (allowUserNavigation) {
			setVisitStopDate(visit);
			Intent intent = new Intent(context, VisitActivity.class);
			intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, OpenMRS.getInstance()
					.getPatientUuid());
			intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visit.getUuid());
			intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visit.getStopDatetime());
			context.startActivity(intent);
		} else {
			patientDashboardActivity.createToast(context.getString(R.string.pending_save));
		}
	}

	private void updateContactInformation(RecyclerViewHeader header) {
		Patient patient = patientDashboardActivity.mPresenter.getPatient();

		String county, subCounty, address, phone;
		county = subCounty = address = phone = "";

		for (PersonAttribute personAttribute : patient.getPerson().getAttributes()) {
			if (personAttribute.getDisplay() != null) {
				String displayName = personAttribute.getDisplay().replaceAll("\\s+", "");
				if (displayName.toLowerCase().startsWith(SUBCOUNTY)) {
					subCounty = displayName.split("=")[1];
				} else if (displayName.toLowerCase().startsWith(COUNTY)) {
					county = displayName.split("=")[1];
				} else if (displayName.toLowerCase().startsWith(TELEPHONE)) {
					phone = displayName.split("=")[1];
				}
			}
		}

		if (!subCounty.equalsIgnoreCase("")) {
			address += subCounty;
		}

		if (!address.equalsIgnoreCase("")) {
			address += ", " + county;
		} else {
			address += county;
		}

		header.patientAddress.setText(address);
		header.patientPhonenumber.setText(phone);

	}

	private void presentClinicalNotes(Encounter encounter, View view, boolean isActiveVisit) {

		if (isActiveVisit && clinicalNoteObs == null) {
			clinicalNote = (TextInputEditText)view.findViewById(R.id.editClinicalNote);
			clinicalNote.addTextChangedListener(clinicalNoteWatcher);
		}

		if (isActiveVisit) {
			view.findViewById(R.id.editClinicNoteContainer).setVisibility(View.VISIBLE);

		} else {
			view.findViewById(R.id.clinicNoteTextContainer).setVisibility(View.VISIBLE);
		}

		TextView clinicalNoteText = (TextView)view.findViewById(R.id.clinicalNoteText);
		TextView primaryDiagnosis = (TextView)view.findViewById(R.id.primaryDiagnosis);
		TextView secondaryDiagnosis = (TextView)view.findViewById(R.id.secondaryDiagnosis);

		String visitNoteStr = "";
		String primaryObsStr = "";
		String secondaryObsStr = "";
		String chiefComplaintObsStr = "";

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
				visitNoteStr = (((((displayStr.replace(CONFIRMED_DIAGNOSIS, "")).replace(CLINICAL_NOTE, ""))
						.replace(PRESUMED_DIAGNOSIS, "")).replace(DIAGNOSES, "")).replace("(text)", ""))
						.replace(":", " ")
						.replaceAll("" + "( +)", " ").replaceAll("^\\W+|\\W+$", "");

				if (isActiveVisit && clinicalNoteObs == null) {
					this.clinicalNoteObs = observation;
					clinicalNote.setText(visitNoteStr);
					view.findViewById(R.id.clinicalNoteTitle).setVisibility(View.VISIBLE);
				} else {
					clinicalNoteText.setText(visitNoteStr);
				}

			} else if (displayStr.contains(CHIEF_COMPLAINT)) {
				chiefComplaintObsStr = (((((displayStr.replace(CONFIRMED_DIAGNOSIS, "")).replace(CHIEF_COMPLAINT, ""))
						.replace(PRESUMED_DIAGNOSIS, "")).replace(DIAGNOSES, "")).replace("(text)", "")).replace(":", " ")
						.replaceAll("" + "( +)", " ").replaceAll("^\\W+|\\W+$", "");
				if (clinicalNote != null && clinicalNote.getText() != null) {
					//TODO Uncomment out this line when diagnosis is done
					//clinicalNote.setText(chiefComplaintObsStr);
				}
			}
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.primaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (!primaryObsStr.equalsIgnoreCase("")) {
			view.findViewById(R.id.secondaryDiagnosisHolder).setVisibility(View.VISIBLE);
		}

		if (clinicalNoteText.getText().length() == 0) {
			clinicalNoteText.setText(context.getString(R.string.no_diagnoses_label));
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

	private Encounter createClinicalNoteEncounter() {

		SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
		prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);

		//create encountertType
		EncounterType mEncountertype = new EncounterType();
		mEncountertype.setUuid(CLINICAL_NOTE_UUID);

		LocalDateTime localDateTime = new LocalDateTime();

		Patient patient = new Patient();
		patient.setUuid(uuids.get(PATIENT_UUID_BUNDLE).toString());

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

	private void setVisitStopDate(Visit visit) {
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visit.getStopDatetime());
		editor.commit();
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_ITEM;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == VIEW_TYPE_HEADER) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.container_patient_address_info, parent, false);
			return new RecyclerViewHeader(v);
		} else if (viewType == VIEW_TYPE_ITEM) {
			View view = LayoutInflater.from(context).inflate(R.layout.container_visits_observations, parent, false);
			return new VisitViewHolder(view);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof RecyclerViewHeader) {

			RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader)holder;

			updateContactInformation(recyclerViewHeader);

		} else if (holder instanceof VisitViewHolder) {

			boolean isActiveVisit = false;

			VisitViewHolder viewHolder = (VisitViewHolder)holder;

			viewHolder.setIsRecyclable(false);

			Visit visit = visits.get(position - 1);//Subtract one to get the first index taken by the header
			View singleVisitView = layoutInflater.inflate(R.layout.container_single_visit_observation, null);
			TextView visitStartDate = (TextView)singleVisitView.findViewById(R.id.startDate);

			//Let's set the visit title
			String startDate = DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.DATE_FORMAT);

			if (startDate.equalsIgnoreCase(DateUtils.getDateToday(DateUtils.DATE_FORMAT))) {
				startDate = context.getString(R.string.today);
			} else if (startDate.equalsIgnoreCase(DateUtils.getDateYesterday(DateUtils.DATE_FORMAT))) {
				startDate = context.getString(R.string.yesterday);
			}

			String stopDate = visit.getStopDatetime();
			if (!StringUtils.notNull(stopDate)) {
				activeVisit = visit;
				isActiveVisit = true;
				singleVisitView.findViewById(R.id.active_visit_badge).setVisibility(View.VISIBLE);
				activeVisitView = singleVisitView;
			}

			visitStartDate.setText(startDate);
			((TextView)singleVisitView.findViewById(R.id.visitTimeago))
					.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime(), true));

			if (!isActiveVisit) {
				TextView visitDuration = (TextView)singleVisitView.findViewById(R.id.visitDuration);
				visitDuration.setText(context.getString(R.string.visit_duration,
						DateUtils.calculateTimeDifference(visit.getStartDatetime(), visit.getStopDatetime())));
				visitDuration.setVisibility(View.VISIBLE);
			}

			//Adding the link to the visit details page
			LinearLayout showVisitDetails = (LinearLayout)singleVisitView.findViewById(R.id.loadVisitDetails);

			if (isActiveVisit) {
				showVisitDetails.setVisibility(View.VISIBLE);
				showVisitDetails.setOnClickListener(v -> loadVisitDetails(visit));
			} else {
				showVisitDetails.setVisibility(View.GONE);
				singleVisitView.setOnClickListener(v -> loadVisitDetails(visit));
			}

			if (visit.getEncounters().size() == 0) {
				presentClinicalNotes(new Encounter(), singleVisitView, isActiveVisit);
			} else {
				for (Encounter encounter : visit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case ApplicationConstants.EncounterTypeDisplays.VISIT_NOTE:
							presentClinicalNotes(encounter, singleVisitView, isActiveVisit);
							break;
						default:
							presentClinicalNotes(new Encounter(), singleVisitView, isActiveVisit);
							break;
					}
				}
			}

			viewHolder.observationsContainer.addView(singleVisitView);

		}

	}

	@Override
	public int getItemCount() {
		return visits == null ? 0 : visits.size() + 1;//Add an index for the header
	}

	private class RecyclerViewHeader extends RecyclerView.ViewHolder {
		TextView patientAddress;
		TextView patientPhonenumber;

		RecyclerViewHeader(View itemView) {
			super(itemView);
			this.patientAddress = (TextView)itemView.findViewById(R.id.patientAddress);
			this.patientPhonenumber = (TextView)itemView.findViewById(R.id.patientPhonenumber);
		}
	}

	private class VisitViewHolder extends RecyclerView.ViewHolder {
		LinearLayout observationsContainer;

		VisitViewHolder(View view) {
			super(view);
			observationsContainer = (LinearLayout)view.findViewById(R.id.observationsContainer);
		}
	}

}