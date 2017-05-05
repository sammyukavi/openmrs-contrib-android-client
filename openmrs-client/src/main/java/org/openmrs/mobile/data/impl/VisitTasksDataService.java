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
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitTasks;

import retrofit2.Call;

public class VisitTasksDataService extends BaseEntityDataService<VisitTasks, VisitTasks> implements
		EntityDataService<VisitTasks> {

	@Override
	protected Call<Results<VisitTasks>> _restGetByPatient(String restPath, PagingInfo pagingInfo, String patientUuid,
			String representation) {
		// Does not allow the getting of the visit tasks using the patient.
		return null;
	}

	@Override
	protected Class<VisitTasks> getRestServiceClass() {
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
	protected Call<VisitTasks> _restGetByUuid(String restPath, String uuid, String representation) {
		return null;
	}

	@Override
	protected Call<Results<VisitTasks>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		return null;
	}

	@Override
	protected Call<VisitTasks> _restCreate(String restPath, VisitTasks entity) {
		return null;
	}

	@Override
	protected Call<VisitTasks> _restUpdate(String restPath, VisitTasks entity) {
		return null;
	}

	@Override
	protected Call<VisitTasks> _restPurge(String restPath, String uuid) {
		return null;
	}
}
