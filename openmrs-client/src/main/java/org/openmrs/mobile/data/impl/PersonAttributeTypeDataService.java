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
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PersonAttributeTypeDbService;
import org.openmrs.mobile.data.rest.PersonAttributeTypeRestService;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class PersonAttributeTypeDataService
		extends BaseMetadataDataService<PersonAttributeType, PersonAttributeTypeDbService, PersonAttributeTypeRestService>
		implements MetadataDataService<PersonAttributeType> {
	@Override
	protected Class<PersonAttributeTypeRestService> getRestServiceClass() {
		return PersonAttributeTypeRestService.class;
	}

	@Override
	protected PersonAttributeTypeDbService getDbService() {
		return new PersonAttributeTypeDbService();
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
	protected Call<Results<PersonAttributeType>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo) {
		return restService.getByName(restPath, name,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}

	@Override
	protected Call<PersonAttributeType> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<PersonAttributeType>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
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
