/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.patientlist;

import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContextModel;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientListPresenter extends BasePresenter implements PatientListContract.Presenter {

    @NonNull
    private PatientListContract.View patientListView;

    private RestApi restApi;

    private int limit = 10;
    private int startIndex = 0;

    public PatientListPresenter(@NonNull PatientListContract.View patientListView) {
        this.patientListView = patientListView;
        this.patientListView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
    }

    public PatientListPresenter(@NonNull PatientListContract.View patientListView, RestApi restApi) {
        this.patientListView = patientListView;
        this.patientListView.setPresenter(this);
        this.restApi = restApi;
    }

    @Override
    public void subscribe() {
        // get all patient lists
        getPatientList();
    }

    public void getPatientList(){
        startIndex = 0;
        setViewBeforeGetPatientList();
        RestServiceBuilder.changeEndPoint(ApplicationConstants.API.REST_ENDPOINT_V2);
        Call<Results<PatientList>> call = restApi.getPatientLists();
        call.enqueue(new Callback<Results<PatientList>>() {
            @Override
            public void onResponse(Call<Results<PatientList>> call, Response<Results<PatientList>> response) {
                if(response.isSuccessful()){
                    List<PatientList> results = response.body().getResults();
                    patientListView.updatePatientLists(results);
                    setViewAfterGetPatientListSuccess(results.isEmpty());
                } else {
                    setViewAfterGetPatientListError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Results<PatientList>> call, Throwable throwable) {
                setViewAfterGetPatientListError(throwable.getMessage());
            }
        });
    }

    @Override
    public void getPatientListData(String patientListUuid){
        Call<Results<PatientListContextModel>> call = restApi.getPatientListData(patientListUuid, startIndex, limit);
        RestServiceBuilder.changeEndPoint(ApplicationConstants.API.REST_ENDPOINT_V2);
        call.enqueue(new Callback<Results<PatientListContextModel>>() {
            @Override
            public void onResponse(Call<Results<PatientListContextModel>> call, Response<Results<PatientListContextModel>> response) {
                if(response.isSuccessful()){
                    List<PatientListContextModel> results = response.body().getResults();
                    patientListView.updatePatientListData(results);
                    computeStartIndex(response.body().getLength());
                }
            }

            @Override
            public void onFailure(Call<Results<PatientListContextModel>> call, Throwable throwable) {
            }
        });
    }

    @Override
    public void loadPreviousResults(String patientListUuid) {
        startIndex -= limit;
        getPatientListData(patientListUuid);
    }

    @Override
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    private void computeStartIndex(int totalNumResults){
        if(startIndex + limit < totalNumResults){
            startIndex += limit;
        }
    }

    @Override
    public void refresh() {

    }

    private void setViewBeforeGetPatientList(){
        patientListView.setSpinnerVisibility(true);
    }

    private void setViewAfterGetPatientListSuccess(boolean emptyList){
        patientListView.setSpinnerVisibility(false);
    }

    private void setViewAfterGetPatientListError(String errorMessage) {
        patientListView.setSpinnerVisibility(false);
        patientListView.setEmptyPatientListText(errorMessage);
    }
}
