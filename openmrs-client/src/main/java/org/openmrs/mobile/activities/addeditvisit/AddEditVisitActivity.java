/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.addeditvisit;

import android.os.Bundle;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.visitphoto.download.DownloadVisitPhotoFragment;
import org.openmrs.mobile.activities.visitphoto.download.DownloadVisitPhotoPresenter;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

public class AddEditVisitActivity extends ACBaseActivity {

	public AddEditVisitContract.Presenter addEditVisitPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_addedit_visit, frameLayout);

		Bundle extras = getIntent().getExtras();
		String patientUuid = "";
		if (extras != null) {
			patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
			if (StringUtils.notEmpty(patientUuid)) {
				AddEditVisitFragment addEditVisitFragment =
						(AddEditVisitFragment)getSupportFragmentManager().findFragmentById(R.id.addeditVisitContentFrame);
				if (addEditVisitFragment == null) {
					addEditVisitFragment = AddEditVisitFragment.newInstance();
				}

				if (!addEditVisitFragment.isActive()) {
					addFragmentToActivity(getSupportFragmentManager(), addEditVisitFragment, R.id.addeditVisitContentFrame);
				}

				addEditVisitPresenter = new AddEditVisitPresenter(addEditVisitFragment, patientUuid);

				if(extras.getBoolean(ApplicationConstants.BundleKeys.END_VISIT_TAG, false)){
					showEndVisitDialog();
				}
			} else {
				ToastUtil.error(getString(R.string.no_patient_selected));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

}
