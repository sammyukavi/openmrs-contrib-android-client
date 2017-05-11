package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ProviderDbService;
import org.openmrs.mobile.data.rest.ProviderRestService;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class ProviderDataService extends BaseMetadataDataService<Provider, ProviderDbService, ProviderRestService> {
	@Override
	protected Class<ProviderRestService> getRestServiceClass() {
		return ProviderRestService.class;
	}

	@Override
	protected ProviderDbService getDbService() {
		return new ProviderDbService();
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
	protected Call<Results<Provider>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<Provider> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<Provider>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
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
