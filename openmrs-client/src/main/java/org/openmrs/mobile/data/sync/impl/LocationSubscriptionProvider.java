package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.LocationDbService;
import org.openmrs.mobile.data.rest.impl.LocationRestServiceImpl;
import org.openmrs.mobile.data.rest.retrofit.LocationRestService;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.RecordInfo;

import javax.inject.Inject;

public class LocationSubscriptionProvider extends AdaptiveSubscriptionProvider<Location, LocationDbService,
		LocationRestServiceImpl> {
	@Inject
	public LocationSubscriptionProvider(LocationDbService dbService, DbService<RecordInfo> recordInfoDbService,
			LocationRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
