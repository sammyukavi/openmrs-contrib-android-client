package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.DiagnosisSearchResult;

import javax.inject.Inject;

public class DiagnosisSearchDbService extends BaseDbService<DiagnosisSearchResult>
		implements DbService<DiagnosisSearchResult> {
	@Inject
	public DiagnosisSearchDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<DiagnosisSearchResult> getEntityTable() {
		return null;
	}
}
