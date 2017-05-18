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
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeDisplays.AUDITDATA;

public class AuditDataPresenter extends BasePresenter implements AuditDataContract.Presenter {

	private OpenMRS openMRS;
	private AuditDataContract.View auditDataView;
	private DataService<Patient> patientDataService;
	private VisitDataService visitDataService;
	private ObsDataService observationDataService;
	private EncounterDataService encounterDataService;
	LocationDataService locationDataService;
	private int startIndex = 0;
	private int limit = 100;

	public AuditDataPresenter(AuditDataContract.View view, OpenMRS openMRS) {
		this.auditDataView = view;
		this.auditDataView.setPresenter(this);
		this.patientDataService = new PatientDataService();
		this.visitDataService = new VisitDataService();
		this.observationDataService = new ObsDataService();
		this.encounterDataService = new EncounterDataService();
		this.openMRS = openMRS;
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
				auditDataView.setEncounter(encounter);
				//auditDataView.updateForm(encounter);
				fetchEncounterObservations(encounter);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};
		encounterDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS,
				fetchEncountercallback);
	}

	@Override
	public void fetchEncounterObservations(Encounter encounter) {

		DataService.GetCallback<Observation> fetchObservationCallback = new DataService.GetCallback<Observation>() {
			@Override
			public void onCompleted(Observation observation) {
				auditDataView.updateForm(observation);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};

		for (Observation observation : encounter.getObs()) {

			observationDataService.getByUUID(observation.getUuid(), QueryOptions.LOAD_RELATED_OBJECTS,
					fetchObservationCallback);
		}

	}

	@Override
	public void createEncounter(Encounter encounter) {

		//some variables
		String locationUuid = "";
		Location location = null;

		//get Location from db
		if (!OpenMRS.getInstance().getLocation().equalsIgnoreCase(null)) {
			locationUuid = openMRS.getLocation();
		}

		DataService.GetCallback<Location> locationDataServiceCallback = new DataService.GetCallback<Location>() {
			@Override
			public void onCompleted(Location location) {

				//assigng location to encounter
				encounter.setLocation(location);
				saveEncounter(location);
			}

			private void saveEncounter(Location location) {

				encounterDataService.create(encounter, new DataService.GetCallback<Encounter>() {
					@Override
					public void onCompleted(Encounter encounter) {
						//ConsoleLogger.dump(encounter);
					}

					@Override
					public void onError(Throwable t) {
						t.printStackTrace();
					}
				});
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};

		locationDataService.getByUUID(locationUuid, QueryOptions.LOAD_RELATED_OBJECTS, locationDataServiceCallback);

	}

}
