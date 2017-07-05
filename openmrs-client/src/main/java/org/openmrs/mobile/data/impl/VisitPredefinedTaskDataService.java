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

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.MetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitPredefinedTaskDbService;
import org.openmrs.mobile.data.rest.VisitPredefinedTasksRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitPredefinedTaskDataService
		extends BaseMetadataDataService<VisitPredefinedTask, VisitPredefinedTaskDbService, VisitPredefinedTasksRestService>
		implements MetadataDataService<VisitPredefinedTask> {
	@Override
	protected Class<VisitPredefinedTasksRestService> getRestServiceClass() {
		return VisitPredefinedTasksRestService.class;
	}

	@Override
	protected VisitPredefinedTaskDbService getDbService() {
		return new VisitPredefinedTaskDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "visittasks/predefinedTask";
	}

	@Override
	protected Call<VisitPredefinedTask> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<VisitPredefinedTask>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}

	@Override
	protected Call<VisitPredefinedTask> _restCreate(String restPath, VisitPredefinedTask entity) {
		return null;
	}

	@Override
	protected Call<VisitPredefinedTask> _restUpdate(String restPath, VisitPredefinedTask entity) {
		return null;
	}

	@Override
	protected Call<VisitPredefinedTask> _restPurge(String restPath, String uuid) {
		return null;
	}

	@Override
	protected Call<Results<VisitPredefinedTask>> _restGetByNameFragment(String restPath, String name,
			QueryOptions options, PagingInfo pagingInfo) {
		return restService.getByName(restPath, name,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}
}
