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

package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseEntityDataService;
import org.openmrs.mobile.data.EntityDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.VisitTasksRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitTasks;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitTasksDataService extends BaseEntityDataService<VisitTasks, VisitTasksRestService> implements
		EntityDataService<VisitTasks> {

	@Override
	protected Call<Results<VisitTasks>> _restGetByPatient(String restPath, PagingInfo pagingInfo, String patientUuid,
			String representation) {
		// Does not allow the getting of the visit tasks using the patient.
		return null;
	}

	@Override
	protected Class<VisitTasksRestService> getRestServiceClass() {
		return VisitTasksRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "visittasks/task";
	}

	@Override
	protected Call<VisitTasks> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<VisitTasks>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		return null;
	}

	@Override
	protected Call<VisitTasks> _restCreate(String restPath, VisitTasks entity) {
		return restService.create(restPath, entity);
	}

	@Override
	protected Call<VisitTasks> _restUpdate(String restPath, VisitTasks entity) {
		return restService.update(restPath, entity.getUuid(), entity);
	}

	@Override
	protected Call<VisitTasks> _restPurge(String restPath, String uuid) {
		return restService.purge(restPath, uuid);
	}

	public void getByName(String status, String query, String patient_uuid, String visit_uuid, PagingInfo pagingInfo,
			GetMultipleCallback<VisitTasks> callback) {
		executeMultipleCallback(callback, null, () -> {
			if (isPagingValid(pagingInfo)) {
				return restService.getByName(buildRestRequestPath(), RestConstants.Representations
						.FULL, status, query, patient_uuid, visit_uuid, pagingInfo.getLimit(), pagingInfo.getStartIndex());
			} else {
				return restService.getByName(buildRestRequestPath(), RestConstants.Representations
						.FULL, status, query, patient_uuid, visit_uuid);
			}
		});
	}

	public void getAll(String status, String patient_uuid, String visit_uuid, PagingInfo pagingInfo,
			GetMultipleCallback<VisitTasks> callback) {
		executeMultipleCallback(callback, null, () -> {
			if (isPagingValid(pagingInfo)) {
				return restService.getAll(buildRestRequestPath(), RestConstants.Representations
						.FULL, status, patient_uuid, visit_uuid, pagingInfo.getLimit(), pagingInfo.getStartIndex());
			} else {
				return restService.getAll(buildRestRequestPath(), RestConstants.Representations
						.FULL, status, patient_uuid, visit_uuid);
			}
		});
	}
}
