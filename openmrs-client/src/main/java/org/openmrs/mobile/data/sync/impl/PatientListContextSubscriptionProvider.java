package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.PatientListContextRestServiceImpl;
import org.openmrs.mobile.data.sync.BaseSubscriptionProvider;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PatientListContext_Table;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PatientListContextSubscriptionProvider extends BaseSubscriptionProvider {
	private PatientListContextDbService listPatientDbService;
	private PatientListContextRestServiceImpl listPatientRestService;

	private DatabaseHelper databaseHelper;

	private PatientListPullProvider patientListPullProvider;
	private VisitPullProvider visitPullProvider;
	private EventBus eventBus;

	private String patientListUuid;

	@Inject
	public PatientListContextSubscriptionProvider(PatientListContextDbService dbService,
			PatientListContextRestServiceImpl restService, PatientListPullProvider patientListPullProvider,
			VisitPullProvider visitPullProvider, DatabaseHelper databaseHelper, EventBus eventBus) {
		this.listPatientDbService = dbService;
		this.listPatientRestService = restService;
		this.patientListPullProvider = patientListPullProvider;
		this.visitPullProvider = visitPullProvider;
		this.databaseHelper = databaseHelper;
		this.eventBus = eventBus;
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
		QueryOptions options = new QueryOptions.Builder()
				.customRepresentation(RestConstants.Representations.PATIENT_LIST_PATIENTS)
				.build();
		List<PatientListContext> patients = RestHelper.getCallListValue(
				listPatientRestService.getListPatients(patientListUuid, options, null));

		// Create record info records
		int size = patients == null ? 0 : patients.size();
		List<RecordInfo> records = new ArrayList<>(size);
		if (patients != null && !patients.isEmpty()) {
			for (PatientListContext patient : patients) {
				RecordInfo record = RecordInfo.fromEntity(patient.getPatient());

				records.add(record);
			}
		}

		// Delete context records that are no longer in patient list
		databaseHelper.diffDelete(PatientListContext.class, PatientListContext_Table.patientList_uuid.eq(patientListUuid),
				records);

		if (patients == null || patients.isEmpty()) {
			return;
		}

		// Insert/update context records
		listPatientDbService.saveAll(patients);

		String syncEntityName = "patients";
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING,
				syncEntityName, null));
		// Pull patient information
		patientListPullProvider.pull(subscription, records);
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE,
				syncEntityName, null));

		syncEntityName = "visits";
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING,
				syncEntityName, null));
		// Pull visit information
		visitPullProvider.pull(subscription, records);
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE,
				syncEntityName, null));
	}
}
