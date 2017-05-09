package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.VisitPhotoRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitPhotoDataService extends BaseDataService<VisitPhoto, VisitPhotoRestService> {

    @Override
    protected Class<VisitPhotoRestService> getRestServiceClass() {
        return VisitPhotoRestService.class;
    }

    @Override
    protected String getRestPath() {
        return ApplicationConstants.API.REST_ENDPOINT_V2 + "/patientlist";
    }

    @Override
    protected String getEntityName() {
        return "photos";
    }

    public void uploadPhoto(VisitPhoto visitPhoto, @NonNull GetSingleCallback<VisitPhoto> callback) {
        executeSingleCallback(callback, () -> {
            RequestBody patient = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getPatient().getUuid());
            RequestBody visit = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getVisit().getUuid());
            RequestBody provider = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getProvider().getUuid());
            RequestBody fileCaption = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getFileCaption());

            return restService.uploadVisitPhoto(buildRestRequestPath(), patient, visit,
                    provider, fileCaption, visitPhoto.getRequestImage());
        });
    }

    public void downloadPhoto(String obsUuid, String view, @NonNull GetSingleCallback<VisitPhoto> callback) {
        final VisitPhoto visitPhoto = new VisitPhoto();
        Call<ResponseBody> call = restService.downloadVisitPhoto(buildRestRequestPath(), obsUuid, view);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                visitPhoto.setResponseImage(response.body());
                callback.onCompleted(visitPhoto);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtil.error(t.getMessage());
                callback.onError(t);
            }
        });
    }

    @Override
    protected Call<VisitPhoto> _restGetByUuid(String restPath, String uuid, String representation) {
        return null;
    }

    @Override
    protected Call<Results<VisitPhoto>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
        return null;
    }

    @Override
    protected Call<VisitPhoto> _restCreate(String restPath, VisitPhoto entity) {
        return null;
    }

    @Override
    protected Call<VisitPhoto> _restUpdate(String restPath, VisitPhoto entity) {
        return null;
    }

    @Override
    protected Call<VisitPhoto> _restPurge(String restPath, String uuid) {
        return null;
    }
}
