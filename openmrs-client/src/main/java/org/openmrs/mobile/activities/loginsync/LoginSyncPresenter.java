package org.openmrs.mobile.activities.loginsync;

import java.util.Timer;
import java.util.TimerTask;

import org.openmrs.mobile.activities.BasePresenter;
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
	private boolean networkConnectionIsFast = true;
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

//		view.updateSyncPushProgress(progress, pullDisplayInformation);
	}

	public void onSyncPullEvent(SyncPullEvent syncPullEvent) {
		arePushing = true;
		double progress;

		// First check for information to determine of progress should be updated
		switch (syncPullEvent.getMessage()) {
			case ApplicationConstants.EventMessages.Sync.Pull.TOTAL_SUBSCRIPTIONS:
				totalSyncPushes = syncPullEvent.getTotalItems();
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_COMPLETE:
				entitiesPulled++;
				break;
		}

		if (totalSyncPulls == entitiesPulled) {
			progress = 100;
			entitiesPulled = 0;
			arePulling = false;
		} else {
			progress = entitiesPulled / totalSyncPushes;
		}

		switch (syncPullEvent.getMessage()) {
			case ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_STARTING:
				view.updateSyncPullProgressForStartingSubscription(progress, syncPullEvent.getEntity());
				currentDownloadingSubscription = syncPullEvent.getEntity();
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_COMPLETE:
				view.updateSyncPullProgressForCompletingSubscription(progress, syncPullEvent.getEntity());
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING:
				view.updateSyncPullProgressForStartingEntity(progress, currentDownloadingSubscription,
						syncPullEvent.getEntity());
				break;
			case ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE:
				view.updateSyncPullProgressForCompletingEntity(progress, currentDownloadingSubscription,
						syncPullEvent.getEntity());
				break;
		}

		if (progress == 100) {
			view.notifySyncPullComplete();
			navigateToNextActivity();
		}
	}

	private void navigateToNextActivity() {
		view.navigateToNextActivity();
	}

	public void onSyncEvent(SyncEvent syncEvent) {
		if (syncEvent.getMessage().equals(ApplicationConstants.EventMessages.Sync.CANT_SYNC_NO_NETWORK)) {
			view.notifyConnectionLost();
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
				boolean previousConnectionSpeedIsFast = networkConnectionIsFast;
				determineNetworkConnectionSpeed();
				if ((previousConnectionSpeedIsFast && !networkConnectionIsFast) || (!previousConnectionSpeedIsFast &&
						networkConnectionIsFast)) {
					notifyViewToUpdateConnectionDisplay();
				}
			}
		}, DELAY, DELAY);
	}

	private void determineNetworkConnectionSpeed() {
		double estimatedTimeUntilDownloadCompletes = AVERAGE_NUMBER_OF_PATIENTS_TO_SYNC
				* AVERAGE_SIZE_OF_PATIENT_PAYLOAD_IN_KB
				/ averageNetworkSpeed;
		if (estimatedTimeUntilDownloadCompletes < TimeConstants.SECONDS_PER_MINUTE) {
			networkConnectionIsFast = true;
		} else {
			// If the network speed is fluctuating enough to have the message go from "fast" to "slow", stop the timer
			// and just keep the message as "slow" to be safe. I'm assuming people like to see things will speed up, not
			// that they're slowing down
			if (networkConnectionIsFast) {
				stopMeasuringConnectivity();
			}
			networkConnectionIsFast = false;
		}
	}

	private void notifyViewToUpdateConnectionDisplay() {
		if (arePushing) {
			if (networkConnectionIsFast) {
				view.notifySyncPushConnectionIsFast();
			} else {
				view.notifySyncPushConnectionIsSlow();
			}
		}
		if (arePulling) {
			if (networkConnectionIsFast) {
				view.notifySyncPullConnectionIsFast();
			} else {
				view.notifySyncPullConnectionIsSlow();
			}
		}
	}

	public void stopMeasuringConnectivity() {
		networkConnectivityCheckTimer.cancel();
	}

	private void calculateNewAverageNetworkSpeed() {
		Double networkSpeed = openMRS.getNetworkUtils().getCurrentConnectionSpeed();
		if (networkSpeed != null && networkSpeed != openMRS.getNetworkUtils().UNKNOWN_CONNECTION_SPEED) {
			averageNetworkSpeed = SMOOTHING_FACTOR * networkSpeed + (1 - SMOOTHING_FACTOR) * averageNetworkSpeed;
		}
	}
}
