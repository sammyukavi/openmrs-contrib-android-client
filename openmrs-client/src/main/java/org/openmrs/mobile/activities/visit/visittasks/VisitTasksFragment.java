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

package org.openmrs.mobile.activities.visit.visittasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTaskStatus;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.Arrays;
import java.util.List;

public class VisitTasksFragment extends VisitFragment implements VisitContract.VisitTasksView {

	private static OpenMRS instance = OpenMRS.getInstance();
	FloatingActionButton fab;
	private View mRootView;
	private RecyclerView viewTasksRecyclerView;
	private LinearLayoutManager layoutManager;
	private LinearLayout addTasklayout;
	private RecyclerView visitTasksRecyclerViewAdapter;
	private List<VisitPredefinedTask> predefinedTasks;
	private List<VisitTask> visitTasksLists;
	private Visit visit;
	private AutoCompleteTextView addtask;
	private TextView noVisitTasks;

	public static VisitTasksFragment newInstance() {
		return new VisitTasksFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setPresenter(mPresenter);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_visit_tasks, container, false);
		resolveViews(mRootView);

		//Adding the Recycler view
		layoutManager = new LinearLayoutManager(this.getActivity());
		visitTasksRecyclerViewAdapter = (RecyclerView)mRootView.findViewById(R.id.visitTasksRecyclerView);
		visitTasksRecyclerViewAdapter.setLayoutManager(layoutManager);

		((VisitTasksPresenter)mPresenter).getVisit();
		((VisitTasksPresenter)mPresenter).getPredefinedTasks();
		((VisitTasksPresenter)mPresenter).getVisitTasks();

		addListeners();
		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));
		return mRootView;
	}

	private void resolveViews(View v) {
		viewTasksRecyclerView = (RecyclerView)v.findViewById(R.id.visitTasksRecyclerView);
		fab = (FloatingActionButton)v.findViewById(R.id.visitTaskFab);
		addtask = (AutoCompleteTextView)v.findViewById(R.id.addVisitTasks);
		addTasklayout = (LinearLayout)v.findViewById(R.id.addTaskLayout);
		noVisitTasks = (TextView)v.findViewById(R.id.noVisitTasks);
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {

	}

	@Override
	public void setVisitTasks(List<VisitTask> visitTaskList) {
		this.visitTasksLists = visitTaskList;
		if (visit != null) {
			if (visitTaskList.size() != 0) {
				VisitTasksRecyclerViewAdapter adapter =
						new VisitTasksRecyclerViewAdapter(this.getActivity(), visitTaskList, visit, this);
				visitTasksRecyclerViewAdapter.setAdapter(adapter);
				//visitTasksRecyclerViewAdapter.addOnScrollListener(recyclerViewOnScrollListener)
				viewTasksRecyclerView.setVisibility(View.VISIBLE);
				noVisitTasks.setVisibility(View.GONE);
			} else {
				viewTasksRecyclerView.setVisibility(View.GONE);
				noVisitTasks.setVisibility(View.VISIBLE);
			}
		}
		addTaskOnFocusListener();
	}

	public void addTaskOnFocusListener() {
		ArrayAdapter adapter =
				new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,
						removeUsedPredefinedTasks(predefinedTasks, visitTasksLists));
		addtask.setAdapter(adapter);

		addtask.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (addtask.getText().length() >= addtask.getThreshold()) {
					addtask.showDropDown();
				}
				if (Arrays.asList(removeUsedPredefinedTasks(predefinedTasks, visitTasksLists))
						.contains(addtask.getText().toString())) {
					addtask.dismissDropDown();
				}
			}
		});
	}

	public void addListeners() {
		addtask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ViewUtils.getInput(addtask) != null) {
					((VisitTasksPresenter)mPresenter).createVisitTasksObject(ViewUtils.getInput(addtask));
				}
			}
		});
	}

	@Override
	public void setPredefinedTasks(List<VisitPredefinedTask> predefinedTasks) {
		this.predefinedTasks = predefinedTasks;
	}

	@Override
	public void setSelectedVisitTask(VisitTask visitTask) {
		visitTask.setStatus(VisitTaskStatus.CLOSED);
		((VisitTasksPresenter)mPresenter).updateVisitTask(visitTask);
	}

	@Override
	public void setUnSelectedVisitTask(VisitTask visitTask) {
		visitTask.setStatus(VisitTaskStatus.OPEN);
		((VisitTasksPresenter)mPresenter).updateVisitTask(visitTask);
	}

	@Override
	public void refresh() {
		((VisitTasksPresenter)mPresenter).getVisit();
		((VisitTasksPresenter)mPresenter).getPredefinedTasks();
		((VisitTasksPresenter)mPresenter).getVisitTasks();
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
		if (!visit.getStopDatetime().equalsIgnoreCase(null)) {
			addTasklayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void clearTextField() {
		addtask.setText(ApplicationConstants.EMPTY_STRING);
	}

	public List<VisitPredefinedTask> removeUsedPredefinedTasks(List<VisitPredefinedTask> visitPredefinedTask,
			List<VisitTask> visitTask) {
		if (visitPredefinedTask.size() != 0) {
			String visitTasksName, predefinedTaskName;
			VisitTaskStatus visitTaskStatus;

			for (int q = 0; q < visitTask.size(); q++) {
				visitTasksName = visitTask.get(q).getName();
				visitTaskStatus = visitTask.get(q).getStatus();

				for (int i = 0; i < visitPredefinedTask.size(); i++) {
					predefinedTaskName = predefinedTasks.get(i).getName();

					if ((predefinedTaskName.equalsIgnoreCase(visitTasksName)) && (visitTaskStatus
							.equals(VisitTaskStatus.OPEN))) {
						visitPredefinedTask.remove(i);
					}
				}
			}

			return visitPredefinedTask;
		} else {
			return visitPredefinedTask;
		}
	}
}
