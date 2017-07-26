package org.openmrs.mobile.sync;

import javax.inject.Inject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dagger.ReceiverComponent;

public class SyncManager {
	// Sync constants
	// The authority for the sync adapter's content provider
	public static final String SYNC_ADAPTER_AUTHORITY = "org.openmrs.mobile.provider";
	// An account type, in the form of a domain name
	public static final String SYNC_ADAPTER_ACCOUNT_TYPE = "org.openmrs.mobile.datasync";
	// The account name (this isn't used by the app, but is needed for syncing)
	public static final String SYNC_ADAPTER_ACCOUNT = "syncAdapterAccount";
	// Sync interval constants
	public static final long SECONDS_PER_MINUTE = 60L;
	public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
	public static final long SYNC_INTERVAL_IN_SECONDS = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

	private Account account;
	private static OpenMRS openMRS;
	private ReceiverComponent receiverComponent;

	@Inject
	public SyncManager(OpenMRS openMRS, ReceiverComponent receiverComponent) {
		this.openMRS = openMRS;
		this.receiverComponent = receiverComponent;
	}

	public void registerReceivers() {
		// Register connectivity receiver
		BroadcastReceiver connectivityReceiver = receiverComponent.connectivityReceiver();
		IntentFilter connectivityFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		openMRS.registerReceiver(connectivityReceiver, connectivityFilter);
	}

	public void initializeDataSync() {
		// Create the dummy account
		account = createSyncAccount(openMRS);
		// Turn on periodic syncing
		ContentResolver.setSyncAutomatically(account, SYNC_ADAPTER_AUTHORITY, true);
		ContentResolver.addPeriodicSync(account, SYNC_ADAPTER_AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL_IN_SECONDS);
	}

	/**
	 * Create a new dummy account for the sync adapter
	 *
	 * @param context The application context
	 */
	private Account createSyncAccount(Context context) {
		if (account != null) {
			return account;
		}
		// Create the account type and default account
		Account newAccount = new Account(SYNC_ADAPTER_ACCOUNT, SYNC_ADAPTER_ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) openMRS.getSystemService(openMRS.ACCOUNT_SERVICE);

		// Below needed for syncing, but is for dummy account
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, SYNC_ADAPTER_AUTHORITY, 1)
             * here.
             */
			// Intentionally left blank
		} else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
			// Intentionally left blank
		}
		return newAccount;
	}

	public void requestSync() {
		ContentResolver.requestSync(account, SYNC_ADAPTER_AUTHORITY, Bundle.EMPTY);
	}
}
