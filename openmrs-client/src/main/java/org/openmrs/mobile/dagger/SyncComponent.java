package org.openmrs.mobile.dagger;

import org.openmrs.mobile.data.sync.SyncAdapter;
import org.openmrs.mobile.data.sync.SyncService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = SyncModule.class)
public interface SyncComponent {
	void inject(SyncAdapter syncAdapter);
	void inject(SyncService syncService);
}
