package org.openmrs.mobile.event;

import android.support.annotation.Nullable;

public class SyncSaveEvent {

	public final String entity;
	public final String message;
	public final int totalRecordsToSave;
	public final int totalRecordsSaved;

	public SyncSaveEvent(String entity, String message, @Nullable int totalRecordsToSave, @Nullable int totalRecordsSaved) {
		this.entity = entity;
		this.message = message;
		this.totalRecordsToSave = totalRecordsToSave;
		this.totalRecordsSaved = totalRecordsSaved;
	}
}
