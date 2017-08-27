package org.openmrs.mobile.activities.syncselection;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;

public class SyncSelectionPresenter extends BasePresenter implements SyncSelectionContract.Presenter {

	private SyncSelectionContract.View view;
	private static OpenMRS openMRS;

	public SyncSelectionPresenter(SyncSelectionContract.View view, OpenMRS openMRS) {
		this.view = view;
		this.openMRS = openMRS;

		this.view.setPresenter(this);
	}

	@Override
	public void subscribe() {
		// Intentionally left blank
	}
}
