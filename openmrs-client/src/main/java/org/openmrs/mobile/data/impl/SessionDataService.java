package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.SessionDbService;
import org.openmrs.mobile.data.rest.SessionRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public class SessionDataService extends BaseDataService<Session, SessionDbService, SessionRestService> {
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

	/*public void getSession(@NonNull GetCallback<Session> callback) {
		checkNotNull(callback);

		executeSingleCallback(callback, null,
				() -> null,
				() -> restService.getSession(buildRestRequestPath()));
	}*/
}
