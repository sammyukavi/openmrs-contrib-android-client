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
import android.widget.Spinner;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.ConceptNameDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.PatientIdentifierTypeDataService;
import org.openmrs.mobile.data.impl.PersonAttributeTypeDataService;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AddEditPatientPresenter extends BasePresenter implements AddEditPatientContract.Presenter {

	private final AddEditPatientContract.View patientRegistrationView;

	private PatientDataService patientDataService;
	private ConceptDataService conceptDataService;
	private PersonAttributeTypeDataService personAttributeTypeDataService;
	private PatientIdentifierTypeDataService patientIdentifierTypeDataService;
	private ConceptNameDataService conceptNameDataService;
	private LocationDataService locationDataService;
	private RestApi restApi;
	private Patient patient;
	private String patientToUpdateId;
	private List<String> mCounties;
	private boolean registeringPatient = false;
	private OpenMRS instance = OpenMRS.getInstance();
	private String locationUuid;

	private int page = 0;
	private int limit = 10;

	public AddEditPatientPresenter(AddEditPatientContract.View patientRegistrationView,
			List<String> countries,
			String patientToUpdateId) {
		this.patientRegistrationView = patientRegistrationView;
		this.patientRegistrationView.setPresenter(this);
		this.mCounties = countries;
		this.patientToUpdateId = patientToUpdateId;
		this.patientDataService = new PatientDataService();
		this.conceptDataService = new ConceptDataService();
		this.patientIdentifierTypeDataService = new PatientIdentifierTypeDataService();
		this.personAttributeTypeDataService = new PersonAttributeTypeDataService();
		this.conceptNameDataService = new ConceptNameDataService();
		this.restApi = RestServiceBuilder.createService(RestApi.class);
		this.locationDataService = new LocationDataService();
	}

	public AddEditPatientPresenter(AddEditPatientContract.View patientRegistrationView, PatientApi patientApi,
			Patient mPatient, String patientToUpdateId,
			List<String> mCounties, RestApi restApi) {
		this.patientRegistrationView = patientRegistrationView;
		this.patientDataService = new PatientDataService();
		this.conceptDataService = new ConceptDataService();
		this.patient = mPatient;
		this.patientToUpdateId = patientToUpdateId;
		this.mCounties = mCounties;
		this.restApi = restApi;
		this.patientRegistrationView.setPresenter(this);
		this.patientIdentifierTypeDataService = new PatientIdentifierTypeDataService();
		this.personAttributeTypeDataService = new PersonAttributeTypeDataService();
		this.conceptNameDataService = new ConceptNameDataService();
		this.locationDataService = new LocationDataService();
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

		patientRegistrationView
				.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, countyError, genderError,
						patientFileNumberError, civilStatusError, occupationError, subCountyError, nationalityError,
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

		// Validate gender
		if (StringUtils.isBlank(patient.getPerson().getGender())) {
			genderError = true;
		}

		//Validate File Number
		if (StringUtils.isBlank(patient.getIdentifier().getIdentifier())) {
			patientFileNumberError = true;
		}

		boolean result =
				!familyNameError && !lastNameError && !dateOfBirthError && !countyError && !genderError
						&& !patientFileNumberError && !civilStatusError && !occupationError && !subCountyError &&
						!nationalityError && !patientIdNoError && !clinicError && !wardError && !phonenumberError
						&& !kinNameError && !kinRelationshipError && !kinPhonenumberError && !kinResidenceError;
		if (result) {
			this.patient = patient;
			return true;
		} else {
			patientRegistrationView
					.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, countyError, genderError,
							patientFileNumberError, civilStatusError, occupationError, subCountyError, nationalityError,
							patientIdNoError, clinicError, wardError, phonenumberError, kinNameError, kinRelationshipError,
							kinPhonenumberError, kinResidenceError);
			return false;
		}
	}

	@Override
	public void subscribe() {
		getPatientIdentifierTypes();
		getPersonAttributeTypes();
		getLoginLocation();
	}

	@Override
	public Patient getPatientToUpdate() {
		Patient patientToUpdate = new PatientDAO().findPatientByID(patientToUpdateId);
		return patientToUpdate;
	}

	@Override
	public void confirmRegister(Patient patient) {
		if (!registeringPatient && validate(patient)) {
			patientRegistrationView.setProgressBarVisibility(true);
			patientRegistrationView.hideSoftKeys();
			registeringPatient = true;
			findSimilarPatients(patient);
		} else {
			patientRegistrationView.scrollToTop();
		}
	}

	@Override
	public void confirmUpdate(Patient patient) {
		if (!registeringPatient && validate(patient)) {
			patientRegistrationView.setProgressBarVisibility(true);
			patientRegistrationView.hideSoftKeys();
			registeringPatient = true;
			updatePatient(patient);
		} else {
			patientRegistrationView.scrollToTop();
		}
	}

	@Override
	public void finishAddPatientActivity() {
		patientRegistrationView.finishAddPatientActivity();
	}

	@Override
	public void registerPatient(Patient patient) {
		setRegistering(true);
		if (NetworkUtils.hasNetwork()) {
			DataService.GetSingleCallback<Patient> getSingleCallback = new DataService.GetSingleCallback<Patient>() {
				@Override
				public void onCompleted(Patient entity) {
					setRegistering(false);
					if (entity != null) {
						patientRegistrationView
								.showToast(ApplicationConstants.entityName.PATIENTS
										+ ApplicationConstants.toastMessages.addSuccessMessage, ToastUtil.ToastType
										.SUCCESS);
						patientRegistrationView.startPatientDashboardActivity(entity);
						//patientRegistrationView.finishAddPatientActivity();
						updatePatient(entity);
					} else {
						patientRegistrationView
								.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants.toastMessages
										.addWarningMessage, ToastUtil.ToastType.WARNING);
					}
				}

				@Override
				public void onError(Throwable t) {
					setRegistering(false);
					patientRegistrationView.setProgressBarVisibility(false);
					patientRegistrationView
							.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants.toastMessages
									.addErrorMessage, ToastUtil.ToastType.ERROR);
				}
			};
			patientDataService.create(patient, getSingleCallback);
		}
	}

	@Override
	public void updatePatient(Patient patient) {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetSingleCallback<Patient> getSingleCallback = new DataService.GetSingleCallback<Patient>() {
				@Override
				public void onCompleted(Patient entity) {
					setRegistering(false);
					if (entity != null) {
					} else {
						patientRegistrationView
								.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants.toastMessages
										.addWarningMessage, ToastUtil.ToastType.WARNING);
					}
				}

				@Override
				public void onError(Throwable t) {
					setRegistering(false);
					patientRegistrationView.setProgressBarVisibility(false);
					patientRegistrationView
							.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants.toastMessages
									.addErrorMessage, ToastUtil.ToastType.ERROR);
				}
			};
			patientDataService.update(patient, getSingleCallback);
		}
	}

	public void findSimilarPatients(Patient patient) {
		if (NetworkUtils.hasNetwork()) {
			PagingInfo pagingInfo = new PagingInfo(page, limit);
			DataService.GetMultipleCallback<Patient> getMultipleCallback = new DataService.GetMultipleCallback<Patient>() {
				@Override
				public void onCompleted(List<Patient> patients, int length) {
					if (patients.isEmpty()) {
						registerPatient(patient);
					} else {
						patientRegistrationView.showSimilarPatientDialog(patients, patient);
					}
				}

				@Override
				public void onError(Throwable t) {
					Log.e("User Error", "Error", t.fillInStackTrace());
					patientRegistrationView.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants
							.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
				}
			};
			//Just check if the identifier are the same. If not it saves the patient.
			patientDataService.getByNameAndIdentifier(patient.getIdentifier().getIdentifier(), pagingInfo,
					getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	public void getConceptNames(String uuid, Spinner conceptAnswersDropdown) {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetMultipleCallback<ConceptName> getMultipleCallback =
					new DataService.GetMultipleCallback<ConceptName>() {

						@Override
						public void onCompleted(List<ConceptName> entities, int length) {
							patientRegistrationView.updateConceptNamesView(conceptAnswersDropdown, entities);
						}

						@Override
						public void onError(Throwable t) {
							Log.e("Concept Answers Error", "Error", t.fillInStackTrace());
							patientRegistrationView
									.showToast(ApplicationConstants.entityName.CIVIL_STATUS + ApplicationConstants
											.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
						}
					};
			conceptNameDataService.getByConceptUuid(uuid, getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	public void getPatientIdentifierTypes() {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetMultipleCallback<PatientIdentifierType> getMultipleCallback =
					new DataService.GetMultipleCallback<PatientIdentifierType>() {
						@Override
						public void onCompleted(List<PatientIdentifierType> entities, int length) {
							if (!entities.isEmpty()) {
								for (int i = 0; i < entities.size(); i++) {
									if (entities.get(i).getRequired()) {
										patientRegistrationView.setPatientIdentifierType(entities.get(i));
									}
								}
							}
						}

						@Override
						public void onError(Throwable t) {
							Log.e("Identifier Type Error", "Error", t.fillInStackTrace());
							patientRegistrationView
									.showToast(ApplicationConstants.entityName.IDENTIFIER_TPYES
											+ ApplicationConstants.toastMessages
											.fetchErrorMessage, ToastUtil.ToastType.ERROR);
						}
					};
			patientIdentifierTypeDataService.getAll(false, null, getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	public void getPersonAttributeTypes() {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetMultipleCallback<PersonAttributeType> getMultipleCallback =
					new DataService.GetMultipleCallback<PersonAttributeType>() {

						@Override
						public void onCompleted(List<PersonAttributeType> personAttributeTypes, int length) {
							if (!personAttributeTypes.isEmpty()) {

								for (int q = 0; q < createUnwantedPersonAttributes().size(); q++) {
									String unwantedUuid = createUnwantedPersonAttributes().get(q);

									for (int i = 0; i < personAttributeTypes.size(); i++) {
										String uuid = personAttributeTypes.get(i).getUuid();

										if (uuid.equalsIgnoreCase(unwantedUuid)) {
											personAttributeTypes.remove(i);
										}
									}
								}
								patientRegistrationView.loadPersonAttributeTypes(personAttributeTypes);
							} else {
								patientRegistrationView
										.showToast(ApplicationConstants.entityName.ATTRIBUTE_TPYES + ApplicationConstants
												.toastMessages.fetchWarningMessage, ToastUtil.ToastType.WARNING);
							}
						}

						@Override
						public void onError(Throwable t) {
							Log.e("Attribute Type Error", "Error", t.fillInStackTrace());
							patientRegistrationView
									.showToast(ApplicationConstants.entityName.ATTRIBUTE_TPYES + ApplicationConstants
											.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
						}
					};
			personAttributeTypeDataService.getAll(false, null, getMultipleCallback);
		}
	}

	@Override
	public <T> T searchPersonAttributeValueByType(PersonAttributeType personAttributeType) {
		if (null != getPatient() && null != getPatient().getPerson().getAttributes()) {
			for (PersonAttribute personAttribute : getPatient().getPerson().getAttributes()) {
				if (personAttribute.getUuid().equalsIgnoreCase(personAttributeType.getUuid())) {
					return (T)personAttribute.getValue();
				}
			}
		}
		return null;
	}

	@Override
	public void getLoginLocation() {
		if (NetworkUtils.hasNetwork()) {
			if (!instance.getLocation().equalsIgnoreCase(null)) {
				locationUuid = instance.getLocation();
			}
			DataService.GetSingleCallback<Location> getSingleCallback =
					new DataService.GetSingleCallback<Location>() {
						@Override
						public void onCompleted(Location entity) {
							if (entity != null) {
								patientRegistrationView.setLoginLocation(entity);
							}
						}

						@Override
						public void onError(Throwable t) {
							Log.e("LocationError", "Error", t.fillInStackTrace());
							patientRegistrationView
									.showToast(ApplicationConstants.entityName.LOCATION + ApplicationConstants
											.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
						}
					};
			locationDataService.getByUUID(locationUuid,getSingleCallback);
		}
	}

	@Override
	public Patient getPatient() {
		return patient;
	}

	@Override
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public boolean isRegisteringPatient() {
		return registeringPatient;
	}

	@Override
	public void setRegistering(boolean registering) {
		this.registeringPatient = registering;
	}

	public ArrayList<String> createUnwantedPersonAttributes() {
		ArrayList<String> unwantedPersonAttributes = new ArrayList<>();
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.TEST_PATIENT_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.UNKNOWN_PATIENT_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.RACE_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.HEALTH_CENTER_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.HEALTH_DISTRICT_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.MOTHER_NAME_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.BIRTH_PLACE_UUID);

		return unwantedPersonAttributes;
	}

}
