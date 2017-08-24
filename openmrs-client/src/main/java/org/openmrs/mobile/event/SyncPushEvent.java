package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class SyncPushEvent {

	public final String entity;
	public final String message;
	public final int totalItemsToPush;

	public SyncPushEvent(String message, @Nullable String entity, @Nullable Integer totalItemsToPush) {
		this.entity = entity;
		this.message = message;
		this.totalItemsToPush = totalItemsToPush;
	}
}
