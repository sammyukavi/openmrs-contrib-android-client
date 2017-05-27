/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.login;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.UserService;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.databases.OpenMRSSQLiteOpenHelper;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

	private VisitApi visitApi;
	private UserService userService;
	private LocationDataService locationDataService;
	private LoginContract.View loginView;
	private OpenMRS mOpenMRS;
	private OpenMRSLogger mLogger;
	private AuthorizationManager authorizationManager;
	private LocationDAO locationDAO;
	private boolean mWipeRequired;

	public LoginPresenter(LoginContract.View loginView, OpenMRS openMRS) {
		this.loginView = loginView;
		this.mOpenMRS = openMRS;
		this.mLogger = openMRS.getOpenMRSLogger();
		this.loginView.setPresenter(this);
		this.authorizationManager = new AuthorizationManager();
		this.locationDAO = new LocationDAO();
		this.visitApi = new VisitApi();
		this.userService = new UserService();
		this.locationDataService = new LocationDataService();
	}

	public LoginPresenter(RestApi restApi, VisitApi visitApi, LocationDAO locationDAO,
			UserService userService, LoginContract.View loginView, OpenMRS mOpenMRS,
			OpenMRSLogger mLogger, AuthorizationManager authorizationManager) {
		this.visitApi = visitApi;
		this.locationDAO = locationDAO;
		this.userService = userService;
		this.loginView = loginView;
		this.mOpenMRS = mOpenMRS;
		this.mLogger = mLogger;
		this.authorizationManager = authorizationManager;
		this.loginView.setPresenter(this);
	}

	@Override
	public void subscribe() {
		// This method is intentionally empty
	}

	@Override
	public void login(String username, String password, String url, String oldUrl) {
		if (validateLoginFields(username, password, url)) {
			loginView.hideSoftKeys();
			if ((!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
					!mOpenMRS.getUsername().equals(username)) ||
					((!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
							!mOpenMRS.getServerUrl().equals(oldUrl))) ||
					mWipeRequired) {
				loginView.showWarningDialog();
			} else {
				authenticateUser(username, password, url);
			}
		}
	}

	@Override
	public void authenticateUser(final String username, final String password, final String url) {
		authenticateUser(username, password, url, mWipeRequired);
	}

	@Override
	public void authenticateUser(final String username, final String password, final String url,
			final boolean wipeDatabase) {
		loginView.showLoadingAnimation();
		mWipeRequired = wipeDatabase;
		RestApi restApi = RestServiceBuilder.createService(RestApi.class,null, username, password);
		Call<Session> call = restApi.getSession();
		call.enqueue(new Callback<Session>() {
			@Override
			public void onResponse(Call<Session> call, Response<Session> response) {
				if (response.isSuccessful()) {
					mLogger.d(response.body().toString());
					Session session = response.body();
					if (session.isAuthenticated()) {
						if (wipeDatabase) {
							mOpenMRS.deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
							setData(session.getSessionId(), url, username, password);
							mWipeRequired = false;
						}
						if (authorizationManager.isUserNameOrServerEmpty()) {
							setData(session.getSessionId(), url, username, password);
						} else {
							mOpenMRS.setSessionToken(session.getSessionId());
						}

						setLogin(true, url);
						userService.updateUserInformation(username);

						loginView.userAuthenticated();
						loginView.finishLoginActivity();
					} else {
						loginView.hideLoadingAnimation();
						loginView.showInvalidLoginOrPasswordSnackbar();
					}
				} else {
					loginView.hideLoadingAnimation();
					loginView.showToast(response.message(), ToastUtil.ToastType.ERROR);
				}
			}

			@Override
			public void onFailure(Call<Session> call, Throwable t) {
				loginView.hideLoadingAnimation();
				loginView.showToast(t.getMessage(), ToastUtil.ToastType.ERROR);
			}
		});
	}

	@Override
	public void saveLocationsToDatabase(List<Location> locationList, String selectedLocation) {
		mOpenMRS.setLocation(selectedLocation);
		locationDAO.deleteAllLocations();
		for (int i = 0; i < locationList.size(); i++) {
			locationDAO.saveLocation(locationList.get(i))
					.observeOn(Schedulers.io())
					.subscribe();
		}
	}

	@Override
	public void loadLocations(final String url) {
		loginView.showLocationLoadingAnimation();
		PagingInfo pagingInfo = new PagingInfo(0, 100);
		DataService.GetCallback<List<Location>> locationGetCallback = new DataService.GetCallback<List<Location>>() {

			@Override
			public void onCompleted(List<Location> locations) {
				if (!locations.isEmpty()) {
					RestServiceBuilder.changeBaseUrl(url.trim());
					mOpenMRS.setServerUrl(url);
					loginView.initLoginForm(locations, url);
					loginView.startFormListService();
					loginView.setLocationErrorOccurred(false);
				} else {
					loginView.showInvalidURLSnackbar("Failed to fetch server's locations");
					loginView.setLocationErrorOccurred(true);
					loginView.initLoginForm(new ArrayList<Location>(), url);
				}
				loginView.hideUrlLoadingAnimation();
			}

			@Override
			public void onError(Throwable t) {
				loginView.hideUrlLoadingAnimation();
				loginView.showInvalidURLSnackbar(t.getMessage());
				loginView.initLoginForm(new ArrayList<Location>(), url);
				loginView.setLocationErrorOccurred(true);
				t.printStackTrace();
			}
		};
		locationDataService.getAll(QueryOptions.LOAD_RELATED_OBJECTS, pagingInfo, locationGetCallback);
	}

	@Override
	public void showEditUrlEditText(boolean visibility) {
		loginView.showEditUrlEditField(true);
	}

	private boolean validateLoginFields(String username, String password, String url) {
		return StringUtils.notEmpty(username) || StringUtils.notEmpty(password) || StringUtils.notEmpty(url);
	}

	private void setData(String sessionToken, String url, String username, String password) {
		mOpenMRS.setSessionToken(sessionToken);
		mOpenMRS.setServerUrl(url);
		mOpenMRS.setUsername(username);
		mOpenMRS.setPassword(password);
	}

	private void setLogin(boolean isLogin, String serverUrl) {
		mOpenMRS.setUserLoggedOnline(isLogin);
		mOpenMRS.setLastLoginServerUrl(serverUrl);
	}
}
