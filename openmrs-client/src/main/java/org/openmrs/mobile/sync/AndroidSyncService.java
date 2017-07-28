package org.openmrs.mobile.sync;

import javax.inject.Inject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.dagger.SyncModule;

public class AndroidSyncService extends Service {
	@Inject
	SyncAdapter syncAdapter;

	public AndroidSyncService() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		DaggerSyncComponent.builder().syncModule(new SyncModule(this)).build().inject(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
}
