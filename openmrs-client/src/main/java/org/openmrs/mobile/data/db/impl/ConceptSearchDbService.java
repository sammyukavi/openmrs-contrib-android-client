package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.ConceptSearchResult;

public class ConceptSearchDbService extends BaseDbService<ConceptSearchResult>
		implements DbService<ConceptSearchResult> {
	@Override
	protected ModelAdapter<ConceptSearchResult> getEntityTable() {
		// Not sure how to model this in the db so leave it null for now
		return null;
	}
}
