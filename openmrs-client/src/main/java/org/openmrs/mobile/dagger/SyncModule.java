package org.openmrs.mobile.dagger;

import android.content.Context;
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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.openmrs.mobile.sync.SyncAdapter;

@Module
public class SyncModule {
	protected Context mContext;

	public SyncModule(Context context) {
		this.mContext = context;
	}

	@Provides
	@Singleton
	public SyncAdapter providesSyncAdapter() {
		return new SyncAdapter(mContext, true, providesSyncService());
	}

	@Provides
	@Singleton
	public SyncService providesSyncService() {
		return new SyncService(providesSyncProvider(), providesSyncLogDbService(), providesPullSubscriptionDbService());
	}

	@Provides
	@Singleton
	public DbService<SyncLog> providesSyncLogDbService() {
		return new SyncLogDbService();
	}

	@Provides
	@Singleton
	public DbService<PullSubscription> providesPullSubscriptionDbService() {
		return new PullSubscriptionDbService();
	}

	@Provides
	@Singleton
	public SyncProvider providesSyncProvider() {
		return new PushSyncProvider(providesPatientDbService());
	}

	@Provides
	@Singleton
	public DbService<Patient> providesPatientDbService() {
		return new PatientDbService();
	}
}
