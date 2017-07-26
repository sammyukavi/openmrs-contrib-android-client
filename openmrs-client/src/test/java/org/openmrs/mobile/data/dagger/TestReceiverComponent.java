package org.openmrs.mobile.data.dagger;

import javax.inject.Singleton;

import dagger.Component;
import org.openmrs.mobile.dagger.ReceiverComponent;
import org.openmrs.mobile.receivers.ConnectivityReceiver;

@Singleton
@Component(modules = TestReceiverModule.class)
public interface TestReceiverComponent extends ReceiverComponent {
	ConnectivityReceiver connectivityReceiver();
}
