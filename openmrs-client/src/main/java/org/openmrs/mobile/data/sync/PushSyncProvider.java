package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.SyncLog;

import javax.inject.Inject;

public class PushSyncProvider implements SyncProvider {

	private DbService<Patient> mPatientDbService;

	@Inject
	public PushSyncProvider(DbService<Patient> patientDbService) {
		this.mPatientDbService = patientDbService;
	}

	@Override
	public void sync(SyncLog record) {

	}
}

