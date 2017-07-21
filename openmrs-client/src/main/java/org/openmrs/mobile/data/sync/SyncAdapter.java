package org.openmrs.mobile.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.SyncLog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.DaggerService;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject SyncService mSyncService;
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
	    DaggerSyncComponent.create().inject(this);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSync) {
        super(context, autoInitialize, allowParallelSync);
	    DaggerSyncComponent.create().inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
	    // Do syncing here
	    Log.i("SyncAdapter", "perform sync");
    }
}
