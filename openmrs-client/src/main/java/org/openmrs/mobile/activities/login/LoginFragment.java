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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.URLValidator;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends ACBaseFragment<LoginContract.Presenter> implements LoginContract.View {

	private static String mLastCorrectURL = "";
	private static List<Location> mLocationsList;
	final private String initialUrl = OpenMRS.getInstance().getServerUrl();
	protected OpenMRS mOpenMRS = OpenMRS.getInstance();
	private View mRootView;
	private EditText mUrl, mUsername, mPassword;
	private Button mLoginButton;
	private ProgressBar mSpinner;
	private Spinner mDropdownLocation;
	private SparseArray<Bitmap> mBitmapCache;
	private ProgressBar mLocationLoadingProgressBar;
	private LoginValidatorWatcher loginValidatorWatcher;
	private ImageView changeUrlIcon;
	private TextInputLayout loginUrlTextLayout;

	public static LoginFragment newInstance() {
		return new LoginFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_login, container, false);

		initViewFields(mRootView);

		initListeners();
		if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING)) {
			mUrl.setText(OpenMRS.getInstance().getServerUrl());
			mLastCorrectURL = OpenMRS.getInstance().getServerUrl();
		} else {
			mUrl.setText(mLastCorrectURL);
		}
		hideURLDialog();
		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return mRootView;
	}

	private void initViewFields(View mRootView) {
		mUrl = (EditText)mRootView.findViewById(R.id.loginUrlField);
		mUsername = (EditText)mRootView.findViewById(R.id.loginUsernameField);
		mUsername.setText(OpenMRS.getInstance().getUsername());
		mPassword = (EditText)mRootView.findViewById(R.id.loginPasswordField);
		mLoginButton = (Button)mRootView.findViewById(R.id.loginButton);
		mSpinner = (ProgressBar)mRootView.findViewById(R.id.loginLoading);
		mDropdownLocation = (Spinner)mRootView.findViewById(R.id.locationSpinner);
		mLocationLoadingProgressBar = (ProgressBar)mRootView.findViewById(R.id.locationLoadingProgressBar);
		changeUrlIcon = (ImageView)mRootView.findViewById(R.id.changeUrlIcon);
		loginUrlTextLayout = (TextInputLayout)mRootView.findViewById(R.id.loginUrlTextLayout);
	}

	private void hideURLDialog() {
		if (mLocationsList == null) {
			mPresenter.loadLocations(mLastCorrectURL);
		} else {
			initLoginForm(mLocationsList, mLastCorrectURL);
		}
	}

	private void initListeners() {
		changeUrlIcon.setOnClickListener(view -> {
			if (loginUrlTextLayout.getVisibility() == View.VISIBLE) {
				showEditUrlEditField(false);
			} else {
				showEditUrlEditField(true);
			}
		});

		loginValidatorWatcher = new LoginValidatorWatcher(mUrl, mUsername, mPassword, mDropdownLocation, mLoginButton);

		mUrl.setOnFocusChangeListener((view, b1) -> {
			if (StringUtils.notEmpty(mUrl.getText().toString())
					&& !view.isFocused()
					&& loginValidatorWatcher.isUrlChanged()
					|| (loginValidatorWatcher.isUrlChanged() && !view.isFocused()
					&& loginValidatorWatcher.isLocationErrorOccurred())
					|| (!loginValidatorWatcher.isUrlChanged() && !view.isFocused())) {
				((LoginFragment)getActivity()
						.getSupportFragmentManager()
						.findFragmentById(R.id.loginContentFrame))
						.setUrl(mUrl.getText().toString());
				loginValidatorWatcher.setUrlChanged(false);
			}
		});

		mLoginButton.setOnClickListener(v -> mPresenter.login(mUsername.getText().toString(),
				mPassword.getText().toString(),
				mUrl.getText().toString(),
				initialUrl));
	}

	public void login() {
		mPresenter.authenticateUser(mUsername.getText().toString(),
				mPassword.getText().toString(),
				mUrl.getText().toString());
	}

	public void login(boolean wipeDatabase) {
		mPresenter.authenticateUser(mUsername.getText().toString(),
				mPassword.getText().toString(),
				mUrl.getText().toString(), wipeDatabase);
	}

	private void showEditUrlEditField(boolean visibility) {
		if (!visibility) {
			loginUrlTextLayout.setVisibility(View.GONE);
		} else {
			loginUrlTextLayout.setVisibility(View.VISIBLE);
		}
	}

	private void setUrl(String url) {
		URLValidator.ValidationResult result = URLValidator.validate(url);
		if (result.isURLValid()) {
			mPresenter.loadLocations(result.getUrl());
		} else {
			showInvalidURLSnackbar("Invalid URL");
		}
	}

	public void showInvalidURLSnackbar(String message) {
		createSnackbar(message);
	}

	@Override
	public void hideSoftKeys() {

	}

	@Override
	public void showWarningDialog() {

	}

	@Override
	public void showLoadingAnimation() {

	}

	@Override
	public void userAuthenticated() {

	}

	@Override
	public void finishLoginActivity() {

	}

	@Override
	public void hideLoadingAnimation() {

	}

	@Override
	public void showInvalidLoginOrPasswordSnackbar() {

	}

	@Override
	public void showErrorOccured(String s) {

	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {

	}

	@Override
	public void showToast(int textId, ToastUtil.ToastType toastType) {

	}

	@Override
	public void showLocationLoadingAnimation() {

	}

	@Override
	public void onResume() {
		super.onResume();
		hideUrlLoadingAnimation();
		bindDrawableResources();
	}

	private void bindDrawableResources() {

	}

	private void hideUrlLoadingAnimation() {
		mLocationLoadingProgressBar.setVisibility(View.INVISIBLE);
		mSpinner.setVisibility(View.GONE);
	}

	public void initLoginForm(List<Location> locationsList, String serverURL) {
		setLocationErrorOccurred(false);
		mLastCorrectURL = serverURL;
		mUrl.setText(serverURL);
		mLocationsList = locationsList;
		List<String> items = getLocationStringList(locationsList);
		final LocationArrayAdapter adapter = new LocationArrayAdapter(this.getActivity(), items);
		mDropdownLocation.setAdapter(adapter);
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).contains(OpenMRS.getInstance().getLocation())) {
				mDropdownLocation.setSelection(i);
				break;
			}
		}

		mLoginButton.setEnabled(false);
		mSpinner.setVisibility(View.GONE);
	}

	private List<String> getLocationStringList(List<Location> locationList) {
		List<String> list = new ArrayList<String>();
		list.add(getString(R.string.login_location_select));
		for (int i = 0; i < locationList.size(); i++) {
			list.add(locationList.get(i).getDisplay());
		}
		return list;
	}

	public void setLocationErrorOccurred(boolean errorOccurred) {
		this.loginValidatorWatcher.setLocationErrorOccurred(errorOccurred);
		mLoginButton.setEnabled(!errorOccurred);
	}
}
