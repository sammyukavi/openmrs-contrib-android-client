package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConceptNameRestService {

	@GET(RestConstants.GET_BY_UUID)
	Call<ConceptName> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);

	@GET(RestConstants.REST_PATH)
	Call<Results<ConceptName>> getByConceptUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Query("conceptUuid") String uuid);
}
