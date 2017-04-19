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

import java.util.ArrayList;
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
    private int totalNumberResults;
    private boolean loading;

    public PatientListPresenter(@NonNull PatientListContract.View patientListView) {
        RestServiceBuilder.setEndPoint(ApplicationConstants.API.REST_ENDPOINT_V2);
        this.patientListView = patientListView;
        this.patientListView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
    }

    public PatientListPresenter(@NonNull PatientListContract.View patientListView, RestApi restApi) {
        RestServiceBuilder.setEndPoint(ApplicationConstants.API.REST_ENDPOINT_V2);
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
        Call<Results<PatientList>> call = restApi.getPatientLists();
        call.enqueue(new Callback<Results<PatientList>>() {
            @Override
            public void onResponse(Call<Results<PatientList>> call, Response<Results<PatientList>> response) {
                if(response.isSuccessful()){
                    List<PatientList> results = response.body().getResults();
                    patientListView.updatePatientLists(results);
                }
            }

            @Override
            public void onFailure(Call<Results<PatientList>> call, Throwable throwable) {
            }
        });
    }

    @Override
    public void getPatientListData(String patientListUuid, int startIndex){
        if(startIndex < 0){
            return;
        }

        setStartIndex(startIndex);
        setLoading(true);
        setViewBeforeLoadData();
        Call<Results<PatientListContextModel>> call = restApi.getPatientListData(patientListUuid, getStartIndex(), limit);
        call.enqueue(new Callback<Results<PatientListContextModel>>() {
            @Override
            public void onResponse(Call<Results<PatientListContextModel>> call, Response<Results<PatientListContextModel>> response) {
                if(response.isSuccessful()){
                    setViewAfterLoadData(false);
                    List<PatientListContextModel> results = response.body().getResults();
                    patientListView.updatePatientListData(results);
                    setTotalNumberResults(response.body().getLength());
                } else {
                    setViewAfterLoadData(true);
                }

                setLoading(false);
            }

            @Override
            public void onFailure(Call<Results<PatientListContextModel>> call, Throwable throwable) {
                patientListView.updatePatientListData(new ArrayList<>());
                setViewAfterLoadData(true);
                setLoading(false);
            }
        });
    }

    @Override
    public void loadResults(String patientListUuid, boolean loadNextResults) {
        getPatientListData(patientListUuid, computeStartIndex(loadNextResults));
    }

    @Override
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public void setTotalNumberResults(int totalNumberResults) {
        this.totalNumberResults = totalNumberResults;
    }

    private int getTotalNumberResults(){
        return totalNumberResults;
    }

    private int computeStartIndex(boolean next){
        int tmpStartIndex = startIndex;
        // check if pagination is required.
        if(tmpStartIndex + limit < getTotalNumberResults()){
            if(next) {
                // calculate startindex for the next page
                tmpStartIndex += limit;
            }
            else if(tmpStartIndex - limit >= 0) {
                // calculate start index for the previous page.
                 tmpStartIndex -= limit;
            }
        } else {
            tmpStartIndex = -1;
        }

        return tmpStartIndex;
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
    public void refresh() {
    }

    private void setViewBeforeLoadData(){
        patientListView.setSpinnerVisibility(true);
        patientListView.setEmptyPatientListVisibility(false);
    }

    private void setViewAfterLoadData(boolean visible){
        patientListView.setSpinnerVisibility(false);
        patientListView.setEmptyPatientListVisibility(visible);
    }
}
