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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.patientlist.PatientListActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.URLValidator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.INVALID_URL;

public class LoginFragment extends ACBaseFragment<LoginContract.Presenter> implements LoginContract.View {

	private static String mLoginUrl = "";
	private static List<HashMap<String, String>> mLocationsList;
	protected OpenMRS mOpenMRS = OpenMRS.getInstance();
	private View mRootView;
	private TextInputEditText mUrl, mUsername, mPassword;
	private Button mLoginButton;
	private ProgressBar mLoadingProgressBar;
	private Spinner mDropdownLocation;
	private SparseArray<Bitmap> mBitmapCache;
	private LoginValidatorWatcher mLoginValidatorWatcher;
	private ImageView mChangeUrlIcon;
	private TextInputLayout mLoginUrlTextLayout;
	private View mViewsContainer;

	public static LoginFragment newInstance() {
		return new LoginFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_login, container, false);

		if (mLoginUrl.equals(ApplicationConstants.EMPTY_STRING)) {
			mLoginUrl = OpenMRS.getInstance().getServerUrl();
		}

		initViewFields(mRootView);

		initListeners();

		loadLocations();

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return mRootView;
	}

	private void initViewFields(View mRootView) {

		mViewsContainer = mRootView.findViewById(R.id.viewsContainer);

		mUrl = (TextInputEditText)mRootView.findViewById(R.id.loginUrlField);

		mDropdownLocation = (Spinner)mRootView.findViewById(R.id.locationSpinner);

		mUsername = (TextInputEditText)mRootView.findViewById(R.id.loginUsernameField);

		mUsername.setText(OpenMRS.getInstance().getUsername());

		mPassword = (TextInputEditText)mRootView.findViewById(R.id.loginPasswordField);

		mLoginButton = (Button)mRootView.findViewById(R.id.loginButton);

		mLoadingProgressBar = (ProgressBar)mRootView.findViewById(R.id.loadingProgressBar);

		mChangeUrlIcon = (ImageView)mRootView.findViewById(R.id.changeUrlIcon);

		mLoginUrlTextLayout = (TextInputLayout)mRootView.findViewById(R.id.loginUrlTextLayout);

		mUrl.setText(mLoginUrl);
	}

	private void loadLocations() {

		mLocationsList = new ArrayList<>();

		String locationsStr = mOpenMRS.getLocations();

		if (StringUtils.notEmpty(locationsStr)) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<HashMap<String, String>>>() {
			}.getType();

			mLocationsList = gson.fromJson(locationsStr, type);

		}

		if (mLocationsList.isEmpty()) {

			mPresenter.loadLocations(mLoginUrl);

		} else {

			updateLocationsSpinner(mLocationsList, mLoginUrl);

		}

	}

	private void initListeners() {
		mChangeUrlIcon.setOnClickListener(view -> {
			if (mLoginUrlTextLayout.getVisibility() == View.VISIBLE) {
				showEditUrlEditField(false);
			} else {
				showEditUrlEditField(true);
			}
		});

		mLoginValidatorWatcher = new LoginValidatorWatcher(mUrl, mUsername, mPassword, mDropdownLocation, mLoginButton);

		mUrl.setOnFocusChangeListener((view, b1) -> {
			if (StringUtils.notEmpty(mUrl.getText().toString())
					&& !view.isFocused()
					&& mLoginValidatorWatcher.isUrlChanged()
					|| (mLoginValidatorWatcher.isUrlChanged() && !view.isFocused()
					&& mLoginValidatorWatcher.isLocationErrorOccurred())
					|| (!mLoginValidatorWatcher.isUrlChanged() && !view.isFocused())) {
				((LoginFragment)getActivity()
						.getSupportFragmentManager()
						.findFragmentById(R.id.loginContentFrame))
						.setUrl(mUrl.getText().toString());
				mLoginValidatorWatcher.setUrlChanged(false);
			}
		});

		mLoginButton.setOnClickListener(v -> mPresenter.login(mUsername.getText().toString(),
				mPassword.getText().toString(),
				mUrl.getText().toString(),
				mLoginUrl));
	}

	public void login(boolean wipeDatabase) {
		mPresenter.authenticateUser(mUsername.getText().toString(),
				mPassword.getText().toString(),
				mUrl.getText().toString(), wipeDatabase);
	}

	private void showEditUrlEditField(boolean visibility) {
		if (!visibility) {
			mLoginUrlTextLayout.setVisibility(View.GONE);
		} else {
			mLoginUrlTextLayout.setVisibility(View.VISIBLE);
		}
	}

	private void setUrl(String url) {

		URLValidator.ValidationResult result = URLValidator.validate(url);

		if (result.isURLValid()) {

			//Append forward slash. Retrofit throws a serious error if a base url does not end with a forward slash

			url = result.getUrl();

			if (!url.endsWith("/")) {
				url += "/";
			}

			mPresenter.loadLocations(url);

		} else {

			showMessage(INVALID_URL);

		}
	}

	@Override
	public void showWarningDialog() {
		CustomDialogBundle bundle = new CustomDialogBundle();
		bundle.setTitleViewMessage(getString(R.string.warning_dialog_title));
		bundle.setTextViewMessage(getString(R.string.warning_lost_data_dialog));
		bundle.setRightButtonText(getString(R.string.dialog_button_ok));
		bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.LOGIN);
		((LoginActivity)this.getActivity())
				.createAndShowDialog(bundle, ApplicationConstants.DialogTAG.WARNING_LOST_DATA_DIALOG_TAG);
	}

	@Override
	public void userAuthenticated() {

		mPresenter.saveLocationsInPreferences(mLocationsList, mDropdownLocation.getSelectedItemPosition());

		Intent intent = new Intent(mOpenMRS.getApplicationContext(), PatientListActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		mOpenMRS.getApplicationContext().startActivity(intent);

	}

	@Override
	public void finishLoginActivity() {
		getActivity().finish();
	}

	public void showToast(String message, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(getContext(), toastType, message);
	}

	public void showToast(int textId, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(getContext(), toastType, getResources().getString(textId));
	}

	@Override
	public void onResume() {
		super.onResume();
		bindDrawableResources();
	}

	private void bindDrawableResources() {
		mBitmapCache = new SparseArray<>();
		ImageView bandaHealthLogo = (ImageView)getActivity().findViewById(R.id.bandaHealthLogo);
		createImageBitmap(R.drawable.banda_logo, bandaHealthLogo.getLayoutParams());
		bandaHealthLogo.setImageBitmap(mBitmapCache.get(R.drawable.banda_logo));
	}

	private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
		if (mBitmapCache.get(key) == null) {
			mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
					layoutParams.width, layoutParams.height));
		}
	}

	private void unbindDrawableResources() {
		if (null != mBitmapCache) {
			for (int i = 0; i < mBitmapCache.size(); i++) {
				Bitmap bitmap = mBitmapCache.valueAt(i);
				bitmap.recycle();
			}
		}
	}

	@Override
	public void updateLoginFormLocations(List<Location> locationsList, String serverURL) {

		mLoginUrl = serverURL;

		mUrl.setText(serverURL);

		List<HashMap<String, String>> items = getLocationStringList(locationsList);

		updateLocationsSpinner(items, serverURL);

	}

	private void updateLocationsSpinner(List<HashMap<String, String>> locations, String serverURL) {

		mLocationsList = locations;

		setProgressBarVisibility(true);

		setViewsContainerVisibility(false);

		mLoginUrl = serverURL;

		mUrl.setText(serverURL);

		int selectedLocation = 0;

		String[] spinnerArray = new String[locations.size()];
		for (int i = 0; i < locations.size(); i++) {
			spinnerArray[i] = locations.get(i).get("display");
			if (locations.get(i).get("uuid").contains(OpenMRS.getInstance().getLocation())) {
				selectedLocation = i;
			}
		}

		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDropdownLocation.setAdapter(adapter);

		mDropdownLocation.setSelection(selectedLocation);

		setProgressBarVisibility(false);

		setViewsContainerVisibility(true);

	}

	@Override
	public void showMessage(String message) {
		super.showError(message);
	}

	@Override
	public void showMessage(int errorCode) {
		super.showError(errorCode);
	}

	@Override
	public void setProgressBarVisibility(boolean visible) {
		if (visible) {
			ACBaseActivity.hideSoftKeyboard(getActivity());
		}
		mLoadingProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setViewsContainerVisibility(boolean visible) {
		mViewsContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	private List<HashMap<String, String>> getLocationStringList(List<Location> locationList) {

		List<HashMap<String, String>> locations = new ArrayList<>();

		for (Location loc : locationList) {

			HashMap<String, String> location = new HashMap<>();

			location.put("uuid", loc.getUuid());
			location.put("display", loc.getDisplay());
			location.put("parentlocationuuid", loc.getParentLocation().getUuid());

			locations.add(location);
		}

		return locations;
	}

}
