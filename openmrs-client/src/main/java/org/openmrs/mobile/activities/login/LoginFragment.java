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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.patientlist.PatientListActivity;
import org.openmrs.mobile.api.FormListService;
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

	private void initListeners() {
		changeUrlIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (loginUrlTextLayout.getVisibility() == View.VISIBLE) {
					showEditUrlEditField(false);
				} else {
					showEditUrlEditField(true);
				}
			}
		});

		loginValidatorWatcher = new LoginValidatorWatcher(mUrl, mUsername, mPassword, mDropdownLocation, mLoginButton);

		mUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
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
			}
		});

		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.login(mUsername.getText().toString(),
						mPassword.getText().toString(),
						mUrl.getText().toString(),
						initialUrl);
			}
		});
	}

	private void initViewFields(View root) {
		mUrl = (EditText)root.findViewById(R.id.loginUrlField);
		mUsername = (EditText)root.findViewById(R.id.loginUsernameField);
		mUsername.setText(OpenMRS.getInstance().getUsername());
		mPassword = (EditText)root.findViewById(R.id.loginPasswordField);
		mLoginButton = (Button)root.findViewById(R.id.loginButton);
		mSpinner = (ProgressBar)root.findViewById(R.id.loginLoading);
		mDropdownLocation = (Spinner)root.findViewById(R.id.locationSpinner);
		mLocationLoadingProgressBar = (ProgressBar)root.findViewById(R.id.locationLoadingProgressBar);
		changeUrlIcon = (ImageView)root.findViewById(R.id.changeUrlIcon);
		loginUrlTextLayout = (TextInputLayout)root.findViewById(R.id.loginUrlTextLayout);
	}

	@Override
	public void onResume() {
		super.onResume();
		hideUrlLoadingAnimation();
		bindDrawableResources();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindDrawableResources();
	}

	@Override
	public void hideSoftKeys() {
		View view = this.getActivity().getCurrentFocus();
		if (view == null) {
			view = new View(this.getActivity());
		}
		InputMethodManager inputMethodManager =
				(InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
	public void showLoadingAnimation() {
		//mLoginFormView.setVisibility(View.GONE);
		//mSpinner.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideLoadingAnimation() {
		//mLoginFormView.setVisibility(View.VISIBLE);
		//mSpinner.setVisibility(View.GONE);
	}

	@Override
	public void showLocationLoadingAnimation() {
		mLoginButton.setEnabled(false);
		mLocationLoadingProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideUrlLoadingAnimation() {
		mLocationLoadingProgressBar.setVisibility(View.INVISIBLE);
		mSpinner.setVisibility(View.GONE);
	}

	@Override
	public void finishLoginActivity() {
		getActivity().finish();
	}

	private void bindDrawableResources() {
		mBitmapCache = new SparseArray<>();
		ImageView openMrsLogoImage = (ImageView)getActivity().findViewById(R.id.openmrsLogo);
		createImageBitmap(R.drawable.banda_logo, openMrsLogoImage.getLayoutParams());
		openMrsLogoImage.setImageBitmap(mBitmapCache.get(R.drawable.banda_logo));
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

	@Override
	public void userAuthenticated() {
		Intent intent = new Intent(mOpenMRS.getApplicationContext(), PatientListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mOpenMRS.getApplicationContext().startActivity(intent);
		mPresenter.saveLocationsToDatabase(mLocationsList, mDropdownLocation.getSelectedItem().toString());
	}

	@Override
	public void startFormListService() {
		Intent i = new Intent(getContext(), FormListService.class);
		getActivity().startService(i);
	}

	@Override
	public void showEditUrlEditField(boolean visibility) {
		if (!visibility) {
			mUrl.setVisibility(View.GONE);
		} else {
			mUrl.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void showInvalidURLSnackbar(String message) {
		createSnackbar(message)
				.setAction(getResources().getString(R.string.snackbar_edit), view -> {
					mUrl.requestFocus();
					mUrl.selectAll();
				})
				.show();
	}

	@Override
	public void showInvalidLoginOrPasswordSnackbar() {
		String message = getResources().getString(R.string.invalid_login_or_password_message);
		createSnackbar(message)
				.setAction(getResources().getString(R.string.snackbar_edit), view -> {
					mPassword.requestFocus();
					mPassword.selectAll();
				})
				.show();
	}

	private Snackbar createSnackbar(String message) {
		return Snackbar
				.make(mRootView, message, Snackbar.LENGTH_LONG);
	}

	@Override
	public void setLocationErrorOccurred(boolean errorOccurred) {
		this.loginValidatorWatcher.setLocationErrorOccurred(errorOccurred);
		mLoginButton.setEnabled(!errorOccurred);
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(getContext(), toastType, message);
	}

	@Override
	public void showToast(int textId, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(getContext(), toastType, getResources().getString(textId));
	}

	private List<String> getLocationStringList(List<Location> locationList) {
		List<String> list = new ArrayList<String>();
		list.add(getString(R.string.login_location_select));
		for (int i = 0; i < locationList.size(); i++) {
			list.add(locationList.get(i).getDisplay());
		}
		return list;
	}

	public void setUrl(String url) {
		URLValidator.ValidationResult result = URLValidator.validate(url);
		if (result.isURLValid()) {
			mPresenter.loadLocations(result.getUrl());
		} else {
			showInvalidURLSnackbar("Invalid URL");
		}
	}

	public void hideURLDialog() {
		if (mLocationsList == null) {
			mPresenter.loadLocations(mLastCorrectURL);
		} else {
			initLoginForm(mLocationsList, mLastCorrectURL);
		}
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

}
