package org.openmrs.mobile.data.dagger;

import android.content.Context;
import org.openmrs.mobile.dagger.SyncModule;
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
import org.openmrs.mobile.sync.SyncAdapter;
import org.powermock.api.mockito.PowerMockito;

public class TestSyncModule extends SyncModule {

	public TestSyncModule(Context context) {
		super(context);
	}

	@Override
	public SyncAdapter providesSyncAdapter() {
		return PowerMockito.mock(SyncAdapter.class);
	}

	@Override
	public SyncService providesSyncService() {
		return PowerMockito.mock(SyncService.class);
	}

	@Override
	public DbService<SyncLog> providesSyncLogDbService() {
		return PowerMockito.mock(SyncLogDbService.class);
	}

	@Override
	public DbService<PullSubscription> providesPullSubscriptionDbService() {
		return PowerMockito.mock(PullSubscriptionDbService.class);
	}

	@Override
	public SyncProvider providesSyncProvider() {
		return PowerMockito.mock(PushSyncProvider.class);
	}

	@Override
	public DbService<Patient> providesPatientDbService() {
		return PowerMockito.mock(PatientDbService.class);
	}
}
