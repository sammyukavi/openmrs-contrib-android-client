package org.openmrs.mobile.data.sync.impl.push;

import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.data.sync.impl.BasePushProvider;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.SyncLog;

import javax.inject.Inject;

public class PatientPushProvider extends BasePushProvider {

	@Inject
	public PatientPushProvider(SyncLogDbService syncLogDbService,
			PatientDbService patientDbService,
			PatientRestServiceImpl patientRestService) {
		super(syncLogDbService, patientDbService, patientRestService);
	}

	@Override
	public void sync(SyncLog record) {
		// get entity from db
		Patient patient = (Patient) getEntity(record.getUuid());

		// push entity and delete record from synclog
		pushEntity(patient, record);
	}
}
