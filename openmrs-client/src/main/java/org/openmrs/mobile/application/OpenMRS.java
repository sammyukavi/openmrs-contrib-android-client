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

package org.openmrs.mobile.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.raizlabs.android.dbflow.config.FlowManager;

import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.mobile.receivers.ConnectivityReceiver;
import org.openmrs.mobile.security.SecretKeyGenerator;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OpenMRS extends Application {
	private static final String OPENMRS_DIR_NAME = "OpenMRS";
	private static final String OPENMRS_DIR_PATH = File.separator + OPENMRS_DIR_NAME;
	private static String mExternalDirectoryPath;

	private static OpenMRS instance;
	private static boolean ENCRYPTED = true;
	private OpenMRSLogger mLogger;

	// Sync constants
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
	private Account mAccount;

	// Connectivity constants
	private Boolean hasWifiSignalBeenLost = false;
	private Boolean hasDataSignalBeenLost = false;

	public static OpenMRS getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		//initializeSQLCipher();
		instance = this;

		if (mExternalDirectoryPath == null) {
			//mExternalDirectoryPath = this.getExternalFilesDir(null).toString();
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mExternalDirectoryPath = getExternalFilesDir(null).toString();
			} else {
				mExternalDirectoryPath = getFilesDir().toString();
			}
		}

		mLogger = new OpenMRSLogger();

		generateKey();
		initializeDB();
		registerReceivers();
		initializeDataSync();
	}

	private void registerReceivers() {
		// Register connectivity receiver
		BroadcastReceiver connectivityReceiver = new ConnectivityReceiver();
		IntentFilter connectivityFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(connectivityReceiver, connectivityFilter);
	}

	private void initializeDataSync() {
		// Create the dummy account
		mAccount = createSyncAccount(this);
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

	protected void initializeDB() {
		/*FlowConfig config = new FlowConfig.Builder(this)
				.openDatabasesOnInit(true)
				.build();*/


		FlowManager.init(this);
	}

	public SharedPreferences getOpenMRSSharedPreferences() {
		return getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
				MODE_PRIVATE);
	}

	public boolean isUserLoggedOnline() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getBoolean(ApplicationConstants.UserKeys.LOGIN, false);
	}

	public void setUserLoggedOnline(boolean firstLogin) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putBoolean(ApplicationConstants.UserKeys.LOGIN, firstLogin);
		editor.commit();
	}

	public String getUsername() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.UserKeys.USER_NAME, ApplicationConstants.EMPTY_STRING);
	}

	public void setUsername(String username) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.UserKeys.USER_NAME, username);
		editor.commit();
	}

	public String getPassword() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.UserKeys.PASSWORD, ApplicationConstants.EMPTY_STRING);
	}

	public void setPassword(String password) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.UserKeys.PASSWORD, password);
		editor.commit();
	}

	public String getServerUrl() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		String url = prefs.getString(ApplicationConstants.SERVER_URL, ApplicationConstants.DEFAULT_OPEN_MRS_URL);

		if (!url.endsWith("/")) {
			url += "/";
		}
		return url;
	}

	public void setServerUrl(String serverUrl) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.SERVER_URL, serverUrl);
		editor.commit();
	}

	public String getLastLoginServerUrl() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.LAST_LOGIN_SERVER_URL, ApplicationConstants.EMPTY_STRING);
	}

	public void setLastLoginServerUrl(String url) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.LAST_LOGIN_SERVER_URL, url);
		editor.commit();
	}

	public String getSessionToken() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
	}

	public void setSessionToken(String serverUrl) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.SESSION_TOKEN, serverUrl);
		editor.commit();
	}

	public String getLastSessionToken() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.LAST_SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
	}

	public String getAuthorizationToken() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.AUTHORIZATION_TOKEN, ApplicationConstants.EMPTY_STRING);
	}

	public void setAuthorizationToken(String authorization) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.AUTHORIZATION_TOKEN, authorization);
		editor.commit();
	}

	public String getLocation() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.LOCATION, ApplicationConstants.EMPTY_STRING);
	}

	public void setLocation(String location) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.LOCATION, location);
		editor.commit();
	}

	public String getParentLocationUuid() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.PARENT_LOCATION, ApplicationConstants.EMPTY_STRING);
	}

	public void setParentLocationUuid(String uuid) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.PARENT_LOCATION, uuid);
		editor.commit();
	}

	public void saveLocations(String locations) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.LOGIN_LOCATIONS, locations);
		editor.commit();
	}

	public String getLocations() {
		SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
		return sharedPreferences.getString(ApplicationConstants.LOGIN_LOCATIONS, ApplicationConstants
				.EMPTY_STRING);
	}

	public String getPatientUuid() {
		SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
		return sharedPreferences.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants
				.EMPTY_STRING);
	}

	public String getVisitUuid() {
		SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
		return sharedPreferences.getString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants
				.EMPTY_STRING);
	}

	public String getVisitTypeUUID() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.VISIT_TYPE_UUID, ApplicationConstants.EMPTY_STRING);
	}

	public String getCurrentProviderUUID() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING);
	}

	public String getCurrentUserUuid() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.UserKeys.USER_UUID, ApplicationConstants.EMPTY_STRING);
	}

	public String getSearchQuery() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, ApplicationConstants.EMPTY_STRING);
	}

	public void setVisitTypeUUID(String visitTypeUUID) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.VISIT_TYPE_UUID, visitTypeUUID);
		editor.commit();
	}

	private void generateKey() {
		// create database key only if not exist
		if (ApplicationConstants.EMPTY_STRING.equals(getSecretKey())) {
			SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
			String key = SecretKeyGenerator.generateKey();
			editor.putString(ApplicationConstants.SECRET_KEY, key);
			editor.commit();
		}
	}

	public String getSecretKey() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(ApplicationConstants.SECRET_KEY, ApplicationConstants.EMPTY_STRING);
	}

	public boolean getSyncState() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getBoolean("sync", true);
	}

	public void setSyncState(boolean enabled) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("sync", enabled);
		editor.apply();
	}

	public void setDefaultFormLoadID(String xFormName, String xFormID) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		editor.putString(xFormName, xFormID);
		editor.commit();
	}

	public String getDefaultFormLoadID(String xFormName) {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		return prefs.getString(xFormName, ApplicationConstants.EMPTY_STRING);
	}

	public void setCurrentUserInformation(Map<String, String> userInformation) {
		SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
		for (Map.Entry<String, String> entry : userInformation.entrySet()) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		editor.commit();
	}

	public Map<String, String> getCurrentLoggedInUserInfo() {
		SharedPreferences prefs = getOpenMRSSharedPreferences();
		Map<String, String> infoMap = new HashMap<String, String>();
		infoMap.put(ApplicationConstants.UserKeys.USER_PERSON_NAME,
				prefs.getString(ApplicationConstants.UserKeys.USER_PERSON_NAME, ApplicationConstants.EMPTY_STRING));
		infoMap.put(ApplicationConstants.UserKeys.USER_UUID,
				prefs.getString(ApplicationConstants.UserKeys.USER_UUID, ApplicationConstants.EMPTY_STRING));
		return infoMap;
	}

	private void clearCurrentLoggedInUserInfo() {
		SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(ApplicationConstants.UserKeys.USER_PERSON_NAME);
		editor.remove(ApplicationConstants.UserKeys.USER_UUID);
	}

	public OpenMRSLogger getOpenMRSLogger() {
		return mLogger;
	}

	public String getOpenMRSDir() {
		return mExternalDirectoryPath + OPENMRS_DIR_PATH;
	}

	public boolean isRunningHoneycombVersionOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public boolean isRunningJellyBeanVersionOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public boolean isRunningKitKatVersionOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	private void initializeSQLCipher() {
		SQLiteDatabase.loadLibs(this);
	}

	public void clearUserPreferencesData() {
		SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(ApplicationConstants.LAST_SESSION_TOKEN,
				prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING));
		editor.remove(ApplicationConstants.SESSION_TOKEN);
		editor.remove(ApplicationConstants.AUTHORIZATION_TOKEN);
		editor.remove(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE);
		editor.remove(ApplicationConstants.LOGIN_LOCATIONS);
		clearCurrentLoggedInUserInfo();
		editor.commit();
	}

	public void requestDataSync() {
		ContentResolver.requestSync(mAccount, AUTHORITY, Bundle.EMPTY);
	}
}
