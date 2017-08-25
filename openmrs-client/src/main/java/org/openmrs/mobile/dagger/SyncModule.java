package org.openmrs.mobile.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

@Module
public class SyncModule {

	@Provides
	@Singleton
	public EventBus providesEventBus() {
		return EventBus.getDefault();
	}
}
