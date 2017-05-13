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

package org.openmrs.mobile.activities.patientdashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitContract;
import org.openmrs.mobile.activities.visitphoto.download.DownloadVisitPhotoFragment;
import org.openmrs.mobile.activities.visitphoto.download.DownloadVisitPhotoPresenter;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

public class PatientDashboardActivity extends ACBaseActivity {

	public PatientDashboardContract.Presenter mPresenter;

	public AddEditVisitContract.Presenter addEditVisitPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_patient_dashboard, frameLayout);
		setTitle(R.string.title_patient_details);
		// Create fragment
		PatientDashboardFragment patientDashboardFragment =
				(PatientDashboardFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (patientDashboardFragment == null) {
			patientDashboardFragment = PatientDashboardFragment.newInstance();
		}
		if (!patientDashboardFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), patientDashboardFragment, R.id.contentFrame);
		}
		mPresenter = new PatientDashboardPresenter(patientDashboardFragment);

		Bundle extras = getIntent().getExtras();
		String patientUuid = "";
		if (extras != null) {
			patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
			if (StringUtils.notEmpty(patientUuid)) {
				// download visit photos.
				DownloadVisitPhotoFragment visitPhotoFragment =
						(DownloadVisitPhotoFragment)getSupportFragmentManager()
								.findFragmentById(R.id.photoDownloadsContentFrame);
				if (visitPhotoFragment == null) {
					visitPhotoFragment = DownloadVisitPhotoFragment.newInstance();
				}

				if (!visitPhotoFragment.isActive()) {
					addFragmentToActivity(getSupportFragmentManager(), visitPhotoFragment, R.id.photoDownloadsContentFrame);
				}

				new DownloadVisitPhotoPresenter(visitPhotoFragment, patientUuid);
			}
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

}
