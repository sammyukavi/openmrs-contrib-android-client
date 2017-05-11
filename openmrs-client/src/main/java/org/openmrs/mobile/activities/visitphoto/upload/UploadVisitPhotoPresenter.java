package org.openmrs.mobile.activities.visitphoto.upload;

import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.impl.VisitPhotoDataService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;

public class UploadVisitPhotoPresenter extends BasePresenter implements UploadVisitPhotoContract.Presenter{

    @NonNull
    private UploadVisitPhotoContract.View visitPhotoView;
    private VisitPhotoDataService visitPhotoDataService;
    private String patientUuid, visitUuid, providerUuid;
    private VisitPhoto visitPhoto;
    private boolean loading;

    public UploadVisitPhotoPresenter(UploadVisitPhotoContract.View visitPhotoView,
                                     String patientUuid, String visitUuid, String providerUuid) {
        this.visitPhotoView = visitPhotoView;
        this.visitPhotoView.setPresenter(this);
        this.patientUuid = patientUuid;
        this.visitUuid = visitUuid;
        this.providerUuid = providerUuid;
        this.visitPhotoDataService = new VisitPhotoDataService();
    }

    @Override
    public void subscribe() {
        initVisitPhoto();
    }

    private void initVisitPhoto(){
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
                System.out.println("image uploaded " + entity);
            }

            @Override
            public void onError(Throwable t) {
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
}
