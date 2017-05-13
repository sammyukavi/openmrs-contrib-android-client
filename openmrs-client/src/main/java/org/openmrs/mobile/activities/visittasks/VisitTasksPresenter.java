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
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitPredefinedTaskDataService;
import org.openmrs.mobile.data.impl.VisitTaskDataService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTaskStatus;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class VisitTasksPresenter extends BasePresenter implements VisitTasksContract.Presenter {

	private VisitTasksContract.View visitTasksView;
	private VisitPredefinedTaskDataService visitPredefinedTaskDataService;
	private VisitTaskDataService visitTaskDataService;
	private VisitDataService visitDataService;
	private OpenMRS instance = OpenMRS.getInstance();

	private int page = 1;
	private int limit = 10;

	public VisitTasksPresenter(VisitTasksContract.View view, OpenMRS openMRS) {
		this.visitTasksView = view;
		this.visitTasksView.setPresenter(this);
		this.visitPredefinedTaskDataService = new VisitPredefinedTaskDataService();
		this.visitTaskDataService = new VisitTaskDataService();
		this.visitDataService = new VisitDataService();
	}

	@Override
	public void subscribe() {

	}

	@Override
	public void getPredefinedTasks() {
		PagingInfo pagingInfo = new PagingInfo(page,100);
		if (NetworkUtils.hasNetwork()) {
			DataService.GetCallback<List<VisitPredefinedTask>> callback = new DataService
					.GetCallback<List<VisitPredefinedTask>>() {
				@Override
				public void onCompleted(List<VisitPredefinedTask> visitPredefinedTasks) {
					if (visitPredefinedTasks.isEmpty()) {
						visitTasksView.setPredefinedTasks(visitPredefinedTasks);
						/*visitTasksView.showToast(ApplicationConstants.toastMessages.predefinedTaskInfo, ToastUtil
						.ToastType
								.NOTICE);*/
					} else {
						visitTasksView.setPredefinedTasks(visitPredefinedTasks);
					}
				}

				@Override
				public void onError(Throwable t) {
					Log.e("Predefined tasks Error", "Error", t.fillInStackTrace());
					visitTasksView
							.showToast(ApplicationConstants.entityName.PREDEFINED_TASKS + ApplicationConstants.toastMessages
									.fetchErrorMessage, ToastUtil.ToastType.ERROR);
				}
			};
			visitPredefinedTaskDataService.getAll(QueryOptions.LOAD_RELATED_OBJECTS, pagingInfo, callback);
		} else {
			// get the users from the local storage.
		}
	}

	@Override
	public void getVisitTasks() {
		PagingInfo pagingInfo = new PagingInfo(page, limit);
		if (NetworkUtils.hasNetwork()) {
			DataService.GetCallback<List<VisitTask>> getMultipleCallback =
					new DataService.GetCallback<List<VisitTask>>() {
						@Override
						public void onCompleted(List<VisitTask> visitTasksList) {
							if (visitTasksList.isEmpty()) {
								visitTasksView.setVisitTasks(visitTasksList);
						/*visitTasksView.showToast(ApplicationConstants.toastMessages.predefinedTaskInfo, ToastUtil
						.ToastType
								.NOTICE);*/
							} else {
								visitTasksView.setVisitTasks(visitTasksList);
								visitTasksView
										.showToast(ApplicationConstants.entityName.VISIT_TASKS
														+ ApplicationConstants.toastMessages.fetchSuccessMessage,
												ToastUtil.ToastType.SUCCESS);
							}
						}

						@Override
						public void onError(Throwable t) {
							Log.e("Visit Tasks Error", "Error", t.fillInStackTrace());
							visitTasksView
									.showToast(
											ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
													.fetchErrorMessage, ToastUtil.ToastType.ERROR);
						}
					};
					System.out.println(instance.getPatientUuid() + "Patient Uuid ");
					System.out.println(instance.getVisitUuid() +  "Visit uuid");
			visitTaskDataService.getAll(ApplicationConstants.EMPTY_STRING, instance.getPatientUuid(),
					instance.getVisitUuid(), QueryOptions.LOAD_RELATED_OBJECTS, pagingInfo, getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	@Override
	public void displayAddTask(Boolean visibility) {
		visitTasksView.showAddTaskDialog(visibility);
	}

	@Override
	public void addVisitTasks(VisitTask visitTask) {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetCallback<VisitTask> getSingleCallback =
					new DataService.GetCallback<VisitTask>() {
						@Override
						public void onCompleted(VisitTask entity) {
							if (entity != null) {
								visitTasksView
										.showToast(ApplicationConstants.entityName.VISIT_TASKS
												+ ApplicationConstants.toastMessages.addSuccessMessage, ToastUtil.ToastType
												.SUCCESS);
								visitTasksView.refresh();
							}
						}

						@Override
						public void onError(Throwable t) {
							Log.e("Add Visit Task Error", "Error", t.fillInStackTrace());
							visitTasksView
									.showToast(
											ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
													.addErrorMessage, ToastUtil.ToastType.ERROR);
						}
					};
			visitTaskDataService.create(visitTask, getSingleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	public void updateVisitTask(VisitTask visitTask) {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetCallback<VisitTask> getSingleCallback = new DataService
					.GetCallback<VisitTask>() {
				@Override
				public void onCompleted(VisitTask entity) {
					if (entity != null) {
						visitTasksView
								.showToast(ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
										.updateSuccessMessage, ToastUtil.ToastType.SUCCESS);
						visitTasksView.refresh();
					}
				}

				@Override
				public void onError(Throwable t) {
					Log.e("Update Visit task", "Error", t.fillInStackTrace());
					visitTasksView
							.showToast(ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
									.updateErrorMessage, ToastUtil.ToastType.ERROR);
				}
			};
			visitTaskDataService.update(visitTask, getSingleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	@Override
	public void createVisitTasksObject(String visitTask) {
		Patient patient = new Patient();
		patient.setUuid(instance.getPatientUuid());

		Visit visit = new Visit();
		visit.setUuid(instance.getVisitUuid());

		VisitTask visitTaskEntity = new VisitTask();

		visitTaskEntity.setName(visitTask);
		visitTaskEntity.setStatus(VisitTaskStatus.OPEN);
		visitTaskEntity.setPatient(patient);
		visitTaskEntity.setVisit(visit);

		addVisitTasks(visitTaskEntity);
	}

	@Override
	public void getVisit() {
		if (NetworkUtils.hasNetwork()) {
			DataService.GetCallback<Visit> getSingleCallback =
					new DataService.GetCallback<Visit>() {
						@Override
						public void onCompleted(Visit entity) {
							if (entity != null) {
								visitTasksView.setVisit(entity);
							}
						}

						@Override
						public void onError(Throwable t) {
							Log.e("Visit Error", "Error", t.fillInStackTrace());
							visitTasksView
									.showToast(ApplicationConstants.entityName.VISITS + ApplicationConstants.toastMessages
											.fetchErrorMessage, ToastUtil.ToastType.ERROR);
						}
					};
			visitDataService.getByUUID(instance.getVisitUuid(), QueryOptions.LOAD_RELATED_OBJECTS, getSingleCallback);
		} else {
			// get the users from the local storage.
		}
	}
}
