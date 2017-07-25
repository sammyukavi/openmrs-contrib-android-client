package org.openmrs.mobile.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.data.sync.SyncService;

import javax.inject.Inject;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	// Injected via constructor injection
    private SyncService mSyncService;

    public SyncAdapter(Context context, boolean autoInitialize, SyncService syncService) {
        super(context, autoInitialize);
	    this.mSyncService = syncService;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSync, SyncService syncService) {
        super(context, autoInitialize, allowParallelSync);
	    this.mSyncService = syncService;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
	    mSyncService.sync();
    }
}
