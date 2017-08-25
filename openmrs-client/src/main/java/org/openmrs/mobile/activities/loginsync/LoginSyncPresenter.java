package org.openmrs.mobile.activities.loginsync;

import android.content.Intent;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.patientlist.PatientListActivity;
import org.openmrs.mobile.application.OpenMRS;

public class LoginSyncPresenter extends BasePresenter implements LoginSyncContract.Presenter {

	private LoginSyncContract.View view;
	private OpenMRS openMRS;

	public LoginSyncPresenter(LoginSyncContract.View view, OpenMRS openMRS) {
		this.view = view;
		this.openMRS = openMRS;

		this.view.setPresenter(this);
	}

	@Override
	public void subscribe() {
		//intentionally left blank
	}

	public void onSyncPushEvent() {
		// Handle getting total amount for push
		// Handle getting incremental amount for push
		// Handle getting push complete

		view.updateSyncPushProgress(0, "Updating!", "This should take less than a minute...");
	}

	public void onSyncPullEvent() {
		// Handle getting total amount for pull
		// Handle getting incremental amount for pull
		// Handle getting pull complete

		int progress = 0;
		view.updateSyncPullProgress(progress, "Updating!", "This should take less than a minute...");

		if (progress == 100) {
			Intent intent = new Intent(openMRS.getApplicationContext(), PatientListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			openMRS.getApplicationContext().startActivity(intent);
			view.finish();
		}
	}
}
