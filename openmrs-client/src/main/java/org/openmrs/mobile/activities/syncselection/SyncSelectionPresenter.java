package org.openmrs.mobile.activities.syncselection;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.PatientListDataService;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.utilities.StringUtils;

public class SyncSelectionPresenter extends BasePresenter implements SyncSelectionContract.Presenter {

	private SyncSelectionContract.View view;
	private static OpenMRS openMRS;

	private PatientListDataService patientListDataService;

	private List<PatientList> patientLists;
	private List<PatientList> selectedPatientListsToSync;

	public SyncSelectionPresenter(SyncSelectionContract.View view, OpenMRS openMRS) {
		this.view = view;
		this.openMRS = openMRS;

		selectedPatientListsToSync = new ArrayList<>(); // temp

		this.view.setPresenter(this);
		this.patientListDataService = dataAccess().patientList();
	}

	@Override
	public void subscribe() {
		// get all patient lists
		if (patientLists == null) {
			getPatientLists();
		}
	}

	private void getPatientLists() {
		view.toggleScreenProgressBar(true);
		patientListDataService.getAll(null, new PagingInfo(1, 100),
				new DataService.GetCallback<List<PatientList>>() {
					@Override
					public void onCompleted(List<PatientList> entities) {
						view.toggleScreenProgressBar(false);
						if (entities != null) {
							patientLists = entities;
							view.displayPatientLists(entities);
						}
					}

					@Override
					public void onError(Throwable t) {
						view.toggleScreenProgressBar(false);
						// TODO: We can't display the list, so we should probably move on to the next screen
					}
				});
	}

	public void toggleSyncSelection(PatientList patientList, boolean isSelected) {
		if (isSelected) {
			selectedPatientListsToSync.add(patientList);
		} else {
			selectedPatientListsToSync.remove(patientList);
		}
		updateAdvanceButtonText();
	}

	@Override
	public void saveUsersSyncSelections() {
		
	}

	private void updateAdvanceButtonText() {
		boolean isAtLeastOnePatientListSelected = selectedPatientListsToSync.size() > 0;
		view.updateAdvanceButton(isAtLeastOnePatientListSelected);
	}
}
