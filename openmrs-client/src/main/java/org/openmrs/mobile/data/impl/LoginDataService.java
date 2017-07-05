package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.SessionDbService;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.data.rest.SessionRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class LoginDataService extends BaseMetadataDataService<Session, SessionDbService, SessionRestService> {

	@Override
	protected Call<Results<Session>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected SessionDbService getDbService() {
		return new SessionDbService();
	}

	@Override
	protected Class<SessionRestService> getRestServiceClass() {
		return SessionRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "session";
	}

	@Override
	protected Call<Session> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return null;
	}

	@Override
	protected Call<Results<Session>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<Session> _restCreate(String restPath, Session entity) {
		return null;
	}

	@Override
	protected Call<Session> _restUpdate(String restPath, Session entity) {
		return null;
	}

	@Override
	protected Call<Session> _restPurge(String restPath, String uuid) {
		return null;
	}

	public void getSession(String serverURl, String username, String password, GetCallback<Session> callback) {

		restService = RestServiceBuilder.createService(getRestServiceClass(), serverURl, username, password);
		executeSingleCallback(callback, null,
				() -> null,
				() -> restService.getSession());
	}
}
