package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.PersonDbService;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;

import javax.inject.Inject;

public class PatientPushProvider extends BasePushProvider<Patient, PatientDbService, PatientRestServiceImpl> {
	private PersonDbService personDbService;

	@Inject
	public PatientPushProvider(PatientDbService patientDbService, PatientRestServiceImpl patientRestService,
			PersonDbService personDbService) {
		super(patientDbService, patientRestService);

		this.personDbService = personDbService;
	}

	@Override
	protected void deleteLocalRelatedRecords(Patient originalEntity, Patient restEntity) {
		if (!originalEntity.getPerson().getUuid().equalsIgnoreCase(restEntity.getPerson().getUuid())) {
			personDbService.update(originalEntity.getPerson().getUuid(), restEntity.getPerson());
		}

		dbService.deleteLocalRelatedObjects(originalEntity);
	}
}
