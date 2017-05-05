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
import org.openmrs.mobile.models.VisitPredefinedTasks;

import retrofit2.Call;

public class VisitTasksPredefinedTasksDataService extends BaseMetadataDataService<VisitPredefinedTasks,
		VisitPredefinedTasksRestService> implements MetadataDataService<VisitPredefinedTasks> {
	@Override
	protected Call<Results<VisitPredefinedTasks>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String
			name,
			String representation) {
		return null;
	}

	@Override
	protected Class<VisitPredefinedTasksRestService> getRestServiceClass() {
		return null;
	}

	@Override
	protected String getRestPath() {
		return null;
	}

	@Override
	protected String getEntityName() {
		return null;
	}

	@Override
	protected Call<VisitPredefinedTasks> _restGetByUuid(String restPath, String uuid, String representation) {
		return null;
	}

	@Override
	protected Call<Results<VisitPredefinedTasks>> _restGetAll(String restPath, PagingInfo pagingInfo,
			String representation) {
		return null;
	}

	@Override
	protected Call<VisitPredefinedTasks> _restCreate(String restPath, VisitPredefinedTasks entity) {
		return null;
	}

	@Override
	protected Call<VisitPredefinedTasks> _restUpdate(String restPath, VisitPredefinedTasks entity) {
		return null;
	}

	@Override
	protected Call<VisitPredefinedTasks> _restPurge(String restPath, String uuid) {
		return null;
	}
}
