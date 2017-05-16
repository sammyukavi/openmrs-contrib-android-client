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

package org.openmrs.mobile.activities.visittasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.View;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.patientheader.PatientHeaderFragment;
import org.openmrs.mobile.activities.patientheader.PatientHeaderPresenter;
import org.openmrs.mobile.application.OpenMRS;

public class VisitTasksActivity extends ACBaseActivity {

	public VisitTasksContract.Presenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_visit_tasks, frameLayout);
		setTitle(R.string.nav_visit_tasks);

		// Create fragment
		VisitTasksFragment visitTasksFragment =
				(VisitTasksFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (visitTasksFragment == null) {
			visitTasksFragment = VisitTasksFragment.newInstance();
		}
		if (!visitTasksFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), visitTasksFragment, R.id.contentFrame);
		}

		//adding
		FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.visitTaskFab);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.displayAddTask(true);
			}
		});

		mPresenter = new VisitTasksPresenter(visitTasksFragment);

		// patient header
		String patientUuid;
		patientUuid = OpenMRS.getInstance().getPatientUuid();

		PatientHeaderFragment headerFragment = (PatientHeaderFragment)getSupportFragmentManager()
				.findFragmentById(R.id.patientHeader);
		if (headerFragment == null) {
			headerFragment = PatientHeaderFragment.newInstance();
		}

		if (!headerFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), headerFragment, R.id.patientHeader);
		}

		new PatientHeaderPresenter(headerFragment, patientUuid);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
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
