package org.openmrs.mobile.data.db.impl;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.Patient;

import java.util.List;

public class PatientDbService extends BaseDbService<Patient> implements DbService<Patient> {
	public List<Patient> getByName(String name, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}
}

