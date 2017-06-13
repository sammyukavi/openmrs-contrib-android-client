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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.auditdata.AuditDataActivity;
import org.openmrs.mobile.activities.capturevitals.CaptureVitalsActivity;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitFragment;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterDiagnosis;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VisitDetailsFragment extends VisitFragment implements VisitContract.VisitDetailsView {

	private TextView visitStartDate;
	private TextView activeVisitBadge;
	private TextView startDuration;
	private TextView visitDuration;
	private TextView visitEndDate;
	private TextView visitType, noVitals, noPrimaryDiagnoses, noSecondaryDiagnoses, noAuditData, visitNoteProvider,
			visitNoteDate, visitVitalsProvider, visitVitalsDate, auditDataCompleteness, auditDataMetadataProvider,
			auditDataMetadataDate;

	private Visit visit;
	private TableLayout visitVitalsTableLayout, auditInfoTableLayout;
	private static TableRow.LayoutParams marginParams;
	private AppCompatButton submitVisitNote;
	private TextInputEditText clinicalNote;
	private AutoCompleteTextView addDiagnosis;
	private RecyclerView primaryDiagnosesRecycler, secondaryDiagnosesRecycler;
	private LinearLayoutManager primaryDiagnosisLayoutManager, secondaryDiagnosisLayoutManager;
	private List<String> primaryDiagnoses;
	private ImageButton addAuditData, addVisitVitals;
	private String patientUuid;
	private String visitUuid;
	private String providerUuid, visitStopDate;
	private Intent intent;
	private List<HashMap<String, Object>> primaryDiagnosisList, secondaryDiagnosisList;
	private ConceptName diagnosisConceptName;
	private FlexboxLayout visitAttributesLayout;
	private RelativeLayout visitNoteAuditInfo, visitVitalsAuditInfo, auditDataMetadata, visitDetailsProgressBar;
	private View visitDetailsView;
	private ScrollView visitDetailsScrollView;

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
		primaryDiagnosisList = new ArrayList<>();
		secondaryDiagnosisList = new ArrayList<>();
		//buildMarginLayout();
		return root;
	}

	private void resolveViews(View v) {
		visitStartDate = (TextView)v.findViewById(R.id.visitStartDate);
		visitType = (TextView)v.findViewById(R.id.visitType);
		noVitals = (TextView)v.findViewById(R.id.noVitals);
		visitVitalsTableLayout = (TableLayout)v.findViewById(R.id.visitVitalsTable);
		auditInfoTableLayout = (TableLayout)v.findViewById(R.id.auditInfoTable);
		submitVisitNote = (AppCompatButton)v.findViewById(R.id.submitVisitNote);
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
		visitAttributesLayout = (FlexboxLayout)v.findViewById(R.id.visitAttributesLayout);
		visitNoteProvider = (TextView)v.findViewById(R.id.visitNoteProvider);
		visitNoteDate = (TextView)v.findViewById(R.id.visitNoteDate);
		visitVitalsDate = (TextView)v.findViewById(R.id.visitVitalsDate);
		visitVitalsProvider = (TextView)v.findViewById(R.id.visitVitalsProvider);
		auditDataCompleteness = (TextView)v.findViewById(R.id.auditDataCompleteness);
		auditDataMetadataProvider = (TextView)v.findViewById(R.id.auditDataMetadataProvider);
		auditDataMetadataDate = (TextView)v.findViewById(R.id.auditDataMetadataDate);

		visitNoteAuditInfo = (RelativeLayout)v.findViewById(R.id.visitNoteAuditInfo);
		visitVitalsAuditInfo = (RelativeLayout)v.findViewById(R.id.visitVitalsAuditInfo);
		auditDataMetadata = (RelativeLayout)v.findViewById(R.id.auditDataMetadata);

		visitDetailsView = v.findViewById(R.id.visitDetailsView);

		visitDetailsProgressBar = (RelativeLayout)v.findViewById(R.id.visitDetailsTabProgressBar);
		visitDetailsScrollView = (ScrollView)v.findViewById(R.id.visitDetailsTab);

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

	@Override
	public void setVisitStopDate(String visitStopDate) {
		this.visitStopDate = visitStopDate;
	}

	@Override
	public void setConcept(Concept concept) {
		for (ConceptName conceptName : concept.getNames()) {

			if (conceptName.getLocale() == Locale.ENGLISH) {
				System.out.println(conceptName.getName() + " Concept Name ");
				//this.diagnosisConceptName = conceptName;
			}
		}
	}

	private void setRecyclerViews() {
		if (primaryDiagnosisList != null) {
			DiagnosisRecyclerViewAdapter adapter =
					new DiagnosisRecyclerViewAdapter(this.getActivity(), primaryDiagnosisList, this);
			primaryDiagnosesRecycler.setAdapter(adapter);
			primaryDiagnosesRecycler.setVisibility(View.VISIBLE);
			System.out.println("I am here primary ");
		}

		if (secondaryDiagnosisList != null) {
			DiagnosisRecyclerViewAdapter adapter =
					new DiagnosisRecyclerViewAdapter(this.getActivity(), secondaryDiagnosisList, this);
			secondaryDiagnosesRecycler.setAdapter(adapter);
			secondaryDiagnosesRecycler.setVisibility(View.VISIBLE);
			System.out.println("I am here secondary ");
		}
	}

	private void addListeners() {
		addAuditData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(getContext(), AuditDataActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
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
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
				startActivity(intent);
			}
		});

		submitVisitNote.setOnClickListener(v -> {
			VisitNote visitNote = new VisitNote();
			visitNote.setPersonId("10527");
			visitNote.setHtmlFormId("7");
			visitNote.setCreateVisit("false");
			visitNote.setFormModifiedTimestamp(String.valueOf(System.currentTimeMillis()));
			visitNote.setEncounterModifiedTimestamp("0");
			visitNote.setVisitId("13417");
			visitNote.setReturnUrl("");
			visitNote.setCloseAfterSubmission("");
			visitNote.setEncounterId("7716");
			visitNote.setW1("21228");
			visitNote.setW3("4");
			visitNote.setW5("2017-06-08");
			visitNote.setW10("complaint: fever4");
			visitNote.setW12("Note section.....4");

			EncounterDiagnosis encounterDiagnosis = new EncounterDiagnosis();
			encounterDiagnosis.setCertainty("PRESUMED");
			encounterDiagnosis.setOrder("PRIMARY");
			encounterDiagnosis.setDiagnosis("ConceptName:16603");

			visitNote.addEncounterDiagnosis(encounterDiagnosis);

			((VisitDetailsPresenter)mPresenter).saveVisitNote(visitNote);
		});

		auditDataCompleteness.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(getContext(), AuditDataActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
				startActivity(intent);
			}
		});
	}

	public void getDiagnosisOnFocusListener() {
		/*ArrayAdapter adapter =
				new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, ((VisitDetailsPresenter)
						mPresenter).getConcept());
		addDiagnosis.setAdapter(adapter);

		addDiagnosis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (addDiagnosis.getText().length() >= addDiagnosis.getThreshold()) {
					addDiagnosis.showDropDown();
				}
				if (Arrays.asList(removeUsedPredefinedTasks(predefinedTasks, visitTasksLists))
						.contains(addDiagnosis.getText().toString())) {
					addDiagnosis.dismissDropDown();
				}
			}
		});*/
	}

	public void setVisitDates(Visit visit) {
		if (StringUtils.notNull(visit.getStopDatetime())) {
			activeVisitBadge.setVisibility(View.GONE);
			visitStartDate.setText(DateUtils
					.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			visitEndDate
					.setText(getContext().getResources().getString(R.string.date_closed) + ": " + DateUtils
							.convertTime1(visit.getStopDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			startDuration.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime()));
			visitDuration.setText(getContext().getString(R.string.visit_duration, DateUtils.calculateTimeDifference(visit
					.getStartDatetime(), visit.getStopDatetime())));

		} else {
			activeVisitBadge.setVisibility(View.VISIBLE);
			visitEndDate.setVisibility(View.GONE);
			visitDuration.setVisibility(View.GONE);
			visitStartDate.setText(
					DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			startDuration.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime()));
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
		if (visit.getAttributes().size() == 0) {
			for (VisitAttributeType visitAttributeType : visitAttributeTypes) {
				createVisitAttributeTypesLayout(visitAttributeType);
			}
		} else {
			for (VisitAttribute visitAttribute : visit.getAttributes()) {
				loadVisitAttributeType(visitAttribute, visitAttributeTypes);
				createVisitAttributeLayout(visitAttribute);
			}
		}

	}

	@Override
	public void showTabSpinner(boolean visibility) {
		if (visibility) {
			visitDetailsProgressBar.setVisibility(View.VISIBLE);
			visitDetailsScrollView.setVisibility(View.GONE);
		} else {
			visitDetailsProgressBar.setVisibility(View.GONE);
			visitDetailsScrollView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void setPrimaryDiagnosis(HashMap<String, Object> primaryDiagnosis) {
		for (int i = 0; i < secondaryDiagnosisList.size(); i++) {
			if (secondaryDiagnosisList.get(i) == primaryDiagnosis) {
				secondaryDiagnosisList.remove(i);
				primaryDiagnosisList.add(primaryDiagnosis);
			}
		}
		setRecyclerViews();
	}

	@Override
	public void setSecondaryDiagnosis(HashMap<String, Object> secondaryDiagnosis) {
		for (int i = 0; i < primaryDiagnosisList.size(); i++) {
			if (primaryDiagnosisList.get(i) == secondaryDiagnosis) {
				primaryDiagnosisList.remove(i);
				secondaryDiagnosisList.add(secondaryDiagnosis);
			}
		}
		setRecyclerViews();
	}

	@Override
	public void setDiagnosisCertainty(HashMap<String, Object> diagnosisCertainty, String order) {
		if (order.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			for (int i = 0; i < primaryDiagnosisList.size(); i++) {
				if (primaryDiagnosisList.get(i) == diagnosisCertainty) {
					primaryDiagnosisList.remove(i);
					primaryDiagnosisList.add(i, diagnosisCertainty);
				}
			}
		} else {
			for (int i = 0; i < secondaryDiagnosisList.size(); i++) {
				if (secondaryDiagnosisList.get(i) == diagnosisCertainty) {
					secondaryDiagnosisList.remove(i);
					secondaryDiagnosisList.add(i, diagnosisCertainty);
				}
			}
		}
		setRecyclerViews();
	}

	@Override
	public void removeDiagnosis(HashMap<String, Object> removeDiagnosis, String order) {
		if (order.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			for (int i = 0; i < primaryDiagnosisList.size(); i++) {
				if (primaryDiagnosisList.get(i) == removeDiagnosis) {
					primaryDiagnosisList.remove(i);
				}
			}
		} else {
			for (int i = 0; i < secondaryDiagnosisList.size(); i++) {
				if (secondaryDiagnosisList.get(i) == removeDiagnosis) {
					secondaryDiagnosisList.remove(i);
				}
			}
		}
		setRecyclerViews();
	}

	private void createVisitAttributeTypesLayout(VisitAttributeType visitAttributeType) {
		LinearLayout linearLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linearLayout.setLayoutParams(params);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		String valueLabel = String.valueOf(ApplicationConstants.EMPTY_STRING);
		TextView nameLabelView = new TextView(getContext());
		nameLabelView.setPadding(0, 10, 10, 10);
		nameLabelView.setText(visitAttributeType.getDisplay() + ":");
		linearLayout.addView(nameLabelView);

		TextView valueLabelView = new TextView(getContext());
		valueLabelView.setPadding(20, 10, 10, 10);
		valueLabelView.setText(valueLabel);

		linearLayout.addView(valueLabelView);

		visitAttributesLayout.addView(linearLayout);
	}

	private void createVisitAttributeLayout(VisitAttribute visitAttribute) {
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

		if (null != visitAttribute.getAttributeType().getDatatypeConfig()) {
			((VisitDetailsPresenter)mPresenter).getConceptAnswer(visitAttribute.getAttributeType().getDatatypeConfig(),
					(String)visitAttribute.getValue(), valueLabelView);
		} else {
			valueLabelView.setText(valueLabel);
		}

		linearLayout.addView(valueLabelView);

		visitAttributesLayout.addView(linearLayout);
	}

	private void loadVisitAttributeType(VisitAttribute visitAttribute, List<VisitAttributeType> attributeTypes) {
		for (VisitAttributeType type : attributeTypes) {
			if (type.getUuid().equalsIgnoreCase(visitAttribute.getAttributeType().getUuid())) {
				visitAttribute.setAttributeType(type);
			}
		}
	}

	public void setVitals(Visit visit) {
		if (visit.getEncounters().size() > 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				if (visit.getEncounters().get(i).getDisplay().contains(ApplicationConstants.EncounterTypeDisplays.VITALS)
						&& i == 0) {
					if (visit.getEncounters().get(i).getEncounterType().getUuid()
							.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.VITALS_UUID)) {

						if (visit.getEncounters().get(i).getObs().size() != 0) {
							visitVitalsAuditInfo.setVisibility(View.VISIBLE);
							visitVitalsDate
									.setText(DateUtils.convertTime(visit.getEncounters().get(i).getEncounterDatetime(),
											DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));

							for (int v = 0; v < visit.getEncounters().get(i).getEncounterProviders().size(); v++) {
								if (v == 0) {

									ArrayList names = splitStrings(
											visit.getEncounters().get(i).getEncounterProviders().get(v).getDisplay(), ":");
									visitVitalsProvider.setText(names.get(0).toString());
								}
							}
							noVitals.setVisibility(View.GONE);
							addVisitVitals.setVisibility(View.GONE);
							visitVitalsTableLayout.setVisibility(View.VISIBLE);
							loadObservationFields(visit.getEncounters().get(i).getObs(), EncounterTypeData.VITALS);
						} else {
							if (visit.getStopDatetime() == null) {
								noVitals.setVisibility(View.VISIBLE);
								addVisitVitals.setVisibility(View.VISIBLE);
								visitVitalsTableLayout.setVisibility(View.GONE);
							}
						}
					}
				}
			}
		} else {
			addVisitVitals.setVisibility(visit.getStopDatetime() == null ? View.VISIBLE : View.GONE);
		}
	}

	public void setAuditData(Visit visit) {
		if (visit.getEncounters().size() > 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				if (visit.getEncounters().get(i).getEncounterType().getUuid()
						.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.AUDIT_DATA_UUID) || visit.getEncounters()
						.get(i).getEncounterType().getDisplay().equalsIgnoreCase(ApplicationConstants
								.EncounterTypeDisplays.AUDITDATA)) {

					if (visit.getEncounters().get(i).getObs().size() != 0) {

						auditDataMetadata.setVisibility(View.VISIBLE);
						auditDataMetadataDate
								.setText(DateUtils.convertTime(visit.getEncounters().get(i).getEncounterDatetime(),
										DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));

						for (int v = 0; v < visit.getEncounters().get(i).getEncounterProviders().size(); v++) {
							if (v == 0) {

								ArrayList names = splitStrings(
										visit.getEncounters().get(i).getEncounterProviders().get(v).getDisplay(), ":");
								auditDataMetadataProvider.setText(names.get(0).toString());
							}
						}

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

				if (encounterType.getUuid().equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.CLINICAL_NOTE_UUID)) {
					submitVisitNote.setText(getString(R.string.update_visit_note));

					for (int v = 0; v < encounter.getObs().size(); v++) {

						ArrayList locators = splitStrings(encounter.getObs().get(v).getDisplay(), ":");

						if (locators.get(0).toString()
								.equalsIgnoreCase(ApplicationConstants.ObservationLocators.CLINICAL_NOTE)) {
							clinicalNote.setText(locators.get(1).toString());
						}
					}

				}
			}
		}
	}

	public void setDiagnoses(Visit visit) {
		if (visit.getEncounters().size() != 0) {
			for (Encounter encounter : visit.getEncounters()) {
				if (encounter.getEncounterType().getUuid()
						.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.CLINICAL_NOTE_UUID)) {
					submitVisitNote.setText(getString(R.string.update_visit_note));
					for (Observation obs : encounter.getObs()) {
						HashMap<String, Object> encounterDiagnosis = new HashMap<>();
						if (obs.getDisplay().startsWith(ApplicationConstants.ObservationLocators.DIAGNOSES)) {
							encounterDiagnosis.put("certainty", checkObsCertainty(obs.getDisplay()));
							encounterDiagnosis.put("diagnosis", "Demo");
							if (obs.getDisplay().contains(ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS)) {
								encounterDiagnosis.put("order", ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER);
								primaryDiagnosisList.add(encounterDiagnosis);

							} else {
								encounterDiagnosis.put("order", ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER);
								secondaryDiagnosisList.add(encounterDiagnosis);
							}

						}
					}
				}
			}

		} else {
			showNoDiagnoses();
		}
		setRecyclerViews();
	}

	public void loadObservationFields(List<Observation> observations, EncounterTypeData type) {
		for (Observation observation : observations) {
			TableRow row = new TableRow(getContext());
			row.setPadding(0, 5, 0, 5);
			row.setGravity(Gravity.CENTER);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(params);

			ArrayList splitValues = splitStrings(observation.getDisplay(), ":");

			TextView label = new TextView(getContext());
			TableRow.LayoutParams labelParams = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
			labelParams.weight = 1;

			label.setTextSize(14);
			if (type == EncounterTypeData.VITALS) {
				label.setText(splitValues.get(0) + " :");
				label.setGravity(Gravity.RIGHT | Gravity.END);
			} else {
				label.setText(splitValues.get(0).toString());
				label.setGravity(Gravity.LEFT | Gravity.START);
			}
			label.setLayoutParams(labelParams);
			label.setTextColor(getResources().getColor(R.color.black));
			row.addView(label, 0);

			TextView value = new TextView(getContext());
			value.setTextSize(14);
			if (type == EncounterTypeData.VITALS) {
				value.setText(splitValues.get(1).toString());
			} else {
				value.setText(": " + splitValues.get(1).toString());
			}
			value.setLayoutParams(labelParams);
			row.addView(value, 1);

			if (type == EncounterTypeData.VITALS) {
				visitVitalsTableLayout.addView(row);
			} else {
				if (observation.getDisplay().contains(ApplicationConstants.EncounterTypeDisplays.AUDIT_DATA_COMPLETENESS)
						&& observation.getDisplay().contains("No")) {
					auditDataCompleteness.setVisibility(View.VISIBLE);
				}
				auditInfoTableLayout.addView(row);
			}
		}
	}

	private ArrayList splitStrings(String display, String splitter) {
		ArrayList<String> displayArray = new ArrayList<>();
		Collections.addAll(displayArray, display.split(splitter));
		return displayArray;
	}

	private void showNoDiagnoses() {
		noPrimaryDiagnoses.setVisibility(View.VISIBLE);
		noSecondaryDiagnoses.setVisibility(View.VISIBLE);
		primaryDiagnosesRecycler.setVisibility(View.GONE);
		secondaryDiagnosesRecycler.setVisibility(View.GONE);
	}

	private String checkObsCertainty(String obsDisplay) {
		if (obsDisplay.contains(ApplicationConstants.ObservationLocators
				.PRESUMED_DIAGNOSIS)) {
			return ApplicationConstants.DiagnosisStrings.PRESUMED;
		} else {
			return ApplicationConstants.DiagnosisStrings.CONFIRMED;
		}
	}

}
