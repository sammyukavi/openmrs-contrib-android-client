package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitPhoto_Table;

public class VisitPhotoDbService extends BaseDbService<VisitPhoto> implements DbService<VisitPhoto> {

	@Override
	protected ModelAdapter<VisitPhoto> getEntityTable() {
		return (VisitPhoto_Table)FlowManager.getInstanceAdapter(VisitPhoto.class);
	}
}

