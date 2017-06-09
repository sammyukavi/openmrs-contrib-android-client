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

package org.openmrs.mobile.activities.patientdashboard;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.ProviderDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {

	private EncounterDataService encounterDataService;
	private ObsDataService observationDataService;
	private PatientDashboardContract.View patientDashboardView;
	private PatientDataService patientDataService;
	private VisitDataService visitDataService;
	private LocationDataService locationDataService;
	private ProviderDataService providerDataService;
	private int startIndex = 0;
	private int limit = 10;
	private Patient patient;
	private boolean loading;

	public PatientDashboardPresenter(PatientDashboardContract.View view) {
		this.patientDashboardView = view;
		this.patientDashboardView.setPresenter(this);
		this.patientDataService = new PatientDataService();
		this.visitDataService = new VisitDataService();
		this.providerDataService = new ProviderDataService();
		this.locationDataService = new LocationDataService();
		this.encounterDataService = new EncounterDataService();
		this.observationDataService = new ObsDataService();
	}

	@Override
	public void subscribe() {
		getCurrentProvider();

		getCurrentLocation();
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public int getStartIndex() {
		return startIndex;
	}

	@Override
	public void fetchPatientData(String uuid) {

		patientDashboardView.showPageSpinner(true);

		patientDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				setPatient(patient);
				patientDashboardView.showPageSpinner(true);
				fetchVisits(patient);
			}

			@Override
			public void onError(Throwable t) {
				patientDashboardView.showPageSpinner(false);
				t.printStackTrace();
			}
		});
	}

	@Override
	public void fetchVisits(Patient patient) {

		patientDashboardView.showPageSpinner(true);

		setLoading(true);

		DataService.GetCallback<List<Visit>> fetchVisitsCallback = new DataService.GetCallback<List<Visit>>() {
			@Override
			public void onCompleted(List<Visit> visits) {

				setLoading(false);

				patientDashboardView.updateContactCard(patient);
				patientDashboardView.updateVisitsCard(visits);

				if (visits.isEmpty()) {
					patientDashboardView.showPageSpinner(false);
					patientDashboardView.showNoVisits(true);
				}
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				patientDashboardView.showPageSpinner(false);
				setLoading(false);
			}
		};

		PagingInfo pagingInfo = new PagingInfo(startIndex, limit);

		visitDataService.getByPatient(patient, new QueryOptions(true, true), pagingInfo, fetchVisitsCallback);
	}

	@Override
	public void fetchVisits(boolean loadNextResults) {

		if (loadNextResults) {
			startIndex += limit;
		} else {
			startIndex -= limit;
		}

		if (startIndex < 0) {
			startIndex = 0;
		}

		System.out.println("Start Index: " + startIndex);

		PagingInfo pagingInfo = new PagingInfo(0, 10);

		setLoading(true);

		setLoading(true);

		DataService.GetCallback<List<Visit>> fetchVisitsCallback = new DataService.GetCallback<List<Visit>>() {
			@Override
			public void onCompleted(List<Visit> results) {

				patientDashboardView.updateVisits(results);

				setLoading(false);
			}

			@Override
			public void onError(Throwable t) {
				setLoading(false);
			}
		};

		visitDataService.getByPatient(patient, new QueryOptions(true, true), pagingInfo, fetchVisitsCallback);
	}

	@Override
	public Patient getPatient() {
		return patientDashboardView.getPatient();
	}

	/**
	 * TODO: create a service to getProviderByPerson, move code to commons
	 */
	private void getCurrentProvider() {

		patientDashboardView.showPageSpinner(true);

		String personUuid = OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID);

		if (StringUtils.notEmpty(personUuid)) {

			providerDataService.getAll(QueryOptions.LOAD_RELATED_OBJECTS, null,
					new DataService.GetCallback<List<Provider>>() {
						@Override
						public void onCompleted(List<Provider> entities) {
							for (Provider entity : entities) {
								if (null != entity.getPerson() && personUuid
										.equalsIgnoreCase(entity.getPerson().getUuid())) {
									patientDashboardView.setProviderUuid(entity.getUuid());
								}
							}
							patientDashboardView.showPageSpinner(false);
						}

						@Override
						public void onError(Throwable t) {
							patientDashboardView.showPageSpinner(false);
							ToastUtil.error(t.getMessage());
						}
					});
		}
	}

	public void getCurrentLocation() {
		//We start by fetching by location, required for creating encounters
		String locationUuid = OpenMRS.getInstance().getLocation();

		if (StringUtils.notEmpty(locationUuid)) {

			patientDashboardView.showPageSpinner(true);

			DataService.GetCallback<Location> locationDataServiceCallback = new DataService.GetCallback<Location>() {
				@Override
				public void onCompleted(Location location) {
					//set location in the fragment and start loading other fields
					patientDashboardView.setLocation(location);
				}

				@Override
				public void onError(Throwable t) {
					patientDashboardView.showPageSpinner(true);
					t.printStackTrace();
				}
			};

			locationDataService.getByUUID(locationUuid, QueryOptions.LOAD_RELATED_OBJECTS, locationDataServiceCallback);
		}

	}

	@Override
	public void saveEncounter(Encounter encounter, boolean isNewEncounter) {

		patientDashboardView.showSavingClinicalNoteProgressBar(true);

		setLoading(true);

		DataService.GetCallback<Encounter> serverResponceCallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter result) {
				patientDashboardView.showSavingClinicalNoteProgressBar(false);

				setLoading(false);

				patientDashboardView.updateClinicVisitNote(result.getObs().get(0));
			}

			@Override
			public void onError(Throwable t) {
				setLoading(false);
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
	public void saveObservation(Observation observation, boolean isNewObservation) {

		patientDashboardView.showSavingClinicalNoteProgressBar(true);

		setLoading(true);

		DataService.GetCallback<Observation> serverResponceCallback = new DataService.GetCallback<Observation>() {
			@Override
			public void onCompleted(Observation result) {

				patientDashboardView.showSavingClinicalNoteProgressBar(false);

				setLoading(false);

				patientDashboardView.updateClinicVisitNote(result);
			}

			@Override
			public void onError(Throwable t) {
				setLoading(false);
				t.printStackTrace();
			}
		};

		if (isNewObservation) {
			observationDataService.create(observation, serverResponceCallback);
		} else {
			observationDataService.update(observation, serverResponceCallback);
		}
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
	}
}