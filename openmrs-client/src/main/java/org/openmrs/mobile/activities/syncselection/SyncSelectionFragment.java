package org.openmrs.mobile.activities.syncselection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;

public class SyncSelectionFragment extends ACBaseFragment<SyncSelectionContract.Presenter>
		implements SyncSelectionContract.View {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_sync_selection, container, false);

		return root;
	}

	public static SyncSelectionFragment newInstance() {
		return new SyncSelectionFragment();
	}
}
