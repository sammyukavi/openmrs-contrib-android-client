package org.openmrs.mobile.data.impl;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.rest.impl.VisitPhotoRestServiceImpl;
import org.openmrs.mobile.models.VisitPhoto;

import okhttp3.ResponseBody;

public class VisitPhotoDataService
		extends BaseDataService<VisitPhoto, VisitPhotoDbService, VisitPhotoRestServiceImpl>
		implements DataService<VisitPhoto> {
	public void uploadPhoto(VisitPhoto visitPhoto, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback, null,
				() -> dbService.save(visitPhoto),
				() -> restService.uploadPhoto(visitPhoto));
	}

	public void downloadPhoto(String obsUuid, String view, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback, null,
				() -> dbService.getByUuid(obsUuid, null),
				() -> restService.downloadPhoto(obsUuid, view),
				(ResponseBody body) -> {
					VisitPhoto photo = new VisitPhoto();
					photo.setDownloadedImage(BitmapFactory.decodeStream(body.byteStream()));

					return photo;
				},
				(e) -> dbService.save(e)
		);
	}
}
