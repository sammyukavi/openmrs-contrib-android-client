package org.openmrs.mobile.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AndroidSyncService extends Service {
	private static final Object SYNC_LOCK = new Object();
	// Storage for an instance of the sync adapter
	private static SyncAdapter sSyncAdapter = null;

	public AndroidSyncService() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
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
