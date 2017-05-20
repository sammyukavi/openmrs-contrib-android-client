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

package org.openmrs.mobile.activities.auditdata;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class AuditDataPresenter extends BasePresenter implements AuditDataContract.Presenter {

	private AuditDataContract.View auditDataView;
	private DataService<Patient> patientDataService;
	private PatientDashboardContract.View patientDashboardView;
	private VisitDataService visitDataService;
	private ObsDataService observationDataService;

	public AuditDataPresenter(AuditDataContract.View view, OpenMRS openMRS) {
		this.auditDataView = view;
		this.auditDataView.setPresenter(this);
		this.patientDataService = new PatientDataService();
		this.visitDataService = new VisitDataService();
	}

	@Override
	public void subscribe() {

	}

	@Override
	public void fetchPatientData(String uuid) {

		patientDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				if (patient != null) {
					auditDataView.updateContactCard(patient);
				}
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				//auditDataView.showSnack("Error occured: Unable to reach server");
			}
		});
	}

	@Override
	public void fetchVisitData(String visitUuid) {
		visitDataService.getByUUID(visitUuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit visit) {
				for (Encounter encounter : visit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case ApplicationConstants.EncounterTypeEntity.AuditData:
							observationDataService.getByEncounter(encounter, QueryOptions.LOAD_RELATED_OBJECTS, new
									PagingInfo(0, 100), new DataService.GetCallback<List<Observation>>() {
								@Override
								public void onCompleted(List<Observation> observations) {
									//ConsoleLogger.dump(observations);
								}

								@Override
								public void onError(Throwable t) {

								}
							});

							break;
					}
				}
			}

			@Override
			public void onError(Throwable t) {

			}
		});
	}
}