package org.openmrs.mobile.activities.loginsync;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.patientlist.PatientListActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.event.SyncPushEvent;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class LoginSyncPresenter extends BasePresenter implements LoginSyncContract.Presenter {

	private LoginSyncContract.View view;
	private OpenMRS openMRS;

	private int totalSyncPushes;
	private int totalSyncPulls;
	private List<String> entitiesPushed;
	private List<String> entitiesPulled;
	private String currentDownloadingSubscription;

	public LoginSyncPresenter(LoginSyncContract.View view, OpenMRS openMRS) {
		this.view = view;
		this.openMRS = openMRS;

		this.view.setPresenter(this);
		entitiesPulled = new ArrayList<>();
		entitiesPushed = new ArrayList<>();

		openMRS.requestDataSync();
	}

	@Override
	public void subscribe() {
		//intentionally left blank
	}

	public void onSyncPushEvent(SyncPushEvent syncPushEvent) {
		// Handle getting total amount for push
		// Handle getting incremental amount for push
		// Handle getting push complete

		view.updateSyncPushProgress(0, "Updating!", "This should take less than a minute...");
	}

	public void onSyncPullEvent(SyncPullEvent syncPullEvent) {
		String pullDisplayInformation = new String();
		String pullDurationInformation = null;
		float progress = 0;

		if (syncPullEvent.message.equals(ApplicationConstants.EventMessages.Sync.Pull.TOTAL_SUBSCRIPTIONS)) {
			totalSyncPushes = syncPullEvent.totalItems;
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_STARTING)) {
			pullDisplayInformation = "Downloading " + syncPullEvent.entity + "...";
			currentDownloadingSubscription = syncPullEvent.entity;
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_COMPLETE)) {
			entitiesPulled.add(syncPullEvent.entity);
			pullDisplayInformation = "Downloading " + syncPullEvent.entity + " complete!";
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING)) {
			pullDisplayInformation = "Downloading " + currentDownloadingSubscription + " - " + syncPullEvent.entity + "...";
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE)) {
			pullDisplayInformation = "Downloading " + currentDownloadingSubscription + " - " + syncPullEvent.entity
					+ " complete!";
		}

		if (totalSyncPulls == entitiesPulled.size()) {
			progress = 100;
			pullDisplayInformation = "Download complete!";
			entitiesPulled = new ArrayList<>();
		} else {
			progress = (float) entitiesPulled.size() / (float) totalSyncPushes;
			pullDurationInformation = "This should take less than a minute...";
		}

		view.updateSyncPullProgress(progress, pullDisplayInformation, pullDurationInformation);

		if (progress == 100) {
			Intent intent = new Intent(openMRS.getApplicationContext(), PatientListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			openMRS.getApplicationContext().startActivity(intent);
			view.finish();
		}
	}
}
