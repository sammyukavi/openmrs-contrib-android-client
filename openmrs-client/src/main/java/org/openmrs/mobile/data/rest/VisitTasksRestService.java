/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitTask;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VisitTasksRestService {
	@GET(RestConstants.GET_BY_UUID)
	Call<VisitTask> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);

	@POST(RestConstants.CREATE)
	Call<VisitTask> create(@Path(value = "restPath", encoded = true) String restPath, @Body VisitTask entity);

	@POST(RestConstants.UPDATE)
	Call<VisitTask> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, @Body VisitTask entity);

	@DELETE(RestConstants.PURGE)
	Call<VisitTask> purge(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid);

	@GET(RestConstants.REST_PATH)
	Call<Results<VisitTask>> getByName(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("status") String status,
			@Query("q") String name,
			@Query("patient_uuid") String patientUuid,
			@Query("visit_uuid") String visitUuid
	);

	@GET(RestConstants.REST_PATH)
	Call<Results<VisitTask>> getByName(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("status") String status,
			@Query("q") String name,
			@Query("patient_uuid") String patientUuid,
			@Query("visit_uuid") String visitUuid,
			@Query("limit") int limit,
			@Query("startIndex") int startIndex);

	@GET(RestConstants.GET_ALL)
	Call<Results<VisitTask>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("status") String status,
			@Query("patient_uuid") String patientUuid,
			@Query("visit_uuid") String visitUuid);

	@GET(RestConstants.GET_ALL)
	Call<Results<VisitTask>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("status") String status,
			@Query("patient_uuid") String patientUuid,
			@Query("visit_uuid") String visitUuid,
			@Query("limit") int limit,
			@Query("startIndex") int startIndex);
}
