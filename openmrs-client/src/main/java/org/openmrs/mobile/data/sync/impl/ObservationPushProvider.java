package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.impl.ObsRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.SyncLog;

import javax.inject.Inject;

public class ObservationPushProvider extends BasePushProvider<Observation, ObsDbService, ObsRestServiceImpl> {

	@Inject
	public ObservationPushProvider(SyncLogDbService syncLogDbService,
			ObsDbService dbService, ObsRestServiceImpl restService) {
		super(syncLogDbService, dbService, restService);
	}

	@Override
	public void sync(SyncLog record) {
		push(record);
	}
}
