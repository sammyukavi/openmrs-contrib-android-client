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
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.ProviderDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {

	private final EncounterDataService encounterDataService;
	private PatientDashboardContract.View patientDashboardView;
	private PatientDataService patientDataService;
	private VisitDataService visitDataService;
	private LocationDataService locationDataService;
	private ProviderDataService providerDataService;
	private final static int page = 1;
	private int limit = 5;
	private int startIndex = 0;
	//private Patient patient;

	public PatientDashboardPresenter(PatientDashboardContract.View view) {
		this.patientDashboardView = view;
		this.patientDashboardView.setPresenter(this);
		this.patientDataService = new PatientDataService();
		this.visitDataService = new VisitDataService();
		this.providerDataService = new ProviderDataService();
		this.locationDataService = new LocationDataService();
		this.encounterDataService = new EncounterDataService();
	}

	@Override
	public void subscribe() {
		getCurrentProvider();
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public void fetchPatientData(String uuid) {
		patientDashboardView.showPageSpinner(true);
		patientDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				if (patient != null) {
					fetchVisits(patient);
				}
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
		visitDataService.getByPatient(patient, new QueryOptions(true, true), new PagingInfo(startIndex, limit),
				new DataService.GetCallback<List<Visit>>() {
					@Override
					public void onCompleted(List<Visit> visits) {
						if (!visits.isEmpty()) {
							patientDashboardView.updateContactCard(patient);
							patientDashboardView.updateActiveVisitCard(visits);
						} else {
							patientDashboardView.showPageSpinner(false);
							patientDashboardView.showNoVisits(true);
						}
					}

					@Override
					public void onError(Throwable t) {
						t.printStackTrace();
						patientDashboardView.showPageSpinner(false);
					}
				});
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
			providerDataService.getAll(QueryOptions.LOAD_RELATED_OBJECTS, new PagingInfo(0, 100),
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

	@Override
	public void fetchLocation(String locationUuid) {
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

	@Override
	public void saveEncounter(Encounter encounter, boolean isNewEncounter) {
		patientDashboardView.upDateProgressBar(true);
		DataService.GetCallback<Encounter> serverResponceCallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter encounter) {
				patientDashboardView.upDateProgressBar(false);

				if (encounter.equals(null)) {
				} else {
					/*TODO
				Ask if the're a parameter you can pass when creating or updating the encounters so that you can get the
				full representation and get to uncomment the commented lines below.
				 */

					//fetchEncounter(encounter.getUuid());

					//patientDashboardView.setEncounterUuid(encounter.getUuid());
					//patientDashboardView.updateFormFields(encounter);
					patientDashboardView.upDateProgressBar(false);
				}
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

}