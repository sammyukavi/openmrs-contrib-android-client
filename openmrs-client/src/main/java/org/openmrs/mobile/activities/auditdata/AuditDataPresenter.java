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
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeDisplays.AUDITDATA;

public class AuditDataPresenter extends BasePresenter implements AuditDataContract.Presenter {

	private AuditDataContract.View auditDataView;
	private DataService<Patient> patientDataService;
	private PatientDashboardContract.View patientDashboardView;
	private VisitDataService visitDataService;

	private EncounterDataService encounterDataService;
	private LocationDataService locationDataService;

	public AuditDataPresenter(AuditDataContract.View view) {
		this.auditDataView = view;
		this.auditDataView.setPresenter(this);
		this.patientDataService = new PatientDataService();
		this.visitDataService = new VisitDataService();
		this.encounterDataService = new EncounterDataService();
		this.locationDataService = new LocationDataService();
	}

	@Override
	public void subscribe() {

	}

	@Override
	public void fetchPatientDetails(String uuid) {

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
			}
		});
	}

	@Override
	public void fetchVisit(String visitUuid) {

		DataService.GetCallback<Visit> fetchEncountersCallback = new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit visit) {
				auditDataView.setVisit(visit);
				auditDataView.updateStartDate(visit.getStartDatetime());
				for (Encounter encounter : visit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case AUDITDATA:
							fetchEncounter(encounter.getUuid());
							break;
					}
				}
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};
		visitDataService.getByUUID(visitUuid, QueryOptions.LOAD_RELATED_OBJECTS, fetchEncountersCallback);
	}

	private void fetchEncounter(String uuid) {

		DataService.GetCallback<Encounter> fetchEncountercallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter encounter) {
				auditDataView.setEncounterUuid(encounter.getUuid());
				auditDataView.updateFormFields(encounter);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};
		encounterDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, fetchEncountercallback);
	}

	@Override
	public void saveEncounter(Encounter encounter, boolean isNewEncounter) {

		DataService.GetCallback<Encounter> serverResponceCallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter encounter) {
				/*TODO
				Ask if the're a parameter you can pass when creating or updating the encounters so that you can get the
				full representation and get to uncomment the commented lines below.
				 */

				fetchEncounter(encounter.getUuid());

				//auditDataView.setEncounterUuid(encounter.getUuid());
				//auditDataView.updateFormFields(encounter);

			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};

		if (isNewEncounter) {
			encounterDataService.create(encounter, serverResponceCallback);
		} else {
			encounterDataService.update(encounter, serverResponceCallback);
		}
	}

	@Override
	public void fetchLocation(String locationUuid) {
		DataService.GetCallback<Location> locationDataServiceCallback = new DataService.GetCallback<Location>() {
			@Override
			public void onCompleted(Location location) {
				//set location in the fragment and start loading other fields
				auditDataView.setLocation(location);
				auditDataView.fetchPatientDetails();
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};

		locationDataService.getByUUID(locationUuid, QueryOptions.LOAD_RELATED_OBJECTS, locationDataServiceCallback);
	}

}

