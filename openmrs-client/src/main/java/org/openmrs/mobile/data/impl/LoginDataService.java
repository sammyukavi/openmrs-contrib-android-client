package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.activities.patientdashboard.ConsoleLogger;
import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.SessionDbService;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.data.rest.SessionRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Session;

import java.io.Console;

import retrofit2.Call;

public class LoginDataService extends BaseMetadataDataService<Session, SessionDbService, SessionRestService> {

	@Override
	protected Call<Results<Session>> _restGetByNameFragment(String restPath, String name, QueryOptions options,
			PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected SessionDbService getDbService() {
		return null;
	}

	@Override
	protected Class<SessionRestService> getRestServiceClass() {
		return SessionRestService.class;
	}

	@Override
	protected String getRestPath() {
		return null;
	}

	@Override
	protected String getEntityName() {
		return null;
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
		//serviceClass, app.getServerUrl(), app.getUsername(), app.getPassword()
		//restService = RestServiceBuilder.createService(getRestServiceClass());
		//restService.getSession(buildRestRequestPath(), callback);
		ConsoleLogger.dump(getRestServiceClass());
	}
}
