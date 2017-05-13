package org.openmrs.mobile.activities.visitphoto.upload;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.VisitPhoto;

public interface UploadVisitPhotoContract {

	interface View extends BaseView<UploadVisitPhotoContract.Presenter> {
		void showPatientDashboard(String patientUuid);
	}

	interface Presenter extends BasePresenterContract {

		void uploadImage();

		VisitPhoto getVisitPhoto();

		boolean isLoading();

		void setLoading(boolean loading);
	}
}
