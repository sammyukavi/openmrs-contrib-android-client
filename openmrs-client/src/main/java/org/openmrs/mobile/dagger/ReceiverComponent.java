package org.openmrs.mobile.dagger;

import javax.inject.Singleton;

import dagger.Component;
import org.openmrs.mobile.receivers.ConnectivityReceiver;

@Singleton
@Component
public interface ReceiverComponent {
	ConnectivityReceiver connectivityReceiver();
}
