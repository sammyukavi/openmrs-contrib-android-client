package org.openmrs.mobile.activities.syncselection;

import java.util.List;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.PatientList;

public interface SyncSelectionContract {

	interface View extends BaseView<Presenter> {

		void toggleScreenProgressBar(boolean makeVisible);

		void toggleSyncSelection(PatientList patientList, boolean isSelected);

		void displayPatientLists(List<PatientList> entities);

		void updateAdvanceButton(boolean isAtLeastOnePatientListSelected);
	}

	interface Presenter extends BasePresenterContract {

		void toggleSyncSelection(PatientList patientList, boolean isSelected);
	}
}
