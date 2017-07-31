package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.PatientListDbService;
import org.openmrs.mobile.data.rest.impl.PatientListRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.RecordInfo;

import javax.inject.Inject;

public class PatientListSubscriptionProvider extends AdaptiveSubscriptionProvider<PatientList, PatientListDbService,
		PatientListRestServiceImpl> {
	@Inject
	public PatientListSubscriptionProvider(PatientListDbService dbService, DbService<RecordInfo> recordInfoDbService,
			PatientListRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
