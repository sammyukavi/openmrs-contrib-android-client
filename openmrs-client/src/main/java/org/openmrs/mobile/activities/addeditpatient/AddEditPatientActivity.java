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

package org.openmrs.mobile.activities.addeditpatient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.Arrays;
import java.util.List;

public class AddEditPatientActivity extends ACBaseActivity {

	public AddEditPatientContract.Presenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_patient_info, frameLayout);
		setTitle(R.string.nav_register_patient);

		// Create fragment
		AddEditPatientFragment addEditPatientFragment =
				(AddEditPatientFragment)getSupportFragmentManager().findFragmentById(R.id.patientInfoContentFrame);
		if (addEditPatientFragment == null) {
			addEditPatientFragment = AddEditPatientFragment.newInstance();
		}
		if (!addEditPatientFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(),
					addEditPatientFragment, R.id.patientInfoContentFrame);
		}

		//Check if bundle includes patient ID
		Bundle patientBundle = savedInstanceState;
		if (patientBundle != null) {
			patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		} else {
			patientBundle = getIntent().getExtras();
		}
		String patientID = "";
		if (patientBundle != null) {
			patientID = patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		}

		List<String> counties = Arrays.asList(getResources().getStringArray(R.array.countiesArray));
		// Create the findPatientPresenter
		mPresenter = new AddEditPatientPresenter(addEditPatientFragment, counties, patientID);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
}