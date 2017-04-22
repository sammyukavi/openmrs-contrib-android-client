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
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Module;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ModuleUtils;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.PatientComparator;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditPatientPresenter extends BasePresenter implements AddEditPatientContract.Presenter {

	private final AddEditPatientContract.View mPatientInfoView;

	private PatientApi patientApi;
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
		this.patientApi = new PatientApi();
		this.restApi = RestServiceBuilder.createService(RestApi.class);
	}

	public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView, PatientApi patientApi,
			Patient mPatient, String patientToUpdateId,
			List<String> mCounties, RestApi restApi) {
		this.mPatientInfoView = mPatientInfoView;
		this.patientApi = patientApi;
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
		boolean addressError = false;
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
		boolean encounterDateError = false;
		boolean encounterDepartmentError = false;
		boolean encounterProviderError = false;

		mPatientInfoView.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, genderError, addressError,
				countyError, patientFileNumberError, civilStatusError, occupationError, subCountyError, nationalityError,
				patientIdNoError,clinicError,wardError,phonenumberError,kinNameError,kinRelationshipError,
				kinPhonenumberError,encounterDateError,encounterDepartmentError,encounterProviderError,kinResidenceError);

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
		if (StringUtils.isBlank(patient.getPerson().getAddress().getSubCounty())
				&& StringUtils.isBlank(patient.getPerson().getAddress().getCounty())) {
			addressError = true;
		}

		if (!StringUtils.isBlank(patient.getPerson().getAddress().getCounty()) &&
				!mCounties.contains(patient.getPerson().getAddress().getCounty())) {
			countyError = true;
		}

		// Validate gender
		if (StringUtils.isBlank(patient.getPerson().getGender())) {
			genderError = true;
		}

		/*
		* Validate File Number
		* */
		if (StringUtils.isBlank(patient.getIdentifier().toString())) {
			patientFileNumberError = true;
		}

		boolean result =
				!familyNameError && !lastNameError && !dateOfBirthError && !addressError && !countyError && !genderError
						&& !patientFileNumberError && !occupationError && !civilStatusError;
		if (result) {
			mPatient = patient;
			return true;
		} else {
			mPatientInfoView.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, genderError, addressError,
					countyError, patientFileNumberError, civilStatusError, occupationError, subCountyError, nationalityError,
					patientIdNoError,clinicError,wardError,phonenumberError,kinNameError,kinRelationshipError,
					kinPhonenumberError,encounterDateError,encounterDepartmentError,encounterProviderError,kinResidenceError);
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
	public void finishPatientInfoActivity() {
		mPatientInfoView.finishPatientInfoActivity();
	}

	@Override
	public void registerPatient() {
		patientApi.registerPatient(mPatient, new DefaultResponseCallbackListener() {
			@Override
			public void onResponse() {
				mPatientInfoView.startPatientDashboardActivity(mPatient);
				mPatientInfoView.finishPatientInfoActivity();
			}

			@Override
			public void onErrorResponse(String errorMessage) {
				registeringPatient = false;
				mPatientInfoView.setProgressBarVisibility(false);
			}
		});
	}

	@Override
	public void updatePatient(Patient patient) {
		patientApi.updatePatient(patient, new DefaultResponseCallbackListener() {
			@Override
			public void onResponse() {
				mPatientInfoView.finishPatientInfoActivity();
			}

			@Override
			public void onErrorResponse(String errorMessage) {
				registeringPatient = false;
				mPatientInfoView.setProgressBarVisibility(false);
			}
		});
	}

	public void findSimilarPatients(final Patient patient) {
		if (NetworkUtils.isOnline()) {
			Call<Results<Module>> moduleCall = restApi.getModules(ApplicationConstants.API.FULL);
			moduleCall.enqueue(new Callback<Results<Module>>() {
				@Override
				public void onResponse(Call<Results<Module>> call, Response<Results<Module>> response) {
					if (response.isSuccessful()) {
						if (ModuleUtils.isRegistrationCore1_7orAbove(response.body().getResults())) {
							fetchSimilarPatientsFromServer(patient);
						} else {
							fetchSimilarPatientAndCalculateLocally(patient);
						}
					} else {
						fetchSimilarPatientAndCalculateLocally(patient);
					}
				}

				@Override
				public void onFailure(Call<Results<Module>> call, Throwable t) {
					registeringPatient = false;
					mPatientInfoView.setProgressBarVisibility(false);
					ToastUtil.error(t.getMessage());
				}
			});
		} else {
		   /* List<Patient> similarPatient = new PatientComparator().findSimilarPatient(new PatientDAO().getAllPatients()
		   .toBlocking().first(), patient);
            if(!similarPatient.isEmpty()){
                mPatientInfoView.showSimilarPatientDialog(similarPatient, patient);
            } else {
                registerPatient();
            }*/
		}
	}

	private void fetchSimilarPatientAndCalculateLocally(final Patient patient) {
		Call<Results<Patient>> call =
				restApi.getPatients(patient.getPerson().getName().getGivenName(), ApplicationConstants.API.FULL);
		call.enqueue(new Callback<Results<Patient>>() {
			@Override
			public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
				registeringPatient = false;
				if (response.isSuccessful()) {
					List<Patient> patientList = response.body().getResults();
					if (!patientList.isEmpty()) {
						List<Patient> similarPatient = new PatientComparator().findSimilarPatient(patientList, patient);
						if (!similarPatient.isEmpty()) {
							mPatientInfoView.showSimilarPatientDialog(similarPatient, patient);
							mPatientInfoView.showUpgradeRegistrationModuleInfo();
						} else {
							registerPatient();
						}
					} else {
						registerPatient();
					}
				} else {
					mPatientInfoView.setProgressBarVisibility(false);
					ToastUtil.error(response.message());
				}
			}

			@Override
			public void onFailure(Call<Results<Patient>> call, Throwable t) {
				registeringPatient = false;
				mPatientInfoView.setProgressBarVisibility(false);
				ToastUtil.error(t.getMessage());
			}
		});
	}

	private void fetchSimilarPatientsFromServer(final Patient patient) {
		Call<Results<Patient>> call = restApi.getSimilarPatients(patient.toMap());
		call.enqueue(new Callback<Results<Patient>>() {
			@Override
			public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
				registeringPatient = false;
				if (response.isSuccessful()) {
					List<Patient> similarPatients = response.body().getResults();
					if (!similarPatients.isEmpty()) {
						mPatientInfoView.showSimilarPatientDialog(similarPatients, patient);
					} else {
						registerPatient();
					}
				} else {
					mPatientInfoView.setProgressBarVisibility(false);
					ToastUtil.error(response.message());
				}
			}

			@Override
			public void onFailure(Call<Results<Patient>> call, Throwable t) {
				registeringPatient = false;
				mPatientInfoView.setProgressBarVisibility(false);
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public boolean isRegisteringPatient() {
		return registeringPatient;
	}

}
