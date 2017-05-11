package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ObsRestService {

	@GET(RestConstants.GET_ALL)
	Call<Results<Observation>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.GET_BY_UUID)
	Call<Observation> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);

	@GET(RestConstants.GET_ALL)
	Call<Results<Observation>> getVisitDocumentsObsByPatientAndConceptList(
			@Path(value = "restPath", encoded = true) String restPath,
			@Query("patient") String patientUuid,
			@Query("conceptList") String conceptList,
			@Query("v") String representation);
}
