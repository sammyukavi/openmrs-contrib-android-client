package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class SyncPushEvent {

	public final String entity;
	public final String message;
	public final int totalRecordsToPush;

	public SyncPushEvent(String entity, String message, @Nullable int totalRecordsToPush) {
		this.entity = entity;
		this.message = message;
		this.totalRecordsToPush = totalRecordsToPush;
	}
}
