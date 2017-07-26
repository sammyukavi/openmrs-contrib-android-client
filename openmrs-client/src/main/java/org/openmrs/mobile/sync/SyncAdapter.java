package org.openmrs.mobile.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import org.openmrs.mobile.data.sync.SyncService;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	// Injected via constructor injection
    private SyncService syncService;

    public SyncAdapter(Context context, boolean autoInitialize, SyncService syncService) {
        super(context, autoInitialize);
	    this.syncService = syncService;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSync, SyncService syncService) {
        super(context, autoInitialize, allowParallelSync);
	    this.syncService = syncService;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
	    syncService.sync();
    }
}
