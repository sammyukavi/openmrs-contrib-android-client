package org.openmrs.mobile.dagger;

import org.openmrs.mobile.data.sync.SyncService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface SyncComponent {
	SyncService sync();
}
