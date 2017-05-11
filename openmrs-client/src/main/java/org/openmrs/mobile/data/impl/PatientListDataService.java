package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.MetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientListDbService;
import org.openmrs.mobile.data.rest.PatientListRestService;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class PatientListDataService
		extends BaseMetadataDataService<PatientList, PatientListDbService, PatientListRestService>
		implements MetadataDataService<PatientList> {
	@Override
	protected PatientListDbService getDbService() {
		return new PatientListDbService();
	}

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
		return "patientList";
	}

	@Override
	protected Call<PatientList> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options));
	}

	@Override
	protected Call<Results<PatientList>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getAll(restPath, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options), PagingInfo.getLimit(pagingInfo),
				PagingInfo.getStartIndex(pagingInfo));
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
	protected Call<Results<PatientList>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo) {
		return restService.getByNameFragment(restPath, name, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options), PagingInfo.getLimit(pagingInfo),
				PagingInfo.getStartIndex(pagingInfo));
	}
}
