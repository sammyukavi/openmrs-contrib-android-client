package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.VisitTaskRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.VisitTask;

import javax.inject.Inject;

public class VisitTaskPushProvider extends BasePushProvider<VisitTask, VisitTaskDbService, VisitTaskRestServiceImpl> {

	@Inject
	public VisitTaskPushProvider(SyncLogDbService syncLogDbService,
			VisitTaskDbService dbService, VisitTaskRestServiceImpl restService) {
		super(syncLogDbService, dbService, restService);
	}
}
