package org.openmrs.mobile.activities.loginsync;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.patientlist.PatientListActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.event.SyncEvent;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.event.SyncPushEvent;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.TimeConstants;

public class LoginSyncPresenter extends BasePresenter implements LoginSyncContract.Presenter {

	private LoginSyncContract.View view;
	private OpenMRS openMRS;

	private double totalSyncPushes;
	private double totalSyncPulls;
	private double entitiesPushed;
	private double entitiesPulled;
	private String currentDownloadingSubscription;
	private double averageNetworkSpeed;
	private Timer networkConnectivityCheckTimer;
	private String networkDurationMessage = new String();
	private boolean arePushing = false;
	private boolean arePulling = false;

	private final int DELAY = 500;
	private final double SMOOTHING_FACTOR = 0.005;

	/**
	 * Conservative estimate of patient information (including context) based on tests with several patients and the
	 * size of all pertinent information
	 */
	private final double AVERAGE_SIZE_OF_PATIENT_PAYLOAD_IN_KB = 80;

	/**
	 * This could be calculated to give better estimates, but is picked ad hoc as of now
	 */
	private final double AVERAGE_NUMBER_OF_PATIENTS_TO_SYNC = 10;

	public LoginSyncPresenter(LoginSyncContract.View view, OpenMRS openMRS) {
		this.view = view;
		this.openMRS = openMRS;

		this.view.setPresenter(this);
		entitiesPulled = 0;
		entitiesPushed = 0;
	}

	public void sync() {
		openMRS.requestDataSync();
	}

	@Override
	public void subscribe() {
		//intentionally left blank
	}

	public void onSyncPushEvent(SyncPushEvent syncPushEvent) {
		arePushing = true;
		String pullDisplayInformation = new String();
		double progress = 0;

		// Handle getting total amount for push
		// Handle getting incremental amount for push
		// Handle getting push complete
		arePushing = false;

		view.updateSyncPushProgress(progress, pullDisplayInformation, networkDurationMessage);
	}

	public void onSyncPullEvent(SyncPullEvent syncPullEvent) {
		arePushing = true;
		String pullDisplayInformation = new String();
		double progress = 0;

		switch (syncPullEvent.message) {
			case ApplicationConstants.EventMessages.Sync.Pull.TOTAL_SUBSCRIPTIONS:
				totalSyncPushes = syncPullEvent.totalItems;
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_STARTING:
				pullDisplayInformation = String.format(getDisplayString(R.string.subscription_remote_pull_starting),
						syncPullEvent.entity);
				currentDownloadingSubscription = syncPullEvent.entity;
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_COMPLETE:
				entitiesPulled++;
				pullDisplayInformation = String.format(getDisplayString(R.string.subscription_remote_pull_complete),
						syncPullEvent.entity);
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING:
				pullDisplayInformation = String.format(getDisplayString(R.string.entity_remote_pull_starting),
						currentDownloadingSubscription, syncPullEvent.entity);
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE:
				pullDisplayInformation = String.format(getDisplayString(R.string.entity_remote_pull_complete),
						currentDownloadingSubscription, syncPullEvent.entity);
				break;
		}

		if (totalSyncPulls == entitiesPulled) {
			progress = 100;
			pullDisplayInformation = getDisplayString(R.string.download_complete);
			entitiesPulled = 0;
			arePulling = false;
		} else {
			progress = entitiesPulled / totalSyncPushes;
		}

		view.updateSyncPullProgress(progress, pullDisplayInformation, networkDurationMessage);

		if (progress == 100) {
			navigateToNextActivity();
		}
	}

	private void navigateToNextActivity() {
		Intent intent = new Intent(openMRS.getApplicationContext(), PatientListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		openMRS.getApplicationContext().startActivity(intent);
		view.finish();
	}

	public void onSyncEvent(SyncEvent syncEvent) {
		if (syncEvent.message.equals(ApplicationConstants.EventMessages.Sync.CANT_SYNC_NO_NETWORK)) {
			view.notify(getDisplayString(R.string.sync_off_for_now_because_network_not_available));
			navigateToNextActivity();
		}
	}

	public void startMeasuringConnectivity() {
		averageNetworkSpeed = 0;
		if (networkConnectivityCheckTimer == null) {
			networkConnectivityCheckTimer = new Timer();
		}
		networkConnectivityCheckTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				calculateNewAverageNetworkSpeed();
				String previousMessage = networkDurationMessage;
				updateDurationMessage();
				if (previousMessage.equals(networkDurationMessage)) {
					view.getParentActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateDurationDisplayText();
						}
					});
				}
			}
		}, DELAY, DELAY);
	}

	private void updateDurationMessage() {
		double estimatedTimeUntilDownloadCompletes = AVERAGE_NUMBER_OF_PATIENTS_TO_SYNC
				* AVERAGE_SIZE_OF_PATIENT_PAYLOAD_IN_KB
				/ averageNetworkSpeed;
		if (estimatedTimeUntilDownloadCompletes < TimeConstants.SECONDS_PER_MINUTE) {
			networkDurationMessage = getDisplayString(R.string.download_upload_speed_is_fast);
		} else {
			// If the network speed is fluctuating enough to have the message go from "fast" to "slow", stop the timer
			// and just keep the message as "slow" to be safe. I'm assuming people like to see things will speed up, not
			// that they're slowing down
			if (networkDurationMessage.equals(getDisplayString(R.string.download_upload_speed_is_fast))) {
				stopMeasuringConnectivity();
			}
			networkDurationMessage = getDisplayString(R.string.download_upload_speed_is_slow);
		}
	}

	private void updateDurationDisplayText() {
		if (arePushing) {
			view.updateSyncPushDuration(networkDurationMessage);
		}
		if (arePulling) {
			view.updateSyncPullDuration(networkDurationMessage);
		}
	}

	public void stopMeasuringConnectivity() {
		networkConnectivityCheckTimer.cancel();
	}

	private void calculateNewAverageNetworkSpeed() {
		Double networkSpeed = openMRS.getNetworkUtils().getCurrentConnectionSpeed();
		if (networkSpeed != null) {
			averageNetworkSpeed = SMOOTHING_FACTOR * networkSpeed + (1 - SMOOTHING_FACTOR) * averageNetworkSpeed;
		}
	}

	private String getDisplayString(int stringResourceId) {
		return view.getParentActivity().getString(stringResourceId);
	}
}
