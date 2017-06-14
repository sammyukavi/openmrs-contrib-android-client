package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.MetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitTypeDbService;
import org.openmrs.mobile.data.rest.VisitTypeRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitTypeDataService extends BaseMetadataDataService<VisitType, VisitTypeDbService, VisitTypeRestService>
		implements MetadataDataService<VisitType> {
	@Override
	protected Class<VisitTypeRestService> getRestServiceClass() {
		return VisitTypeRestService.class;
	}

	@Override
	protected VisitTypeDbService getDbService() {
		return new VisitTypeDbService();
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
	protected Call<VisitType> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<VisitType>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
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

	@Override
	protected Call<Results<VisitType>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo) {
		return null;
	}
}
