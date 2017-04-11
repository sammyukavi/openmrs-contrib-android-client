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
package org.openmrs.mobile.api.retrofit;

import org.openmrs.mobile.api.PatientListRestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.listeners.retrofit.PatientListCallbackListener;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContextModel;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link PatientList} service methods
 */

public class PatientListApi extends RetrofitApi{

    private PatientListRestApi apiService = RestServiceBuilder.createService(PatientListRestApi.class);

    /**
     * Synchronously get patient lists
     */
    public void getPatientLists(final PatientListCallbackListener callbackListener){
        Call<Results<PatientList>> call = apiService.getPatientLists();
        call.enqueue(new Callback<Results<PatientList>>() {
            @Override
            public void onResponse(Call<Results<PatientList>> call, Response<Results<PatientList>> response) {
                if(response.isSuccessful()){
                    callbackListener.onGetPatientList(response.body().getResults());
                } else {
                    callbackListener.onErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(Call<Results<PatientList>> call, Throwable throwable) {
                callbackListener.onErrorResponse(throwable.getMessage());
            }
        });
    }

    /**
     * Asynchronously fetch patient list data
     * @param callbackListener
     */
    public void getPatientListData(String patientListUuid, int startIndex, int size, final PatientListCallbackListener callbackListener){
        Call<Results<PatientListContextModel>> call = apiService.getPatientListData(patientListUuid, startIndex, size);
        call.enqueue(new Callback<Results<PatientListContextModel>>() {
            @Override
            public void onResponse(Call<Results<PatientListContextModel>> call, Response<Results<PatientListContextModel>> response) {
                if(response.isSuccessful()){
                    callbackListener.onGetPatientListData(response.body().getResults());
                } else {
                    callbackListener.onErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(Call<Results<PatientListContextModel>> call, Throwable throwable) {
                callbackListener.onErrorResponse(throwable.getMessage());
            }
        });
    }
}
