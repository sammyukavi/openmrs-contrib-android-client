/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.auditdata;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Value;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_NO;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_UNKNOWN;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_YES;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_NO;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_UNKNOWN;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_YES;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_AUDIT_COMPLETE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_CD4_COUNT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_DEATH_IN_HOSPITAL;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HBA1C;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HDU_COMGMT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HDU_STAY;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HIV_POSITIVE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ICU_STAY;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_INPATIENT_SERVICE_TYPE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_PALLIATIVE_CONSULT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_PREOP_RISK_ASSESMENT;
import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeEntity.AUDIT_DATA_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.FORM_UUIDS.AUDIT_DATA_FORM_UUID;

public class AuditDataFragment extends ACBaseFragment<AuditDataContract.Presenter>
		implements AuditDataContract.View {

	private Visit visit;
	//private Patient patient;
	private View fragmentView;
	private Location location;
	private EditText cd4, hBa1c;
	private String encounterUuid = null;
	private String visitUuid, patientUuid;
	private OpenMRS instance = OpenMRS.getInstance();

	private Observation deathInHospitalObservation, palliativeConsultObservation, preopRiskAssessmentObservation,
			icuStayObservation, hduStayObservation, hduComgmtObservation, hivPositiveObservation, cd4Observation,
			hBa1cObservation, inpatientServiceTypeObservation, auditCompleteObservation;
	private RadioButton deathInHospitalYes, deathInHospitalNo, palliativeConsultYes, palliativeConsultNo,
			palliativeConsultUknown, preopRiskAssessmentYes, preopRiskAssessmentNo, preopRiskAssessmentUknown, icuStayYes,
			icuStayNo, icuStayUnknown, hduStayYes, hduStayNo, hduStayUnknown, hduComgmtYes, hduComgmtNo, hduComgmtUnknown,
			hivPositiveYes, hivPositiveNo, hivPositiveUnknown;
	private CheckBox auditComplete;

	private Spinner inpatientServiceType;

	public static AuditDataFragment newInstance() {
		return new AuditDataFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		this.patientUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		this.visitUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);

		fragmentView = inflater.inflate(R.layout.fragment_audit_form, container, false);

		initViewFields();

		initRadioButtonListeners(deathInHospitalYes, deathInHospitalNo, palliativeConsultYes, palliativeConsultNo,
				palliativeConsultUknown, preopRiskAssessmentYes, preopRiskAssessmentNo, preopRiskAssessmentUknown,
				icuStayYes, icuStayNo, icuStayUnknown, hduStayYes, hduStayNo, hduStayUnknown, hduComgmtYes, hduComgmtNo,
				hduComgmtUnknown, hivPositiveYes, hivPositiveNo, hivPositiveUnknown);

		initCheckboxListeners(auditComplete);

		//We start by fetching by location, required for creating encounters
		String locationUuid = "";
		if (!instance.getLocation().equalsIgnoreCase(null)) {
			locationUuid = instance.getLocation();
		}

		mPresenter.fetchLocation(locationUuid);
		mPresenter.fetchVisit(visitUuid);

		initObservations();

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return fragmentView;
	}

	private void initViewFields() {

		deathInHospitalYes = (RadioButton)fragmentView.findViewById(R.id.is_death_in_hospital_yes);
		deathInHospitalNo = (RadioButton)fragmentView.findViewById(R.id.is_death_in_hospital_no);

		palliativeConsultYes = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_yes);
		palliativeConsultNo = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_no);
		palliativeConsultUknown = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_unknown);

		preopRiskAssessmentYes = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_yes);
		preopRiskAssessmentNo = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_no);
		preopRiskAssessmentUknown = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_unknown);

		icuStayYes = (RadioButton)fragmentView.findViewById(R.id.is_icu_stay_yes);
		icuStayNo = (RadioButton)fragmentView.findViewById(R.id.is_icu_stay_no);
		icuStayUnknown = (RadioButton)fragmentView.findViewById(R.id.is_icu_stay_unknown);

		hduStayYes = (RadioButton)fragmentView.findViewById(R.id.is_hdu_stay_yes);
		hduStayNo = (RadioButton)fragmentView.findViewById(R.id.is_hdu_stay_no);
		hduStayUnknown = (RadioButton)fragmentView.findViewById(R.id.is_hdu_stay_unknown);

		hduComgmtYes = (RadioButton)fragmentView.findViewById(R.id.is_hdu_comgmt_yes);
		hduComgmtNo = (RadioButton)fragmentView.findViewById(R.id.is_hdu_comgmt_no);
		hduComgmtUnknown = (RadioButton)fragmentView.findViewById(R.id.is_hdu_comgmt_unknown);

		hivPositiveYes = (RadioButton)fragmentView.findViewById(R.id.is_hiv_positive_yes);
		hivPositiveNo = (RadioButton)fragmentView.findViewById(R.id.is_hiv_positive_no);
		hivPositiveUnknown = (RadioButton)fragmentView.findViewById(R.id.is_hiv_positive_unknown);

		auditComplete = (CheckBox)fragmentView.findViewById(R.id.audit_complete);

		cd4 = (EditText)fragmentView.findViewById(R.id.cd4);

		hBa1c = (EditText)fragmentView.findViewById(R.id.hba1c);

		inpatientServiceType = (Spinner)fragmentView.findViewById(R.id.inpatient_service_type);

		Button submitForm = (Button)fragmentView.findViewById(R.id.submitConfirm);
		submitForm.setOnClickListener(v -> {
			performDataSend();
		});
	}

	private void initObservations() {
		deathInHospitalObservation = palliativeConsultObservation = preopRiskAssessmentObservation =
				icuStayObservation = hduStayObservation = hduComgmtObservation =
						hivPositiveObservation = auditCompleteObservation = hBa1cObservation = cd4Observation = null;

	}

	private void initRadioButtonListeners(RadioButton... params) {
		for (RadioButton radioButton : params) {
			radioButton.setOnClickListener(
					view -> applyEvent(radioButton.getId()));
		}
	}

	private void initCheckboxListeners(CheckBox... params) {
		for (CheckBox checkBox : params) {
			checkBox.setOnClickListener(
					view -> applyEvent(checkBox.getId()));
		}
	}

	private void applyEvent(int id) {
		switch (id) {
			case R.id.is_death_in_hospital_yes:

				deathInHospitalObservation = setObservationFields(deathInHospitalObservation, CONCEPT_DEATH_IN_HOSPITAL,
						CONCEPT_ANSWER_YES);

				break;
			case R.id.is_death_in_hospital_no:

				deathInHospitalObservation = setObservationFields(deathInHospitalObservation, CONCEPT_DEATH_IN_HOSPITAL,
						CONCEPT_ANSWER_NO);

				break;

			case R.id.is_palliative_consult_yes:

				palliativeConsultObservation = setObservationFields(palliativeConsultObservation,
						CONCEPT_PALLIATIVE_CONSULT,
						CONCEPT_ANSWER_YES);

				break;

			case R.id.is_palliative_consult_no:

				palliativeConsultObservation = setObservationFields(palliativeConsultObservation,
						CONCEPT_PALLIATIVE_CONSULT,
						CONCEPT_ANSWER_NO);

				break;

			case R.id.is_palliative_consult_unknown:

				palliativeConsultObservation = setObservationFields(palliativeConsultObservation,
						CONCEPT_PALLIATIVE_CONSULT,
						CONCEPT_ANSWER_UNKNOWN);

				break;

			case R.id.is_preop_risk_assessment_only_yes:

				preopRiskAssessmentObservation = setObservationFields(preopRiskAssessmentObservation,
						CONCEPT_PREOP_RISK_ASSESMENT, CONCEPT_ANSWER_YES);

				break;

			case R.id.is_preop_risk_assessment_only_no:

				preopRiskAssessmentObservation = setObservationFields(preopRiskAssessmentObservation,
						CONCEPT_PREOP_RISK_ASSESMENT, CONCEPT_ANSWER_NO);

				break;

			case R.id.is_preop_risk_assessment_only_unknown:

				preopRiskAssessmentObservation = setObservationFields(preopRiskAssessmentObservation,
						CONCEPT_PREOP_RISK_ASSESMENT, CONCEPT_ANSWER_UNKNOWN);

				break;

			case R.id.is_icu_stay_yes:

				icuStayObservation = setObservationFields(icuStayObservation,
						CONCEPT_ICU_STAY, CONCEPT_ANSWER_YES);

				break;

			case R.id.is_icu_stay_no:

				icuStayObservation = setObservationFields(icuStayObservation,
						CONCEPT_ICU_STAY, CONCEPT_ANSWER_NO);

				break;
			case R.id.is_icu_stay_unknown:

				icuStayObservation = setObservationFields(icuStayObservation,
						CONCEPT_ICU_STAY, CONCEPT_ANSWER_UNKNOWN);

				break;
			case R.id.is_hdu_stay_yes:

				hduStayObservation = setObservationFields(hduStayObservation,
						CONCEPT_HDU_STAY, CONCEPT_ANSWER_YES);

				break;
			case R.id.is_hdu_stay_no:

				hduStayObservation = setObservationFields(hduStayObservation,
						CONCEPT_HDU_STAY, CONCEPT_ANSWER_NO);

				break;
			case R.id.is_hdu_stay_unknown:

				hduStayObservation = setObservationFields(hduStayObservation,
						CONCEPT_HDU_STAY, CONCEPT_ANSWER_UNKNOWN);

				break;

			case R.id.is_hdu_comgmt_yes:

				hduComgmtObservation = setObservationFields(hduComgmtObservation,
						CONCEPT_HDU_COMGMT, CONCEPT_ANSWER_YES);

				break;
			case R.id.is_hdu_comgmt_no:

				hduComgmtObservation = setObservationFields(hduComgmtObservation,
						CONCEPT_HDU_COMGMT, CONCEPT_ANSWER_NO);

				break;
			case R.id.is_hdu_comgmt_unknown:

				hduComgmtObservation = setObservationFields(hduComgmtObservation,
						CONCEPT_HDU_COMGMT, CONCEPT_ANSWER_UNKNOWN);

				break;

			case R.id.is_hiv_positive_yes:

				hivPositiveObservation = setObservationFields(hivPositiveObservation,
						CONCEPT_HIV_POSITIVE, CONCEPT_ANSWER_YES);

				break;
			case R.id.is_hiv_positive_no:

				hivPositiveObservation = setObservationFields(hivPositiveObservation,
						CONCEPT_HIV_POSITIVE, CONCEPT_ANSWER_NO);

				break;
			case R.id.is_hiv_positive_unknown:

				hivPositiveObservation = setObservationFields(hivPositiveObservation,
						CONCEPT_HIV_POSITIVE, CONCEPT_ANSWER_UNKNOWN);

				break;

			default:
				break;
		}
	}

	/**
	 * This function  assigns values to an Observation object
	 * @param observation         the observation that you want to set the values
	 * @param questionConceptUuid The uuid of the Concept that the is used a question
	 * @param answerConceptUuid   The uuid of the Concept that the user has selected as the answer
	 */

	private Observation setObservationFields(Observation observation, String questionConceptUuid, String
			answerConceptUuid) {
		if (observation == null) {
			observation = new Observation();
		}

		//here we assign all observations current time
		LocalDateTime localDateTime = new LocalDateTime();
		String timeString = localDateTime.toString();

		Concept concept = new Concept();
		concept.setUuid(questionConceptUuid);

		Value obsValue = new Value();
		obsValue.setUuid(answerConceptUuid);

		Person person = new Person();
		person.setUuid(patientUuid);

		observation.setConcept(concept);
		observation.setPerson(person);
		observation.setValue(obsValue);
		observation.setObsDatetime(timeString);

		return observation;
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	@Override
	public void setEncounterUuid(String encounterUuid) {
		this.encounterUuid = encounterUuid;
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public void showSnackbar(String message) {
		((ACBaseActivity)getActivity()).showSnackbar(message);
	}

	@Override
	public void updateFormFields(Encounter encounter) {

		for (Observation observation : encounter.getObs()) {

			String displayValue = observation.getDisplayValue().trim().toLowerCase();

			switch (observation.getConcept().getUuid()) {

				case CONCEPT_DEATH_IN_HOSPITAL:
					deathInHospitalObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						deathInHospitalYes.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						deathInHospitalNo.setChecked(true);
					}

					break;

				case CONCEPT_PALLIATIVE_CONSULT:

					palliativeConsultObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						palliativeConsultYes.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						palliativeConsultNo.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						palliativeConsultUknown.setChecked(true);
					}

					break;

				case CONCEPT_PREOP_RISK_ASSESMENT:

					preopRiskAssessmentObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						preopRiskAssessmentYes.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						preopRiskAssessmentNo.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						preopRiskAssessmentUknown.setChecked(true);
					}

					break;

				case CONCEPT_ICU_STAY:

					icuStayObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						icuStayYes.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						icuStayNo.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						icuStayUnknown.setChecked(true);
					}

					break;

				case CONCEPT_HDU_STAY:

					hduStayObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						hduStayYes.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						hduStayNo.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						hduStayUnknown.setChecked(true);
					}

					break;
				case CONCEPT_HDU_COMGMT:

					hduComgmtObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						hduComgmtYes.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						hduComgmtNo.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						hduComgmtUnknown.setChecked(true);
					}

					break;
				case CONCEPT_HIV_POSITIVE:

					hivPositiveObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						hivPositiveYes.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						hivPositiveNo.setChecked(true);
					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						hivPositiveUnknown.setChecked(true);
					}

					break;
				case CONCEPT_CD4_COUNT:

					cd4Observation = observation;
					cd4.setText(displayValue);

					break;
				case CONCEPT_HBA1C:

					hBa1cObservation = observation;

					hBa1c.setText(displayValue);

					break;
				case CONCEPT_INPATIENT_SERVICE_TYPE:

					/*inpatientServiceTypeObservation = observation;
					//Short fix. Values mismatch from server.
					//TODO  find a way of putting thisngs in the settings page
					if (displayValue.equalsIgnoreCase("surgical service")) {
						displayValue = "Surgery service";
					}

					ArrayAdapter<CharSequence> adapter = ArrayAdapter
							.createFromResource(getContext(), R.array.inpatient_service_types, android.R.layout
									.simple_spinner_item);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					inpatientServiceType.setAdapter(adapter);
					if (!displayValue.equals(null)) {
						int spinnerPosition = adapter.getPosition(displayValue);
						inpatientServiceType.setSelection(spinnerPosition);
					}*/

					break;
				case CONCEPT_AUDIT_COMPLETE:

					auditCompleteObservation = observation;

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						auditComplete.setChecked(true);
					}

					break;
				default:
					break;

			}
		}

	}

	private void performDataSend() {
		//create form instance
		Form auditDataForm = new Form();
		auditDataForm.setUuid(AUDIT_DATA_FORM_UUID);

		//create encountertype
		EncounterType auditFormEncounterType = new EncounterType();
		auditFormEncounterType.setUuid(AUDIT_DATA_UUID);

		//create provider
		Provider provider = new Provider();
		provider.setUuid(instance.getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID));

		List<Observation> observations = new ArrayList<Observation>();
		List<Provider> providers = new ArrayList<Provider>();
		providers.add(provider);

		if (deathInHospitalObservation != null) {
			observations.add(deathInHospitalObservation);
		}

		if (palliativeConsultObservation != null) {
			observations.add(palliativeConsultObservation);
		}

		if (preopRiskAssessmentObservation != null) {
			observations.add(preopRiskAssessmentObservation);
		}

		if (icuStayObservation != null) {
			observations.add(icuStayObservation);
		}

		if (hduStayObservation != null) {
			observations.add(hduStayObservation);
		}

		if (hduComgmtObservation != null) {
			observations.add(hduComgmtObservation);
		}

		if (hivPositiveObservation != null) {
			observations.add(hivPositiveObservation);
		}

		if (cd4Observation != null) {
			observations.add(cd4Observation);
		}

		if (hBa1cObservation != null) {
			observations.add(hBa1cObservation);
		}

		if (inpatientServiceTypeObservation != null) {
			observations.add(inpatientServiceTypeObservation);
		}

		if (auditComplete.isChecked()) {
			auditCompleteObservation = setObservationFields(auditCompleteObservation,
					CONCEPT_AUDIT_COMPLETE, CONCEPT_ANSWER_YES);
		} else {
			auditCompleteObservation = setObservationFields(auditCompleteObservation,
					CONCEPT_AUDIT_COMPLETE, CONCEPT_ANSWER_NO);
		}

		observations.add(auditCompleteObservation);

		boolean isNewEncounter = false;

		Encounter encounter = new Encounter();

		if (encounterUuid == null) {
			ConsoleLogger.dump("Creating new encounter");
			isNewEncounter = true;
		} else {
			encounter.setUuid(encounterUuid);
		}

		encounter.setObs(observations);
		encounter.setPatient(visit.getPatient());
		encounter.setForm(auditDataForm);
		encounter.setLocation(location);
		encounter.setVisit(visit);
		encounter.setProvider(instance.getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID));
		encounter.setEncounterType(auditFormEncounterType);
		encounter.setEncounterDatetime(visit.getStartDatetime());

		mPresenter.saveEncounter(encounter, isNewEncounter);
	}

}