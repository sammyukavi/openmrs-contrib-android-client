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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Patient List UI screen.
 */
public class PatientListFragment extends ACBaseFragment<PatientListContract.Presenter> implements PatientListContract.View {

	private ProgressBar patientListSpinner;
	private Spinner patientListDropdown;
	private TextView emptyPatientList;
	private TextView noPatientLists;
	private TextView numberOfPatients;
	private RecyclerView patientListModelRecyclerView;
	private LinearLayoutManager layoutManager;
	private LinearLayout patientListScreen;
	private RelativeLayout patientListProgressBar;

	private PatientList selectedPatientList;

	private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			if (!mPresenter.isLoading()) {
				if (!recyclerView.canScrollVertically(1)) {
					// load next page
					mPresenter.loadResults(selectedPatientList.getUuid(), true);
				}

				if (!recyclerView.canScrollVertically(-1) && dy < 0) {
					// load previous page
					mPresenter.loadResults(selectedPatientList.getUuid(), false);
				}
			}
		}
	};

	public static PatientListFragment newInstance() {
		return new PatientListFragment();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		patientListModelRecyclerView.removeOnScrollListener(recyclerViewOnScrollListener);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_patient_list, container, false);
		patientListSpinner = (ProgressBar)root.findViewById(R.id.patientListLoadingProgressBar);
		patientListDropdown = (Spinner)root.findViewById(R.id.patientListDropdown);
		emptyPatientList = (TextView)root.findViewById(R.id.emptyPatientList);
		noPatientLists = (TextView)root.findViewById(R.id.noPatientLists);
		numberOfPatients = (TextView)root.findViewById(R.id.numberOfPatients);
		patientListProgressBar = (RelativeLayout)root.findViewById(R.id.patientListScreenProgressBar);
		patientListScreen = (LinearLayout)root.findViewById(R.id.patientListScreen);

		layoutManager = new LinearLayoutManager(this.getActivity());
		patientListModelRecyclerView = (RecyclerView)root.findViewById(R.id.patientListModelRecyclerView);
		patientListModelRecyclerView.setLayoutManager(layoutManager);

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return root;
	}

	@Override
	public void setEmptyPatientListVisibility(boolean visible) {
		emptyPatientList.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setNoPatientListsVisibility(boolean visible) {
		noPatientLists.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setPatientListScreenVisibility(boolean visible) {
		if (visible) {
			patientListScreen.setVisibility(View.VISIBLE);
			patientListProgressBar.setVisibility(View.GONE);
		} else {
			patientListScreen.setVisibility(View.GONE);
			patientListProgressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void setSpinnerVisibility(boolean visible) {
		patientListSpinner.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void updatePatientLists(List<PatientList> patientLists) {
		PatientList patientList = new PatientList();
		patientList.setName(getString(R.string.select_patient_list));

		patientLists.add(0, patientList);
		ArrayAdapter<PatientList> adapter = new ArrayAdapter<PatientList>(getContext(),
				android.R.layout.simple_spinner_dropdown_item, patientLists);
		patientListDropdown.setAdapter(adapter);
		patientListDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setSelectedPatientList(patientLists.get(position));
				if (selectedPatientList.getUuid() == null) {
					setNumberOfPatientsView(0);
					List<PatientListContext> patientListContextList = new ArrayList<>();
					updatePatientListData(patientListContextList);
				} else {
					mPresenter.getPatientListData(selectedPatientList.getUuid(), mPresenter.getPage());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@Override
	public void setSelectedPatientList(PatientList selectedPatientList) {
		this.selectedPatientList = selectedPatientList;
	}

	@Override
	public void updatePatientListData(List<PatientListContext> patientListData) {
		PatientListModelRecyclerViewAdapter adapter =
				new PatientListModelRecyclerViewAdapter(this.getActivity(), patientListData, this);
		patientListModelRecyclerView.setAdapter(adapter);

		patientListModelRecyclerView.addOnScrollListener(recyclerViewOnScrollListener);
	}

	@Override
	public void setNumberOfPatientsView(int length) {
		numberOfPatients.setText(getString(R.string.number_of_patients, String.valueOf(length)));
		numberOfPatients.setVisibility(length <= 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean isActive() {
		return isAdded();
	}
}
