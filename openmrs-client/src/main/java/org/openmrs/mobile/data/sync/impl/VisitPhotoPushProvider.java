package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.VisitPhotoRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.VisitPhoto;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitPhotoPushProvider extends BasePushProvider<VisitPhoto, VisitPhotoDbService, VisitPhotoRestServiceImpl> {
	private VisitPhotoDbService dbService;
	private VisitPhotoRestServiceImpl restService;
	private SyncLogDbService syncLogDbService;

	@Inject
	public VisitPhotoPushProvider(SyncLogDbService syncLogDbService,
			VisitPhotoDbService dbService, VisitPhotoRestServiceImpl restService) {
		super(syncLogDbService, dbService, restService);
		this.dbService = dbService;
		this.restService = restService;
		this.syncLogDbService = syncLogDbService;
	}

	@Override
	protected void push(SyncLog syncLog) {
		if(syncLog.getKey() == null) {
			throw new DataOperationException("Entity UUID must be set");
		}

		VisitPhoto entity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);

		if (entity == null) {
			throw new DataOperationException("Entity not found");
		}

		Call<VisitPhoto> call = restService.upload(entity);

		// perform rest call
		RestHelper.getCallValue(call);

		syncLogDbService.delete(syncLog);
	}
}
