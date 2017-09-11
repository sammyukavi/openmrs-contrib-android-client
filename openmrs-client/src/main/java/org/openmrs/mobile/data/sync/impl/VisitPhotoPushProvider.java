package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.rest.impl.VisitPhotoRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.VisitPhoto;

import javax.inject.Inject;

public class VisitPhotoPushProvider extends BasePushProvider<VisitPhoto, VisitPhotoDbService, VisitPhotoRestServiceImpl> {
	@Inject
	public VisitPhotoPushProvider(VisitPhotoDbService dbService, VisitPhotoRestServiceImpl restService) {
		super(dbService, restService);
	}

	@Override
	protected void deleteLocalRelatedRecords(VisitPhoto originalEntity, VisitPhoto restEntity) {

	}
}
