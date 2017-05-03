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

package org.openmrs.mobile.activities.addeditpatient;

import android.util.Log;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AddEditPatientPresenter extends BasePresenter implements AddEditPatientContract.Presenter {

	private final AddEditPatientContract.View mPatientInfoView;

	private PatientDataService patientDataService;
	private ConceptDataService conceptDataService;
	private RestApi restApi;
	private Patient mPatient;
	private String patientToUpdateId;
	private List<String> mCounties;
	private boolean registeringPatient = false;

	public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView,
			List<String> countries,
			String patientToUpdateId) {
		this.mPatientInfoView = mPatientInfoView;
		this.mPatientInfoView.setPresenter(this);
		this.mCounties = countries;
		this.patientToUpdateId = patientToUpdateId;
		this.patientDataService = new PatientDataService();
		this.conceptDataService = new ConceptDataService();
		this.restApi = RestServiceBuilder.createService(RestApi.class);
	}

	public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView, PatientApi patientApi,
			Patient mPatient, String patientToUpdateId,
			List<String> mCounties, RestApi restApi) {
		this.mPatientInfoView = mPatientInfoView;
		this.patientDataService = new PatientDataService();
		this.conceptDataService = new ConceptDataService();
		this.mPatient = mPatient;
		this.patientToUpdateId = patientToUpdateId;
		this.mCounties = mCounties;
		this.restApi = restApi;
		this.mPatientInfoView.setPresenter(this);
	}

	private boolean validate(Patient patient) {

		boolean familyNameError = false;
		boolean lastNameError = false;
		boolean dateOfBirthError = false;
		boolean genderError = false;
		boolean countyError = false;
		boolean patientFileNumberError = false;
		boolean civilStatusError = false;
		boolean occupationError = false;
		boolean subCountyError = false;
		boolean nationalityError = false;
		boolean patientIdNoError = false;
		boolean clinicError = false;
		boolean wardError = false;
		boolean phonenumberError = false;
		boolean kinNameError = false;
		boolean kinRelationshipError = false;
		boolean kinPhonenumberError = false;
		boolean kinResidenceError = false;

		mPatientInfoView.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, genderError, subCountyError,
				countyError, patientFileNumberError, civilStatusError, occupationError, subCountyError, nationalityError,
				patientIdNoError, clinicError, wardError, phonenumberError, kinNameError, kinRelationshipError,
				kinPhonenumberError, kinResidenceError);

		// Validate names
		if (StringUtils.isBlank(patient.getPerson().getName().getGivenName())) {
			familyNameError = true;
		}
		if (StringUtils.isBlank(patient.getPerson().getName().getFamilyName())) {
			lastNameError = true;
		}

		// Validate date of birth
		if (StringUtils.isBlank(patient.getPerson().getBirthdate())) {
			dateOfBirthError = true;
		}

		// Validate address
		if (StringUtils.isBlank(patient.getPerson().getAddress().getAddress1())
				&& StringUtils.isBlank(patient.getPerson().getAddress().getAddress2())
				&& StringUtils.isBlank(patient.getPerson().getAddress().getCityVillage())
				&& StringUtils.isBlank(patient.getPerson().getAddress().getStateProvince())
				&& StringUtils.isBlank(patient.getPerson().getAddress().getCountry())
				&& StringUtils.isBlank(patient.getPerson().getAddress().getPostalCode())) {
			//addressError = true;
		}

		// Validate gender
		if (StringUtils.isBlank(patient.getPerson().getGender())) {
			genderError = true;
		}

		//Validate File Number
		if (StringUtils.isBlank(patient.getIdentifier().getIdentifier())) {
			patientFileNumberError = true;
		}

		boolean result =
				!familyNameError && !lastNameError && !dateOfBirthError && !subCountyError && !countyError && !genderError
						&& !patientFileNumberError && !occupationError && !civilStatusError && !subCountyError &&
						!nationalityError && !patientIdNoError && !clinicError && !wardError && !phonenumberError
						&& !kinNameError && !kinRelationshipError && !kinPhonenumberError  && !kinResidenceError;
		if (result) {
			mPatient = patient;
			return true;
		} else {
			mPatientInfoView.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, genderError,
					subCountyError, countyError, patientFileNumberError, civilStatusError, occupationError, subCountyError,
					nationalityError, patientIdNoError, clinicError, wardError, phonenumberError, kinNameError,
					kinRelationshipError, kinPhonenumberError, kinResidenceError);
			return false;
		}
	}

	@Override
	public void subscribe() {
		// This method is intentionally empty
	}

	@Override
	public Patient getPatientToUpdate() {
		Patient patientToUpdate = new PatientDAO().findPatientByID(patientToUpdateId);
		return patientToUpdate;
	}

	@Override
	public void confirmRegister(Patient patient) {
		if (!registeringPatient && validate(patient)) {
			mPatientInfoView.setProgressBarVisibility(true);
			mPatientInfoView.hideSoftKeys();
			registeringPatient = true;
			findSimilarPatients(patient);
		} else {
			mPatientInfoView.scrollToTop();
		}
	}

	@Override
	public void confirmUpdate(Patient patient) {
		if (!registeringPatient && validate(patient)) {
			mPatientInfoView.setProgressBarVisibility(true);
			mPatientInfoView.hideSoftKeys();
			registeringPatient = true;
			updatePatient(patient);
		} else {
			mPatientInfoView.scrollToTop();
		}
	}

	@Override
	public void finishAddPatientActivity() {
		mPatientInfoView.finishAddPatientActivity();
	}

	@Override
	public void registerPatient(Patient patient) {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetSingleCallback<Patient> getSingleCallback = new DataService.GetSingleCallback<Patient>() {

				@Override
				public void onCompleted(Patient entity) {
					//mPatientInfoView.startPatientDashboardActivity(mPatient);
					mPatientInfoView.finishAddPatientActivity();
				}

				@Override
				public void onError(Throwable t) {
					mPatientInfoView.setProgressBarVisibility(false);
				}
			};
			patientDataService.create(patient, getSingleCallback);
		}
	}

	@Override
	public void updatePatient(Patient patient) {
	/*	patientApi.updatePatient(patient, new DefaultResponseCallbackListener() {
			@Override
			public void onResponse() {
				mPatientInfoView.finishAddPatientActivity();
			}

			@Override
			public void onErrorResponse(String errorMessage) {
				registeringPatient = false;
				mPatientInfoView.setProgressBarVisibility(false);
			}
		});*/
	}

	public void findSimilarPatients(final Patient patient) {
		if (NetworkUtils.hasNetwork()) {
			PagingInfo pagingInfo = new PagingInfo(0, 20);
			DataService.GetMultipleCallback<Patient> getMultipleCallback = new DataService.GetMultipleCallback<Patient>() {
				@Override
				public void onCompleted(List<Patient> patients) {
					if (patients == null || patients.isEmpty()) {
						registerPatient(patient);
					} else {
						mPatientInfoView.showSimilarPatientDialog(patients, patient);
					}
				}
				@Override
				public void onError(Throwable t) {
					Log.e("User Error","Error",t.fillInStackTrace());
				}
			};
			patientDataService.getByName(patient.getPerson().getName().getNameString(), pagingInfo, getMultipleCallback);
		} else {
		   // get the users from the local storage.
		}
	}

	public void getCivilStatus() {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetSingleCallback<Concept> getSingleCallback = new DataService.GetSingleCallback<Concept>() {
				@Override
				public void onCompleted(Concept entity) {
					//mPatientInfoView.setCivilStatus(entity);
					System.out.print(entity);
				}

				@Override
				public void onError(Throwable t) {
					Log.e("Civil Status Error","Error",t.fillInStackTrace());
				}
			};
			conceptDataService.getByUUID(ApplicationConstants.CIVIL_STATUS_UUID, getSingleCallback);
		} else {
			// get the users from the local storage.
		}
	}


	@Override
	public boolean isRegisteringPatient() {
		return registeringPatient;
	}

}
