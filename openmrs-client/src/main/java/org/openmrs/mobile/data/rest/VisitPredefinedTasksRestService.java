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
import org.openmrs.mobile.models.VisitPredefinedTask;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VisitPredefinedTasksRestService {
	@GET(RestConstants.GET_ALL)
	Call<Results<VisitPredefinedTask>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation);

	@GET(RestConstants.GET_ALL)
	Call<Results<VisitPredefinedTask>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("limit") int limit,
			@Query("startIndex") int startIndex);

	@GET(RestConstants.REST_PATH)
	Call<Results<VisitPredefinedTask>> getByName(@Path(value = "restPath", encoded = true) String restPath,
			@Query("q") String name,
			@Query("v") String representation);

	@GET(RestConstants.REST_PATH)
	Call<Results<VisitPredefinedTask>> getByName(@Path(value = "restPath", encoded = true) String restPath,
			@Query("q") String name,
			@Query("v") String representation,
			@Query("limit") int limit,
			@Query("startIndex") int startIndex);

	@GET(RestConstants.GET_BY_UUID)
	Call<VisitPredefinedTask> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);
}
