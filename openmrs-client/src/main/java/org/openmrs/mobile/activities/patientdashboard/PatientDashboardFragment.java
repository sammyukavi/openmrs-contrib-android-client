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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ConsoleLogger;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.List;

public class PatientDashboardFragment extends ACBaseFragment<PatientDashboardContract.Presenter>
		implements PatientDashboardContract.View {

	private View fragmentView;
	private TextView patientDisplayName;
	private TextView patientGender;
	private TextView patientAge;
	private TextView patientIdentifier;
	private TextView visitDetails;
	private FloatingActionButton startAuditFormButton;
	private Visit activeVisit;
	private Patient patient;
	private LinearLayout observationsContainer;
	private LinearLayout observationHolder;
	private ImageView observationIcon;
	private TextView observationTextView;
	private CustomDialogBundle createEditVisitNoteDialog;
	private Bundle dialogBundle;

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
		initializeListeners();
		mPresenter.fetchPatientData(patientId);
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));
		return fragmentView;
	}

	private void initializeListeners() {
		startAuditFormButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Intent intent = new Intent(getContext(), AuditDataActivity.class);
				//intent.putExtra(ApplicationConstants.BundleKeys.PATIENT, patient);
				//intent.putExtra(ApplicationConstants.BundleKeys.VISIT, mainVisit);
				//startActivity(intent);
			}
		});
	}

	private void initViewFields() {
		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		patientIdentifier = (TextView)fragmentView.findViewById(R.id.fetchedPatientIdentifier);
		patientGender = (TextView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		visitDetails = (TextView)fragmentView.findViewById(R.id.visitDetails);
		//TextView moreLabel = (TextView) fragmentView.findViewById(R.id.more_label);
		FloatingActionMenu floatingActionMenu = (FloatingActionMenu)getActivity().findViewById(R.id.floatingActionMenu);
		floatingActionMenu.setVisibility(View.VISIBLE);
		startAuditFormButton = (FloatingActionButton)getActivity().findViewById(R.id.audit_data_form);
		ViewGroup.LayoutParams linearLayoutParams =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
						.WRAP_CONTENT,
						1.0f);
		observationHolder = new LinearLayout(getContext());
		observationHolder.setLayoutParams(linearLayoutParams);
		observationHolder.setOrientation(LinearLayout.HORIZONTAL);
		observationHolder.setPadding(0, 0, 0, 0);
		observationIcon = new ImageView(getContext());
		observationIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_menu_edit));
		observationIcon.setPadding(0, 0, 0, 0);
		observationTextView = new TextView(getContext());
		observationTextView.setPadding(10, 0, 10, 0);
		observationTextView.setGravity(Gravity.LEFT);
		observationTextView.setHintTextColor(ContextCompat.getColor(getContext(), R.color.openmrs_color_grey));
		observationTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.openmrs_color_grey));
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
	}

	@Override
	public void updateActiveVisitCard(List<Visit> visits) {
		for (Visit visit : visits) {
			if (!StringUtils.notNull(visit.getStopDatetime())) {
				this.activeVisit = visit;
				visits.remove(visit);
			}
		}
		if (activeVisit != null) {
			visitDetails.setText(getString(R.string.active_visit_label) + ": " + DateUtils
					.convertTime1(activeVisit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_DATE_FORMAT));
			ConsoleLogger.dump(activeVisit.getEncounters().size());
			if (activeVisit.getEncounters().size() == 0) {
				//We add a view to create a visit note
				observationTextView.setHint(getResources().getString(R.string.add_a_note));
				observationHolder.addView(observationIcon);
				observationHolder.addView(observationTextView);
				observationsContainer.addView(observationHolder);
				createEditVisitNoteDialog.setEditNoteTextViewMessage("");
				createEditVisitNoteDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.CREATE_VISIT_NOTE);
				createEditVisitNoteDialog.setArguments(dialogBundle);
				observationIcon.setOnClickListener(switchToEditMode);
				observationTextView.setOnClickListener(switchToEditMode);
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
	}

	@Override
	public void updateActiveVisitObservationsCard(Observation observation) {
		observationTextView.setText(observation.getDiagnosisNote());
		observationHolder.addView(observationIcon);
		observationHolder.addView(observationTextView);
		observationsContainer.addView(observationHolder);
		createEditVisitNoteDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.SAVE_VISIT_NOTE);
		createEditVisitNoteDialog.setEditNoteTextViewMessage(observation.getDiagnosisNote());
		dialogBundle.putSerializable(ApplicationConstants.BundleKeys.OBSERVATION, observation);
		dialogBundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT, patient);
		createEditVisitNoteDialog.setArguments(dialogBundle);
		observationIcon.setOnClickListener(switchToEditMode);
		observationTextView.setOnClickListener(switchToEditMode);
	}

	public LinearLayout getVisitNoteContainer() {
		return observationsContainer;
	}

}
