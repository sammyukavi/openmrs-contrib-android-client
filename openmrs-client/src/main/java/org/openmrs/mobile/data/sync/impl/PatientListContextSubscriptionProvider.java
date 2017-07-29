package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataUtil;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.impl.PatientListContextRestServiceImpl;
import org.openmrs.mobile.data.sync.BaseSubscriptionProvider;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PatientListContext_Table;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PatientListContextSubscriptionProvider extends BaseSubscriptionProvider {
	private PatientListContextDbService listPatientDbService;
	private PatientListContextRestServiceImpl listPatientRestService;

	private String patientListUuid;

	@Inject
	public PatientListContextSubscriptionProvider(PatientListContextDbService dbService,
			PatientListContextRestServiceImpl restService) {
		this.listPatientDbService = dbService;
		this.listPatientRestService = restService;
	}

	@Override
	public void initialize(PullSubscription subscription) {
		super.initialize(subscription);

		// Get list key
		patientListUuid = subscription.getSubscriptionKey();
	}

	@Override
	public void pull(PullSubscription subscription) {
		if (StringUtils.isBlank(patientListUuid)) {
			return;
		}

		// Get list patients
		QueryOptions options = new QueryOptions();
		options.setCustomRepresentation(RestConstants.Representations.PATIENT_LIST_PATIENTS);
		List<PatientListContext> patients = getCallListValue(listPatientRestService.getListPatients(patientListUuid, null,
				null));
		if (patients == null || patients.size() == 0) {
			return;
		}

		// Create record info records
		List<RecordInfo> records = new ArrayList<>(patients.size());
		for (PatientListContext patient : patients) {
			RecordInfo record = new RecordInfo();
			record.setUuid(patient.getPatient().getUuid());
			record.setDateChanged(patient.getPatient().getDateChanged());

			records.add(record);
		}

		// Delete context records that are no longer in patient list
		DataUtil dataUtil = OpenMRS.getInstance().getDataUtil();
		dataUtil.diffDelete(PatientListContext.class, PatientListContext_Table.patientList_uuid.eq(patientListUuid),
				records);

		// Insert/update context records
		listPatientDbService.saveAll(patients);
	}
}
