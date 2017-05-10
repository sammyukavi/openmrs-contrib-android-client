package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.VisitTypeRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitTypeDataService extends BaseDataService<VisitType, VisitTypeRestService> {

	@Override
	protected Class<VisitTypeRestService> getRestServiceClass() {
		return VisitTypeRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "visittype";
	}

	@Override
	protected Call<VisitType> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<VisitType>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		return restService.getAll(restPath, representation);
	}

	@Override
	protected Call<VisitType> _restCreate(String restPath, VisitType entity) {
		return null;
	}

	@Override
	protected Call<VisitType> _restUpdate(String restPath, VisitType entity) {
		return null;
	}

	@Override
	protected Call<VisitType> _restPurge(String restPath, String uuid) {
		return null;
	}
}
