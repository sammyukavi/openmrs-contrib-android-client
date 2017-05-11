package org.openmrs.mobile.activities.visitphoto.download;

import android.graphics.Bitmap;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.data.DataService;

import java.util.List;

public class DownloadVisitPhotoContract {

	interface View extends BaseView<DownloadVisitPhotoContract.Presenter> {

		void updateVisitImageUrls(List<String> urls);

		void downloadImage(String obsUuid, DataService.GetSingleCallback<Bitmap> callback);
	}

	interface Presenter extends BasePresenterContract {

		void downloadImage(String obsUuid, DataService.GetSingleCallback<Bitmap> callback);

		void loadVisitDocumentObservations();

		boolean isLoading();

		void setLoading(boolean loading);
	}
}
