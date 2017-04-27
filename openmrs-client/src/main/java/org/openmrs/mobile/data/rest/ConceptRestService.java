package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Concept;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConceptRestService {

    @GET(RestConstants.GET_BY_UUID)
    Call<Concept> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
                            @Path("uuid") String uuid,
                            @Query("v") String representation);
}
