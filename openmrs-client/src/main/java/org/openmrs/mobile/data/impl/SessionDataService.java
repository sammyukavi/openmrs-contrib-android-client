package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.SessionDbService;
import org.openmrs.mobile.data.rest.SessionRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Session;

import retrofit2.Call;

/**
 * Created by dubdabasoduba on 27/05/2017.
 */

public class SessionDataService extends BaseDataService<Session, SessionDbService, SessionRestService> {
	@Override
	protected SessionDbService getDbService() {
		return null;
	}

	@Override
	protected Class<SessionRestService> getRestServiceClass() {
		return null;
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
}
