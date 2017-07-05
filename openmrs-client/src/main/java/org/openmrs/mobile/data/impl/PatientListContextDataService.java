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
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.data.rest.PatientListContextRestService;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class PatientListContextDataService
		extends BaseDataService<PatientListContext, PatientListContextDbService, PatientListContextRestService>
		implements DataService<PatientListContext> {

	@Override
	protected Class<PatientListContextRestService> getRestServiceClass() {
		return PatientListContextRestService.class;
	}

	@Override
	protected PatientListContextDbService getDbService() {
		return new PatientListContextDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "patientlist/data";
	}

	public void getListPatients(String patientListUuid, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<PatientListContext>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getListPatients(patientListUuid, options, pagingInfo),
				() -> restService.getAll(buildRestRequestPath(), patientListUuid,
						QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
						PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo)));
	}

	@Override
	protected Call<PatientListContext> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return null;
	}

	@Override
	protected Call<Results<PatientListContext>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<PatientListContext> _restCreate(String restPath, PatientListContext entity) {
		return null;
	}

	@Override
	protected Call<PatientListContext> _restUpdate(String restPath, PatientListContext entity) {
		return null;
	}

	@Override
	protected Call<PatientListContext> _restPurge(String restPath, String uuid) {
		return null;
	}
}
