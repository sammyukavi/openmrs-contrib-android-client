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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.visit.VisitActivity;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.ConceptSearchResult;
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
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
	private String providerUuid, visitStopDate, encounterUuid;
	private Intent intent;
	private List<EncounterDiagnosis> primaryDiagnosesList, secondaryDiagnosesList;
	private ConceptName diagnosisConceptName;
	private FlexboxLayout visitAttributesLayout;
	private RelativeLayout visitNoteAuditInfo, visitVitalsAuditInfo, auditDataMetadata, visitDetailsProgressBar;
	private View visitDetailsView;
	private ScrollView visitDetailsScrollView;
	private List<Concept> diagnosis;
	private Boolean changesMade;
	private int initialPrimaryDiagnosesListHashcode;
	private int initialSecondaryDiagnosesListHashcode;
	private int initialClinicNoteHashcode;
	private int clinicalNoteLength;

	private int subsequentPrimaryDiagnosesListHashcode;
	private int subsequentSecondaryDiagnosesListHashcode;
	private int subsequentClinicalNoteHashcode;

	static VisitContract.VisitDetailsMainPresenter staticPresenter;

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
		primaryDiagnosesList = new ArrayList<>();
		secondaryDiagnosesList = new ArrayList<>();
		diagnosis = new ArrayList<>();
		staticPresenter = mPresenter;
		//buildMarginLayout();
		changesMade = false;
		visitNoteWatcher();
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

	private void visitNoteWatcher() {
		clinicalNote.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				subsequentClinicalNoteHashcode = s.toString().hashCode();
				if (subsequentClinicalNoteHashcode != initialClinicNoteHashcode) {
					changesMade = true;
					submitVisitNote.setEnabled(true);
				} else {
					submitVisitNote.setEnabled(false);
				}
			}
		});
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

			addDiagnosisAdapter();
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

	private void addDiagnosisAdapter() {
		addDiagnosis.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (addDiagnosis.getText().length() >= 2) {
					((VisitDetailsPresenter)mPresenter).findConcept(addDiagnosis.getText().toString());
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void setRecyclerViews() {
		if (primaryDiagnosesList.size() <= 0) {
			primaryDiagnosesRecycler.setVisibility(View.GONE);
			noPrimaryDiagnoses.setVisibility(View.VISIBLE);
		} else {
			primaryDiagnosesRecycler.setVisibility(View.VISIBLE);
			noPrimaryDiagnoses.setVisibility(View.GONE);
		}

		if (secondaryDiagnosesList.size() <= 0) {
			secondaryDiagnosesRecycler.setVisibility(View.GONE);
			noSecondaryDiagnoses.setVisibility(View.VISIBLE);
		} else {
			secondaryDiagnosesRecycler.setVisibility(View.VISIBLE);
			noSecondaryDiagnoses.setVisibility(View.GONE);
		}

		if (initialPrimaryDiagnosesListHashcode != subsequentPrimaryDiagnosesListHashcode
				|| initialSecondaryDiagnosesListHashcode != subsequentSecondaryDiagnosesListHashcode) {
			submitVisitNote.setEnabled(true);
		} else {
			submitVisitNote.setEnabled(false);
		}

		DiagnosisRecyclerViewAdapter primaryDiagnosesAdapter =
				new DiagnosisRecyclerViewAdapter(this.getActivity(), primaryDiagnosesList, this);
		primaryDiagnosesRecycler.setAdapter(primaryDiagnosesAdapter);

		DiagnosisRecyclerViewAdapter secondaryDiagnosesAdapter =
				new DiagnosisRecyclerViewAdapter(this.getActivity(), secondaryDiagnosesList, this);
		secondaryDiagnosesRecycler.setAdapter(secondaryDiagnosesAdapter);
	}

	private void addListeners() {
		submitVisitNote.setEnabled(false);

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
			((VisitDetailsPresenter) mPresenter).saveVisitNote(createVisitNote(encounterUuid));
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

		addDiagnosis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (ViewUtils.getInput(addDiagnosis) != null) {
					ConceptSearchResult conceptSearchResult =
							(ConceptSearchResult)addDiagnosis.getAdapter().getItem(position);
					createEncounterDiagnosis(null, ViewUtils.getInput(addDiagnosis),
							conceptSearchResult.getValue());
				}
			}
		});
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
	public void setPrimaryDiagnosis(EncounterDiagnosis primaryDiagnosis) {
		for (int i = 0; i < secondaryDiagnosesList.size(); i++) {
			if (secondaryDiagnosesList.get(i) == primaryDiagnosis) {
				secondaryDiagnosesList.remove(i);
				primaryDiagnosesList.add(primaryDiagnosis);
			}
		}
		setRecyclerViews();
		subsequentPrimaryDiagnosesListHashcode = primaryDiagnosesList.hashCode();
	}

	@Override
	public void setSecondaryDiagnosis(EncounterDiagnosis secondaryDiagnosis) {
		for (int i = 0; i < primaryDiagnosesList.size(); i++) {
			if (primaryDiagnosesList.get(i) == secondaryDiagnosis) {
				primaryDiagnosesList.remove(i);
				secondaryDiagnosesList.add(secondaryDiagnosis);
			}
		}
		setRecyclerViews();
		subsequentSecondaryDiagnosesListHashcode = secondaryDiagnosesRecycler.hashCode();
	}

	@Override
	public void setDiagnosisCertainty(EncounterDiagnosis diagnosisCertainty) {
		if (diagnosisCertainty.getOrder().equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			for (int i = 0; i < primaryDiagnosesList.size(); i++) {
				if (primaryDiagnosesList.get(i) == diagnosisCertainty) {
					primaryDiagnosesList.remove(i);
					subsequentPrimaryDiagnosesListHashcode = primaryDiagnosesList.hashCode();
					primaryDiagnosesList.add(i, diagnosisCertainty);
				}
			}
		} else {
			for (int i = 0; i < secondaryDiagnosesList.size(); i++) {
				if (secondaryDiagnosesList.get(i) == diagnosisCertainty) {
					secondaryDiagnosesList.remove(i);
					subsequentSecondaryDiagnosesListHashcode = secondaryDiagnosesList.hashCode();
					secondaryDiagnosesList.add(i, diagnosisCertainty);
				}
			}
		}
		setRecyclerViews();
	}

	@Override
	public void removeDiagnosis(EncounterDiagnosis removeDiagnosis, String order) {
		if (order.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			for (int i = 0; i < primaryDiagnosesList.size(); i++) {
				if (primaryDiagnosesList.get(i) == removeDiagnosis) {
					primaryDiagnosesList.remove(i);
				}
			}
		} else {
			for (int i = 0; i < secondaryDiagnosesList.size(); i++) {
				if (secondaryDiagnosesList.get(i) == removeDiagnosis) {
					secondaryDiagnosesList.remove(i);
				}
			}
		}
		setRecyclerViews();
		subsequentPrimaryDiagnosesListHashcode = primaryDiagnosesList.hashCode();
		subsequentSecondaryDiagnosesListHashcode = secondaryDiagnosesList.hashCode();
	}

	@Override
	public void setDiagnoses(List<ConceptSearchResult> concepts) {
		ArrayAdapter<ConceptSearchResult> adapter =
				new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,
						concepts);
		addDiagnosis.setAdapter(adapter);
		addDiagnosis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (addDiagnosis.getText().length() >= 2) {
					addDiagnosis.showDropDown();
				}
				if (Arrays.asList(concepts)
						.contains(addDiagnosis.getText().toString())) {
					addDiagnosis.dismissDropDown();
				}
			}
		});
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

									ArrayList names = StringUtils.splitStrings(
											visit.getEncounters().get(i).getEncounterProviders().get(v).getDisplay(), ":");
									visitVitalsProvider.setText(names.get(0).toString());
								}
							}
							noVitals.setVisibility(View.GONE);
							addVisitVitals.setVisibility(View.GONE);
							visitVitalsTableLayout.setVisibility(View.VISIBLE);
							visitVitalsTableLayout.removeAllViews();
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

								ArrayList names = StringUtils.splitStrings(
										visit.getEncounters().get(i).getEncounterProviders().get(v).getDisplay(), ":");
								auditDataMetadataProvider.setText(names.get(0).toString());
							}
						}

						noAuditData.setVisibility(View.GONE);
						addAuditData.setVisibility(View.GONE);
						auditInfoTableLayout.setVisibility(View.VISIBLE);
						auditInfoTableLayout.removeAllViews();
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
					this.encounterUuid = encounter.getUuid();
					submitVisitNote.setText(getString(R.string.update_visit_note));

					for (int v = 0; v < encounter.getObs().size(); v++) {

						ArrayList locators = StringUtils.splitStrings(encounter.getObs().get(v).getDisplay(), ":");

						if (locators.get(0).toString()
								.equalsIgnoreCase(ApplicationConstants.ObservationLocators.CLINICAL_NOTE)) {
							initialClinicNoteHashcode = locators.get(1).toString().hashCode();
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
						createEncounterDiagnosis(obs, null, null);
					}
				}
			}
			setRecyclerViews();
		} else {
			showNoDiagnoses();
		}
		initialPrimaryDiagnosesListHashcode = primaryDiagnosesList.hashCode();
		initialSecondaryDiagnosesListHashcode = secondaryDiagnosesList.hashCode();
	}

	public void loadObservationFields(List<Observation> observations, EncounterTypeData type) {
		for (Observation observation : observations) {
			TableRow row = new TableRow(getContext());
			row.setPadding(0, 5, 0, 5);
			row.setGravity(Gravity.CENTER);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(params);

			ArrayList splitValues = StringUtils.splitStrings(observation.getDisplay(), ":");

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

	private EncounterDiagnosis createEncounterDiagnosis(Observation observation, String diagnosis, String value) {
		EncounterDiagnosis encounterDiagnosis = new EncounterDiagnosis();
		if (observation != null) {

			if (observation.getDisplay().startsWith(ApplicationConstants.ObservationLocators.DIAGNOSES)) {
				encounterDiagnosis.setCertainty(checkObsCertainty(observation.getDisplay()));
				diagnosis = "ConceptName:" + StringUtils.getConceptName(observation.getDisplay());
				encounterDiagnosis.setDisplay(StringUtils.getConceptName(observation.getDisplay()));
				encounterDiagnosis.setDiagnosis(diagnosis);

				if (observation.getDisplay().contains(ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS)) {
					encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER);
					primaryDiagnosesList.add(encounterDiagnosis);

				} else {
					encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER);
					secondaryDiagnosesList.add(encounterDiagnosis);
				}
				encounterDiagnosis.setExistingObs(observation.getUuid());
			}
		} else {
			encounterDiagnosis.setCertainty(ApplicationConstants.DiagnosisStrings.PRESUMED);
			encounterDiagnosis.setDisplay(diagnosis);
			encounterDiagnosis.setDiagnosis(value);
			encounterDiagnosis.setExistingObs(null);
			if (primaryDiagnosesList.size() <= 0) {
				encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER);
				primaryDiagnosesList.add(encounterDiagnosis);
			} else {
				encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER);
				secondaryDiagnosesList.add(encounterDiagnosis);
			}
			setRecyclerViews();
		}

		return encounterDiagnosis;
	}

	private VisitNote createVisitNote(String encounterUuid) {
		List<EncounterDiagnosis> encounterDiagnosises = new ArrayList<>();

		VisitNote visitNote = new VisitNote();
		visitNote.setPersonId(patientUuid);
		visitNote.setHtmlFormId("7");
		visitNote.setCreateVisit("false");
		visitNote.setFormModifiedTimestamp(String.valueOf(System.currentTimeMillis()));
		visitNote.setEncounterModifiedTimestamp("0");
		visitNote.setVisitId(visitUuid);
		visitNote.setReturnUrl("");
		visitNote.setCloseAfterSubmission("");
		visitNote.setEncounterId(encounterUuid == null ? ApplicationConstants.EMPTY_STRING : encounterUuid);
		visitNote.setW1(OpenMRS.getInstance().getCurrentUserUuid());
		visitNote.setW3(OpenMRS.getInstance().getParentLocationUuid());
		visitNote.setW5(DateUtils.getDateToday(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT));
		visitNote.setW10(ApplicationConstants.EMPTY_STRING);
		visitNote.setW12(ViewUtils.getInput(clinicalNote));

		encounterDiagnosises.addAll(primaryDiagnosesList);
		encounterDiagnosises.addAll(secondaryDiagnosesList);

		visitNote.setEncounterDiagnoses(encounterDiagnosises);

		return visitNote;
	}

	public static void refreshVitalsDetails() {
		((VisitDetailsPresenter)staticPresenter).getVisit();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (changesMade || (initialPrimaryDiagnosesListHashcode != subsequentPrimaryDiagnosesListHashcode) ||
				(initialSecondaryDiagnosesListHashcode != subsequentSecondaryDiagnosesListHashcode)) {
			showPendingVisitNoteCahngesDialog();
		}
	}

	private void showPendingVisitNoteCahngesDialog() {
		CustomDialogBundle bundle = new CustomDialogBundle();
		bundle.setTitleViewMessage(getString(R.string.visit_note_changes_pending_title));
		bundle.setTextViewMessage(getString(R.string.visit_note_changes_pending_message));
		bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.END_VISIT);
		bundle.setRightButtonText(getString(R.string.dialog_button_confirm));
		((VisitActivity)this.getActivity())
				.createAndShowDialog(bundle, ApplicationConstants.DialogTAG.PENDING_VISIT_NOTE_CHANGES_TAG);
	}
}
