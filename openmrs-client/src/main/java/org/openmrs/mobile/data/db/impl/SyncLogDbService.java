package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.SyncLog_Table;

public class SyncLogDbService extends BaseDbService<SyncLog> {
	@Override
	protected ModelAdapter<SyncLog> getEntityTable() {
		return (SyncLog_Table)FlowManager.getInstanceAdapter(SyncLog.class);
	}
}
