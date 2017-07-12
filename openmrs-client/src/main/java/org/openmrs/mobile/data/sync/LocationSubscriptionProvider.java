package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.RecordInfo;

import java.util.List;

public class LocationSubscriptionProvider extends AdaptiveSubscriptionProvider<Location> {

	@Override
	protected List<Location> getRestAll() {
		return null;
	}

	@Override
	protected List<RecordInfo> getRestRecordInfo() {
		return null;
	}

	@Override
	protected Location getRestObject(String uuid) {
		return null;
	}
}
