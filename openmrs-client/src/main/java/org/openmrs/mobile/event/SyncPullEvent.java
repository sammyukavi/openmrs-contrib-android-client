package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class SyncPullEvent {

	public final String entity;
	public final String message;

	public SyncPullEvent(String entity, String message) {
		this.entity = entity;
		this.message = message;
	}
}
