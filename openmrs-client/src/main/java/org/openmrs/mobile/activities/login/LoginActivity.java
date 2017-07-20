/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class LoginActivity extends ACBaseActivity {

	// Constants
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "org.openmrs.mobile.provider";
	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "org.openmrs.mobile.datasync";
	// The account name (this isn't used by the app, but is needed for syncing)
	public static final String ACCOUNT = "defaultAccount";
	// Sync interval constants
	public static final long SECONDS_PER_MINUTE = 60L;
	public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
	public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

	public LoginContract.Presenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Create fragment
		LoginFragment loginFragment =
				(LoginFragment)getSupportFragmentManager().findFragmentById(R.id.loginContentFrame);
		if (loginFragment == null) {
			loginFragment = LoginFragment.newInstance();
		}
		if (!loginFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(),
					loginFragment, R.id.loginContentFrame);
		}

		mPresenter = new LoginPresenter(loginFragment, mOpenMRS);

		turnOnSyncing();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	private void turnOnSyncing() {
		// Create the dummy account
		Account mAccount = createSyncAccount(this);
		// Turn on periodic syncing
		ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
		ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, 3600);
	}

	/**
	 * Create a new dummy account for the sync adapter
	 *
	 * @param context The application context
	 */
	public static Account createSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

		// Below needed for syncing, but is for dummy account
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
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
}
