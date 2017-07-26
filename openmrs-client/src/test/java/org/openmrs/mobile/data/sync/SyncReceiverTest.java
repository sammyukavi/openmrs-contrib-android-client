package org.openmrs.mobile.data.sync;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.receivers.SyncReceiver;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SyncReceiverTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	OpenMRS openMRS;

	@Mock
	Intent intent;

	@Mock
	ConnectivityManager connectivityManager;

	@Mock
	NetworkInfo networkInfo;

	private SyncReceiver syncReceiver;

	@Before
	public void setUp() {
		Mockito.when(openMRS.getApplicationContext()).thenReturn(openMRS);
		Mockito.when(openMRS.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);

		syncReceiver = new SyncReceiver();
	}

	@Test
	public void connectivityReceiver_whenReceiveBroadcastWithoutActiveNetworkSyncIsNotCalled() {
		Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(null);

		syncReceiver.onReceive(openMRS, intent);

		Mockito.verify(openMRS, Mockito.times(0)).requestDataSync();
	}

	@Test
	public void connectivityReceiver_whenReceiveBroadcastWithoutNetworkConnectionSyncIsNotCalled() {
		Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
		Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(false);

		syncReceiver.onReceive(openMRS, intent);

		Mockito.verify(openMRS, Mockito.times(0)).requestDataSync();
	}

	@Test
	public void connectivityReceiver_whenReceiveBroadcastAndServerAvailableSyncIsCalled() {
		Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
		Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);

		syncReceiver.onReceive(openMRS, intent);

		Mockito.verify(openMRS, Mockito.times(1)).requestDataSync();
	}
}
