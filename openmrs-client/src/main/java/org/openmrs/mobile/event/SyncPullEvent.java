package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class SyncPullEvent {

	public final String itemName;
	public final String message;
	public final int totalItemsToPull;

	public SyncPullEvent(String message, @Nullable String itemName, @Nullable Integer totalItemsToPull) {
		this.itemName = itemName;
		this.message = message;
		this.totalItemsToPull = totalItemsToPull;
	}
}
