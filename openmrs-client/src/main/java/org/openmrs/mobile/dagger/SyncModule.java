package org.openmrs.mobile.dagger;

import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.sync.PushSyncProvider;
import org.openmrs.mobile.data.sync.SyncProvider;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.SyncLog;

import java.io.SyncFailedException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SyncModule {

	@Provides
	@Singleton
	SyncService providesSyncService() {
		return new SyncService();
	}

	@Provides
	@Singleton
	DbService<SyncLog> providesSyncLogDbService() {
		return new SyncLogDbService();
	}

	@Provides
	@Singleton
	DbService<PullSubscription> providesPullSubscriptionDbService() {
		return new PullSubscriptionDbService();
	}

	@Provides
	@Singleton
	SyncProvider providesSyncProvider() {
		return new PushSyncProvider();
	}

	@Provides
	@Singleton
	DbService<Patient> providesPatientDbService() {
		return new PatientDbService();
	}
}
