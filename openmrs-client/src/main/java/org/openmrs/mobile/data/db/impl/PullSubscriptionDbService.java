package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.PullSubscription_Table;

public class PullSubscriptionDbService extends BaseDbService<PullSubscription> {
	@Override
	protected ModelAdapter<PullSubscription> getEntityTable() {
		return (PullSubscription_Table)FlowManager.getInstanceAdapter(PullSubscription.class);
	}
}
