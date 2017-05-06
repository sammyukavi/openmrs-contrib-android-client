package org.openmrs.mobile.activities.visitphoto;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.VisitPhoto;

public interface VisitPhotoContract {

    interface View extends BaseView<VisitPhotoContract.Presenter> {

    }

    interface Presenter extends BasePresenterContract {

        void uploadImage();

        VisitPhoto getVisitPhoto();

        boolean isUploading();

        void setUploading(boolean uploading);
    }
}
