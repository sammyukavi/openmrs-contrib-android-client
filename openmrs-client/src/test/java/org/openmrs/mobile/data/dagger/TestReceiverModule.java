package org.openmrs.mobile.data.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.openmrs.mobile.receivers.SyncReceiver;
import org.powermock.api.mockito.PowerMockito;

@Module
public class TestReceiverModule {
	@Provides
	@Singleton
	public SyncReceiver provideConnectivityReceiver() {
		return PowerMockito.mock(SyncReceiver.class);
	}
}
