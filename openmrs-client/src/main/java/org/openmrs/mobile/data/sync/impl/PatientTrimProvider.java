package org.openmrs.mobile.data.sync.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encounter_Table;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PatientListContext_Table;
import org.openmrs.mobile.models.Patient_Table;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTask_Table;
import org.openmrs.mobile.models.Visit_Table;
import org.openmrs.mobile.models.queryModel.EntityUuid;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PatientTrimProvider {
	private PatientDbService patientDbService;
	private VisitDbService visitDbService;
	private VisitNoteDbService visitNoteDbService;
	private VisitPhotoDbService visitPhotoDbService;
	private VisitTaskDbService visitTaskDbService;
	private ObsDbService obsDbService;
	private EncounterDbService encounterDbService;

	private Repository repository;

	private static final Visit_Table visitTable = (Visit_Table)FlowManager.getInstanceAdapter(Visit.class);
	private static final VisitTask_Table visitTaskTable = (VisitTask_Table)FlowManager.getInstanceAdapter(VisitTask.class);
	private static final Encounter_Table encounterTable = (Encounter_Table)FlowManager.getInstanceAdapter(Encounter.class);
	private static final Patient_Table patientTable = (Patient_Table)FlowManager.getInstanceAdapter(Patient.class);

	@Inject
	public PatientTrimProvider(PatientDbService patientDbService, VisitDbService visitDbService,
			VisitNoteDbService visitNoteDbService, VisitPhotoDbService visitPhotoDbService,
			VisitTaskDbService visitTaskDbService, Repository repository) {
		this.patientDbService = patientDbService;
		this.visitDbService = visitDbService;
		this.visitNoteDbService = visitNoteDbService;
		this.visitPhotoDbService = visitPhotoDbService;
		this.visitTaskDbService = visitTaskDbService;
		this.repository = repository;
	}

	public void trim() {
		// Get uuid's for all patients that are not subscribed
		List<String> patientsToTrim = calculatePatientsToTrim();
		if (patientsToTrim == null || patientsToTrim.isEmpty()) {
			// No patients to trim
			return;
		}

		// Trim patient information
		trimPatients(patientsToTrim);
	}

	public void trimFromPatientList(String patientListUuid) {
		// Get uuid's for all patients that are not subscribed
		List<String> patientsToTrim = calculatePatientListPatientsToTrim(patientListUuid);
		if (patientsToTrim == null || patientsToTrim.isEmpty()) {
			// No patients to trim
			return;
		}

		// Trim patient information
		trimPatients(patientsToTrim);

		ModelQueriable<PatientListContext> query = SQLite.delete(PatientListContext.class)
				.where(PatientListContext_Table.patientList_uuid.eq(patientListUuid));
		repository.deleteAll(query);
	}

	private List<String> calculatePatientListPatientsToTrim(String patientListUuid) {
		// Get patients associated with the list
		ModelQueriable<Patient> query = SQLite.select(Patient_Table.uuid.withTable())
				.from(Patient.class)
				.leftOuterJoin(PatientListContext.class)
				.on(Patient_Table.uuid.withTable().eq(PatientListContext_Table.patient_uuid.withTable()));
		query = ((From<Patient>) query).where(PatientListContext_Table.patientList_uuid.eq(patientListUuid));
		List<String> patientsAssociatedWithList = StreamSupport.stream(repository.queryCustom(EntityUuid.class, query))
				.map(EntityUuid::getUuid).collect(Collectors.toList());

		if (patientsAssociatedWithList == null || patientsAssociatedWithList.isEmpty()) {
			return new ArrayList<String>();
		}

		// Get patients associated with other lists
		query = SQLite.select(Patient_Table.uuid.withTable())
				.from(Patient.class)
				.leftOuterJoin(PatientListContext.class)
				.on(Patient_Table.uuid.withTable().eq(PatientListContext_Table.patient_uuid.withTable()));
		query = ((From<Patient>) query).where(PatientListContext_Table.patientList_uuid.notEq(patientListUuid));
		List<String> patientsToRemain = StreamSupport.stream(repository.queryCustom(EntityUuid.class, query))
				.map(EntityUuid::getUuid).collect(Collectors.toList());

		List<String> patientsToTrim = new ArrayList<>();
		if (patientsToRemain == null || patientsToRemain.isEmpty()) {
			patientsToTrim = patientsAssociatedWithList;
		} else {
			// If the patient to trim is not associated with another list, keep it
			for (String patientUuid : patientsAssociatedWithList) {
				if (!patientsToRemain.contains(patientUuid)) {
					patientsToTrim.add(patientUuid);
				}
			}
		}

		return patientsToTrim;
	}

	private List<String> calculatePatientsToTrim() {
		// Currently, the all subscribed patients are found in the PatientListContext table so any patients not found in
		// that table can be safely trimmed

		// NOTE: If other types of subscriptions are added in the future then they need to be included in this calculation

		ModelQueriable<Patient> query = SQLite.select(Patient_Table.uuid.withTable())
				.from(Patient.class)
				.leftOuterJoin(PatientListContext.class)
				.on(Patient_Table.uuid.withTable().eq(PatientListContext_Table.patient_uuid.withTable()));
		query = ((From<Patient>) query).where(PatientListContext_Table.uuid.withTable().isNull());

		return StreamSupport.stream(repository.queryCustom(EntityUuid.class, query))
				.map(EntityUuid::getUuid).collect(Collectors.toList());
	}

	private void trimPatients(List<String> patientsToTrim) {
		for (String uuid : patientsToTrim) {
			Patient patient = patientDbService.getByUuid(uuid, null);
			if (patient != null) {
				trimVisits(patient);

				// Note: This will delete the patient and also all related models of the specified patient
				patientTable.delete(patient);
			}
		}
	}

	private void trimVisits(Patient patient) {
		// Trim visit tasks for patient
		visitTaskTable.deleteAll(visitTaskDbService.getByPatient(patient, null, null));

		List<Visit> visits = visitDbService.getByPatient(patient, null, null);
		if (visits != null && !visits.isEmpty()) {
			for (Visit visit : visits) {
				// Trim visit encounters
				encounterTable.deleteAll(encounterDbService.getByVisit(visit.getUuid(), null, null));
			}

			// TODO: Trim visit documents for user

			// Trim the visit and associated visit tables
			visitTable.deleteAll(visits);
		}
	}
}

