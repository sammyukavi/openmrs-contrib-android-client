package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Session;

import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dubdabasoduba on 27/05/2017.
 */

public interface SessionRestService {

	Call<Session> getSession(@Path(value = "restPath", encoded = true) String restPath);
}
