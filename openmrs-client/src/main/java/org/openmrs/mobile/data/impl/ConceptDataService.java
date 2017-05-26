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
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ConceptDbService;
import org.openmrs.mobile.data.rest.ConceptRestService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConceptDataService extends BaseDataService<Concept, ConceptDbService, ConceptRestService> {
	@Override
	protected Class<ConceptRestService> getRestServiceClass() {
		return ConceptRestService.class;
	}

	@Override
	protected ConceptDbService getDbService() {
		return new ConceptDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "concept";
	}

	@Override
	protected Call<Concept> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<Concept>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<Concept> _restCreate(String restPath, Concept entity) {
		return null;
	}

	@Override
	protected Call<Concept> _restUpdate(String restPath, Concept entity) {
		return null;
	}

	@Override
	protected Call<Concept> _restPurge(String restPath, String uuid) {
		return null;
	}

	public void getByName(@NonNull String conceptName, @Nullable QueryOptions options,
			@NonNull GetCallback<List<Concept>> callback) {
		checkNotNull(conceptName);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, null,
				() -> null,
				() -> restService.getByConceptName(buildRestRequestPath(), conceptName, QueryOptions.getRepresentation(options)));
	}
}
