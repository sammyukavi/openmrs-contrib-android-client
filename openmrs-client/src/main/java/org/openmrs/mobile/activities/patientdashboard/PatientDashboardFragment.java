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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.visitphoto.upload.UploadVisitPhotoActivity;
import org.openmrs.mobile.activities.visittasks.VisitTasksActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.List;

public class PatientDashboardFragment extends ACBaseFragment<PatientDashboardContract.Presenter>
		implements PatientDashboardContract.View {

	private View fragmentView;
	private TextView patientDisplayName, patientGender, patientAge, patientIdentifier, visitDetails;
	private Visit activeVisit;
	private LinearLayout observationsContainer;
	private CustomDialogBundle createEditVisitNoteDialog;
	private Bundle dialogBundle;
	private FloatingActionButton startAuditFormButton, addVisitImageButton,
			addVisitTaskButton, startVisitButton, endVisitButton;
	private Patient patient;
	private OpenMRS instance = OpenMRS.getInstance();
	private DynamicObsDataViews dynamicObsDataViews;
	SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();

	public static PatientDashboardFragment newInstance() {
		return new PatientDashboardFragment();
	}

	View.OnClickListener switchToEditMode = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			((PatientDashboardActivity)getActivity())
					.createAndShowDialog(createEditVisitNoteDialog, ApplicationConstants.DialogTAG.VISIT_NOTE_TAG);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);
		observationsContainer = (LinearLayout)fragmentView.findViewById(R.id.observationsContainer);
		String patientId = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		initViewFields();
		initializeListeners(startAuditFormButton, addVisitImageButton, addVisitTaskButton, startVisitButton,
				endVisitButton);
		mPresenter.fetchPatientData(patientId);
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));
		return fragmentView;
	}

	private void initializeListeners(FloatingActionButton... params) {
		for (FloatingActionButton patientActionButtons : params) {
			patientActionButtons.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startSelectedPatientDashboardActivity(patientActionButtons.getId());
				}
			});
		}
	}

	private void startSelectedPatientDashboardActivity(int selectedId) {
		switch (selectedId) {
			case R.id.add_visit_image:
				Intent intent = new Intent(getContext(), UploadVisitPhotoActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
			case R.id.add_visit_task:
				intent = new Intent(getContext(), VisitTasksActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
			case R.id.audit_data_form:
				//intent = new Intent(getContext(), AuditDataActivity.class);
				//intent.putExtra(ApplicationConstants.BundleKeys.PATIENT, patient);
				//intent.putExtra(ApplicationConstants.BundleKeys.VISIT, mainVisit);
				//startActivity(intent);
				break;
			case R.id.start_visit:
				intent = new Intent(getContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
			case R.id.end_visit:
				intent = new Intent(getContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
		}
	}

	private void initViewFields() {
		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		patientIdentifier = (TextView)fragmentView.findViewById(R.id.fetchedPatientIdentifier);
		patientGender = (TextView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		visitDetails = (TextView)fragmentView.findViewById(R.id.visitDetails);
		addVisitImageButton = (FloatingActionButton)getActivity().findViewById(R.id.add_visit_image);
		addVisitTaskButton = (FloatingActionButton)getActivity().findViewById(R.id.add_visit_task);
		startAuditFormButton = (FloatingActionButton)getActivity().findViewById(R.id.audit_data_form);
		startVisitButton = (FloatingActionButton)getActivity().findViewById(R.id.start_visit);
		endVisitButton = (FloatingActionButton)getActivity().findViewById(R.id.end_visit);
		//TextView moreLabel = (TextView) fragmentView.findViewById(R.id.more_label);
		FloatingActionMenu floatingActionMenu = (FloatingActionMenu)getActivity().findViewById(R.id.floatingActionMenu);
		floatingActionMenu.setVisibility(View.VISIBLE);
		startAuditFormButton = (FloatingActionButton)getActivity().findViewById(R.id.audit_data_form);
		dynamicObsDataViews = new DynamicObsDataViews(getContext());
		createEditVisitNoteDialog = new CustomDialogBundle();
		createEditVisitNoteDialog.setTitleViewMessage(getString(R.string.visit_note));
		createEditVisitNoteDialog.setRightButtonText(getString(R.string.label_save));
		dialogBundle = new Bundle();
	}

	@Override
	public void showSnack(String text) {
		Snackbar.make(fragmentView, text, Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
	}

	@Override
	public void updateContactCard(Patient patient) {
		this.patient = patient;
		Person person = patient.getPerson();
		patientDisplayName.setText(person.getName().getNameString());
		patientGender.setText(person.getGender());
		patientIdentifier.setText(patient.getIdentifier().getIdentifier());
		DateTime date = DateUtils.convertTimeString(person.getBirthdate());
		patientAge.setText(DateUtils.calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
		mPresenter.fetchVisits(patient);
		setPatientUuid(patient);
	}

	@Override
	public void updateActiveVisitCard(List<Visit> visits) {
		for (Visit visit : visits) {
			if (!StringUtils.notNull(visit.getStopDatetime())) {
				this.activeVisit = visit;
				setVisitUuid(visit);
				visits.remove(visit);
				break;
			}

		}
		if (activeVisit != null) {
			fragmentView.findViewById(R.id.observationsCardView).setVisibility(View.VISIBLE);
			visitDetails.setText(getString(R.string.active_visit_label) + ": " + DateUtils
					.convertTime1(activeVisit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_DATE_FORMAT));
			if (activeVisit.getEncounters().size() == 0) {
				//We add a view to create a visit note
				dynamicObsDataViews = dynamicObsDataViews.instance(getContext());
				dynamicObsDataViews.setHint(getResources().getString(R.string.add_a_note));
				observationsContainer.addView(dynamicObsDataViews.getLayoutView());

				createEditVisitNoteDialog.setEditNoteTextViewMessage("");
				createEditVisitNoteDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.CREATE_VISIT_NOTE);
				createEditVisitNoteDialog.setArguments(dialogBundle);

				dynamicObsDataViews.observationIcon.setOnClickListener(switchToEditMode);
				dynamicObsDataViews.observationText.setOnClickListener(switchToEditMode);
			} else {
				for (Encounter encounter : activeVisit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case ApplicationConstants.EncounterTypeEntitys.VISIT_NOTE:
							mPresenter.fetchEncounterObservations(encounter);
							break;
					}
				}
			}
		}

		RecyclerView previousVisits = (RecyclerView)fragmentView.findViewById(R.id.previousVisits);
		previousVisits.setLayoutManager(new LinearLayoutManager(getContext()));
		VisitsAdapter visitsAdapter = new VisitsAdapter(previousVisits, visits, getActivity());
		previousVisits.setAdapter(visitsAdapter);

		visitsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				if (visits.size() <= 2) {
					visits.add(null);
					visitsAdapter.notifyItemInserted(visits.size() - 1);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							visits.remove(visits.size() - 1);
							visitsAdapter.notifyItemRemoved(visits.size());
							//Load more from server here
							visitsAdapter.notifyDataSetChanged();
							visitsAdapter.setLoaded();
						}
					}, 5000);
				} else {
					Toast.makeText(getContext(), "Loading data completed", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void updateActiveVisitObservationsCard(Observation observation) {
		Observation newObs = new Observation();
		newObs.setUuid(observation.getUuid());
		newObs.setConcept(observation.getConcept());
		newObs.setPerson(patient.getPerson());
		newObs.setObsDatetime(DateUtils.now(DateUtils.OPEN_MRS_REQUEST_FORMAT));

		dynamicObsDataViews = dynamicObsDataViews.instance(getContext());
		dynamicObsDataViews.setText(observation.getDiagnosisNote());
		dynamicObsDataViews.observationIcon.setOnClickListener(switchToEditMode);
		dynamicObsDataViews.observationText.setOnClickListener(switchToEditMode);
		observationsContainer.addView(dynamicObsDataViews.getLayoutView());

		dialogBundle.putSerializable(ApplicationConstants.BundleKeys.OBSERVATION, newObs);
		createEditVisitNoteDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.SAVE_VISIT_NOTE);
		createEditVisitNoteDialog.setEditNoteTextViewMessage(observation.getDiagnosisNote());
		createEditVisitNoteDialog.setArguments(dialogBundle);
	}

	@Override
	public LinearLayout getVisitNoteContainer() {
		return observationsContainer;
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

}