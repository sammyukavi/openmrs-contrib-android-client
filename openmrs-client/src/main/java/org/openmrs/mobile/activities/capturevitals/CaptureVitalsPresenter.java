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

package org.openmrs.mobile.activities.capturevitals;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;

public class CaptureVitalsPresenter extends BasePresenter implements CaptureVitalsContract.Presenter {

	private CaptureVitalsContract.View captureVitalsView;
	private VisitDataService visitDataService;

	private EncounterDataService encounterDataService;
	private LocationDataService locationDataService;

	public CaptureVitalsPresenter(CaptureVitalsContract.View view) {
		this.captureVitalsView = view;
		this.captureVitalsView.setPresenter(this);
		this.visitDataService = new VisitDataService();
		this.encounterDataService = new EncounterDataService();
		this.locationDataService = new LocationDataService();
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void fetchLocation(String locationUuid) {
		DataService.GetCallback<Location> locationDataServiceCallback = new DataService.GetCallback<Location>() {
			@Override
			public void onCompleted(Location location) {
				//set location in the fragment and start loading other fields
				captureVitalsView.setLocation(location);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};

		locationDataService.getByUUID(locationUuid, QueryOptions.LOAD_RELATED_OBJECTS, locationDataServiceCallback);
	}

	@Override
	public void attemptSave(Encounter encounter) {
		DataService.GetCallback<Encounter> serverResponceCallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter encounter) {
				if (encounter.equals(null)) {
					captureVitalsView.showSnackbar(captureVitalsView.getContext().getString(R.string.error_occured));
				} else {
					captureVitalsView.showSnackbar(captureVitalsView.getContext().getString(R.string.saved));
					captureVitalsView.disableButton();
				}
			}

			@Override
			public void onError(Throwable t) {
				captureVitalsView.showSnackbar(captureVitalsView.getContext().getString(R.string.error_occured));
				t.printStackTrace();
			}
		};

		encounterDataService.create(encounter, serverResponceCallback);

	}

}

