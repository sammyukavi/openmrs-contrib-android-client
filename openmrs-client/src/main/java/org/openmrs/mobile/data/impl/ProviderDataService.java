package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.ProviderRestService;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class ProviderDataService extends BaseMetadataDataService<Provider, ProviderRestService> {

	@Override
	protected Call<Results<Provider>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String name,
			String representation) {
		return null;
	}

	@Override
	protected Class<ProviderRestService> getRestServiceClass() {
		return ProviderRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "provider";
	}

	@Override
	protected Call<Provider> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<Provider>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		return restService.getAll(restPath, representation);
	}

	@Override
	protected Call<Provider> _restCreate(String restPath, Provider entity) {
		return null;
	}

	@Override
	protected Call<Provider> _restUpdate(String restPath, Provider entity) {
		return null;
	}

	@Override
	protected Call<Provider> _restPurge(String restPath, String uuid) {
		return null;
	}
}
