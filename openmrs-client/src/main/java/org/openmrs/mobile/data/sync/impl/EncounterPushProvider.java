package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.SyncLog;

import javax.inject.Inject;

public class EncounterPushProvider extends BasePushProvider<Encounter, EncounterDbService, EncounterRestServiceImpl> {

	@Inject
	public EncounterPushProvider(SyncLogDbService syncLogDbService,
			EncounterDbService encounterDbService,
			EncounterRestServiceImpl encounterRestService) {
		super(syncLogDbService, encounterDbService, encounterRestService);
	}

	@Override
	public void sync(SyncLog record) {
		push(record);
	}
}
