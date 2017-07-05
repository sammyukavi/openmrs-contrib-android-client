package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitAttributeTypeDbServie;
import org.openmrs.mobile.data.rest.VisitAttributeTypeRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitAttributeTypeDataService
		extends BaseDataService<VisitAttributeType, VisitAttributeTypeDbServie, VisitAttributeTypeRestService>
		implements DataService<VisitAttributeType> {
	@Override
	protected Class<VisitAttributeTypeRestService> getRestServiceClass() {
		return VisitAttributeTypeRestService.class;
	}

	@Override
	protected VisitAttributeTypeDbServie getDbService() {
		return new VisitAttributeTypeDbServie();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "visitattributetype";
	}

	@Override
	protected Call<VisitAttributeType> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<VisitAttributeType>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}

	@Override
	protected Call<VisitAttributeType> _restCreate(String restPath, VisitAttributeType entity) {
		return restService.create(restPath, entity);
	}

	@Override
	protected Call<VisitAttributeType> _restUpdate(String restPath, VisitAttributeType entity) {
		return restService.update(restPath, entity.getUuid(), entity);
	}

	@Override
	protected Call<VisitAttributeType> _restPurge(String restPath, String uuid) {
		return restService.purge(restPath, uuid);
	}
}
