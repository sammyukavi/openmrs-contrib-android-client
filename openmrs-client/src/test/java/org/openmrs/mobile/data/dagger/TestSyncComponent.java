package org.openmrs.mobile.data.dagger;

import org.openmrs.mobile.dagger.SyncComponent;
import org.openmrs.mobile.dagger.SyncModule;
import org.openmrs.mobile.data.sync.PushSyncProvider;
import org.openmrs.mobile.data.sync.SyncAdapterTest;

import javax.inject.Singleton;

import dagger.Component;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.sync.SyncAdapter;

@Singleton
@Component(modules = SyncModule.class)
public interface TestSyncComponent extends SyncComponent {
	void inject(SyncAdapterTest syncAdapterTest);
	void inject(SyncAdapter syncAdapter);
	void inject(SyncService syncService);
	void inject(PushSyncProvider pushSyncProvider);
}
