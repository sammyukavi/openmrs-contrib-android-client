package org.openmrs.mobile.receivers;

import javax.inject.Inject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.NetworkUtils;

public class SyncReceiver extends BroadcastReceiver {
	@Inject
	public SyncReceiver() {}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (isInitialStickyBroadcast()) {
//			return;
		}
		OpenMRS openMRS = (OpenMRS) context.getApplicationContext();
		if (openMRS.getNetworkUtils().hasNetwork()) {
			openMRS.requestDataSync();
		}
	}
}
