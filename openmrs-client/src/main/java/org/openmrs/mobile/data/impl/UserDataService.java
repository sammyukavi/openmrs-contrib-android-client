package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.UserDbService;
import org.openmrs.mobile.data.rest.UserRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class UserDataService extends BaseDataService<User, UserDbService, UserRestService> {

	@Override
	protected UserDbService getDbService() {
		return new UserDbService();
	}

	@Override
	protected Class<UserRestService> getRestServiceClass() {
		return UserRestService.class;
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "user";
	}

	@Override
	protected Call<User> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return restService.getByUuid(restPath, uuid, QueryOptions.getRepresentation(options));
	}

	@Override
	protected Call<Results<User>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<User> _restCreate(String restPath, User entity) {
		return null;
	}

	@Override
	protected Call<User> _restUpdate(String restPath, User entity) {
		return null;
	}

	@Override
	protected Call<User> _restPurge(String restPath, String uuid) {
		return null;
	}

	public void getByUsername(String username, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<User>> callback) {

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByUsername(username, options, pagingInfo),
				() -> restService.getByUsername(buildRestRequestPath(), username,
						QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
						PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo)));
	}
}

