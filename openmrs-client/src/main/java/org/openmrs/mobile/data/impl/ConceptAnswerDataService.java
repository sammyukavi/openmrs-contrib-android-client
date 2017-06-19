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
import org.openmrs.mobile.data.db.impl.ConceptAnswerDbService;
import org.openmrs.mobile.data.rest.ConceptAnswerRestService;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConceptAnswerDataService extends BaseDataService<ConceptAnswer, ConceptAnswerDbService, ConceptAnswerRestService> {
	@Override
	protected Class<ConceptAnswerRestService> getRestServiceClass() {
		return ConceptAnswerRestService.class;
	}

	@Override
	protected ConceptAnswerDbService getDbService() {
		return new ConceptAnswerDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "custom";
	}

	@Override
	protected String getEntityName() {
		return "conceptanswer";
	}

	@Override
	protected Call<ConceptAnswer> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	public void getByConceptUuid(@NonNull String conceptUuid, @Nullable QueryOptions options,
			@NonNull GetCallback<List<ConceptAnswer>> callback) {
		checkNotNull(conceptUuid);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, null,
				() -> dbService.getByConceptUuid(conceptUuid, options),
				() -> restService.getByConceptUuid(buildRestRequestPath(), conceptUuid));
	}

	@Override
	protected Call<Results<ConceptAnswer>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<ConceptAnswer> _restCreate(String restPath, ConceptAnswer entity) {
		return null;
	}

	@Override
	protected Call<ConceptAnswer> _restUpdate(String restPath, ConceptAnswer entity) {
		return null;
	}

	@Override
	protected Call<ConceptAnswer> _restPurge(String restPath, String uuid) {
		return null;
	}
}
