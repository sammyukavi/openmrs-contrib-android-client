package org.openmrs.mobile.activities.visitphoto.download;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.VisitPhoto;

import java.util.List;

public class DownloadVisitPhotoContract {

	interface View extends BaseView<DownloadVisitPhotoContract.Presenter> {

		void updateVisitImages(List<VisitPhoto> visitPhotos);
	}

	interface Presenter extends BasePresenterContract {

		void downloadImages();

		boolean isLoading();

		void setLoading(boolean loading);
	}
}
