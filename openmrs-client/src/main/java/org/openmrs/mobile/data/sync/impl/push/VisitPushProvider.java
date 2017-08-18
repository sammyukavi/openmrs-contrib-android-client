package org.openmrs.mobile.data.sync.impl.push;

import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.sync.impl.BasePushProvider;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.Visit;

public class VisitPushProvider extends BasePushProvider<Visit, VisitDbService, VisitRestServiceImpl> {

	public VisitPushProvider(SyncLogDbService syncLogDbService,
			VisitDbService dbService, VisitRestServiceImpl restService) {
		super(syncLogDbService, dbService, restService);
	}

	@Override
	public void sync(SyncLog record) {
		// push entity and delete record from synclog
		pushEntity(getEntity(record.getUuid()), record);
	}
}
