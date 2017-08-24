package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class SyncPullEvent extends SyncEvent {

	public SyncPullEvent(String message, @Nullable String entity, @Nullable Integer totalItemsToPush) {
		super(message, entity, totalItemsToPush);
	}
}
