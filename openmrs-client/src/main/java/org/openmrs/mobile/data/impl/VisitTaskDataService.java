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
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.VisitTasksRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class VisitTaskDataService
		extends BaseEntityDataService<VisitTask, VisitTaskDbService, VisitTasksRestService>
		implements EntityDataService<VisitTask> {

	@Override
	protected Class<VisitTasksRestService> getRestServiceClass() {
		return VisitTasksRestService.class;
	}

	@Override
	protected VisitTaskDbService getDbService() {
		return new VisitTaskDbService();
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
	protected Call<VisitTask> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<VisitTask>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<VisitTask> _restCreate(String restPath, VisitTask entity) {
		return restService.create(restPath, entity);
	}

	@Override
	protected Call<VisitTask> _restUpdate(String restPath, VisitTask entity) {
		return restService.update(restPath, entity.getUuid(), entity);
	}

	@Override
	protected Call<VisitTask> _restPurge(String restPath, String uuid) {
		return restService.purge(restPath, uuid);
	}

	@Override
	protected Call<Results<VisitTask>> _restGetByPatient(String restPath, String patientUuid, QueryOptions options,
			PagingInfo pagingInfo) {
		return null;
	}

	public void getByName(String status, String query, String patient_uuid, String visit_uuid,
			QueryOptions options, PagingInfo pagingInfo, GetCallback<List<VisitTask>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> null,
				() -> restService.getByName(buildRestRequestPath(), status, query, patient_uuid, visit_uuid,
						QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
						PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo))

		);
	}

	public void getAll(String status, String patient_uuid, String visit_uuid,
			QueryOptions options, PagingInfo pagingInfo, GetCallback<List<VisitTask>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> null,
				() -> restService.getAll(buildRestRequestPath(), status, patient_uuid, visit_uuid,
						QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
						PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo))
		);
	}

}
