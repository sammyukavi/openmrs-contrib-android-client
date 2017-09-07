package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.Visit;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitPushProvider extends BasePushProvider<Visit, VisitDbService, VisitRestServiceImpl> {

	private VisitDbService dbService;
	private VisitRestServiceImpl restService;
	private SyncLogDbService syncLogDbService;

	@Inject
	public VisitPushProvider(SyncLogDbService syncLogDbService,
			VisitDbService dbService, VisitRestServiceImpl restService) {
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

		Visit entity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);

		if (entity == null) {
			throw new DataOperationException("Entity not found");
		}

		Call<Visit> call = null;

		switch (syncLog.getAction()) {
			case NEW:
				call = restService.create(entity);
				break;
			case UPDATED:
				call = restService.updateVisit(entity);
				break;
		}

		// perform rest call
		RestHelper.getCallValue(call);

		syncLogDbService.delete(syncLog);
	}
}
