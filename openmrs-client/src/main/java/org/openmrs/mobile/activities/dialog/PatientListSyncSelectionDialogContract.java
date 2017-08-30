package org.openmrs.mobile.activities.dialog;

import java.util.List;

import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.PatientList;

public interface PatientListSyncSelectionDialogContract {

	interface View extends BaseView<Presenter> {

		void dismissDialog();

		void displayPatientLists(List<PatientList> patientLists, List<PatientList> syncingPatientLists);
	}

	interface Presenter {

		void saveUsersSyncSelections();

		void toggleSyncSelection(PatientList patientList, boolean isSelected);

		void dialogCreated();
	}
}
