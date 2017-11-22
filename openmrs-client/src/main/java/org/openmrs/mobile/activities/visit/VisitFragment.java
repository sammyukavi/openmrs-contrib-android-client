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

package org.openmrs.mobile.activities.visit;

import android.os.Bundle;

import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.utilities.ToastUtil;

public class VisitFragment extends ACBaseFragment<VisitContract.VisitDashboardPresenter>
		implements VisitContract.VisitDashboardView {

	private VisitActivity visitActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public static VisitFragment newInstance() {
		return new VisitFragment();
	}

	public void refreshBaseData() {
		mPresenter.refreshBaseData();
	}

	@Override
	public void displayRefreshingData(boolean visible) {
		visitActivity.displayRefreshingData(visible);
	}

	@Override
	public void refreshDependentData() {
		visitActivity.refreshDependentData();
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(getContext(), toastType, message);
	}

	public void setActivity(VisitActivity visitActivity) {
		this.visitActivity = visitActivity;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		visitActivity = null;
	}
}
