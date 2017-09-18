package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.rest.impl.VisitPhotoRestServiceImpl;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.VisitPhoto;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class VisitPhotoDataService
		extends BaseDataService<VisitPhoto, VisitPhotoDbService, VisitPhotoRestServiceImpl>
		implements DataService<VisitPhoto> {

	@Inject
	public VisitPhotoDataService() {
	}

	public void uploadPhoto(VisitPhoto visitPhoto, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback,
				new QueryOptions.Builder().requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL).build(),
				() -> {
					VisitPhoto result = dbService.save(visitPhoto);
					syncLogDbService.save(createSyncLog(result, SyncAction.NEW));
					return result;
				},
				() -> restService.upload(visitPhoto),
				(e) -> dbService.save(visitPhoto));
	}

	public void getPhotosByVisit(@NonNull String visitUuid, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<VisitPhoto>> callback) {
		QueryOptions options = new QueryOptions.Builder()
				.requestStrategy(RequestStrategy.LOCAL_ONLY)
				.build();

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getPhotosByVisit(visitUuid, options, pagingInfo),
				() -> restService.getAll(options, pagingInfo));
	}
}
