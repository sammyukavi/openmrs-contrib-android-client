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

package org.openmrs.mobile.activities.visitdetails;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import net.yanzm.mth.MaterialTabHost;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.visitphoto.download.DownloadVisitPhotoFragment;
import org.openmrs.mobile.activities.visitphoto.download.DownloadVisitPhotoPresenter;
import org.openmrs.mobile.activities.visittasks.VisitTasksFragment;
import org.openmrs.mobile.activities.visittasks.VisitTasksPresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.TabUtil;

import java.util.ArrayList;

public class VisitDetailsActivity extends ACBaseActivity {

	VisitDetailsContract.VisitDetailsMainPresenter visitDetailsPresenter;
	private String patientUuid;
	private String visitUuid;
	private String providerUuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_visit_details, frameLayout);
		getSupportActionBar().setElevation(0);
		setTitle(R.string.nav_visit_details);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
			visitUuid = extras.getString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);
			providerUuid = OpenMRS.getInstance().getCurrentProviderUUID();
			initViewPager(new VisitDetailsPageAdapter(getSupportFragmentManager(), patientUuid, visitUuid, providerUuid));
		}
	}

	private void initViewPager(VisitDetailsPageAdapter visitDetailsPageAdapter) {
		MaterialTabHost tabHost = (MaterialTabHost)findViewById(R.id.visitDetailsTabHost);
		tabHost.setType(MaterialTabHost.Type.FullScreenWidth);
		for (int i = 0; i < visitDetailsPageAdapter.getCount(); i++) {
			tabHost.addTab(getTabNames().get(i).toUpperCase());
		}
		final ViewPager viewPager = (ViewPager)findViewById(R.id.visitDetailsPager);
		viewPager.setAdapter(visitDetailsPageAdapter);
		viewPager.addOnPageChangeListener(tabHost);
		tabHost.setOnTabChangeListener(new MaterialTabHost.OnTabChangeListener() {
			@Override
			public void onTabSelected(int position) {
				viewPager.setCurrentItem(position);
			}
		});
	}

	private ArrayList<String> getTabNames() {
		ArrayList<String> tabNames = new ArrayList<>();
		tabNames.add(getString(R.string.visit_scroll_tab_details_label));
		tabNames.add(getString(R.string.visi_scroll_tab_visit_tasks_label));
		tabNames.add(getString(R.string.visit_scroll_tab_visit_images_label));
		return tabNames;
	}

	private void attachPresenterToFragment(Fragment fragment) {
		Bundle patientBundle = getIntent().getExtras();
		patientUuid = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE));
		visitUuid = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE));
		if (fragment instanceof VisitTasksFragment) {
			visitDetailsPresenter = new VisitTasksPresenter(patientUuid, visitUuid, ((VisitTasksFragment)fragment));
		} else if (fragment instanceof DownloadVisitPhotoFragment) {
			visitDetailsPresenter = new DownloadVisitPhotoPresenter(((DownloadVisitPhotoFragment)fragment),patientUuid);
		}
		/*else if (fragment instanceof PatientVitalsFragment){
			visitDetailsPresenter = new PatientDashboardVitalsPresenter(patientUuid, ((PatientVitalsFragment) fragment));
		}*/
	}

	@Override
	public void onConfigurationChanged(final Configuration config) {
		super.onConfigurationChanged(config);
		TabUtil.setHasEmbeddedTabs(getSupportActionBar(), getWindowManager(),
				TabUtil.MIN_SCREEN_WIDTH_FOR_VISITDETAILSACTIVITY);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		attachPresenterToFragment(fragment);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		outState.putString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
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
