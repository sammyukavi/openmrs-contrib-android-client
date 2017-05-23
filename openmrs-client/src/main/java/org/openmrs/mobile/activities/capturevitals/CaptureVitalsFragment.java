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

package org.openmrs.mobile.activities.capturevitals;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Value;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

public class CaptureVitalsFragment extends ACBaseFragment<CaptureVitalsContract.Presenter>
		implements CaptureVitalsContract.View {

	private Visit visit;
	private Patient patient;
	private View fragmentView;
	private Location location;
	private EditText patientHeight, patientWeight, patientBMI, patientTemperature, patientPulse, patientRespiratoryRate,
			patientBloodPressure, patientBloodOxygenSaturation;
	private String encounterUuid = null;
	private String visitUuid, patientUuid;
	private OpenMRS instance = OpenMRS.getInstance();

	private TextView patientDisplayName, patientGender, patientAge, patientIdentifier, patientDob;

	public static CaptureVitalsFragment newInstance() {
		return new CaptureVitalsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		this.patientUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		this.visitUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);

		fragmentView = inflater.inflate(R.layout.fragment_capture_vitals, container, false);

		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		patientIdentifier = (TextView)fragmentView.findViewById(R.id.fetchedPatientIdentifier);
		patientGender = (TextView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		patientDob = (TextView)fragmentView.findViewById(R.id.fetchedPatientBirthDate);

		initViewFields();

		//We start by fetching by location, required for creating encounters
		String locationUuid = "";
		if (!instance.getLocation().equalsIgnoreCase(null)) {
			locationUuid = instance.getLocation();
		}

		mPresenter.fetchLocation(locationUuid);

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return fragmentView;
	}

	private void initViewFields() {

		patientHeight = (EditText)fragmentView.findViewById(R.id.patientHeight);
		patientWeight = (EditText)fragmentView.findViewById(R.id.patientWeight);
		patientBMI = (EditText)fragmentView.findViewById(R.id.patientBMI);
		patientTemperature = (EditText)fragmentView.findViewById(R.id.patientTemperature);
		patientPulse = (EditText)fragmentView.findViewById(R.id.patientRespiratoryRate);
		patientBloodPressure = (EditText)fragmentView.findViewById(R.id.patientBloodPressure);

		Button submitForm = (Button)fragmentView.findViewById(R.id.submitConfirm);
		submitForm.setOnClickListener(v -> {
			performDataSend();
		});
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

		Concept concept = new Concept();
		concept.setUuid(questionConceptUuid);

		Value obsValue = new Value();
		obsValue.setUuid(answerConceptUuid);

		observation.setConcept(concept);
		observation.setPerson(patient.getPerson());
		observation.setValue(obsValue);

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
	public void fetchPatientDetails() {
		mPresenter.fetchPatientDetails(patientUuid);
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

	}

	@Override
	public void updateFormFields(Encounter encounter) {
		//Todo fix the bug here before end of closing business day
	}

	private void performDataSend() {
		//Todo fix the bug here before end of closing business day
	}

}
