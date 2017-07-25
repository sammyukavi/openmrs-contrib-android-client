package org.openmrs.mobile.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.sync.SyncManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.annotation.Config;

@PrepareForTest(ContentResolver.class)
@RunWith(PowerMockRunner.class)
@Config(constants = BuildConfig.class)
public class SyncManagerTest {
	@Mock
	AccountManager accountManager;

	@Mock
	OpenMRS openMRS;

	private SyncManager mSyncManager;

	@Before
	public void setUp() {
		PowerMockito.mockStatic(ContentResolver.class);

		Mockito.when(accountManager.addAccountExplicitly(Mockito.any(Account.class), Mockito.anyString(),
				Mockito.any(Bundle.class))).thenReturn(true);
		Mockito.when(openMRS.getSystemService(openMRS.ACCOUNT_SERVICE)).thenReturn(accountManager);

		mSyncManager = new SyncManager(openMRS);
	}

	@Test
	public void syncManager_syncInitializationCreatesSyncAccount() {
		mSyncManager.initializeDataSync();

		Mockito.verify(accountManager, Mockito.times(1)).addAccountExplicitly(Mockito.any(Account.class),
				Mockito.anyString(), Mockito.any(Bundle.class));
	}

	@Test
	public void syncManager_syncInitializationRegistersPeriodicSync() {
		mSyncManager.initializeDataSync();

		PowerMockito.verifyStatic(Mockito.times(1));
		ContentResolver.addPeriodicSync(Mockito.any(Account.class), Mockito.anyString(), Mockito.eq(Bundle.EMPTY), Mockito.anyLong());
	}
}
