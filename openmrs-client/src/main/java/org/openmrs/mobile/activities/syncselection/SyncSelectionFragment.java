package org.openmrs.mobile.activities.syncselection;

import org.openmrs.mobile.activities.ACBaseFragment;

public class SyncSelectionFragment extends ACBaseFragment<SyncSelectionContract.Presenter>
		implements SyncSelectionContract.View {


	public static SyncSelectionFragment newInstance() {
		return new SyncSelectionFragment();
	}
}
