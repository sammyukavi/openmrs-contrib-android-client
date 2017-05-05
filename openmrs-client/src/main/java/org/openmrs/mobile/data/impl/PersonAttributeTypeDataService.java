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
import org.openmrs.mobile.data.rest.PersonAttributeTypeRestService;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

/**
 * Created by dubdabasoduba on 05/05/2017.
 */

public class PersonAttributeTypeDataService extends BaseMetadataDataService<PersonAttributeType, PersonAttributeTypeRestService>
		implements MetadataDataService<PersonAttributeType> {
	@Override
	protected Call<Results<PersonAttributeType>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String name,
			String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getByName(restPath, name, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getByName(restPath, name, representation);
		}
	}

	@Override
	protected Class<PersonAttributeTypeRestService> getRestServiceClass() {
		return PersonAttributeTypeRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "personattributetype";
	}

	@Override
	protected Call<PersonAttributeType> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<PersonAttributeType>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getAll(restPath, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getAll(restPath, representation);
		}
	}

	@Override
	protected Call<PersonAttributeType> _restCreate(String restPath, PersonAttributeType entity) {
		return null;
	}

	@Override
	protected Call<PersonAttributeType> _restUpdate(String restPath, PersonAttributeType entity) {
		return null;
	}

	@Override
	protected Call<PersonAttributeType> _restPurge(String restPath, String uuid) {
		return null;
	}
}
