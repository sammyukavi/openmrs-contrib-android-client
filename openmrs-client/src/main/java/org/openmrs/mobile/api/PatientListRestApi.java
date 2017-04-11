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
package org.openmrs.mobile.api;

import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContextModel;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * {@link PatientList} Restful calls
 */
public interface PatientListRestApi extends RestApi{

    @GET("patientlist/list")
    Call<Results<PatientList>> getPatientLists();

    @GET("patientlist/data")
    Call<Results<PatientListContextModel>> getPatientListData(@Query("uuid") String uuid,
                                                              @Query("startIndex") int startIndex,
                                                              @Query("limit") int limit);

}
