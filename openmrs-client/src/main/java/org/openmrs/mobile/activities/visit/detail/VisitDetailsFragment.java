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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.auditdata.AuditDataActivity;
import org.openmrs.mobile.activities.capturevitals.CaptureVitalsActivity;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitFragment;
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

	private TextView visitStartDate, bedNumber, ward, visitType, noVitals, noPrimaryDiagnoses, noSecondaryDiagnoses,
			noAuditData, activeVisitBadge, startDuration, visitDuration, visitEndDate;
	private Visit visit;
	private TableLayout visitVitalsTableLayout, auditInfoTableLayout;
	private static TableRow.LayoutParams marginParams;
	private Button submitVisitNote;
	private TextInputEditText clinicalNote;
	private AutoCompleteTextView addDiagnosis;
	private RecyclerView primaryDiagnosesRecycler, secondaryDiagnosesRecycler;
	private LinearLayoutManager primaryDiagnosisLayoutManager, secondaryDiagnosisLayoutManager;
	private List<String> primaryDiagnoses;
	private ImageButton addAuditData, addVisitVitals;
	private String patientUuid;
	private String visitUuid;
	private String providerUuid;
	private Intent intent;

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
		addListeners();
		primaryDiagnosisLayoutManager = new LinearLayoutManager(this.getActivity());
		secondaryDiagnosisLayoutManager = new LinearLayoutManager(this.getActivity());

		primaryDiagnosesRecycler.setLayoutManager(primaryDiagnosisLayoutManager);
		secondaryDiagnosesRecycler.setLayoutManager(secondaryDiagnosisLayoutManager);

		((VisitDetailsPresenter)mPresenter).getVisit();
		((VisitDetailsPresenter)mPresenter).getPatientUUID();
		((VisitDetailsPresenter)mPresenter).getVisitUUID();
		((VisitDetailsPresenter)mPresenter).getProviderUUID();
		//buildMarginLayout();
		return root;
	}

	private void resolveViews(View v) {
		visitStartDate = (TextView)v.findViewById(R.id.visitStartDate);
		bedNumber = (TextView)v.findViewById(R.id.fetchedBedNumber);
		ward = (TextView)v.findViewById(R.id.fetchedWard);
		visitType = (TextView)v.findViewById(R.id.visitType);
		noVitals = (TextView)v.findViewById(R.id.noVitals);
		visitVitalsTableLayout = (TableLayout)v.findViewById(R.id.visitVitalsTable);
		auditInfoTableLayout = (TableLayout)v.findViewById(R.id.auditInfoTable);
		submitVisitNote = (Button)v.findViewById(R.id.submitVisitNote);
		clinicalNote = (TextInputEditText)v.findViewById(R.id.clinicalNotes);
		addDiagnosis = (AutoCompleteTextView)v.findViewById(R.id.diagnosisInput);
		noPrimaryDiagnoses = (TextView)v.findViewById(R.id.noPrimaryDiagnosis);
		noSecondaryDiagnoses = (TextView)v.findViewById(R.id.noSecondaryDiagnosis);
		noAuditData = (TextView)v.findViewById(R.id.noAuditInfo);
		primaryDiagnosesRecycler = (RecyclerView)v.findViewById(R.id.primaryDiagnosisRecyclerView);
		secondaryDiagnosesRecycler = (RecyclerView)v.findViewById(R.id.secondaryDiagnosisRecyclerView);
		activeVisitBadge = (TextView)v.findViewById(R.id.activeVisitBadge);
		visitEndDate = (TextView)v.findViewById(R.id.visitEndDate);
		visitDuration = (TextView)v.findViewById(R.id.visitDuration);
		startDuration = (TextView)v.findViewById(R.id.startDuration);
		addVisitVitals = (ImageButton)v.findViewById(R.id.visitVitalsAdd);
		addAuditData = (ImageButton)v.findViewById(R.id.visitAuditInfoAdd);
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
			setClinicalNote(visit);
			setDiagnoses(visit);
			setAuditData(visit);
		}

	}

	@Override
	public void setPatientUUID(String uuid) {
		this.patientUuid = uuid;
	}

	@Override
	public void setVisitUUID(String uuid) {
		this.visitUuid = uuid;
	}

	@Override
	public void setProviderUUID(String uuid) {
		this.providerUuid = uuid;
	}

	private void addListeners() {
		addAuditData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(getContext(), AuditDataActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				startActivity(intent);
			}
		});

		addVisitVitals.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(getContext(), CaptureVitalsActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				startActivity(intent);
			}
		});
	}

	public void setVisitDates(Visit visit) {
		if (StringUtils.notNull(visit.getStopDatetime())) {
			activeVisitBadge.setVisibility(View.GONE);
			visitStartDate.setText(getContext().getResources().getString(R.string.date_started) + ": " + DateUtils
					.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			visitEndDate
					.setText(getContext().getResources().getString(R.string.date_started) + ": " + DateUtils
							.convertTime1(visit.getStopDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			visitDuration.setText("Not now");
			startDuration.setText("Not now");

		} else {
			activeVisitBadge.setVisibility(View.VISIBLE);
			visitEndDate.setVisibility(View.GONE);
			visitDuration.setVisibility(View.GONE);
			visitStartDate.setText(
					DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			startDuration.setText("Not now");
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
						addVisitVitals.setVisibility(View.GONE);
						visitVitalsTableLayout.setVisibility(View.VISIBLE);
						loadObservationFields(visit.getEncounters().get(i).getObs(), EncounterTypeData.VITALS);
					} else {
						noVitals.setVisibility(View.VISIBLE);
						addVisitVitals.setVisibility(View.VISIBLE);
						visitVitalsTableLayout.setVisibility(View.GONE);
					}
					break;
				}
			}
		}
	}

	public void setAuditData(Visit visit) {
		if (visit.getEncounters().size() != 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				if (visit.getEncounters().get(i).getEncounterType().getUuid()
						.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.AUDIT_DATA) || visit.getEncounters()
						.get(i).getEncounterType().getDisplay().equalsIgnoreCase(ApplicationConstants
								.EncounterTypeDisplays.AUDITDATA)) {
					if (visit.getEncounters().get(i).getObs().size() != 0) {
						noAuditData.setVisibility(View.GONE);
						addAuditData.setVisibility(View.GONE);
						auditInfoTableLayout.setVisibility(View.VISIBLE);
						loadObservationFields(visit.getEncounters().get(i).getObs(), EncounterTypeData.AUDIT_DATA);
					} else {
						noAuditData.setVisibility(View.VISIBLE);
						addAuditData.setVisibility(View.VISIBLE);
						auditInfoTableLayout.setVisibility(View.GONE);
					}

				}
			}
		}
	}

	public void setClinicalNote(Visit visit) {
		if (visit.getEncounters().size() != 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				Encounter encounter = visit.getEncounters().get(i);
				EncounterType encounterType = visit.getEncounters().get(i).getEncounterType();

				if (encounterType.getUuid().equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.VISIT_NOTE_UUID)) {
					submitVisitNote.setText(getString(R.string.action_update));

					for (int v = 0; v < encounter.getObs().size(); v++) {

						ArrayList locators = splitStrings(encounter.getObs().get(v).getDisplay(), ":");

						if (locators.get(0).toString()
								.equalsIgnoreCase(ApplicationConstants.ObservationLocators.CLINICAL_NOTE)) {
							clinicalNote.setText(locators.get(1).toString());
						}
					}

				} else {

				}
			}
		}
	}

	public void setDiagnoses(Visit visit) {
		if (visit.getEncounters().size() != 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				Encounter encounter = visit.getEncounters().get(i);
				EncounterType encounterType = visit.getEncounters().get(i).getEncounterType();

				if (encounterType.getUuid().equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.VISIT_NOTE_UUID)) {
					submitVisitNote.setText(getString(R.string.update_visit_note));
					for (int v = 0; v < encounter.getObs().size(); v++) {
						ArrayList locators = splitStrings(encounter.getObs().get(v).getDisplay(), ":");

						if (locators.get(0).toString()
								.equalsIgnoreCase(ApplicationConstants.ObservationLocators.DIANOSES)) {
							ArrayList diagnosis = splitStrings(locators.get(1).toString(), ",");
							for (int d = 0; d < diagnosis.size(); d++) {
								String diagnosisString = diagnosis.get(d).toString();
								if (diagnosisString.equals(ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS)) {
									System.out.println("Found Primary");
								}
							}
						}
					}
				}
			}
		}
	}

	public void loadObservationFields(List<Observation> observations, EncounterTypeData type) {
		for (Observation observation : observations) {
			TableRow row = new TableRow(getContext());
			row.setPadding(0, 5, 0, 5);
			row.setGravity(Gravity.CENTER);
			row.setLayoutParams(
					new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			ArrayList splitValues = splitStrings(observation.getDisplay(), ":");

			TextView label = new TextView(getContext());
			label.setText(splitValues.get(0) + " :");
			label.setTextSize(14);
			if (type == EncounterTypeData.VITALS) {
				label.setGravity(Gravity.RIGHT | Gravity.END);
			} else {
				label.setGravity(Gravity.LEFT | Gravity.START);
			}
			label.setMaxLines(4);
			label.setSingleLine(false);

			label.setTextColor(getResources().getColor(R.color.black));
			row.addView(label, 0);

			TextView value = new TextView(getContext());
			value.setText(splitValues.get(1).toString());
			value.setTextSize(14);
			label.setMaxLines(4);
			label.setSingleLine(false);
			row.addView(value, 1);

			if (type == EncounterTypeData.VITALS) {
				visitVitalsTableLayout.addView(row);
			} else {
				auditInfoTableLayout.addView(row);
			}
		}
	}

	private ArrayList splitStrings(String display, String splitter) {
		ArrayList<String> displayArray = new ArrayList<>();
		Collections.addAll(displayArray, display.split(splitter));
		return displayArray;
	}
}
