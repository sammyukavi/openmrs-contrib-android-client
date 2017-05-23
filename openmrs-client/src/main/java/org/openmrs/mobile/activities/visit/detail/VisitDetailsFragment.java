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

package org.openmrs.mobile.activities.visit.detail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitActivity;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisitDetailsFragment extends VisitFragment implements VisitContract.VisitDetailsView {

	private TextView visitDate, bedNumber, ward, visitType, noVitals;
	private Visit visit;
	private TableLayout visitVitalsTableLayout;
	private static TableRow.LayoutParams marginParams;
	private Button submitVisitNote;
	private TextInputEditText chiefComplaint, clinicalNote;
	private AutoCompleteTextView addDiagnosis;
	private Intent intent;
	private OpenMRS instance = OpenMRS.getInstance();
	private SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
	private FloatingActionButton endVisitButton, editVisitButton;

	public static VisitDetailsFragment newInstance() {
		return new VisitDetailsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setPresenter(mPresenter);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_visit_details, container, false);
		resolveViews(root);
		((VisitDetailsPresenter)mPresenter).getVisit();
		//buildMarginLayout();
		initializeListeners(endVisitButton, editVisitButton);
		return root;
	}

	private void resolveViews(View v) {
		visitDate = (TextView)v.findViewById(R.id.visitDates);
		bedNumber = (TextView)v.findViewById(R.id.fetchedBedNumber);
		ward = (TextView)v.findViewById(R.id.fetchedWard);
		visitType = (TextView)v.findViewById(R.id.visitType);
		noVitals = (TextView)v.findViewById(R.id.noVitals);
		visitVitalsTableLayout = (TableLayout)v.findViewById(R.id.visitVitalsTable);
		submitVisitNote = (Button)v.findViewById(R.id.submitVisitNote);
		clinicalNote = (TextInputEditText)v.findViewById(R.id.clinicalNotes);
		addDiagnosis = (AutoCompleteTextView)v.findViewById(R.id.diagnosisInput);
		chiefComplaint = (TextInputEditText)v.findViewById(R.id.chiefComplaint);
		editVisitButton = (FloatingActionButton)v.findViewById(R.id.edit_visit);
		endVisitButton = (FloatingActionButton)v.findViewById(R.id.end_visit);
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {

	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
		if (visit != null) {
			setVisitDates(visit);
			setVisitType(visit);
			setAttributeTypes(visit);
			setVitals(visit);
			setVisitNote(visit);
		}

	}

	public void setVisitDates(Visit visit) {
		if (StringUtils.notNull(visit.getStopDatetime())) {
			visitDate.setText(getContext().getString(R.string.date_started) + ": " + DateUtils
					.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT) + " - "
					+ getContext().getString(R.string.date_closed) + ": " + DateUtils.convertTime1(visit.getStopDatetime(),
					DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
		} else {
			visitDate.setText(getContext().getString(R.string.active_visit_label) + ": " + DateUtils.convertTime1
					(visit.getStartDatetime(), DateUtils.DATE_FORMAT) + " (started " + DateUtils.convertTime1
					(visit.getStartDatetime(), DateUtils.TIME_FORMAT) + ")");
			visitDate.setTextColor(getResources().getColor(R.color.color_white));
			visitDate.setBackgroundColor(getResources().getColor(R.color.color_primary));
		}
	}

	public void setVisitType(Visit visit) {
		if (visit.getVisitType() != null) {
			visitType.setText(visit.getVisitType().getDisplay());
		} else {
			visitType.setText(ApplicationConstants.EMPTY_STRING);
		}
	}

	public void setAttributeTypes(Visit visit) {
		if (visit.getAttributes().size() != 0) {
			for (int i = 0; i < visit.getAttributes().size(); i++) {
				if (visit.getAttributes().get(i).getUuid().equalsIgnoreCase(ApplicationConstants.visitAttributeTypes
						.BED_NUMBER_UUID)) {
					bedNumber.setText(visit.getAttributes().get(i).getValue().toString());
				} else if (visit.getAttributes().get(i).getUuid()
						.equalsIgnoreCase(ApplicationConstants.visitAttributeTypes.WARD_UUID)) {
					ward.setText(visit.getAttributes().get(i).getValue().toString());

				}
			}
		}
	}

	public void setVitals(Visit visit) {
		if (visit.getEncounters().size() != 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				if (visit.getEncounters().get(i).getEncounterType().getUuid()
						.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.VITALS_UUID)) {
					if (visit.getEncounters().get(i).getObs().size() != 0) {
						noVitals.setVisibility(View.GONE);
						visitVitalsTableLayout.setVisibility(View.VISIBLE);
						loadObservationsFields(visit.getEncounters().get(i).getObs());
					} else {
						noVitals.setVisibility(View.VISIBLE);
						visitVitalsTableLayout.setVisibility(View.GONE);
					}

				}
			}
		}
	}

	public void setVisitNote(Visit visitNote) {
		if (visitNote.getEncounters().size() != 0) {
			for (int i = 0; i < visitNote.getEncounters().size(); i++) {
				Encounter encounter = visitNote.getEncounters().get(i);
				EncounterType encounterType = visitNote.getEncounters().get(i).getEncounterType();

				if (encounterType.getUuid().equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.VISIT_NOTE_UUID)) {
					submitVisitNote.setText(getString(R.string.action_update));

					for (int v = 0; v < encounter.getObs().size(); v++) {

						ArrayList locators = splitStrings(encounter.getObs().get(v).getDisplay());

						if (locators.get(0).toString()
								.equalsIgnoreCase(ApplicationConstants.ObserationLocators.CLINICAL_NOTE)) {
							ArrayList clinicalNoteText = splitStrings(encounter.getObs().get(v).getDisplay());
							clinicalNote.setText(locators.get(1).toString());
							System.out.println(clinicalNoteText.get(1).toString());
						} else if (locators.get(0).toString()
								.equalsIgnoreCase(ApplicationConstants.ObserationLocators.CHIEF_COMPLAINT)) {
							chiefComplaint.setText(locators.get(1).toString());
						} else {

						}

					}

				} else {

				}
			}
		}
	}

	public void loadObservationsFields(List<Observation> observations) {
		for (Observation observation : observations) {

			TableRow row = new TableRow(getContext());
			row.setPadding(0, 20, 0, 10);
			row.setGravity(Gravity.CENTER);

			ArrayList splitValues = splitStrings(observation.getDisplay());

			TextView label = new TextView(getContext());
			label.setText(splitValues.get(0) + " :");
			label.setTextSize(14);
			label.setGravity(Gravity.RIGHT | Gravity.END);
			label.setTextColor(getResources().getColor(R.color.black));
			row.addView(label, 0);

			TextView vitalValue = new TextView(getContext());
			vitalValue.setText(splitValues.get(1).toString());
			vitalValue.setTextSize(14);
			vitalValue.setTextColor(getResources().getColor(R.color.dark_grey));
			row.addView(vitalValue, 1);

			visitVitalsTableLayout.addView(row);
		}
	}

	private ArrayList splitStrings(String display) {
		ArrayList<String> displayArray = new ArrayList<>();
		Collections.addAll(displayArray, display.split(":"));
		return displayArray;
	}

	private void buildMarginLayout() {
		if (marginParams == null) {
			marginParams = new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
			marginParams.setMargins(70, 0, 0, 0);
		}
	}

	private void initializeListeners(FloatingActionButton... params) {
		for (FloatingActionButton patientActionButtons : params) {
			patientActionButtons.setOnClickListener(
					view -> startSelectedPatientDashboardActivity(patientActionButtons.getId()));
		}
	}

	private void startSelectedPatientDashboardActivity(int selectedId) {
		switch (selectedId) {
			case R.id.edit_visit:
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
				intent.putExtra(ApplicationConstants.BundleKeys.END_VISIT_TAG, true);
				startActivity(intent);
				break;
		}
	}

}
