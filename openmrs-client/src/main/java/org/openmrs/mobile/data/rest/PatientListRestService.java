package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PatientListRestService {

    @GET(RestConstants.GET_BY_UUID)
    Call<PatientList> getByUuid(@Path("restPath") String restPath,
                                @Path("uuid") String uuid,
                                @Query("v") String representation);

    @GET(RestConstants.GET_ALL)
    Call<Results<PatientList>> getAll(@Path(value = "restPath", encoded = true) String restPath,
                                      @Query("v") String representation);

    @GET(RestConstants.GET_ALL)
    Call<Results<PatientList>> getAll(@Path("restPath") String restPath,
                                      @Query("v") String representation,
                                      @Query("limit") int limit,
                                      @Query("startIndex") int startIndex);

    @POST(RestConstants.CREATE)
    Call<PatientList> create(@Path("restPath") String restPath, PatientList entity);

    @POST(RestConstants.UPDATE)
    Call<PatientList> update(@Path("restPath") String restPath,
                             @Path("uuid") String uuid, PatientList entity);

    @DELETE(RestConstants.PURGE)
    Call<PatientList> purge(@Path("restPath") String restPath,
                            @Path("uuid") String uuid);

    @GET(RestConstants.REST_PATH)
    Call<Results<PatientList>> getByNameFragment(@Path("restPath") String restPath,
                                                 @Query("q") String name,
                                                 @Query("v") String representation);

    @GET(RestConstants.REST_PATH)
    Call<Results<PatientList>> getByNameFragment(@Path("restPath") String restPath,
                                                 @Query("q") String name,
                                                 @Query("v") String representation,
                                                 @Query("limit") int limit,
                                                 @Query("startIndex") int startIndex);
}
