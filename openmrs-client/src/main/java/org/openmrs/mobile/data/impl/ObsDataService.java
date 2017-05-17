package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.rest.ObsRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public class ObsDataService extends BaseDataService<Observation, ObsDbService, ObsRestService> {

	@Override
	protected Class<ObsRestService> getRestServiceClass() {
		return ObsRestService.class;
	}

	@Override
	protected ObsDbService getDbService() {
		return new ObsDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "obs";
	}

	@Override
	protected Call<Observation> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<Observation>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options), PagingInfo.getLimit(pagingInfo),
				PagingInfo.getStartIndex(pagingInfo));
	}

	@Override
	protected Call<Observation> _restCreate(String restPath, Observation entity) {
		return null;
	}

	@Override
	protected Call<Observation> _restUpdate(String restPath, Observation entity) {
		return null;
	}

	@Override
	protected Call<Observation> _restPurge(String restPath, String uuid) {
		return null;
	}

	public void getVisitDocumentsObsByPatientAndConceptList(String patientUuid, QueryOptions options,
			GetCallback<List<Observation>> callback) {
		executeMultipleCallback(callback, options, null,
				() -> null,
				() -> restService.getVisitDocumentsObsByPatientAndConceptList(
						buildRestRequestPath(), patientUuid,
						"7cac8397-53cd-4f00-a6fe-028e8d743f8e,42ed45fd-f3f6-44b6-bfc2-8bde1bb41e00",
						RestConstants.Representations.FULL));
	}

	public void getByEncounter(@NonNull Encounter encounter, @Nullable QueryOptions options,
			@Nullable PagingInfo pagingInfo, @NonNull GetCallback<List<Observation>> callback) {
		checkNotNull(encounter);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> null,
				() -> restService.getByEncounter(buildRestRequestPath(), encounter.getUuid(),
						QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
						pagingInfo.getLimit(), pagingInfo.getStartIndex())
		);
	}
}
