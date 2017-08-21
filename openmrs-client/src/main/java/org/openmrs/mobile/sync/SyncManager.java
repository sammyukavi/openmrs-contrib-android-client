package org.openmrs.mobile.sync;

import javax.inject.Inject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.receivers.SyncReceiver;
import org.openmrs.mobile.utilities.TimeConstants;

public class SyncManager {
	// Sync interval constants
	private final long SYNC_INTERVAL_IN_MINUTES = 5L;
	private final long SYNC_INTERVAL_IN_MILLISECONDS = SYNC_INTERVAL_IN_MINUTES * TimeConstants.MILLIS_PER_MINUTE;

	private static final String ALARM_SYNC_INTENT = "alarmSyncIntent";

	private SyncService syncService;
	private static OpenMRS openMRS;
	private SyncReceiver syncReceiver;

	@Inject
	public SyncManager(OpenMRS openMRS, SyncReceiver syncReceiver, SyncService syncService) {
		this.openMRS = openMRS;
		this.syncReceiver = syncReceiver;
		this.syncService = syncService;
	}

	private void registerReceivers() {
		// Register connectivity receiver
		IntentFilter syncFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		syncFilter.addAction(ALARM_SYNC_INTENT);
		openMRS.registerReceiver(syncReceiver, syncFilter);
	}

	public void initializeDataSync() {
		createAlarmForSyncing();
		registerReceivers();
	}

	private void createAlarmForSyncing() {
		Intent startIntent = new Intent(ALARM_SYNC_INTENT);
		int dummyRequestCode = 0;
		int dummyFlag = 0;
		PendingIntent appIntent = PendingIntent.getBroadcast(openMRS, dummyRequestCode, startIntent, dummyFlag);
		AlarmManager alarmManager = (AlarmManager) openMRS.getSystemService(openMRS.ALARM_SERVICE);
		int initialIntervalInMillis = 0;
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, initialIntervalInMillis,
				SYNC_INTERVAL_IN_MILLISECONDS, appIntent);
	}

	public void requestSync() {
		if (openMRS.getAuthorizationManager().isUserLoggedIn()) {
			syncService.sync();
		}
	}
}
