package org.openmrs.mobile.data;

import org.openmrs.mobile.data.cache.CacheService;
import org.openmrs.mobile.data.cache.SimpleCacheService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class DataModule {
	@Provides
	@Singleton
	static CacheService provideCacheService() {
		return SimpleCacheService.getInstance();
	}
}
