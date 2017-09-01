package org.openmrs.mobile.data.sync.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitTaskRestServiceImpl;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encounter_Table;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTask_Table;
import org.openmrs.mobile.models.Visit_Table;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitPullProvider {
	private static final int SYNC_VISIT_COUNT = 3;

	private VisitDbService visitDbService;
	private VisitRestServiceImpl visitRestService;
	private EncounterDbService encounterDbService;
	private EncounterRestServiceImpl encounterRestService;
	private VisitTaskDbService visitTaskDbService;
	private VisitTaskRestServiceImpl visitTaskRestService;

	private DatabaseHelper databaseHelper;

	@Inject
	public VisitPullProvider(VisitDbService visitDbService, VisitRestServiceImpl visitRestService,
			EncounterDbService encounterDbService, EncounterRestServiceImpl encounterRestService,
			VisitTaskDbService visitTaskDbService, VisitTaskRestServiceImpl visitTaskRestService,
			DatabaseHelper databaseHelper) {
		this.visitDbService = visitDbService;
		this.visitRestService = visitRestService;
		this.encounterDbService = encounterDbService;
		this.encounterRestService = encounterRestService;
		this.visitTaskDbService = visitTaskDbService;
		this.visitTaskRestService = visitTaskRestService;
		this.databaseHelper = databaseHelper;
	}

	public void pull(@NonNull PullSubscription subscription, @NonNull List<RecordInfo> patientInfo) {
		checkNotNull(subscription);
		checkNotNull(patientInfo);

		for (RecordInfo patientRecord : patientInfo) {
			List<RecordInfo> visitInfo = pullVisits(subscription, patientRecord);

			pullVisitTasks(subscription, patientRecord, visitInfo);

			pullVisitDocuments(subscription, patientRecord, visitInfo);

			pullVisitEncounters(subscription, patientRecord, visitInfo);
		}
	}

	private List<RecordInfo> pullVisits(@NonNull PullSubscription subscription, RecordInfo patientRecord) {
		// Get record info for patient visits
		List<RecordInfo> visitInfo = RestHelper.getCallListValue(
				visitRestService.getRecordInfoByPatient(patientRecord.getUuid(), null, PagingInfo.ALL));

		// Delete any missing visits
		databaseHelper.diffDelete(Visit.class, Visit_Table.patient_uuid.eq(patientRecord.getUuid()), visitInfo);

		// Pull any updated visit information
		List<RecordInfo> checkedVisits = new ArrayList<>();
		List<Visit> visits = new ArrayList<>();
		for (int i = 0; i < SYNC_VISIT_COUNT && i < visitInfo.size(); i++) {
			RecordInfo visitRecord = visitInfo.get(i);
			if (visitRecord.isUpdatedSince(subscription)) {
				Visit visit = RestHelper.getCallValue(
						visitRestService.getByUuid(visitRecord.getUuid(), QueryOptions.INCLUDE_ALL_FULL_REP));

				visit.processRelationships();
				visits.add(visit);
			}

			checkedVisits.add(visitRecord);
		}

		// Save the updated visits
		if (!visits.isEmpty()) {
			visitDbService.saveAll(visits);
		}

		return checkedVisits;
	}

	private void pullVisitTasks(PullSubscription subscription, RecordInfo patientRecord, List<RecordInfo> visitInfo) {
		QueryOptions options = new QueryOptions.Builder().customRepresentation(RestConstants.Representations.VISIT_TASKS)
				.build();

		for (RecordInfo visitRecord : visitInfo) {
			List<RecordInfo> visitTasks = RestHelper.getCallListValue(visitTaskRestService.getRecordInfoByVisit
					(visitRecord.getUuid(), null, PagingInfo.ALL));

			databaseHelper.diffDelete(VisitTask.class, VisitTask_Table.visit_uuid.eq(visitRecord.getUuid()), visitTasks);

			List<VisitTask> visitTaskList = new ArrayList<>();
			for (RecordInfo visitTasksRecord : visitTasks) {
				if (visitTasksRecord.isUpdatedSince(subscription)) {
					VisitTask visitTask = RestHelper.getCallValue(visitTaskRestService.getByUuid(visitTasksRecord.getUuid
							(), options));
					visitTask.processRelationships();
					visitTaskList.add(visitTask);
				}
			}

			if (!visitTaskList.isEmpty()) {
				visitTaskDbService.saveAll(visitTaskList);
			}
		}
	}

	private void pullVisitDocuments(PullSubscription subscription, RecordInfo patientRecord, List<RecordInfo> visitInfo) {
		// TODO: Implement after researching visit document services and image storage
	}

	private void pullVisitEncounters(PullSubscription subscription, RecordInfo patientRecord, List<RecordInfo> visitInfo) {
		QueryOptions options = new QueryOptions.Builder()
				.customRepresentation(RestConstants.Representations.VISIT_ENCOUNTER)
				.build();

		for (RecordInfo visitRecord : visitInfo) {
			// Get record info for visit encounters
			List<RecordInfo> encounterInfo = RestHelper.getCallListValue(
					encounterRestService.getRecordInfoByVisit(visitRecord.getUuid(), null, PagingInfo.ALL));

			// Delete any missing encounters
			databaseHelper.diffDelete(Encounter.class, Encounter_Table.visit_uuid.eq(visitRecord.getUuid()), encounterInfo);

			// Pull any updated encounter information
			List<Encounter> encounters = new ArrayList<>();
			for (RecordInfo encounterRecord : encounterInfo) {
				if (encounterRecord.isUpdatedSince(subscription)) {
					Encounter encounter = RestHelper.getCallValue(
							encounterRestService.getByUuid(encounterRecord.getUuid(), options));

					encounter.processRelationships();
					encounters.add(encounter);
				}
			}

			// Save the updated encounters
			if (!encounters.isEmpty()) {
				encounterDbService.saveAll(encounters);
			}
		}
	}
}
