package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.MetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.PatientListRestService;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class PatientListDataService extends BaseMetadataDataService<PatientList, PatientListRestService>
		implements MetadataDataService<PatientList> {
	@Override
	protected Class<PatientListRestService> getRestServiceClass() {
		return PatientListRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "patientlist/list";
	}

	@Override
	protected Call<PatientList> _restGetByUuid(String restPath, String uuid, String representation) {
		return restService.getByUuid(restPath, uuid, representation);
	}

	@Override
	protected Call<Results<PatientList>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService.getAll(restPath, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getAll(restPath, representation);
		}
	}

	@Override
	protected Call<PatientList> _restCreate(String restPath, PatientList entity) {
		return restService.create(restPath, entity);
	}

	@Override
	protected Call<PatientList> _restUpdate(String restPath, PatientList entity) {
		return restService.update(restPath, entity.getUuid(), entity);
	}

	@Override
	protected Call<PatientList> _restPurge(String restPath, String uuid) {
		return restService.purge(restPath, uuid);
	}

	@Override
	protected Call<Results<PatientList>> _restGetByNameFragment(String restPath, PagingInfo pagingInfo, String name,
			String representation) {
		if (isPagingValid(pagingInfo)) {
			return restService
					.getByNameFragment(restPath, name, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
		} else {
			return restService.getByNameFragment(restPath, name, representation);
		}
	}
}
