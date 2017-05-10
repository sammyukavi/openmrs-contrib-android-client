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

package org.openmrs.mobile.activities.visittasks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTaskStatus;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class VisitTasksFragment extends ACBaseFragment<VisitTasksContract.Presenter> implements VisitTasksContract.View {

	private static OpenMRS instance = OpenMRS.getInstance();
	FloatingActionButton fab;
	private View mRootView;
	private RecyclerView viewTasksRecyclerView;
	private LinearLayoutManager layoutManager;
	private RecyclerView visitTasksRecyclerViewAdapter;
	private List<VisitPredefinedTask> predefinedTasks;
	private List<VisitTask> visitTasksLists;
	private Visit visit;

	public static VisitTasksFragment newInstance() {
		return new VisitTasksFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_visit_tasks, container, false);
		resolveViews(mRootView);

		//Adding the Recycler view
		layoutManager = new LinearLayoutManager(this.getActivity());
		visitTasksRecyclerViewAdapter = (RecyclerView)mRootView.findViewById(R.id.visitTasksRecyclerView);
		visitTasksRecyclerViewAdapter.setLayoutManager(layoutManager);

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));
		mPresenter.getVisit();
		mPresenter.getPredefinedTasks();
		mPresenter.getVisitTasks();

		return mRootView;
	}

	private void resolveViews(View v) {
		viewTasksRecyclerView = (RecyclerView)v.findViewById(R.id.visitTasksRecyclerView);
		fab = (FloatingActionButton)v.findViewById(R.id.visitTaskFab);
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(getContext(), toastType, message);
	}

	@Override
	public void setVisitTasks(List<VisitTask> visitTaskList) {
		this.visitTasksLists = visitTaskList;
		if (visit != null) {
			VisitTasksRecyclerViewAdapter adapter =
					new VisitTasksRecyclerViewAdapter(this.getActivity(), visitTaskList, visit, this);
			visitTasksRecyclerViewAdapter.setAdapter(adapter);
			//visitTasksRecyclerViewAdapter.addOnScrollListener(recyclerViewOnScrollListener);

		}
	}

	@Override
	public void showAddTaskDialog(Boolean visibility) {
		CustomDialogBundle addVisitTasksDialog = new CustomDialogBundle();
		addVisitTasksDialog.setTitleViewMessage(getString(R.string.add_visit_task_dialog_title));
		addVisitTasksDialog.setRightButtonText(getString(R.string.action_submit));
		addVisitTasksDialog.setAutoCompleteTextView(removeUsedPredefinedTasks(predefinedTasks, visitTasksLists));
		if (visit.getStopDatetime() != null) {
			addVisitTasksDialog.setDisableAutoCompleteText(true);
		} else {
			addVisitTasksDialog.setDisableAutoCompleteText(false);
		}
		addVisitTasksDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.ADD_VISIT_TASKS);
		((VisitTasksActivity)this.getActivity())
				.createAndShowDialog(addVisitTasksDialog, ApplicationConstants.DialogTAG.ADD_VISIT_TASK_DIALOG_TAG);
	}

	@Override
	public void setPredefinedTasks(List<VisitPredefinedTask> predefinedTasks) {
		this.predefinedTasks = predefinedTasks;
	}

	@Override
	public void setSelectedVisitTask(VisitTask visitTask) {
		visitTask.setStatus(VisitTaskStatus.CLOSED);
		mPresenter.updateVisitTask(visitTask);
	}

	@Override
	public void setUnSelectedVisitTask(VisitTask visitTask) {
		visitTask.setStatus(VisitTaskStatus.OPEN);
		mPresenter.updateVisitTask(visitTask);
	}

	@Override
	public void refresh() {
		mPresenter.getVisit();
		mPresenter.getPredefinedTasks();
		mPresenter.getVisitTasks();
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	@Override
	public String getPatientUuid() {
		SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
		return sharedPreferences.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants
				.EMPTY_STRING);
	}

	@Override
	public String getVisitUuid() {
		SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
		return sharedPreferences.getString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants
				.EMPTY_STRING);
	}

	public List<VisitPredefinedTask> removeUsedPredefinedTasks(List<VisitPredefinedTask> visitPredefinedTask,
			List<VisitTask> visitTask) {
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
	}
}
