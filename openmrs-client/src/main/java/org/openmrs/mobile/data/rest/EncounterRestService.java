package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EncounterRestService {

	@GET(RestConstants.GET_ALL)
	Call<Results<Encounter>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.GET_BY_UUID)
	Call<Encounter> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);

	@GET(RestConstants.GET_ALL)
	Call<Results<Encounter>> getVisitDocumentsObsByPatientAndConceptList(
			@Path(value = "restPath", encoded = true) String restPath,
			@Query("patient") String patientUuid,
			@Query("conceptList") String conceptList,
			@Query("v") String representation);

	@GET(RestConstants.REST_PATH)
	Call<Results<Encounter>> getByEncounter(@Path(value = "restPath", encoded = true) String restPath,
			@Query("encounter") String encounterUuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@POST(RestConstants.CREATE)
	Call<Encounter> create(@Path(value = "restPath", encoded = true) String restPath,
			@Body Encounter entity);

	@POST(RestConstants.UPDATE)
	Call<Encounter> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, @Body Encounter entity);

}
