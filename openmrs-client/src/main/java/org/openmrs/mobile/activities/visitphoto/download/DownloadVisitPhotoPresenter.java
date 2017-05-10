package org.openmrs.mobile.activities.visitphoto.download;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.greenrobot.greendao.annotation.NotNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.VisitPhotoDataService;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadVisitPhotoPresenter extends BasePresenter implements DownloadVisitPhotoContract.Presenter {

	@NotNull
	private DownloadVisitPhotoContract.View view;
	private String patientUuid;
	private boolean loading;
	private VisitPhotoDataService visitPhotoDataService;
	private ObsDataService obsDataService;

	public DownloadVisitPhotoPresenter(DownloadVisitPhotoContract.View view, String patientUuid) {
		this.view = view;
		this.view.setPresenter(this);
		this.patientUuid = patientUuid;
		this.visitPhotoDataService = new VisitPhotoDataService();
		this.obsDataService = new ObsDataService();
	}

	@Override
	public void loadVisitDocumentObservations() {
		// get obs for patient.
		obsDataService.getVisitDocumentsObsByPatientAndConceptList(patientUuid,
				new DataService.GetMultipleCallback<Observation>() {
					@Override
					public void onCompleted(List<Observation> observations, int length) {
						List<String> imageUrls = new ArrayList<>();
						for (Observation observation : observations) {
							imageUrls.add(observation.getUuid());
						}

						view.updateVisitImageUrls(imageUrls);
					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});
	}

	@Override
	public void downloadImage(String obsUuid, DataService.GetSingleCallback<Bitmap> callback) {
		visitPhotoDataService.downloadPhoto(obsUuid, ApplicationConstants.THUMBNAIL_VIEW,
				new DataService.GetSingleCallback<VisitPhoto>() {
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
		loadVisitDocumentObservations();
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
