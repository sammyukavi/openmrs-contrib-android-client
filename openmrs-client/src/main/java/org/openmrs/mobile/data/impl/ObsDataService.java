package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.ObsRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public class ObsDataService extends BaseDataService<Observation, ObsRestService> {

	@Override
	protected Class<ObsRestService> getRestServiceClass() {
		return ObsRestService.class;
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
	protected Call<Observation> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<Observation>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		return restService.getAll(restPath, representation);
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

	public void getVisitDocumentsObsByPatientAndConceptList(String patientUuid, GetMultipleCallback<Observation> callback) {
		executeMultipleCallback(callback, null, () -> {
			return restService.getVisitDocumentsObsByPatientAndConceptList(buildRestRequestPath(), patientUuid,
					"7cac8397-53cd-4f00-a6fe-028e8d743f8e,42ed45fd-f3f6-44b6-bfc2-8bde1bb41e00",
					RestConstants.Representations.FULL);
		});
	}

	protected Call<Results<Observation>> _restGetByEncounter(String restPath, PagingInfo pagingInfo, String pncounterUuid,
			String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getByEncounter(restPath, pncounterUuid, representation,
					pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getByEncounter(restPath, pncounterUuid, representation);
		}
	}

	public void getByEncounter(@NonNull Encounter encounter, boolean includeInactive,
			@Nullable PagingInfo pagingInfo,
			@NonNull GetMultipleCallback<Observation> callback) {
		checkNotNull(encounter);
		checkNotNull(callback);

		executeMultipleCallback(callback, pagingInfo,
				() -> _restGetByEncounter(buildRestRequestPath(), pagingInfo, encounter.getUuid(),
						RestConstants.Representations.FULL));
	}

}
