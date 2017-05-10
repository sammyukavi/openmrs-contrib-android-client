package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.VisitAttributeTypeRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitAttributeTypeDataService extends BaseDataService<VisitAttributeType, VisitAttributeTypeRestService> {

	@Override
	protected Class<VisitAttributeTypeRestService> getRestServiceClass() {
		return VisitAttributeTypeRestService.class;
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
	protected Call<VisitAttributeType> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<VisitAttributeType>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		return restService.getAll(restPath, representation);
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

	@Override
	public void getAll(boolean includeInactive, @Nullable PagingInfo pagingInfo,
			@NonNull GetMultipleCallback<VisitAttributeType> callback) {
		executeMultipleCallback(callback, null, () -> {
			if (isPagingValid(pagingInfo)) {
				return restService.getAll(buildRestRequestPath(),
						RestConstants.Representations.FULL,
						pagingInfo.getLimit(), pagingInfo.getStartIndex());
			} else {
				return restService.getAll(buildRestRequestPath(), RestConstants.Representations.FULL);
			}
		});
	}
}
