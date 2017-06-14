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

package org.openmrs.mobile.activities.findpatientrecord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class FindPatientRecordActivity extends ACBaseActivity {

	public FindPatientRecordContract.Presenter findPatientPresenter;
	FindPatientRecordFragment findPatientRecordFragment;
	SearchView searchView;
	private String query;
	private OpenMRS instance = OpenMRS.getInstance();
	private SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_find_patient_record, frameLayout);
		setTitle(R.string.nav_find_patient);

		// Create fragment
		findPatientRecordFragment =
				(FindPatientRecordFragment)getSupportFragmentManager().findFragmentById(R.id
						.findPatientContentFrame);
		if (findPatientRecordFragment == null) {
			findPatientRecordFragment = FindPatientRecordFragment.newInstance();
		}
		if (!findPatientRecordFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), findPatientRecordFragment, R.id.findPatientContentFrame);
		}

		if (savedInstanceState != null) {
			query = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, "");
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment, query);
		} else {
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment);
		}

		//Add menu autocolse
		FloatingActionMenu findPatientMenu = (FloatingActionMenu)findViewById(R.id.findPatientMenu);
		findPatientMenu.setClosedOnTouchOutside(true);

		//adding
		FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_Patient);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findPatientMenu.close(true);
				Intent intent = new Intent(FindPatientRecordActivity.this, AddEditPatientActivity.class);
				startActivity(intent);
			}
		});

		if (savedInstanceState != null) {
			query = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, "");
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment, query);
		} else {
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//String query = searchView.getQuery().toString();
		//outState.putString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, query);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_find_patient_record, menu);
		MenuItem mFindPatientMenuItem = menu.findItem(R.id.action_search);

		if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
			searchView = (SearchView)mFindPatientMenuItem.getActionView();
		} else {
			searchView = (SearchView)MenuItemCompat.getActionView(mFindPatientMenuItem);
		}

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				if (query.length() >= 3) {
					findPatientPresenter.findPatient(query);
					setSearchQuery(query);
				} else {
					if (OpenMRS.getInstance().getSearchQuery().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
						findPatientPresenter.getLastViewed();
					} else {
						findPatientPresenter.findPatient(query);
					}
					findPatientRecordFragment.setNoPatientsVisibility(false);
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String query) {
				if (query.length() >= 3) {
					findPatientPresenter.findPatient(query);
					setSearchQuery(query);
				} else {
					findPatientRecordFragment.setNumberOfPatientsView(0);
					findPatientRecordFragment.setNoPatientsVisibility(false);
				}
				return false;
			}
		});
		return true;
	}

	public void setSearchQuery(String query) {
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, query);
		editor.commit();
	}
}
