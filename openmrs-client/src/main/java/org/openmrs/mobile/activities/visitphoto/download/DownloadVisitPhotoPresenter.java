package org.openmrs.mobile.activities.visitphoto.download;

import org.greenrobot.greendao.annotation.NotNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.VisitPhotoDataService;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadVisitPhotoPresenter extends BasePresenter implements DownloadVisitPhotoContract.Presenter {

    private final String THUMBNAIL_VIEW = "complexdata.view.thumbnail";
    @NotNull
    private DownloadVisitPhotoContract.View view;
    private String patientUuid;
    private boolean loading;
    private VisitPhotoDataService visitPhotoDataService;
    private ObsDataService obsDataService;
    private List<VisitPhoto> images = new ArrayList<>();

    public DownloadVisitPhotoPresenter(DownloadVisitPhotoContract.View view, String patientUuid) {
        this.view = view;
        this.view.setPresenter(this);
        this.patientUuid = patientUuid;
        this.visitPhotoDataService = new VisitPhotoDataService();
        this.obsDataService = new ObsDataService();
    }

    @Override
    public void downloadImages() {
        images.clear();
        // get observations for patient.
        obsDataService.getVisitDocumentsObsByPatientAndConceptList(patientUuid, new DataService.GetMultipleCallback<Observation>() {
            @Override
            public void onCompleted(List<Observation> observations, int length) {
                for (Observation observation : observations) {
                    // download images
                    visitPhotoDataService.downloadPhoto(observation.getUuid(), THUMBNAIL_VIEW, new DataService.GetSingleCallback<VisitPhoto>() {
                        @Override
                        public void onCompleted(VisitPhoto entity) {
                            entity.setFileCaption(observation.getComment());
                            images.add(entity);

                            if (observations.indexOf(observation) == observations.size() - 1) {
                                view.updateVisitImages(images);
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            ToastUtil.error(t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable t) {
                ToastUtil.error(t.getMessage());
            }
        });
    }

    @Override
    public void subscribe() {
        downloadImages();
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
