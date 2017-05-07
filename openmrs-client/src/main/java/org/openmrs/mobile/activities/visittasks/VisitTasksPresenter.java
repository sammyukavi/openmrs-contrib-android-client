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

import android.util.Log;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.VisitPredefinedTasksDataService;
import org.openmrs.mobile.data.impl.VisitTasksDataService;
import org.openmrs.mobile.models.VisitPredefinedTasks;
import org.openmrs.mobile.models.VisitTasks;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class VisitTasksPresenter extends BasePresenter implements VisitTasksContract.Presenter {

	private VisitTasksContract.View visitTasksView;
	private VisitPredefinedTasksDataService visitPredefinedTasksDataService;
	private VisitTasksDataService visitTasksDataService;

	private int page = 0;
	private int limit = 10;

	public VisitTasksPresenter(VisitTasksContract.View view, OpenMRS openMRS) {
		this.visitTasksView = view;
		this.visitTasksView.setPresenter(this);
		this.visitPredefinedTasksDataService = new VisitPredefinedTasksDataService();
		this.visitTasksDataService = new VisitTasksDataService();
	}

	@Override
	public void subscribe() {

	}

	@Override
	public void getPredefinedTasks() {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetMultipleCallback<VisitPredefinedTasks> getMultipleCallback = new DataService
					.GetMultipleCallback<VisitPredefinedTasks>() {
				@Override
				public void onCompleted(List<VisitPredefinedTasks> visitPredefinedTasks) {
					if (visitPredefinedTasks.isEmpty()) {
						visitTasksView.showToast(ApplicationConstants.toastMessages.predefinedTaskInfo, ToastUtil.ToastType
								.NOTICE);
					} else {
						visitTasksView.showToast(ApplicationConstants.toastMessages.predefinedTaskSucess, ToastUtil
								.ToastType
								.SUCCESS);
					}
				}

				@Override
				public void onError(Throwable t) {
					Log.e("Patient Error", "Error", t.fillInStackTrace());
					visitTasksView
							.showToast(ApplicationConstants.toastMessages.predefinedTaskError, ToastUtil.ToastType.ERROR);
				}
			};
			visitPredefinedTasksDataService.getAll(false, null, getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	@Override
	public void getVisitTasks() {
		PagingInfo pagingInfo = new PagingInfo(page, limit);
		if (NetworkUtils.hasNetwork()) {
			DataService.GetMultipleCallback<VisitTasks> getMultipleCallback = new DataService
					.GetMultipleCallback<VisitTasks>() {
				@Override
				public void onCompleted(List<VisitTasks> visitTasksList) {
					if (visitTasksList.isEmpty()) {
						visitTasksView.getVisitTasks(visitTasksList);
						visitTasksView.showToast(ApplicationConstants.toastMessages.predefinedTaskInfo, ToastUtil.ToastType
								.NOTICE);
					} else {
						visitTasksView.getVisitTasks(visitTasksList);
						visitTasksView.showToast(ApplicationConstants.toastMessages.predefinedTaskSucess, ToastUtil
								.ToastType
								.SUCCESS);
					}
				}

				@Override
				public void onError(Throwable t) {
					Log.e("Patient Error", "Error", t.fillInStackTrace());
					visitTasksView
							.showToast(ApplicationConstants.toastMessages.predefinedTaskError, ToastUtil.ToastType.ERROR);
				}
			};
			visitTasksDataService.getAll(ApplicationConstants.EMPTY_STRING, ApplicationConstants.PATIENT_UUID,
					ApplicationConstants.VISIT_UUID, pagingInfo, getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}
}
