package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseEntityDbService;
import org.openmrs.mobile.data.db.EntityDbService;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.Visit_Table;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.Date;

public class VisitDbService extends BaseEntityDbService<Visit> implements EntityDbService<Visit> {
	@Override
	protected ModelAdapter<Visit> getEntityTable() {
		return (Visit_Table)FlowManager.getInstanceAdapter(Visit.class);
	}

	public Visit endVisit(Visit visit) {
		visit.setStopDatetime(DateUtils.convertTime(new Date().getTime()));

		return save(visit);
	}
}
