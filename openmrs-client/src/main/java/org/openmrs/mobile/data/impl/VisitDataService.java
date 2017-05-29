package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseEntityDataService;
import org.openmrs.mobile.data.EntityDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.rest.VisitRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitDataService extends BaseEntityDataService<Visit, VisitDbService, VisitRestService>
		implements EntityDataService<Visit> {
	@Override
	protected VisitDbService getDbService() {
		return new VisitDbService();
	}

	@Override
	protected Class<VisitRestService> getRestServiceClass() {
		return VisitRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "visit";
	}

	// Begin Retrofit Workaround

	@Override
	protected Call<Visit> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<Visit>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options), PagingInfo.getLimit(pagingInfo),
				PagingInfo.getStartIndex(pagingInfo));
	}

	@Override
	protected Call<Visit> _restCreate(String restPath, Visit entity) {
		return restService.create(restPath, entity);
	}

	@Override
	protected Call<Visit> _restUpdate(String restPath, Visit entity) {
		return restService.update(restPath, entity.getUuid(), entity);
	}

	@Override
	protected Call<Visit> _restPurge(String restPath, String uuid) {
		return restService.purge(restPath, uuid);
	}

	@Override
	protected Call<Results<Visit>> _restGetByPatient(String restPath, String patientUuid, QueryOptions options,
			PagingInfo pagingInfo) {
		return restService.getByPatient(restPath, patientUuid, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options));
	}

	// End Retrofit Workaround

	public void endVisit(@NonNull String uuid, @NonNull Visit visit, @Nullable QueryOptions options,
			@NonNull GetCallback<Visit> callback) {
		checkNotNull(uuid);
		checkNotNull(visit);
		checkNotNull(callback);

		executeSingleCallback(callback, options,
				() -> null,
				() -> restService.endVisit(buildRestRequestPath(), uuid, visit));
	}

	public void updateVisit(String visitUuid, Visit updatedVisit, GetCallback<Visit> callback){
		executeSingleCallback(callback, null,
				() -> null,
				() -> restService.updateVisit(ApplicationConstants.API.REST_ENDPOINT_V2 + "/patientlist/visitedit",
						visitUuid, updatedVisit.getVisitType().getUuid(), updatedVisit.getAttributes()));
	}
}
