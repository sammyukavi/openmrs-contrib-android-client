package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Patient;

import javax.inject.Inject;

public class PatientPushProvider extends BasePushProvider<Patient, PatientDbService, PatientRestServiceImpl> {

	@Inject
	public PatientPushProvider(SyncLogDbService syncLogDbService,
			PatientDbService patientDbService,
			PatientRestServiceImpl patientRestService) {
		super(syncLogDbService, patientDbService, patientRestService);
	}
}
