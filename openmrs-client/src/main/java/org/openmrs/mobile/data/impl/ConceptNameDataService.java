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

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ConceptNameDbService;
import org.openmrs.mobile.data.rest.ConceptNameRestSevice;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class ConceptNameDataService extends BaseDataService<ConceptName, ConceptNameDbService, ConceptNameRestSevice> {
	@Override
	protected Class<ConceptNameRestSevice> getRestServiceClass() {
		return ConceptNameRestSevice.class;
	}

	@Override
	protected ConceptNameDbService getDbService() {
		return new ConceptNameDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "patientlist";
	}

	@Override
	protected String getEntityName() {
		return "conceptname";
	}

	@Override
	protected Call<ConceptName> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	public void getByConceptUuid(String conceptUuid, @NonNull GetCallback<List<ConceptName>> callback) {
		executeMultipleCallback(callback, null,
				() -> null,
				() -> restService.getByConceptUuid(buildRestRequestPath(), conceptUuid));
	}

	@Override
	protected Call<Results<ConceptName>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<ConceptName> _restCreate(String restPath, ConceptName entity) {
		return null;
	}

	@Override
	protected Call<ConceptName> _restUpdate(String restPath, ConceptName entity) {
		return null;
	}

	@Override
	protected Call<ConceptName> _restPurge(String restPath, String uuid) {
		return null;
	}
}
