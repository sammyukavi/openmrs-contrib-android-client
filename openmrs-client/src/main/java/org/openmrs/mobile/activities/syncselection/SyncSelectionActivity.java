package org.openmrs.mobile.activities.syncselection;

import android.os.Bundle;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class SyncSelectionActivity extends ACBaseActivity {

	public SyncSelectionContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Create fragment
		SyncSelectionFragment syncSelectionFragment =
				(SyncSelectionFragment) getSupportFragmentManager().findFragmentById(R.id.syncSelectionContentFrame);
		if (syncSelectionFragment == null) {
			syncSelectionFragment = SyncSelectionFragment.newInstance();
		}
		if (!syncSelectionFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(),
					syncSelectionFragment, R.id.syncSelectionContentFrame);
		}

		presenter = new SyncSelectionPresenter(syncSelectionFragment, openMRS);
	}
}
