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

import android.widget.Spinner;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public interface AddEditPatientContract {

	interface View extends BaseView<Presenter> {

		void finishAddPatientActivity();

		void setErrorsVisibility(boolean givenNameError, boolean familyNameError, boolean dayOfBirthError,
				boolean addressError, boolean county_Error, boolean genderError, boolean patientFileNumberError,
				boolean civilStatusError, boolean occupationError, boolean subCounty_Error, boolean nationality_Error,
				boolean patientIdNo_Error, boolean clinic_Error, boolean ward_Error, boolean phonenumber_Error,
				boolean kinName_Error, boolean kinRelationship_Error, boolean kinPhonenumber_Error,
				boolean kinResidence_Error
		);

		void scrollToTop();

		void hideSoftKeys();

		void setProgressBarVisibility(boolean visibility);

		void showSimilarPatientDialog(List<Patient> patients, Patient newPatient);

		void startPatientDashboardActivity(Patient patient);

		void showUpgradeRegistrationModuleInfo();

		void setPatientIdentifierType(PatientIdentifierType patientIdentifierType);

		void showToast(String message, ToastUtil.ToastType toastType);

		void loadPersonAttributeTypes(List<PersonAttributeType> personAttributeTypeList);

		void updateConceptNamesView(Spinner conceptNamesDropdown, List<ConceptName> conceptNames);

	}

	interface Presenter extends BasePresenterContract {

		Patient getPatientToUpdate();

		boolean isRegisteringPatient();

		void confirmRegister(Patient patient);

		void confirmUpdate(Patient patient);

		void finishAddPatientActivity();

		void registerPatient(Patient patient);

		void updatePatient(Patient patient);

		void getConceptNames(String uuid, Spinner conceptAnswersDropdown);

		void getPatientIdentifierTypes();

		void getPersonAttributeTypes();

		<T> T searchPersonAttributeValueByType(PersonAttributeType personAttributeType);

		Patient getPatient();
	}

}
