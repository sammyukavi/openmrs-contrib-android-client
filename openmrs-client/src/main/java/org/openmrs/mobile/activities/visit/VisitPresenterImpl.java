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

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

public class VisitPresenterImpl extends BasePresenter implements VisitContract.VisitDashboardPresenter {

	private VisitContract.VisitDashboardView visitDashboardView;
	private VisitDataService visitDataService;

	private String visitUuid;

	public VisitPresenterImpl(VisitContract.VisitDashboardView visitDashboardView, String visitUuid) {
		this.visitDashboardView = visitDashboardView;
		this.visitUuid = visitUuid;

		visitDataService = dataAccess().visit();
	}

	@Override
	public void subscribe() {
		// Intentionally left blank
	}

	@Override
	public void refreshBaseData() {
		visitDashboardView.displayRefreshingData(true);
		getVisit(true);
	}

	private void getVisit(boolean forceRefresh) {
		QueryOptions options = QueryOptions.FULL_REP;
		if (forceRefresh) {
			options = new QueryOptions.Builder().requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL)
					.customRepresentation(RestConstants.Representations.FULL).build();
		}

		visitDataService.getByUuid(visitUuid, options, new DataService.GetCallback<Visit>() {

			@Override
			public void onCompleted(Visit visit) {
				visitDashboardView.refreshDependentData();
			}

			@Override
			public void onError(Throwable t) {
				visitDashboardView.displayRefreshingData(false);
				visitDashboardView
						.showToast(ApplicationConstants.entityName.VISITS + ApplicationConstants.toastMessages
								.fetchErrorMessage, ToastUtil.ToastType.ERROR);
			}
		});
	}
}
