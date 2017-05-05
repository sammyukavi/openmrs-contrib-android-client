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

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.ConceptRestService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class ConceptDataService extends BaseDataService<Concept, ConceptRestService> {
	@Override
	protected Class<ConceptRestService> getRestServiceClass() {
		return ConceptRestService.class;
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
	protected Call<Concept> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<Concept>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
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

}
