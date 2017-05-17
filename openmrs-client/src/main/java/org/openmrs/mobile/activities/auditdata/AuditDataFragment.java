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
import android.widget.RadioButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

public class AuditDataFragment extends ACBaseFragment<AuditDataContract.Presenter>
		implements AuditDataContract.View {

	private View fragmentView;
	private TextView patientDisplayName, patientGender, patientAge, patientIdentifier,
			patientDob;
	private Observation auditCompleteObservation, deathInHospitalObservation, palliativeConsultObservation,
			preopRiskAssessmentObservation, icuStay, hduStay, hduComgmt;
	private Concept yesConcept, noConcept;
	private RadioButton auditCompleteYes, auditCompleteNo;
	private Patient patient;

	public AuditDataFragment() {
	}

	public static AuditDataFragment newInstance() {
		return new AuditDataFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_audit_form, container, false);
		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		patientIdentifier = (TextView)fragmentView.findViewById(R.id.fetchedPatientIdentifier);
		patientGender = (TextView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		patientDob = (TextView)fragmentView.findViewById(R.id.fetchedPatientBirthDate);

		String patientId = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		String visitUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);

		mPresenter.fetchPatientData(patientId);

		mPresenter.fetchVisitData(visitUuid);
		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		initViewFields();

		initObservations();

		initConcepts();

		initRadioListeners(auditCompleteYes, auditCompleteNo);

		return fragmentView;
	}

	private void initViewFields() {
		auditCompleteYes = (RadioButton)fragmentView.findViewById(R.id.is_audit_complete_yes);
		auditCompleteNo = (RadioButton)fragmentView.findViewById(R.id.is_audit_complete_no);
	}

	private void initConcepts() {
		yesConcept = new Concept();
		yesConcept.setUuid("");

		noConcept = new Concept();
		noConcept.setUuid("");
	}

	private void initObservations() {
		auditCompleteObservation = new Observation();
		auditCompleteObservation.setUuid("");

		deathInHospitalObservation = new Observation();
		deathInHospitalObservation.setUuid("");

		palliativeConsultObservation = new Observation();
		palliativeConsultObservation.setUuid("");

		preopRiskAssessmentObservation = new Observation();
		preopRiskAssessmentObservation.setUuid("");

		icuStay = new Observation();
		icuStay.setUuid("");

		hduStay = new Observation();
		hduStay.setUuid("");

		hduComgmt = new Observation();
		hduComgmt.setUuid("");
	}

	private void initRadioListeners(RadioButton... params) {
		for (RadioButton radioButton : params) {
			radioButton.setOnClickListener(
					view -> applyEvent(radioButton.getId()));
		}
	}

	private void applyEvent(int id) {
		switch (id) {
			case R.id.is_audit_complete_yes:
				ConsoleLogger.dump("audit complete is yes");
				break;
			case R.id.is_audit_complete_no:
				ConsoleLogger.dump("audit complete is yes");
				break;
			default:
				break;
		}
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
	}
}
