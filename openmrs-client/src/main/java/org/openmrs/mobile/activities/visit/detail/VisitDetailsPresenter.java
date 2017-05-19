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

package org.openmrs.mobile.activities.visit.detail;

import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitPresenterImpl;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitPredefinedTaskDataService;
import org.openmrs.mobile.data.impl.VisitTaskDataService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

public class VisitDetailsPresenter extends VisitPresenterImpl implements VisitContract.VisitDetailsPresenter {

	VisitContract.VisitDetailsView visitDetailsView;
	private VisitPredefinedTaskDataService visitPredefinedTaskDataService;
	private VisitTaskDataService visitTaskDataService;
	private VisitDataService visitDataService;
	private ConceptDataService conceptDataService;
	private String patientUUID, visitUUID, providerUuid;

	private int page = 1;
	private int limit = 10;

	public VisitDetailsPresenter(String patientUuid, String visitUuid, String providerUuid, VisitContract.VisitDetailsView
			visitDetailsView) {
		this.visitDetailsView = visitDetailsView;
		this.visitDetailsView.setPresenter(this);
		this.visitPredefinedTaskDataService = new VisitPredefinedTaskDataService();
		this.visitTaskDataService = new VisitTaskDataService();
		this.visitDataService = new VisitDataService();
		this.conceptDataService = new ConceptDataService();
		this.visitUUID = visitUuid;
		this.patientUUID = patientUuid;
		this.providerUuid = providerUuid;
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void unsubscribe() {

	}

	@Override
	public void getVisit() {
		DataService.GetCallback<Visit> getSingleCallback =
				new DataService.GetCallback<Visit>() {
					@Override
					public void onCompleted(Visit entity) {
						if (entity != null) {
							visitDetailsView.setVisit(entity);
						}
					}

					@Override
					public void onError(Throwable t) {
						visitDetailsView
								.showToast(ApplicationConstants.entityName.VISITS + ApplicationConstants.toastMessages
										.fetchErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		visitDataService.getByUUID(visitUUID, QueryOptions.LOAD_RELATED_OBJECTS, getSingleCallback);
	}

	@Override
	public void getConcept(String uuid) {
		conceptDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService
				.GetCallback<Concept>() {

			@Override
			public void onCompleted(Concept conceptAnswer) {
				conceptAnswer.getDisplay();
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}

}
