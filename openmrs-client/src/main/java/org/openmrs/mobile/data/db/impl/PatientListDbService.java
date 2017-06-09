package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.MetadataDbService;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientList_Table;

public class PatientListDbService extends BaseMetadataDbService<PatientList> implements MetadataDbService<PatientList> {

	@Override
	protected ModelAdapter<PatientList> getEntityTable() {
		return (PatientList_Table)FlowManager.getInstanceAdapter(PatientList.class);
	}
}
