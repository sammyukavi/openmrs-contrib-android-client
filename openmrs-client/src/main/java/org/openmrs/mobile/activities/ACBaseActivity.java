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
package org.openmrs.mobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import static com.google.common.base.Preconditions.checkNotNull;


public abstract class ACBaseActivity extends AppCompatActivity {

    protected FragmentManager mFragmentManager;
    protected final OpenMRS mOpenMRS = OpenMRS.getInstance();
    protected final OpenMRSLogger mOpenMRSLogger = mOpenMRS.getOpenMRSLogger();
    protected AuthorizationManager mAuthorizationManager;
    private MenuItem mSyncbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mAuthorizationManager = new AuthorizationManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        //if (!(this instanceof LoginActivity || this instanceof DialogActivity) && !mAuthorizationManager.isUserLoggedIn()) {
        //    mAuthorizationManager.moveToLoginActivity();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        mSyncbutton = menu.findItem(R.id.syncbutton);
        MenuItem logoutMenuItem = menu.findItem(R.id.actionLogout);
        if (logoutMenuItem != null) {
            logoutMenuItem.setTitle(getString(R.string.action_logout) + " " + mOpenMRS.getUsername());
        }
        if(mSyncbutton !=null) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
            final Boolean syncState = prefs.getBoolean("sync", true);
            setSyncButtonState(syncState);
        }
        return true;
    }

    private void setSyncButtonState(boolean syncState) {
        if (syncState) {
            mSyncbutton.setIcon(R.drawable.ic_sync_on);
        }
        else {
            mSyncbutton.setIcon(R.drawable.ic_sync_off);
        }
    }

    public void logout() {
        mOpenMRS.clearUserPreferencesData();
        mAuthorizationManager.moveToLoginActivity();
        OpenMRSDBOpenHelper.getInstance().closeDatabases();
    }

    public void showShortToast(boolean errorOccurred, Integer successMessageId, Integer errorMessageId) {
        if (!errorOccurred && successMessageId != null) {
            ToastUtil.showShortToast(this,
                    ToastUtil.ToastType.SUCCESS,
                    successMessageId);
        } else if (errorMessageId != null) {
            ToastUtil.showShortToast(this,
                    ToastUtil.ToastType.ERROR,
                    errorMessageId);
        }
    }
    public AuthorizationManager getAuthorizationManager() {
        return mAuthorizationManager;
    }

    public void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

}
