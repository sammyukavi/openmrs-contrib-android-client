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
import android.widget.RadioButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.ANSWER_NO;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.ANSWER_YES;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.AUDIT_COMPLETE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.DEATH_IN_HOSPITAL;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.HDU_COMGMT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.HDU_STAY;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.HIV_POSITIVE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.ICU_STAY;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.PALLIATIVE_CONSULT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.PREOP_RISK_ASSESMENT;

public class AuditDataFragment extends ACBaseFragment<AuditDataContract.Presenter>
		implements AuditDataContract.View {

	private View fragmentView;
	private TextView patientDisplayName, patientGender, patientAge, patientIdentifier, patientDob;
	private Observation deathInHospitalObservation, palliativeConsultObservation, preopRiskAssessmentObservation,
			icuStayObservation, hduStayObservation, hduComgmtObservation, hivPositiveObservation, auditCompleteObservation;
	private Concept yesConcept, noConcept;
	private RadioButton deathInHospitalYes, deathInHospitalNo, palliativeConsultYes, palliativeConsulNo,
			preopRiskAssessmentYes, preopRiskAssessmentNo, icuStayYes, icuStayNo, icuStayUnknown, hduStayYes, hduStayNo,
			hduStayUnknown, hduComgmtYes, hduComgmtNo, hduComgmtUnknown, hivPositiveYes, hivPositiveNo, hivPositiveUnknown,
			auditCompleteYes, auditCompleteNo;
	private Patient patient;
	private Encounter encounter;
	private OpenMRS instance = OpenMRS.getInstance();
	private Visit visit;
	private LocalDateTime localDateTime;
	private String visitUuid, patientUuid;

	public AuditDataFragment() {
		localDateTime = new LocalDateTime();
	}

	public static AuditDataFragment newInstance() {
		return new AuditDataFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		this.patientUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		this.visitUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);

		fragmentView = inflater.inflate(R.layout.fragment_audit_form, container, false);

		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		patientIdentifier = (TextView)fragmentView.findViewById(R.id.fetchedPatientIdentifier);
		patientGender = (TextView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		patientDob = (TextView)fragmentView.findViewById(R.id.fetchedPatientBirthDate);

		initViewFields();

		initEncounters();

		initConcepts();

		initRadioListeners(deathInHospitalYes, deathInHospitalNo, palliativeConsultYes, palliativeConsulNo,
				preopRiskAssessmentYes, preopRiskAssessmentNo, icuStayYes, icuStayNo, icuStayUnknown, hduStayYes, hduStayNo,
				hduStayUnknown, hduComgmtYes, hduComgmtNo, hduComgmtUnknown, hivPositiveYes, hivPositiveNo,
				hivPositiveUnknown,
				auditCompleteYes, auditCompleteNo);

		mPresenter.fetchPatientDetails(patientUuid);

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return fragmentView;
	}

	private void initEncounters() {
		encounter = new Encounter();
	}

	private void initViewFields() {

		deathInHospitalYes = (RadioButton)fragmentView.findViewById(R.id.is_death_in_hospital_yes);
		deathInHospitalNo = (RadioButton)fragmentView.findViewById(R.id.is_death_in_hospital_no);

		palliativeConsultYes = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_yes);
		palliativeConsulNo = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_no);

		preopRiskAssessmentYes = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_yes);
		preopRiskAssessmentNo = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_no);

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

		auditCompleteYes = (RadioButton)fragmentView.findViewById(R.id.is_audit_complete_yes);
		auditCompleteNo = (RadioButton)fragmentView.findViewById(R.id.is_audit_complete_no);

		Button submitForm = (Button)fragmentView.findViewById(R.id.submitConfirm);
		submitForm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				/*//create audit info
				User user = new User();
				user.setUuid(instance.getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys
						.USER_UUID));

				AuditInfo auditInfo = new AuditInfo();
				auditInfo.setCreator(user);

				Form encounterForm = new Form();
				encounterForm.setUuid("667dc18e-740f-44ce-ae0a-5ba6b33308b0");

				//create observation
				Observation observation = new Observation();
				observation.setConcept(yesConcept);
				//observation.setValue(yesConcept.getUuid());
				observation.setPerson(patient.getPerson());
				observation.setObsDatetime(localDateTime.toString());
				observation.setCreator(user);
				observation.setAuditInfo(auditInfo);

				observation.setLocation("7f65d926-57d6-4402-ae10-a5b3bcbf7986");

				List<Observation> observationList = new ArrayList<>();
				observationList.add(observation);

				EncounterType mEncountertype = new EncounterType();
				mEncountertype.setUuid(AUDITDATA);

				encounter.setPatient(patient);
				encounter.setEncounterType(mEncountertype);
				encounter.setObs(observationList);
				encounter.setVisit(visit);
				encounter.setEncounterDatetime(localDateTime.toString());
				encounter.setAuditInfo(auditInfo);

				encounter.setForm(encounterForm);
				mPresenter.createEncounter(encounter);*/
			}
		});
	}

	private void initConcepts() {
		yesConcept = new Concept();
		yesConcept.setUuid(ANSWER_YES);

		noConcept = new Concept();
		noConcept.setUuid(ANSWER_NO);
	}

	private void initObservations() {
		deathInHospitalObservation = palliativeConsultObservation = preopRiskAssessmentObservation =
				icuStayObservation = hduStayObservation =
						hduComgmtObservation = hivPositiveObservation = auditCompleteObservation = new Observation();

		deathInHospitalObservation.setUuid(DEATH_IN_HOSPITAL);
		//deathInHospitalObservation.setPerson(patient.getPerson());

		//deathInHospitalObservation.setObsDatetime(localDateTime.toString());

		palliativeConsultObservation.setUuid(PALLIATIVE_CONSULT);

		preopRiskAssessmentObservation.setUuid(PREOP_RISK_ASSESMENT);

		icuStayObservation.setUuid(ICU_STAY);

		hduStayObservation.setUuid(HDU_STAY);

		hduComgmtObservation.setUuid(HDU_COMGMT);

		hivPositiveObservation.setUuid(HIV_POSITIVE);

		auditCompleteObservation.setUuid(AUDIT_COMPLETE);
	}

	private void initRadioListeners(RadioButton... params) {
		for (RadioButton radioButton : params) {
			radioButton.setOnClickListener(
					view -> applyEvent(radioButton.getId()));
		}
	}

	private void applyEvent(int id) {
		switch (id) {
			case R.id.is_death_in_hospital_yes:
				//deathInHospitalObservation.setConcept(yesConcept);
				//deathInHospitalObservation.setValue(yesConcept.getUuid());
				break;
			case R.id.is_death_in_hospital_no:

				//deathInHospitalObservation.setValue(noConcept.getUuid());

				break;

			case R.id.is_palliative_consult_yes:

				//palliativeConsultObservation.setValue(yesConcept.getUuid());

				break;
			case R.id.is_palliative_consult_no:

				//palliativeConsultObservation.setValue(noConcept.getUuid());

				break;

			case R.id.is_preop_risk_assessment_only_yes:

				break;
			case R.id.is_preop_risk_assessment_only_no:

				break;
			case R.id.is_icu_stay_yes:

				break;
			case R.id.is_icu_stay_no:

				break;
			case R.id.is_icu_stay_unknown:

				break;
			case R.id.is_hdu_stay_yes:

				break;
			case R.id.is_hdu_stay_no:

				break;
			case R.id.is_hdu_stay_unknown:

				break;

			case R.id.is_hdu_comgmt_yes:

				break;
			case R.id.is_hdu_comgmt_no:

				break;
			case R.id.is_hdu_comgmt_unknown:

				break;

			case R.id.is_hiv_positive_yes:

				break;
			case R.id.is_hiv_positive_no:

				break;
			case R.id.is_hiv_positive_unknown:

				break;

			case R.id.is_audit_complete_yes:

				break;
			case R.id.is_audit_complete_no:

				break;
			default:
				break;
		}
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	@Override
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	@Override
	public void updateContactCard(Patient patient) {
		this.patient = patient;
		Person person = patient.getPerson();
		patientDisplayName.setText(person.getName().getNameString());
		patientGender.setText(person.getGender());
		patientIdentifier.setText(patient.getIdentifier().getIdentifier());
		DateTime date = DateUtils.convertTimeString(person.getBirthdate());
		patientAge.setText(DateUtils.calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
		patientDob.setText(DateUtils.convertTime1(person.getBirthdate(), DateUtils.PATIENT_DASHBOARD_DOB_DATE_FORMAT));

		mPresenter.fetchVisit(visitUuid);

		initObservations();
	}

	@Override
	public void updateForm(Observation observation) {
		System.out.println("======================================================================");
		//ConsoleLogger.dump(observation);
		System.out.println(observation.getValue());
		System.out.println("======================================================================");
	}

}
