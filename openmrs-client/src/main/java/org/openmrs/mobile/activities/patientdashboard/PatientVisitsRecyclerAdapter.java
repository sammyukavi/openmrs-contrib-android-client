package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.IBaseDiagnosisFragment;
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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.LOCATION_UUID_BUNDLE;
import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE;
import static org.openmrs.mobile.utilities.ApplicationConstants.ClinicalFormConcepts.ClinicFormUUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeEntity.CLINICAL_NOTE_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.FORM_UUIDS.CLINICAL_FORM_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.CLINICAL_NOTE;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.SECONDARY_DIAGNOSIS;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.COUNTY;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.SUBCOUNTY;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.TELEPHONE;

public class PatientVisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int VIEW_TYPE_HEADER = 0;
	private final int VIEW_TYPE_ITEM = 1;
	private HashMap<String, String> uuids;
	private Context context;
	private List<Visit> visits;
	private LayoutInflater layoutInflater;
	private OpenMRS instance = OpenMRS.getInstance();
	private long delay = 3000; //seconds after user stops typing
	private long lastTextEdit = 0;
	private LocalDateTime localDateTime;
	private PatientDashboardActivity patientDashboardActivity;
	private Visit activeVisit;
	private View activeVisitView;
	private Observation clinicalNoteObservation;
	private boolean firstTimeEdit;
	private IBaseDiagnosisFragment baseDiagnosisFragment;
	private boolean hasStartedDiagnoses;

	private LinearLayoutManager primaryDiagnosisLayoutManager, secondaryDiagnosisLayoutManager;
	private LinearLayout diagnosesLayout, pastDiagnosisLayout, loadVisitDetails;
	private RelativeLayout singleVisitTitle;
	private TextInputEditText clinicalNote;

	public PatientVisitsRecyclerAdapter(RecyclerView visitsRecyclerView, List<Visit> visits,
			Context context, IBaseDiagnosisFragment baseDiagnosisFragment) {
		this.visits = visits;
		this.context = context;
		this.localDateTime = new LocalDateTime();
		this.layoutInflater = LayoutInflater.from(context);
		this.patientDashboardActivity = (PatientDashboardActivity)context;
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		visitsRecyclerView.setLayoutManager(linearLayoutManager);
		this.baseDiagnosisFragment = baseDiagnosisFragment;
		this.hasStartedDiagnoses = false;
	}

	// condition for header
	private boolean isPositionHeader(int position) {
		return position == 0;
	}

	// getItem -1 because we have add 1 header
	private Visit getItem(int position) {
		return visits.get(position - 1);
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionHeader(position)) {
			return VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_ITEM;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_HEADER) {
			View personAddressView =
					LayoutInflater.from(parent.getContext()).inflate(R.layout.container_patient_address_info, parent,
							false);
			return new RecyclerViewHeader(personAddressView);
		} else if (viewType == VIEW_TYPE_ITEM) {
			View patientVisitView =
					LayoutInflater.from(parent.getContext()).inflate(R.layout.container_visits_observations, parent,
							false);
			return new PatientVisitViewHolder(patientVisitView);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof RecyclerViewHeader) {
			RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader)holder;
			updateContactInformation(recyclerViewHeader);
		} else if (holder instanceof PatientVisitViewHolder) {
			boolean isActiveVisit = false;
			PatientVisitViewHolder viewHolder = (PatientVisitViewHolder)holder;
			viewHolder.setIsRecyclable(false);
			Visit visit = getItem(position);//Subtract one to get the first index taken by the header
			View singleVisitView = layoutInflater.inflate(R.layout.container_single_visit_observation, null);
			diagnosesLayout = (LinearLayout)singleVisitView.findViewById(R.id.diagnosesLayout);
			pastDiagnosisLayout = (LinearLayout)singleVisitView.findViewById(R.id.pastDiagnosisLayout);
			singleVisitTitle = (RelativeLayout)singleVisitView.findViewById(R.id.singleVisitTitle);
			diagnosesLayout.setVisibility(View.GONE);
			pastDiagnosisLayout.setVisibility(View.GONE);
			TextView visitStartDate = (TextView)singleVisitView.findViewById(R.id.startDate);
			clinicalNote = (TextInputEditText)singleVisitView.findViewById(R.id.editClinicalNote);

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
				diagnosesLayout.setVisibility(View.VISIBLE);
				pastDiagnosisLayout.setVisibility(View.GONE);
				// init diagnoses
				initDiagnosesComponents(singleVisitView);
				baseDiagnosisFragment.setVisit(visit);
			}

			visitStartDate.setText(startDate);
			((TextView)singleVisitView.findViewById(R.id.visitTimeago))
					.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime(), true));

			if (!isActiveVisit) {
				TextView visitDuration = (TextView)singleVisitView.findViewById(R.id.visitDuration);
				visitDuration.setText(context.getString(R.string.visit_duration,
						DateUtils.calculateTimeDifference(visit.getStartDatetime(), visit.getStopDatetime())));
				visitDuration.setVisibility(View.VISIBLE);
				pastDiagnosisLayout.setVisibility(View.VISIBLE);
				diagnosesLayout.setVisibility(View.GONE);
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
					if (encounter.getEncounterType().getDisplay()
							.equalsIgnoreCase(ApplicationConstants.EncounterTypeDisplays.VISIT_NOTE)) {
						if (activeVisit == visit) {
							baseDiagnosisFragment.setEncounterUuid(encounter.getUuid());
							baseDiagnosisFragment.setClinicalNote(clinicalNote.getText().toString());
						}
						presentClinicalNotes(encounter, singleVisitView, isActiveVisit);
						break;
					} else {
						presentClinicalNotes(new Encounter(), singleVisitView, isActiveVisit);
						break;
					}
				}
			}

			if (!hasStartedDiagnoses && isActiveVisit) {
				baseDiagnosisFragment.initializeListeners();
				baseDiagnosisFragment.setDiagnoses(activeVisit);
				hasStartedDiagnoses = true;
			}

			if (isActiveVisit) {
				singleVisitTitle.setClickable(true);
				singleVisitTitle.setOnClickListener(v -> loadVisitDetails(visit));
			}

			viewHolder.patientVisitDetailsContainer.addView(singleVisitView);
		}
	}

	private void initDiagnosesComponents(View view) {
		if (baseDiagnosisFragment.getSearchDiagnosisView() == null) {
			baseDiagnosisFragment.setSearchDiagnosisView(
					(AutoCompleteTextView)view.findViewById(R.id.searchDiagnosis));
			baseDiagnosisFragment.setNoPrimaryDiagnoses(
					(TextView)view.findViewById(R.id.noPrimaryDiagnosis));
			baseDiagnosisFragment.setNoSecondaryDiagnoses(
					(TextView)view.findViewById(R.id.noSecondaryDiagnosis));
			baseDiagnosisFragment.setPrimaryDiagnosesRecycler(
					(RecyclerView)view.findViewById(R.id.primaryDiagnosisRecyclerView));
			baseDiagnosisFragment.setSecondaryDiagnosesRecycler(
					(RecyclerView)view.findViewById(R.id.secondaryDiagnosisRecyclerView));
			baseDiagnosisFragment.setSubmitVisitNote(
					(AppCompatButton)view.findViewById(R.id.submitVisitNote));
			baseDiagnosisFragment.getSubmitVisitNote().setVisibility(View.GONE);

			primaryDiagnosisLayoutManager = new LinearLayoutManager(context);
			secondaryDiagnosisLayoutManager = new LinearLayoutManager(context);

			baseDiagnosisFragment.getPrimaryDiagnosesRecycler().setLayoutManager(primaryDiagnosisLayoutManager);
			baseDiagnosisFragment.getSecondaryDiagnosesRecycler().setLayoutManager(secondaryDiagnosisLayoutManager);
		}
	}

	public void setUuids(HashMap<String, String> uuids) {
		this.uuids = uuids;
	}

	public void updateSavingClinicalNoteProgressBar(boolean show) {
		if (activeVisitView != null) {
			activeVisitView.findViewById(R.id.savingProgressBarView).setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	public void updateClinicalNoteObs(Observation observation) {
		this.clinicalNoteObservation = observation;
	}

	private void loadVisitDetails(Visit visit) {
		if (!patientDashboardActivity.mPresenter.isLoading()) {
			setVisitStopDate(visit);
			Intent intent = new Intent(context, VisitActivity.class);
			intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, OpenMRS.getInstance()
					.getPatientUuid());
			intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visit.getUuid());
			intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visit.getStopDatetime());

			intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);

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
		firstTimeEdit = true;

		Handler handler = new Handler();
		Runnable inputCompleteChecker = () -> {
			if (System.currentTimeMillis() > (lastTextEdit + delay - 500)) {
				if (clinicalNoteObservation != null) {
					clinicalNoteObservation.setValue(clinicalNote.getText().toString());
					clinicalNoteObservation.setObsDatetime(localDateTime.toString());
					patientDashboardActivity.mPresenter.saveObservation(clinicalNoteObservation, false);

				} else {
					//create concept
					Concept concept = new Concept();
					concept.setUuid(ClinicFormUUID);

					Person person = new Person();
					person.setUuid(uuids.get(PATIENT_UUID_BUNDLE));

					Provider provider = new Provider();
					provider.setUuid(OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys
							.USER_UUID));

					Location location = new Location();
					location.setUuid(uuids.get(LOCATION_UUID_BUNDLE));

					//create observation
					Observation observation = new Observation();
					observation.setConcept(concept);
					observation.setPerson(person);
					observation.setObsDatetime(localDateTime.toString());
					observation.setProvider(provider);
					observation.setLocation(uuids.get(LOCATION_UUID_BUNDLE));
					observation.setValue(clinicalNote.getText().toString());

					List<Observation> observationList = new ArrayList<>();

					observationList.add(observation);

					Encounter clinicNoteEncounter = createClinicalNoteEncounter();
					clinicNoteEncounter.setObs(observationList);

					patientDashboardActivity.mPresenter.saveEncounter(clinicNoteEncounter, true);
				}

			}
		};

		clinicalNote.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(final CharSequence s, int start, int before,
					int count) {
				//Remove this to run only once
				handler.removeCallbacks(inputCompleteChecker);
			}

			@Override
			public void afterTextChanged(final Editable s) {

				if (s.length() > 0 && !firstTimeEdit) {
					lastTextEdit = System.currentTimeMillis();
					handler.postDelayed(inputCompleteChecker, delay);
				} else {
					firstTimeEdit = false;
				}

			}
		});

		if (isActiveVisit) {
			view.findViewById(R.id.editClinicNoteContainer).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.clinicNoteTextContainer).setVisibility(View.VISIBLE);
		}

		TextView clinicalNoteText = (TextView)view.findViewById(R.id.clinicalNoteText);
		TextView primaryDiagnosis = (TextView)view.findViewById(R.id.primaryDiagnosis);
		TextView secondaryDiagnosis = (TextView)view.findViewById(R.id.secondaryDiagnosis);

		ArrayList clinicalNoteString;
		String primaryDiagnosisString = "";
		String secondaryDiagnosisString = "";

		for (Observation observation : encounter.getObs()) {
			String observationDisplay = observation.getDisplay();

			if (observationDisplay.contains(PRIMARY_DIAGNOSIS)) {
				if (!primaryDiagnosisString.equalsIgnoreCase("")) {
					primaryDiagnosisString += ", ";
				}
				primaryDiagnosisString += (StringUtils.getConceptName(observationDisplay));
				primaryDiagnosis.setText(primaryDiagnosisString);
			} else if (observationDisplay.contains(SECONDARY_DIAGNOSIS)) {
				if (!secondaryDiagnosisString.equalsIgnoreCase("")) {
					secondaryDiagnosisString += ", ";
				}
				secondaryDiagnosisString += (StringUtils.getConceptName(observationDisplay));
				secondaryDiagnosis.setText(secondaryDiagnosisString);
			} else if (observationDisplay.contains(CLINICAL_NOTE)) {
				clinicalNoteString = StringUtils.splitStrings(observationDisplay, ":");

				if (isActiveVisit) {
					clinicalNoteObservation = observation;
					clinicalNote.setText(clinicalNoteString.get(1).toString());
					view.findViewById(R.id.clinicalNoteTitle).setVisibility(View.VISIBLE);
				} else {
					view.findViewById(R.id.clinicalNoteTitle).setVisibility(View.VISIBLE);
					clinicalNoteText.setText(clinicalNoteString.get(1).toString());
				}

			}
		}

		if (clinicalNoteText.getText().length() == 0) {
			clinicalNoteText.setText(context.getString(R.string.no_clinical_note));
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

	private class PatientVisitViewHolder extends RecyclerView.ViewHolder {
		LinearLayout patientVisitDetailsContainer;

		PatientVisitViewHolder(View view) {
			super(view);
			patientVisitDetailsContainer = (LinearLayout)view.findViewById(R.id.patientVisitDetailsContainer);
		}
	}
}