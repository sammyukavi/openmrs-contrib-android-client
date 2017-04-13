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
package org.openmrs.mobile.activities.patientlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContextModel;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

/**
 * Main Patient List UI screen.
 */
public class PatientListFragment extends ACBaseFragment<PatientListContract.Presenter> implements PatientListContract.View{

    private ProgressBar patientListSpinner;
    private Spinner patientListDropdown;
    private TextView emptyPatientList;
    private PatientListArrayAdapter patientListAdapter;
    private RecyclerView patientListModelRecyclerView;

    public static PatientListFragment newInstance(){
        return new PatientListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_list, container, false);
        patientListSpinner = (ProgressBar) root.findViewById(R.id.patientListLoadingProgressBar);
        patientListDropdown = (Spinner) root.findViewById(R.id.patientListDropdown);
        emptyPatientList = (TextView) root.findViewById(R.id.emptyPatientList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        patientListModelRecyclerView = (RecyclerView) root.findViewById(R.id.patientListModelRecyclerView);
        patientListModelRecyclerView.setLayoutManager(linearLayoutManager);

        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));

        return root;
    }

    @Override
    public void setEmptyPatientListText(String text) {
        emptyPatientList.setText(text);
    }

    @Override
    public void updatePatientLists(List<PatientList> patientLists) {
        patientListAdapter = new PatientListArrayAdapter(this.getActivity(), patientLists);
        patientListDropdown.setAdapter(patientListAdapter);
        patientListDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PatientList patientList = patientLists.get(position);
                mPresenter.getPatientListData(patientList.getUuid(), 1, 10);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void updatePatientListData(List<PatientListContextModel> patientListData) {
        PatientListModelRecyclerViewAdapter adapter = new PatientListModelRecyclerViewAdapter(this.getActivity(), patientListData, this);
        patientListModelRecyclerView.setAdapter(adapter);
    }

    @Override
    public PatientList getSelectedPatientList(int position) {
        return patientListAdapter.getItem(position);
    }

    @Override
    public void setSpinnerVisibility(boolean visibility) {
        patientListSpinner.setVisibility(visibility ? View.VISIBLE : View.GONE);
        //patientListLayout.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
