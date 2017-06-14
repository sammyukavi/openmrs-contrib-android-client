package org.openmrs.mobile.data.cache;

public interface CacheService {
	Object get(String key);

	void set(String key, Object value);

	void clear();
}
