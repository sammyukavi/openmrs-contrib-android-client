package org.openmrs.mobile.dagger;

import javax.inject.Singleton;

import dagger.Component;
import org.openmrs.mobile.receivers.SyncReceiver;

@Singleton
@Component
public interface ReceiverComponent {
	SyncReceiver syncReceiver();
}
