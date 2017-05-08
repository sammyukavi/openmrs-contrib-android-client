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
import org.openmrs.mobile.data.rest.VisitPredefinedTasksRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitPredefinedTasksDataService extends BaseMetadataDataService<VisitPredefinedTask,
		VisitPredefinedTasksRestService> implements MetadataDataService<VisitPredefinedTask> {
	@Override
	protected Call<Results<VisitPredefinedTask>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String
			name,
			String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getByName(restPath, name, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getByName(restPath, name, representation);
		}
	}

	@Override
	protected Class<VisitPredefinedTasksRestService> getRestServiceClass() {
		return VisitPredefinedTasksRestService.class;
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
	protected Call<VisitPredefinedTask> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<VisitPredefinedTask>> _restGetAll(String restPath, PagingInfo pagingInfo,
			String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getAll(restPath, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getAll(restPath, representation);
		}
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
}
