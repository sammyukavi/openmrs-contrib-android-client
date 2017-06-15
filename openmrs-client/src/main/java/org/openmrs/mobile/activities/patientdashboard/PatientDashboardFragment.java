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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.HashMap;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.LOCATION_UUID_BUNDLE;
import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE;

public class PatientDashboardFragment extends ACBaseFragment<PatientDashboardContract.Presenter>
		implements PatientDashboardContract.View {

	private View fragmentView;
	private FloatingActionButton startVisitButton, editPatient;
	private Patient patient;
	private OpenMRS instance = OpenMRS.getInstance();
	private SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
	private Intent intent;
	private Location location;
	private RelativeLayout dashboardScreen;
	private ProgressBar dashboardProgressBar;
	private TextView noVisitNoteLabel;
	private String patientUuid;
	private VisitsRecyclerAdapter visitsRecyclerAdapter;
	private PatientDashboardActivity patientDashboardActivity;
	private FloatingActionMenu patientDashboardMenu;
	private int startIndex = 0, limit = 5;
	private static PatientDashboardContract.Presenter staticPresenter;
	private static String staticPatientUuid;
	private static boolean hasActiveVisit;
	private static FloatingActionButton staticStartVisitButton;

	public static PatientDashboardFragment newInstance() {
		return new PatientDashboardFragment();
	}

	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);

		staticPatientUuid = patientUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys
				.PATIENT_UUID_BUNDLE);

		initViewFields();

		initializeListeners(startVisitButton, editPatient);

		//set start index incase it's cached somewhere
		mPresenter.setStartIndex(startIndex);

		//set limit for visits
		mPresenter.setLimit(limit);

		mPresenter.fetchPatientData(patientUuid);

		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		staticPresenter = mPresenter;

		return fragmentView;
	}

	private void initializeListeners(FloatingActionButton... params) {
		for (FloatingActionButton patientActionButtons : params) {
			patientActionButtons.setOnClickListener(
					view -> startSelectedPatientDashboardActivity(patientActionButtons.getId()));
		}
	}

	private void startSelectedPatientDashboardActivity(int selectedId) {

		patientDashboardMenu.close(true);

		switch (selectedId) {
			case R.id.start_visit:
				intent = new Intent(getContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				startActivity(intent);
				break;
			case R.id.edit_Patient:
				intent = new Intent(getContext(), AddEditPatientActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				startActivity(intent);

				break;
		}
	}

	private void initViewFields() {

		staticStartVisitButton = startVisitButton = (FloatingActionButton)fragmentView.findViewById(R.id.start_visit);
		editPatient = (FloatingActionButton)fragmentView.findViewById(R.id.edit_Patient);
		dashboardScreen = (RelativeLayout)fragmentView.findViewById(R.id.dashboardScreen);
		dashboardProgressBar = (ProgressBar)fragmentView.findViewById(R.id.dashboardProgressBar);
		noVisitNoteLabel = (TextView)fragmentView.findViewById(R.id.noVisitNoteLabel);
		patientDashboardMenu = (FloatingActionMenu)fragmentView.findViewById(R.id.patientDashboardMenu);
		patientDashboardMenu.setClosedOnTouchOutside(true);

	}

	@Override
	public void updateContactCard(Patient patient) {
		this.patient = patient;
		setPatientUuid(patient);
	}

	@Override
	public void updateVisitsCard(List<Visit> visits) {
		hasActiveVisit = false;
		for (Visit visit : visits) {
			if (visit.getStopDatetime() == null) {
				hasActiveVisit = true;
				startVisitButton.setVisibility(View.GONE);
				setVisitUuid(visit);
				break;
			}
		}

		HashMap<String, String> uuidsHashmap = new HashMap<>();
		uuidsHashmap.put(PATIENT_UUID_BUNDLE, patient == null ? "" : patient.getUuid());
		uuidsHashmap.put(LOCATION_UUID_BUNDLE, location == null ? "" : location.getUuid());
		RecyclerView visitsRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.pastVisits);
		visitsRecyclerAdapter = new VisitsRecyclerAdapter(visitsRecyclerView, visits, getActivity());
		visitsRecyclerAdapter.setUuids(uuidsHashmap);
		visitsRecyclerView.setAdapter(visitsRecyclerAdapter);

	}

	@Override
	public void updateVisits(List<Visit> results) {
		visitsRecyclerAdapter.updateVisits(results);
	}

	@Override
	public Patient getPatient() {
		return patient;
	}

	public void setPatientUuid(Patient patient) {
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patient.getPerson().getUuid());
		editor.commit();
	}

	public void setVisitUuid(Visit visit) {
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visit.getUuid());
		editor.commit();
	}

	@Override
	public void setProviderUuid(String providerUuid) {
		if (StringUtils.isBlank(providerUuid))
			return;
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
		editor.commit();
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public void showSavingClinicalNoteProgressBar(boolean show) {
		visitsRecyclerAdapter.updateSavingClinicalNoteProgressBar(show);
	}

	@Override
	public void showPageSpinner(boolean visibility) {
		if (visibility) {
			dashboardProgressBar.setVisibility(View.VISIBLE);
			dashboardScreen.setVisibility(View.GONE);
		} else {
			dashboardProgressBar.setVisibility(View.GONE);
			dashboardScreen.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void showNoVisits(boolean visibility) {
		if (visibility) {
			noVisitNoteLabel.setVisibility(View.VISIBLE);
		} else {
			noVisitNoteLabel.setVisibility(View.GONE);
		}
	}

	@Override
	public void updateClinicVisitNote(Observation observation) {
		visitsRecyclerAdapter.updateClinicalNoteObs(observation);
	}

	public static void fetchPatientData() {
		staticPresenter.fetchPatientData(staticPatientUuid);
		//staticStartVisitButton.setVisibility(hasActiveVisit ? View.GONE : View.VISIBLE);
	}
}