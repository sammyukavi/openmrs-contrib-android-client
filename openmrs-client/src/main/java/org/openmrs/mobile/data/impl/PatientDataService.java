package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.rest.PatientRestService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class PatientDataService extends BaseDataService<Patient, PatientDbService, PatientRestService> {
	@Override
	protected PatientDbService getDbService() {
		return new PatientDbService();
	}

	@Override
	protected Class<PatientRestService> getRestServiceClass() {
		return PatientRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "patient";
	}

	@Override
	public void getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<Patient>> callback) {
		// The patient rest service does not support getting all patients
		return;
	}

	// Begin Retrofit Workaround

	@Override
	protected Call<Patient> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options),
				QueryOptions.getIncludeInactive(options));
	}

	@Override
	protected Call<Results<Patient>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		throw new UnsupportedOperationException("The patients rest service does not support a get all method.");
	}

	@Override
	protected Call<Patient> _restCreate(String restPath, Patient entity) {
		return restService.create(restPath, entity);
	}

	@Override
	protected Call<Patient> _restUpdate(String restPath, Patient entity) {
		return restService.update(restPath, entity.getUuid(), entity);
	}

	@Override
	protected Call<Patient> _restPurge(String restPath, String uuid) {
		return restService.purge(restPath, uuid);
	}

	// End Retrofit Workaround

	public void getByName(String name, QueryOptions options, PagingInfo pagingInfo, GetCallback<List<Patient>> callback) {
		executeMultipleCallback(callback, pagingInfo,
				() -> dbService.getByName(name, options, pagingInfo),
				() -> {
					return restService.getByName(buildRestRequestPath(), name, QueryOptions.getRepresentation(options),
							QueryOptions.getIncludeInactive(options), PagingInfo.getLimit(pagingInfo),
							PagingInfo.getStartIndex(pagingInfo));
				});
	}

	public void getByNameAndIdentifier(String query, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<Patient>> callback) {
		executeMultipleCallback(callback, pagingInfo,
				() -> null,
				() -> restService.getByNameAndIdentifier(buildRestRequestPath(), query, query,
						QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
						PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo))
		);
	}

	public void getLastViewed(String lastViewed, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<Patient>> callback) {
		executeMultipleCallback(callback, pagingInfo,
				() -> null,
				() -> restService.getLastViewed(buildRestRequestPath(), lastViewed,
						QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
						PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo))
		);
	}
}
