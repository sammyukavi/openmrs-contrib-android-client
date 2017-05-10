/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.PatientListContextModelRestService;
import org.openmrs.mobile.models.PatientListContextModel;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class PatientListContextModelDataService extends BaseDataService<PatientListContextModel,
		PatientListContextModelRestService> {

	@Override
	protected Class<PatientListContextModelRestService> getRestServiceClass() {
		return PatientListContextModelRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "patientlist/data";
	}

	public void getAll(String patientListUuid, PagingInfo pagingInfo,
			GetMultipleCallback<PatientListContextModel> callback) {
		executeMultipleCallback(callback, pagingInfo, () -> {
			return restService
					.getAll(buildRestRequestPath(), patientListUuid, pagingInfo.getStartIndex(), pagingInfo.getLimit());
		});
	}

	@Override
	protected Call<PatientListContextModel> _restGetByUuid(String restPath, String uuid, String representation) {
		return null;
	}

	@Override
	protected Call<Results<PatientListContextModel>> _restGetAll(String restPath, PagingInfo pagingInfo,
			String representation) {
		return null;
	}

	@Override
	protected Call<PatientListContextModel> _restCreate(String restPath, PatientListContextModel entity) {
		return null;
	}

	@Override
	protected Call<PatientListContextModel> _restUpdate(String restPath, PatientListContextModel entity) {
		return null;
	}

	@Override
	protected Call<PatientListContextModel> _restPurge(String restPath, String uuid) {
		return null;
	}
}
