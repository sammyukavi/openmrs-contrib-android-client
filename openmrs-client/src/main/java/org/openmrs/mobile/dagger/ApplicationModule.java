package org.openmrs.mobile.dagger;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.net.NetworkManager;
import org.openmrs.mobile.sync.SyncManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
	protected OpenMRS openMRS;

	public ApplicationModule(OpenMRS openMRS) {
		this.openMRS = openMRS;
	}

	@Provides
	@Singleton
	public SyncManager provideSyncManager() {
		return new SyncManager(openMRS, DaggerReceiverComponent.create());
	}

	@Provides
	@Singleton
	public AuthorizationManager provideAuthorizationManager() {
		return new AuthorizationManager(openMRS);
	}

	@Provides
	@Singleton
	public NetworkManager provideNetworkManager() {
		return new NetworkManager(openMRS, DaggerReceiverComponent.create());
	}
}
