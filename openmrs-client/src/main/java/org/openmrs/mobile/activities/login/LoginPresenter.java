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

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.UserService;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.LoginDataService;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.databases.OpenMRSSQLiteOpenHelper;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import rx.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

	private LoginContract.View loginView;
	private OpenMRS mOpenMRS;
	private boolean mWipeRequired;
	private AuthorizationManager authorizationManager;
	private LoginDataService loginDataService;
	private LocationDataService locationDataService;
	private UserService userService;
	private LocationDAO locationDAO;

	public LoginPresenter(LoginContract.View view, OpenMRS mOpenMRS) {
		this.loginView = view;
		this.loginView.setPresenter(this);
		this.mOpenMRS = mOpenMRS;
		this.authorizationManager = new AuthorizationManager();
		this.loginDataService = new LoginDataService();
		this.userService = new UserService();
		this.locationDataService = new LocationDataService();
		this.locationDAO = new LocationDAO();
	}

	@Override
	public void subscribe() {
		//intentionally left blank
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
		if (NetworkUtils.isOnline()) {
			mWipeRequired = wipeDatabase;

			DataService.GetCallback<Session> loginUserCallback = new DataService.GetCallback<Session>() {
				@Override
				public void onCompleted(Session session) {
					if (session != null) {
						if (session.isAuthenticated()) {
							RestServiceBuilder.setBaseUrl(false);
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
						loginView.showErrorOccured("An error occured logging you in. Is the server online?");
					}
				}

				@Override
				public void onError(Throwable t) {
					loginView.showErrorOccured(t.getMessage());
				}
			};

			loginDataService.getSession(url, username, password, loginUserCallback);

		} else {
			if (mOpenMRS.isUserLoggedOnline() && url.equals(mOpenMRS.getLastLoginServerUrl())) {
				if (mOpenMRS.getUsername().equals(username) && mOpenMRS.getPassword().equals(password)) {
					mOpenMRS.setSessionToken(mOpenMRS.getLastSessionToken());
					loginView.showToast("LoggedIn in offline mode.", ToastUtil.ToastType.NOTICE);
					loginView.userAuthenticated();
					loginView.finishLoginActivity();
				} else {
					loginView.hideLoadingAnimation();
					loginView.showToast(R.string.auth_failed_dialog_message,
							ToastUtil.ToastType.ERROR);
				}
			} else if (NetworkUtils.hasNetwork()) {
				loginView.showToast(R.string.offline_mode_unsupported_in_first_login,
						ToastUtil.ToastType.ERROR);
				loginView.hideLoadingAnimation();
			} else {
				loginView.showToast(R.string.no_internet_conn_dialog_message,
						ToastUtil.ToastType.ERROR);
				loginView.hideLoadingAnimation();
			}
		}
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
	public void loadLocations(String url) {
		loginView.showLocationLoadingAnimation();

		locationDataService.getAll(new DataService.GetCallback<List<Location>>() {
			@Override
			public void onCompleted(List<Location> locations) {
				RestServiceBuilder.setBaseUrl(true);
				mOpenMRS.setServerUrl(url);
				loginView.initLoginForm(locations, url);
				loginView.setLocationErrorOccurred(false);
				hideLocationLoadingAnimation();
			}

			@Override
			public void onError(Throwable t) {
				hideLocationLoadingAnimation();
				loginView.showInvalidURLSnackbar(t.getMessage());
				loginView.initLoginForm(new ArrayList<Location>(), url);
				loginView.setLocationErrorOccurred(true);
			}
		});

	}

	private void hideLocationLoadingAnimation() {
		loginView.hideLocationLoadingAnimation();
		loginView.hideUrlLoadingAnimation();
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