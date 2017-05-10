package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.LocationRestService;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class LocationDataService extends BaseMetadataDataService<Location, LocationRestService> {

	@Override
	protected Call<Results<Location>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String name,
			String representation) {
		return null;
	}

	@Override
	protected Class<LocationRestService> getRestServiceClass() {
		return LocationRestService.class;
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
	protected Call<Location> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<Location>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		return restService.getAll(restPath, representation);
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
}
