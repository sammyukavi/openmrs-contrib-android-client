package org.openmrs.mobile.data.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.openmrs.mobile.receivers.ConnectivityReceiver;
import org.powermock.api.mockito.PowerMockito;

@Module
public class TestReceiverModule {
	@Provides
	@Singleton
	public ConnectivityReceiver provideConnectivityReceiver() {
		return PowerMockito.mock(ConnectivityReceiver.class);
	}
}
