package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LocationRestService {

	@GET(RestConstants.GET_ALL)
	Call<Results<Location>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation);

	@GET(RestConstants.GET_BY_UUID)
	Call<Location> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);
}
