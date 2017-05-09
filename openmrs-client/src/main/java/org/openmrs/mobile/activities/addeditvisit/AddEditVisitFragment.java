/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.addeditvisit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.visitphoto.upload.UploadVisitPhotoActivity;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ViewUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditVisitFragment extends ACBaseFragment<AddEditVisitContract.Presenter> implements AddEditVisitContract.View{

    private TextView visitTitle;
    private TableLayout visitTableLayout;
    private ProgressBar progressBar;
    private TextView confirmMessage;
    private Spinner visitTypeDropdown;
    private Button visitSubmitButton;
    private ImageView capturePhoto;
    private Map<String, VisitAttribute> visitAttributeMap = new HashMap<>();
    private Map<View, VisitAttributeType> viewVisitAttributeTypeMap = new HashMap<>();
    private static TableRow.LayoutParams marginParams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addedit_visit, container, false);
        visitTitle = (TextView) root.findViewById(R.id.visitTitle);
        capturePhoto = (ImageView) root.findViewById(R.id.capture_photo);
        visitTableLayout = (TableLayout) root.findViewById(R.id.visitTableLayout);
        progressBar = (ProgressBar) root.findViewById(R.id.visitLoadingProgressBar);
        confirmMessage = (TextView) root.findViewById(R.id.confirmMessage);
        visitTypeDropdown = (Spinner) root.findViewById(R.id.visit_type);
        visitSubmitButton = (Button) root.findViewById(R.id.visitSubmitButton);
        addListeners();
        buildMarginLayout();

        return root;
    }

    private void addListeners(){
        visitSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPresenter.isProcessing()) {
                    buildVisitAttributeValues();
                }
            }
        });

        capturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadVisitPhotoActivity.class);
                intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE,
                       mPresenter.getPatient().getUuid());
                intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID_BUNDLE,
                       mPresenter.getVisit().getUuid());
                intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE,
                       mPresenter.getProvider().getUuid());
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void initView(boolean startVisit) {
        if(startVisit && mPresenter.getPatient() != null){
          confirmMessage.setText(getString(R.string.start_visit_dialog_message,
                 mPresenter.getPatient().getPerson().getName().getNameString()));
        } else {
            confirmMessage.setVisibility(View.GONE);
        }

        setVisitTitleText(startVisit ? getString(R.string.label_start_visit) : getString(R.string.label_edit_visit));
        setSpinnerVisibility(false);
    }

    private void buildVisitAttributeValues(){
        for(Map.Entry<View, VisitAttributeType> set : viewVisitAttributeTypeMap.entrySet()){
            View componentType = set.getKey();
            VisitAttribute visitAttribute = new VisitAttribute();
            visitAttribute.setAttributeType(set.getValue());

            if(componentType instanceof RadioButton){
                visitAttribute.setValue(((RadioButton) componentType).isChecked());
            } else if(componentType instanceof EditText){
                visitAttribute.setValue(ViewUtils.getInput((EditText) componentType));
            }

            if(visitAttribute.getValue() != null) {
                visitAttributeMap.put(set.getValue().getUuid(), visitAttribute);
            }
        }

        if(!mPresenter.isProcessing()) {
            setSpinnerVisibility(true);
            if(StringUtils.notEmpty(mPresenter.getVisit().getUuid())){
                mPresenter.updateVisit(new ArrayList<>(visitAttributeMap.values()));
            } else {
                mPresenter.startVisit(new ArrayList<>(visitAttributeMap.values()));
            }
        }
    }

    @Override
    public void loadVisitAttributeTypeFields(List<VisitAttributeType> visitAttributeTypes) {
        for (VisitAttributeType visitAttributeType : visitAttributeTypes) {
            TableRow row = new TableRow(getContext());
            row.setPadding(0, 20, 0, 10);
            TextView label = new TextView(getContext());
            label.setText(visitAttributeType.getDisplay() + ":");
            label.setTextSize(17);
            label.setTextColor(getResources().getColor(R.color.dark_grey));
            row.addView(label, 0);

            String datatypeClass = visitAttributeType.getDatatypeClassname();
            if (StringUtils.isBlank(datatypeClass)) {
                continue;
            }

            if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.BooleanDatatype")) {
                RadioButton booleanType = new RadioButton(getContext());
                booleanType.setLayoutParams(marginParams);

                // set default value
                Boolean defaultValue = mPresenter.searchVisitAttributeValueByType(visitAttributeType);
                if(defaultValue != null){
                    booleanType.setChecked(defaultValue);
                }

                row.addView(booleanType, 1);
                viewVisitAttributeTypeMap.put(booleanType, visitAttributeType);
            } else if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.DateDatatype")) {
                EditText dateType = new EditText(getContext());
                dateType.setFocusable(true);
                dateType.setTextSize(17);
                dateType.setLayoutParams(marginParams);

                // set default value
                String defaultValue = mPresenter.searchVisitAttributeValueByType(visitAttributeType);
                if(StringUtils.notEmpty(defaultValue)){
                    dateType.setText(defaultValue);
                }
                row.addView(dateType, 1);
                viewVisitAttributeTypeMap.put(dateType, visitAttributeType);
            } else if (datatypeClass.equalsIgnoreCase("org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype")) {
                // get coded concept uuid
                String conceptUuid = visitAttributeType.getDatatypeConfig();
                Spinner conceptAnswersDropdown = new Spinner(getContext());
                conceptAnswersDropdown.setLayoutParams(marginParams);
                mPresenter.getConceptNames(conceptUuid, conceptAnswersDropdown);
                row.addView(conceptAnswersDropdown, 1);
                viewVisitAttributeTypeMap.put(conceptAnswersDropdown, visitAttributeType);
            } else if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.FreeTextDatatype")) {
                EditText freeTextType = new EditText(getContext());
                freeTextType.setFocusable(true);
                freeTextType.setTextSize(17);
                freeTextType.setLayoutParams(marginParams);

                // set default value
                String defaultValue = mPresenter.searchVisitAttributeValueByType(visitAttributeType);
                if(StringUtils.notEmpty(defaultValue)){
                    freeTextType.setText(defaultValue);
                }

                row.addView(freeTextType, 1);
                viewVisitAttributeTypeMap.put(freeTextType, visitAttributeType);
            }

            visitTableLayout.addView(row);
        }
    }

    @Override
    public void updateConceptNamesView(Spinner conceptNamesDropdown, List<ConceptName> conceptNames){
        VisitAttributeType visitAttributeType = viewVisitAttributeTypeMap.get(conceptNamesDropdown);
        ArrayAdapter<ConceptName> conceptNameArrayAdapter = new ArrayAdapter<ConceptName>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, conceptNames);
        conceptNamesDropdown.setAdapter(conceptNameArrayAdapter);

        // set existing visit attribute if any
        String visitTypeUuid = mPresenter.searchVisitAttributeValueByType(visitAttributeType);
        if(null != visitTypeUuid){
            setDefaultDropdownSelection(conceptNameArrayAdapter, visitTypeUuid, conceptNamesDropdown);
        }

        conceptNamesDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ConceptName conceptName = conceptNames.get(position);
                VisitAttribute visitAttribute = new VisitAttribute();
                visitAttribute.setValue(conceptName.getUuid());
                visitAttribute.setAttributeType(visitAttributeType);
                visitAttributeMap.clear();
                visitAttributeMap.put(conceptName.getUuid(), visitAttribute);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void updateVisitTypes(List<VisitType> visitTypes) {
        ArrayAdapter<VisitType> visitTypeArrayAdapter = new ArrayAdapter(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, visitTypes);
        visitTypeDropdown.setAdapter(visitTypeArrayAdapter);

        // set existing visit type if any
        if(null != mPresenter.getVisit().getVisitType()){
            setDefaultDropdownSelection(visitTypeArrayAdapter, mPresenter.getVisit().getVisitType().getUuid(), visitTypeDropdown);
        }

        visitTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.getVisit().setVisitType(visitTypes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void setSpinnerVisibility(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static AddEditVisitFragment newInstance(){
        return new AddEditVisitFragment();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setVisitTitleText(String text) {
        visitTitle.setText(text);
    }

    @Override
    public void showPatientDashboard() {
        Intent intent = new Intent(getContext(), PatientDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE,
                mPresenter.getPatient().getUuid());
        getContext().startActivity(intent);
    }

    private void buildMarginLayout(){
        if(marginParams == null) {
            marginParams = new TableRow.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(70, 0, 0, 0);
        }
    }

    private <T extends BaseOpenmrsObject> void setDefaultDropdownSelection(ArrayAdapter<T> arrayAdapter, String searchUuid, Spinner dropdown){
        for(int count = 0; count < arrayAdapter.getCount(); count++){
            if(arrayAdapter.getItem(count).getUuid().equalsIgnoreCase(searchUuid)){
                dropdown.setSelection(count);
            }
        }
    }
}
