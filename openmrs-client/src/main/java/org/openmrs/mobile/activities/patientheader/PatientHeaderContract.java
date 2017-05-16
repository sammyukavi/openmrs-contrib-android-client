package org.openmrs.mobile.activities.patientheader;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Patient;

public interface PatientHeaderContract {

	interface View extends BaseView<Presenter> {

		void updatePatientHeader(Patient patient);
	}

	interface Presenter extends BasePresenterContract {
		void getPatient();
	}
}
