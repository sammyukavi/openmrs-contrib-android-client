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

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContextModel;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main Patient List UI screen.
 */
public class PatientListFragment extends Fragment implements PatientListContract.View{

    private PatientListContract.Presenter patientListPresenter;

    private ProgressBar patientListSpinner;
    private Spinner patientListDropdown;
    private LinearLayout patientListLayout;
    private TextView emptyPatientList;
    private PatientListArrayAdapter patientListAdapter;

    public static PatientListFragment newInstance(){
        return new PatientListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_list, container, false);
        patientListSpinner = (ProgressBar) root.findViewById(R.id.patientListLoadingProgressBar);
        patientListDropdown = (Spinner) root.findViewById(R.id.patientListDropdown);
        patientListLayout = (LinearLayout) root.findViewById(R.id.patientListDataLayout);
        emptyPatientList = (TextView) root.findViewById(R.id.emptyPatientList);

        return root;
    }

    @Override
    public void setPresenter(@NonNull PatientListContract.Presenter patientListPresenter) {
        this.patientListPresenter = checkNotNull(patientListPresenter);
    }

    @Override
    public void setEmptyPatientListText(String text) {
        emptyPatientList.setText(text);
    }

    @Override
    public void updatePatientLists(List<PatientList> patientList) {
        patientListAdapter = new PatientListArrayAdapter(this.getActivity(), patientList);
        patientListDropdown.setAdapter(patientListAdapter);
    }

    @Override
    public void updatePatientListData(List<PatientListContextModel> patientListData) {

    }

    @Override
    public PatientList getSelectedPatientList(int position) {
        return patientListAdapter.getItem(position);
    }

    @Override
    public void setSpinnerVisibility(boolean visibility) {
        patientListSpinner.setVisibility(visibility ? View.VISIBLE : View.GONE);
        patientListLayout.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
