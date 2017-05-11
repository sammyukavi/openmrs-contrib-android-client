package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.BaseEntityDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.EntityDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.VisitRestService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitDataService extends BaseDataService<Visit, VisitRestService>
		implements DataService<Visit> {
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
	protected Call<Visit> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, RestConstants.Representations.FULL);
	}

	@Override
	protected Call<Results<Visit>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getAll(restPath, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getAll(restPath, representation);
		}
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

	protected Call<Results<Visit>> _restGetByPatient(String restPath, PagingInfo pagingInfo, String patientUuid,
			boolean includeInactive,
			String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getByPatient(restPath, patientUuid, representation,
					pagingInfo.getLimit(), pagingInfo.getStartIndex(), includeInactive);
		} else {
			return restService.getByPatient(restPath, patientUuid, representation, includeInactive);
		}
	}

	public void getByPatient(@NonNull Patient patient, boolean includeInactive,
			@Nullable PagingInfo pagingInfo,
			@NonNull GetMultipleCallback<Visit> callback) {
		executeMultipleCallback(callback, pagingInfo, () -> {
			if (isPagingValid(pagingInfo)) {
				return restService.getByPatient(buildRestRequestPath(), patient.getUuid(), RestConstants.Representations.FULL,
						pagingInfo.getLimit(), pagingInfo.getStartIndex(), includeInactive);
			} else {
				return restService.getByPatient(buildRestRequestPath(), patient.getUuid(), RestConstants.Representations.FULL,
						includeInactive);
			}
		});
	}

	public void endVisit(String uuid, String stopDatetime, GetSingleCallback<Visit> callback) {
		executeSingleCallback(callback, () -> {
			return restService.endVisit(buildRestRequestPath(), uuid, stopDatetime);
		});
	}
}

