package org.openmrs.mobile.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.SyncLog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class SyncService extends Service {
	private static final Object SYNC_LOCK = new Object();
	// Storage for an instance of the sync adapter
	private static SyncAdapter sSyncAdapter = null;

	@Override
	public void onCreate() {
		synchronized (SYNC_LOCK) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}
}

