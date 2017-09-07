package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.VisitNoteRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.VisitNote;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitNotePushProvider extends BasePushProvider<VisitNote, VisitNoteDbService, VisitNoteRestServiceImpl> {
	private SyncLogDbService syncLogDbService;
	private VisitNoteDbService dbService;
	private VisitNoteRestServiceImpl restService;

	@Inject
	public VisitNotePushProvider(SyncLogDbService syncLogDbService,
			VisitNoteDbService dbService, VisitNoteRestServiceImpl restService) {
		super(syncLogDbService, dbService, restService);

		this.syncLogDbService = syncLogDbService;
		this.dbService = dbService;
		this.restService = restService;
	}

	@Override
	protected void push(SyncLog syncLog) {
		if(syncLog.getKey() == null) {
			throw new DataOperationException("Entity UUID must be set");
		}

		VisitNote entity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);

		if (entity == null) {
			throw new DataOperationException("Entity not found");
		}

		Call<VisitNote> call = restService.save(entity);

		// perform rest call
		RestHelper.getCallValue(call);

		syncLogDbService.delete(syncLog);
	}
}
