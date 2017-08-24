package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseMetadataRestService;
import org.openmrs.mobile.data.rest.retrofit.VisitPredefinedTaskRestService;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitPredefinedTaskRestServiceImpl
		extends BaseMetadataRestService<VisitPredefinedTask, VisitPredefinedTaskRestService> {
	@Inject
	public VisitPredefinedTaskRestServiceImpl() {
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "visittasks/predefinedTask";
	}

	public Call<Results<RecordInfo>> getSetVisitPredefinedTasksRecordInfo(QueryOptions options) {
		return restService.getRecordInfo(getRestPath(), options.getCustomRepresentation(), false);
	}
}
