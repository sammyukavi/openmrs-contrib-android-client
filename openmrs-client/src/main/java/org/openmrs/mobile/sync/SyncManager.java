package org.openmrs.mobile.sync;

import javax.inject.Inject;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.models.PatientListContext;
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
	private PatientListContextDbService patientListContextDbService;

	private boolean canSync = false;

	@Inject
	public SyncManager(OpenMRS openMRS, SyncReceiver syncReceiver, SyncService syncService,
			PatientListContextDbService patientListContextDbService) {
		this.openMRS = openMRS;
		this.syncReceiver = syncReceiver;
		this.syncService = syncService;
		this.patientListContextDbService = patientListContextDbService;
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
		long initialTriggerTimeInMillis = AlarmManager.ELAPSED_REALTIME + SYNC_INTERVAL_IN_MILLISECONDS;
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, initialTriggerTimeInMillis,
				SYNC_INTERVAL_IN_MILLISECONDS, appIntent);
	}

	public void requestSync() {
		if (openMRS.getAuthorizationManager().isUserLoggedIn()) {
			if (canSync) {
				new Thread(() -> {
					try {
						syncService.sync();
					}
					catch (Exception ex) {
						openMRS.getOpenMRSLogger().e("Error running the sync service", ex);
					}
				}).start();
			}
		} else {
			canSync = false;
		}
	}

	public void requestInitialSync() {
		canSync = true;
		requestSync();
	}

	public void deleteUnsyncedPatientListData(String patientListUuid) {
		List<PatientListContext> patientListContextsToDelete = patientListContextDbService.getListPatients(patientListUuid,
				null, null);
		for (PatientListContext patientListContextToDelete : patientListContextsToDelete) {
			patientListContextDbService.delete(patientListContextToDelete);
		}
	}
}
