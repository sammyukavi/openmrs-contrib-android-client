package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class SyncEvent {

	protected final String entity;
	protected final String message;
	protected final Integer totalItems;

	public SyncEvent(String message, @Nullable String entity, @Nullable Integer totalItems) {
		this.entity = entity;
		this.message = message;
		this.totalItems = totalItems;
	}

	public String getEntity() {
		return entity;
	}

	public String getMessage() {
		return message;
	}

	public Integer getTotalItems() {
		return totalItems;
	}
}
