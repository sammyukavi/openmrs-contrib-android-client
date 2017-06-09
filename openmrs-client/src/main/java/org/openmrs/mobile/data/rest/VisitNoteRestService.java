package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.VisitPhoto;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface VisitNoteRestService {
	@Multipart
	@POST("{restPath}")
	Call<VisitPhoto> saveVisitNote(@Path(value = "restPath", encoded = true) String restPath,
			@Part("personId") RequestBody patient, @Part("visit") RequestBody visit,
			@Part("provider") RequestBody provider, @Part("fileCaption") RequestBody caption,
			@Part MultipartBody.Part request);
}
