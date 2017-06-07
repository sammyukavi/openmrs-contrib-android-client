package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.LocationDbService;
import org.openmrs.mobile.data.rest.LocationRestService;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class LocationDataService extends BaseMetadataDataService<Location, LocationDbService, LocationRestService> {
	@Override
	protected Class<LocationRestService> getRestServiceClass() {
		return LocationRestService.class;
	}

	@Override
	protected LocationDbService getDbService() {
		return new LocationDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "location";
	}

	@Override
	protected Call<Results<Location>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<Location> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<Location>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options), PagingInfo.getLimit(pagingInfo),
				PagingInfo.getStartIndex(pagingInfo));
	}

	@Override
	protected Call<Location> _restCreate(String restPath, Location entity) {
		return null;
	}

	@Override
	protected Call<Location> _restUpdate(String restPath, Location entity) {
		return null;
	}

	@Override
	protected Call<Location> _restPurge(String restPath, String uuid) {
		return null;
	}

	public void getAll(GetCallback<List<Location>> callback) {
		executeMultipleCallback(callback, null, null,
				() -> null,
				() -> restService.getLoginLocations(getRestPath())
		);
	}
}
