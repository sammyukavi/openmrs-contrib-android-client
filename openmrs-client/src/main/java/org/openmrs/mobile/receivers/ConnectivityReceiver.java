package org.openmrs.mobile.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.openmrs.mobile.application.OpenMRS;

public class ConnectivityReceiver extends BroadcastReceiver {
	private static final String TAG = "OpenMRSBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
			((OpenMRS) context.getApplicationContext()).requestDataSync();
		}
	}
}
