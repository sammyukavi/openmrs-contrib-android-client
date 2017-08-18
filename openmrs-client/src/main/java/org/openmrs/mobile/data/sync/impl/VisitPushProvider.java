package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.Visit;

import javax.inject.Inject;

public class VisitPushProvider extends BasePushProvider<Visit, VisitDbService, VisitRestServiceImpl> {

	@Inject
	public VisitPushProvider(SyncLogDbService syncLogDbService,
			VisitDbService dbService, VisitRestServiceImpl restService) {
		super(syncLogDbService, dbService, restService);
	}

	@Override
	public void sync(SyncLog record) {
		push(record);
	}
}
