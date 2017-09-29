package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PatientListContext_Table;

import java.util.List;

import javax.inject.Inject;

public class PatientListContextDbService extends BaseDbService<PatientListContext>
		implements DbService<PatientListContext> {

	private DatabaseHelper databaseHelper;

	@Inject
	public PatientListContextDbService(Repository repository, DatabaseHelper databaseHelper) {
		super(repository);
		this.databaseHelper = databaseHelper;
	}

	@Override
	protected ModelAdapter<PatientListContext> getEntityTable() {
		return (PatientListContext_Table)FlowManager.getInstanceAdapter(PatientListContext.class);
	}

	public List<PatientListContext> getListPatients(String patientListUuid, QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo,
				(f) -> f.where(PatientListContext_Table.patientList_uuid.eq(patientListUuid)));
	}

	@Override
	protected void postSaveAll(@NonNull List<PatientListContext> entities) {
		if (entities.isEmpty() || entities.get(0).getPatientList() == null) {
			return;
		}
		String patientListUuid = entities.get(0).getPatientList().getUuid();
		boolean allEntitiesPertainToOnePatientList = true;

		for (PatientListContext patientListContext : entities) {
			if (patientListContext.getPatientList() == null
					|| !patientListUuid.equals(patientListContext.getPatientList().getUuid())) {
				allEntitiesPertainToOnePatientList = false;
			}
		}
		if (!allEntitiesPertainToOnePatientList) {
			return;
		}

		// Get all contexts associated with the patient list and delete ones that shouldn't be there
		List<PatientListContext> patientListContextsInTheDB = getListPatients(patientListUuid, null, null);
		List<String> patientListContextUuidsInTheDB = databaseHelper.getEntityUuids(patientListContextsInTheDB);
		List<String> savedPatientListContextUuids = databaseHelper.getEntityUuids(entities);
		for (String patientListContextUuidInTheDB : patientListContextUuidsInTheDB) {
			if (!savedPatientListContextUuids.contains(patientListContextUuidInTheDB)) {
				delete(getByUuid(patientListContextUuidInTheDB, null));
			}
		}
	}
}


