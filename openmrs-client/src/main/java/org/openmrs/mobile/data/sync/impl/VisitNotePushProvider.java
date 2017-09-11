package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.rest.impl.VisitNoteRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.VisitNote;

import javax.inject.Inject;

public class VisitNotePushProvider extends BasePushProvider<VisitNote, VisitNoteDbService, VisitNoteRestServiceImpl> {

	@Inject
	public VisitNotePushProvider(SyncLogDbService syncLogDbService,
			VisitNoteDbService dbService, VisitNoteRestServiceImpl restService) {
		super(syncLogDbService, dbService, restService);
	}
}
