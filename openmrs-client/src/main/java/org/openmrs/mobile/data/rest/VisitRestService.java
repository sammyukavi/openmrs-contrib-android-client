package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VisitRestService {
	@GET(RestConstants.GET_BY_UUID)
	Call<Visit> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);

	@GET(RestConstants.GET_ALL)
	Call<Results<Visit>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation);

	@GET(RestConstants.GET_ALL)
	Call<Results<Visit>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") int limit,
			@Query("startIndex") int startIndex);

	@POST(RestConstants.CREATE)
	Call<Visit> create(@Path(value = "restPath", encoded = true) String restPath, @Body Visit entity);

	@POST(RestConstants.UPDATE)
	Call<Visit> endVisit(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, @Body Visit stopDatetime);

	@POST(RestConstants.UPDATE)
	Call<Visit> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, @Body Visit entity);

	@POST(RestConstants.REST_PATH)
	Call<Visit> updateVisit(@Path(value = "restPath", encoded = true) String restPath,
			@Query("visit") String visitUuid, @Query("visitType") String visitTypeUuid,
			@Body List<VisitAttribute> visit);

	@DELETE(RestConstants.PURGE)
	Call<Visit> purge(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid);

	@GET(RestConstants.REST_PATH)
	Call<Results<Visit>> getByPatient(@Path(value = "restPath", encoded = true) String restPath,
			@Query("patient") String patientUuid,
			@Query("v") String representation,
			@Query("includeInactive") Boolean includeAll);
}
