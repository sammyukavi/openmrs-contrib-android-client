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

import org.openmrs.mobile.activities.visitdetails.VisitDetailsContract;
import org.openmrs.mobile.activities.visitdetails.VisitDetailsPresenter;
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
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class VisitTasksPresenter extends VisitDetailsPresenter implements VisitDetailsContract.VisitTasksPresenter {

	private VisitDetailsContract.VisitTasksView visitTasksView;
	private VisitPredefinedTaskDataService visitPredefinedTaskDataService;
	private VisitTaskDataService visitTaskDataService;
	private VisitDataService visitDataService;
	private String patientUUID, visitUUID;

	private int page = 1;
	private int limit = 10;

	public VisitTasksPresenter(String patientUuid, String visitUuid, VisitDetailsContract.VisitTasksView visitTasksView) {
		this.visitTasksView = visitTasksView;
		this.visitTasksView.setPresenter(this);
		this.visitPredefinedTaskDataService = new VisitPredefinedTaskDataService();
		this.visitTaskDataService = new VisitTaskDataService();
		this.visitDataService = new VisitDataService();
		this.visitUUID = visitUuid;
		this.patientUUID = patientUuid;
	}

	@Override
	public void subscribe() {}

	@Override
	public void getPredefinedTasks() {
		PagingInfo pagingInfo = new PagingInfo(page, 100);
		DataService.GetCallback<List<VisitPredefinedTask>> callback = new DataService
				.GetCallback<List<VisitPredefinedTask>>() {
			@Override
			public void onCompleted(List<VisitPredefinedTask> visitPredefinedTasks) {
				if (visitPredefinedTasks.isEmpty()) {
					visitTasksView.setPredefinedTasks(visitPredefinedTasks);
				} else {
					visitTasksView.setPredefinedTasks(visitPredefinedTasks);
				}
			}

			@Override
			public void onError(Throwable t) {
				visitTasksView
						.showToast(ApplicationConstants.entityName.PREDEFINED_TASKS + ApplicationConstants.toastMessages
								.fetchErrorMessage, ToastUtil.ToastType.ERROR);
			}
		};
		visitPredefinedTaskDataService.getAll(QueryOptions.LOAD_RELATED_OBJECTS, pagingInfo, callback);
	}

	@Override
	public void getVisitTasks() {
		PagingInfo pagingInfo = new PagingInfo(page, limit);
		DataService.GetCallback<List<VisitTask>> getMultipleCallback =
				new DataService.GetCallback<List<VisitTask>>() {
					@Override
					public void onCompleted(List<VisitTask> visitTasksList) {
						if (visitTasksList.isEmpty()) {
							visitTasksView.setVisitTasks(visitTasksList);
						} else {
							visitTasksView.setVisitTasks(visitTasksList);
						}
					}

					@Override
					public void onError(Throwable t) {
						visitTasksView
								.showToast(
										ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
												.fetchErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		visitTaskDataService
				.getAll(ApplicationConstants.EMPTY_STRING, patientUUID, visitUUID, QueryOptions.LOAD_RELATED_OBJECTS,
						pagingInfo, getMultipleCallback);
	}

	@Override
	public void addVisitTasks(VisitTask visitTask) {
		DataService.GetCallback<VisitTask> getSingleCallback =
				new DataService.GetCallback<VisitTask>() {
					@Override
					public void onCompleted(VisitTask entity) {
						if (entity != null) {
							visitTasksView.refresh();
						}
					}

					@Override
					public void onError(Throwable t) {
						visitTasksView
								.showToast(
										ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
												.addErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		visitTaskDataService.create(visitTask, getSingleCallback);
	}

	public void updateVisitTask(VisitTask visitTask) {
		DataService.GetCallback<VisitTask> getSingleCallback = new DataService
				.GetCallback<VisitTask>() {
			@Override
			public void onCompleted(VisitTask entity) {
				if (entity != null) {
					visitTasksView.refresh();
				}
			}

			@Override
			public void onError(Throwable t) {
				visitTasksView
						.showToast(ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
								.updateErrorMessage, ToastUtil.ToastType.ERROR);
			}
		};
		visitTaskDataService.update(visitTask, getSingleCallback);
	}

	@Override
	public void createVisitTasksObject(String visitTask) {
		Patient patient = new Patient();
		patient.setUuid(patientUUID);

		Visit visit = new Visit();
		visit.setUuid(visitUUID);

		VisitTask visitTaskEntity = new VisitTask();

		visitTaskEntity.setName(visitTask);
		visitTaskEntity.setStatus(VisitTaskStatus.OPEN);
		visitTaskEntity.setPatient(patient);
		visitTaskEntity.setVisit(visit);

		addVisitTasks(visitTaskEntity);
	}

	@Override
	public void getVisit() {
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
						visitTasksView
								.showToast(ApplicationConstants.entityName.VISITS + ApplicationConstants.toastMessages
										.fetchErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		visitDataService.getByUUID(visitUUID, QueryOptions.LOAD_RELATED_OBJECTS, getSingleCallback);
	}
}
