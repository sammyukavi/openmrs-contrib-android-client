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

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.openmrs.mobile.utilities.ViewUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.ConceptAnswer;
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
    private VisitTypeArrayAdapter visitTypeArrayAdapter;
    private Spinner visitTypeDropdown;
    private Button visitSubmitButton;
    private Map<String, VisitAttribute> visitAttributeMap = new HashMap<>();
    private Map<View, VisitAttributeType> viewVisitAttributeTypeMap = new HashMap<>();
    private static TableRow.LayoutParams marginParams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addedit_visit, container, false);
        visitTitle = (TextView) root.findViewById(R.id.visitTitle);
        visitTableLayout = (TableLayout) root.findViewById(R.id.visitTableLayout);
        progressBar = (ProgressBar) root.findViewById(R.id.visitLoadingProgressBar);
        confirmMessage = (TextView) root.findViewById(R.id.confirmMessage);
        visitTypeDropdown = (Spinner) root.findViewById(R.id.visit_type);
        visitSubmitButton = (Button) root.findViewById(R.id.visitSubmitButton);
        visitSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPresenter.isProcessing()) {
                    buildVisitParameters();
                }
            }
        });

        buildMarginLayout();

        return root;
    }

    @Override
    public void initView(boolean startVisit) {
        if(startVisit && mPresenter.getPatient() != null){
          confirmMessage.setText(getString(R.string.start_visit_dialog_message,
                 mPresenter.getPatient().getPerson().getName().getNameString()));
        }

        setVisitTitleText(startVisit ? getString(R.string.start_visit_title) : getString(R.string.edit_visit_title));

        setSpinnerVisibility(false);
    }

    private void buildVisitParameters(){
        for(Map.Entry<View, VisitAttributeType> set : viewVisitAttributeTypeMap.entrySet()){
            View componentType = set.getKey();
            VisitAttribute visitAttribute = new VisitAttribute();
            visitAttribute.setAttributeType(set.getValue());

            if(componentType instanceof RadioButton){
                visitAttribute.setValueReference(((RadioButton) componentType).getText().toString());
            } else if(componentType instanceof EditText){
                visitAttribute.setValueReference(ViewUtils.getInput((EditText) componentType));
            }

            if(visitAttribute.getValueReference() != null) {
                visitAttributeMap.put(set.getValue().getUuid(), visitAttribute);
            }
        }

        if(!mPresenter.isProcessing()) {
            setSpinnerVisibility(true);
            if(StringUtils.notEmpty(mPresenter.getVisit().getUuid())){
                mPresenter.updateVisit();
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
            if (StringUtils.notEmpty(datatypeClass)) {
                if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.BooleanDatatype")) {
                    RadioButton booleanType = new RadioButton(getContext());
                    booleanType.setLayoutParams(marginParams);
                    row.addView(booleanType, 1);
                    viewVisitAttributeTypeMap.put(booleanType, visitAttributeType);
                } else if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.DateDatatype")) {
                    EditText dateType = new EditText(getContext());
                    dateType.setFocusable(true);
                    dateType.setTextSize(17);
                    dateType.setLayoutParams(marginParams);
                    row.addView(dateType, 1);
                    viewVisitAttributeTypeMap.put(dateType, visitAttributeType);
                } else if (datatypeClass.equalsIgnoreCase("org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype")) {
                    // get coded concept uuid
                    String conceptUuid = visitAttributeType.getDatatypeConfig();
                    Spinner conceptAnswersDropdown = new Spinner(getContext());
                    conceptAnswersDropdown.setLayoutParams(marginParams);
                    mPresenter.getConceptAnswers(conceptUuid, conceptAnswersDropdown);
                    row.addView(conceptAnswersDropdown, 1);
                    viewVisitAttributeTypeMap.put(conceptAnswersDropdown, visitAttributeType);
                } else if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.FreeTextDatatype")) {
                    EditText freeTextType = new EditText(getContext());
                    freeTextType.setFocusable(true);
                    freeTextType.setTextSize(17);
                    freeTextType.setLayoutParams(marginParams);
                    row.addView(freeTextType, 1);
                    viewVisitAttributeTypeMap.put(freeTextType, visitAttributeType);
                }
            }

            visitTableLayout.addView(row);
        }
    }

    @Override
    public void updateConceptAnswersView(Spinner conceptAnswersDropdown, List<ConceptAnswer> conceptAnswers){
        ConceptAnswerArrayAdapter conceptAnswerArrayAdapter = new ConceptAnswerArrayAdapter(this.getActivity(), conceptAnswers);
        conceptAnswersDropdown.setAdapter(conceptAnswerArrayAdapter);
        conceptAnswersDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ConceptAnswer answer = conceptAnswers.get(position);
                VisitAttribute visitAttribute = new VisitAttribute();
                visitAttribute.setValueReference(answer.getUuid());
                visitAttribute.setAttributeType(viewVisitAttributeTypeMap.get(view));

                visitAttributeMap.put(answer.getUuid(), visitAttribute);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void updateVisitTypes(List<VisitType> visitTypes) {
        visitTypeArrayAdapter = new VisitTypeArrayAdapter(this.getActivity(), visitTypes);
        visitTypeDropdown.setAdapter(visitTypeArrayAdapter);
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

    private void buildMarginLayout(){
        if(marginParams == null) {
            marginParams = new TableRow.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(70, 0, 0, 0);
        }
    }
}
