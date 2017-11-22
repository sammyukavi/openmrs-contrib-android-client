package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class VisitDashboardDataRefreshEvent {

	private String message;

	public VisitDashboardDataRefreshEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
