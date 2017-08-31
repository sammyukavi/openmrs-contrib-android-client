package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public abstract class SyncEvent {

	public final String entity;
	public final String message;
	public final Integer totalItems;

	public SyncEvent(String message, @Nullable String entity, @Nullable Integer totalItems) {
		this.entity = entity;
		this.message = message;
		this.totalItems = totalItems;
	}
}
