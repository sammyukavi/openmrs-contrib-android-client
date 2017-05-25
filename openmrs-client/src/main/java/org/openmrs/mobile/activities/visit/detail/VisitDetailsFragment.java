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

import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisitDetailsFragment extends VisitFragment implements VisitContract.VisitDetailsView {

	private TextView visitDate, visitType, noVitals, noPrimaryDiagnoses, noSecondaryDiagnoses, noAuditData;
	private Visit visit;
	private TableLayout visitVitalsTableLayout, auditInfoTableLayout;
	private static TableRow.LayoutParams marginParams;
	private Button submitVisitNote;
	private TextInputEditText clinicalNote;
	private AutoCompleteTextView addDiagnosis;
	private RecyclerView primaryDiagnosesRecycler, secondaryDiagnosesRecycler;
	private LinearLayoutManager primaryDiagnosisLayoutManager, secondaryDiagnosisLayoutManager;
	private List<String> primaryDiagnoses;
	private FlexboxLayout visitAttributesLayout;

	private OpenMRS instance = OpenMRS.getInstance();
	private SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();

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
		primaryDiagnosisLayoutManager = new LinearLayoutManager(this.getActivity());
		secondaryDiagnosisLayoutManager = new LinearLayoutManager(this.getActivity());

		primaryDiagnosesRecycler.setLayoutManager(primaryDiagnosisLayoutManager);
		secondaryDiagnosesRecycler.setLayoutManager(secondaryDiagnosisLayoutManager);

		//buildMarginLayout();
		return root;
	}

	private void resolveViews(View v) {
		visitDate = (TextView)v.findViewById(R.id.visitDates);
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
		visitAttributesLayout = (FlexboxLayout) v.findViewById(R.id.visitAttributesLayout);
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
			setVitals(visit);
			setClinicalNote(visit);
			setDiagnoses(visit);
			setAuditData(visit);
		}
	}

	public void setVisitDates(Visit visit) {
		if (StringUtils.notNull(visit.getStopDatetime())) {
			visitDate.setText(getContext().getString(R.string.date_started) + ": " + DateUtils
					.convertTime1(visit.getStartDatetime(), DateUtils.DATE_FORMAT) + " - "
					+ getContext().getString(R.string.date_closed) + ": " + DateUtils.convertTime1(visit.getStopDatetime(),
					DateUtils.DATE_FORMAT));
			visitDate.setBackground(getResources().getDrawable(R.color.color_white));
		} else {
			visitDate.setText(getContext().getString(R.string.active_visit_label) + ": " + DateUtils.convertTime1
					(visit.getStartDatetime(), DateUtils.DATE_FORMAT) + " (started " + DateUtils.convertTime1
					(visit.getStartDatetime(), DateUtils.TIME_FORMAT) + ")");
			visitDate.setTextColor(getResources().getColor(R.color.color_white));
		}
	}

	public void setVisitType(Visit visit) {
		if (visit.getVisitType() != null) {
			visitType.setText(visit.getVisitType().getDisplay());
		} else {
			visitType.setText(ApplicationConstants.EMPTY_STRING);
		}
	}

	@Override
	public void setAttributeTypes(List<VisitAttributeType> visitAttributeTypes) {
		visitAttributesLayout.removeAllViews();
		if(null == visit.getAttributes()){
			return;
		}

		for(VisitAttribute visitAttribute : visit.getAttributes()){
			loadVisitAttributeType(visitAttribute, visitAttributeTypes);
			LinearLayout linearLayout = new LinearLayout(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			linearLayout.setLayoutParams(params);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);

			String valueLabel = String.valueOf(visitAttribute.getValue());
			TextView nameLabelView = new TextView(getContext());
			nameLabelView.setPadding(0, 10, 10, 10);
			nameLabelView.setText(visitAttribute.getAttributeType().getDisplay() + ":");
			linearLayout.addView(nameLabelView);

			TextView valueLabelView = new TextView(getContext());
			valueLabelView.setPadding(20, 10, 10, 10);

			if(null != visitAttribute.getAttributeType().getDatatypeConfig()){
				((VisitDetailsPresenter) mPresenter).getConceptName(
						visitAttribute.getAttributeType().getDatatypeConfig(),
						(String) visitAttribute.getValue(), valueLabelView);
			} else {
				valueLabelView.setText(valueLabel);
			}

			linearLayout.addView(valueLabelView);

			visitAttributesLayout.addView(linearLayout);
		}
	}

	private void loadVisitAttributeType(VisitAttribute visitAttribute, List<VisitAttributeType> attributeTypes){
		for(VisitAttributeType type : attributeTypes){
			if(type.getUuid().equalsIgnoreCase(visitAttribute.getAttributeType().getUuid())){
				visitAttribute.setAttributeType(type);
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
						loadObservationFields(visit.getEncounters().get(i).getObs(), EncounterDataType.VITALS);
					} else {
						noVitals.setVisibility(View.VISIBLE);
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
						auditInfoTableLayout.setVisibility(View.VISIBLE);
						loadObservationFields(visit.getEncounters().get(i).getObs(), EncounterDataType.AUDIT_DATA);
					} else {
						noAuditData.setVisibility(View.VISIBLE);
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

	public void loadObservationFields(List<Observation> observations, EncounterDataType type) {
		for (Observation observation : observations) {
			TableRow row = new TableRow(getContext());
			row.setPadding(0, 20, 0, 10);
			row.setGravity(Gravity.CENTER);
			row.setLayoutParams(
					new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			ArrayList splitValues = splitStrings(observation.getDisplay(), ":");

			TextView label = new TextView(getContext());
			label.setText(splitValues.get(0) + " :");
			label.setTextSize(14);
			if (type == EncounterDataType.VITALS) {
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

			if (type == EncounterDataType.VITALS) {
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
