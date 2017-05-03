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

package org.openmrs.mobile.activities.findpatientrecord;

import android.os.Bundle;
import android.util.Log;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class FindPatientRecordPresenter extends BasePresenter implements FindPatientRecordContract.Presenter {
	
	
	private FindPatientRecordContract.View findPatientView;
	private int totalNumberResults;
	private PatientDataService patientDataService;
	private String lastQuery = "";
	
	public FindPatientRecordPresenter(FindPatientRecordContract.View view, String lastQuery) {
		this.findPatientView = view;
		this.findPatientView.setPresenter(this);
		this.lastQuery = lastQuery;
		this.patientDataService = new PatientDataService();
	}

	public FindPatientRecordPresenter(FindPatientRecordContract.View view) {
		this.findPatientView = view;
		this.findPatientView.setPresenter(this);
		this.patientDataService = new PatientDataService();
	}
	
	@Override
	public void subscribe() {
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}


	public void findPatient(String query) {
		findPatientView.setProgressBarVisibility(true);
		if (NetworkUtils.hasNetwork()) {
			PagingInfo pagingInfo = new PagingInfo(0, 7);
			DataService.GetMultipleCallback<Patient> getMultipleCallback = new DataService.GetMultipleCallback<Patient>() {
				@Override
				public void onCompleted(List<Patient> patients) {
					findPatientView.setProgressBarVisibility(false);
					if (patients == null || patients.isEmpty()) {
						findPatientView.setNumberOfPatientsView(0);
						findPatientView.setSearchPatientVisibility(false);
						findPatientView.setNoPatientsVisibility(true);
					} else {
						findPatientView.setNoPatientsVisibility(false);
						findPatientView.setSearchPatientVisibility(false);
						findPatientView.setNumberOfPatientsView(patients.size());
						findPatientView.fetchPatients(patients);
					}
				}
				@Override
				public void onError(Throwable t) {
					findPatientView.setProgressBarVisibility(false);
					Log.e("Patient Error","Error",t.fillInStackTrace());
				}
			};
			patientDataService.getByName(query, pagingInfo, getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}
}
