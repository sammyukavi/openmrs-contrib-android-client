package org.openmrs.mobile.data;

import android.support.annotation.Nullable;

import org.openmrs.mobile.data.rest.RestConstants;

public class QueryOptions {
	public static final boolean DEFAULT_INCLUDE_INACTIVE = false;
	public static final boolean DEFAULT_LOAD_RELATED_OBJECTS = false;

	private boolean includeInactive = DEFAULT_INCLUDE_INACTIVE;
	private boolean loadRelatedObjects = DEFAULT_LOAD_RELATED_OBJECTS;

	public static final QueryOptions INCLUDE_INACTIVE = new QueryOptions(true, false);
	public static final QueryOptions LOAD_RELATED_OBJECTS = new QueryOptions(false, true);

	public static boolean getIncludeInactive(@Nullable QueryOptions options) {
		return options == null ? DEFAULT_INCLUDE_INACTIVE : options.includeInactive();
	}

	public static boolean getLoadRelatedObjects(@Nullable QueryOptions options) {
		return options == null ? DEFAULT_LOAD_RELATED_OBJECTS : options.loadRelatedObjects();
	}

	public static String getRepresentation(@Nullable QueryOptions options) {
		return getLoadRelatedObjects(options) ? RestConstants.Representations.FULL : RestConstants.Representations.DEFAULT;
	}

	public QueryOptions() { }

	public QueryOptions(boolean includeInactive, boolean loadRelatedObjects) {
		this.includeInactive = includeInactive;
		this.loadRelatedObjects = loadRelatedObjects;
	}

	public boolean includeInactive() {
		return includeInactive;
	}

	public void setIncludeInactive(boolean includeInactive) {
		this.includeInactive = includeInactive;
	}

	public boolean loadRelatedObjects() {
		return loadRelatedObjects;
	}

	public void setLoadRelatedObjects(boolean loadRelatedObjects) {
		this.loadRelatedObjects = loadRelatedObjects;
	}
}
