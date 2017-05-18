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

package org.openmrs.mobile.activities.visitphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.visitdetails.VisitDetailsActivity;
import org.openmrs.mobile.activities.visitdetails.VisitDetailsContract;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.VisitPhotoDataService;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class VisitPhotoPresenter extends VisitDetailsActivity implements VisitDetailsContract.VisitPhotoPresenter {

	@NonNull
	private VisitDetailsContract.VisitPhotoView view;
	private String patientUuid, visitUuid, providerUuid;
	private boolean loading;
	private VisitPhotoDataService visitPhotoDataService;
	private ObsDataService obsDataService;
	private VisitPhoto visitPhoto;

	public VisitPhotoPresenter(VisitDetailsContract.VisitPhotoView view, String patientUuid, String visitUuid,
			String providerUuid) {
		this.view = view;
		this.view.setPresenter(this);
		this.patientUuid = patientUuid;
		this.visitUuid = visitUuid;
		this.providerUuid = providerUuid;
		this.visitPhotoDataService = new VisitPhotoDataService();
		this.obsDataService = new ObsDataService();
	}

	@Override
	public void loadVisitDocumentObservations() {
		// get obs for patient.
		obsDataService.getVisitDocumentsObsByPatientAndConceptList(patientUuid, QueryOptions.DEFAULT,
				new DataService.GetCallback<List<Observation>>() {
					@Override
					public void onCompleted(List<Observation> observations) {
						List<String> imageUrls = new ArrayList<>();
						for (Observation observation : observations) {
							imageUrls.add(observation.getUuid());
						}
						if (imageUrls.size() != 0){
							view.updateVisitImageUrls(imageUrls);
						} else {
							view.showNoVisitPhoto();
						}

					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});
	}

	@Override
	public void downloadImage(String obsUuid, DataService.GetCallback<Bitmap> callback) {
		visitPhotoDataService.downloadPhoto(obsUuid, ApplicationConstants.THUMBNAIL_VIEW,
				new DataService.GetCallback<VisitPhoto>() {
					@Override
					public void onCompleted(VisitPhoto entity) {
						callback.onCompleted(BitmapFactory.decodeStream(entity.getResponseImage().byteStream()));
					}

					@Override
					public void onError(Throwable t) {
						callback.onError(t);
						ToastUtil.error(t.getMessage());
					}
				});
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void initVisitPhoto() {
		visitPhoto = new VisitPhoto();
		Visit visit = new Visit();
		visit.setUuid(visitUuid);

		Provider provider = new Provider();
		provider.setUuid(providerUuid);

		Patient patient = new Patient();
		patient.setUuid(patientUuid);

		visitPhoto.setVisit(visit);
		visitPhoto.setProvider(provider);
		visitPhoto.setPatient(patient);
	}

	@Override
	public void uploadImage() {
		visitPhotoDataService.uploadPhoto(visitPhoto, new DataService.GetCallback<VisitPhoto>() {
			@Override
			public void onCompleted(VisitPhoto entity) {
				view.refresh();
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
				System.out.println(t.getMessage());
			}
		});
	}

	@Override
	public VisitPhoto getVisitPhoto() {
		return visitPhoto;
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public void unsubscribe() {

	}
}
