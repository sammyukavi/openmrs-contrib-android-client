/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.utilities;

import javax.inject.Inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.openmrs.mobile.application.OpenMRS;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

	@Inject
	public NetworkUtils() {
	}

	public boolean hasNetwork() {
		NetworkInfo activeNetworkInfo = getNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public boolean isOnline() {

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
		boolean toggle = prefs.getBoolean("sync", true);

		if (toggle) {
			NetworkInfo activeNetworkInfo = getNetworkInfo();
			boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
			if (isConnected)
				return true;
			else {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("sync", false);
				editor.commit();
				return false;
			}

		} else
			return false;

	}

	public boolean checkIfServerOnline() {
		if (hasNetwork()) {
			try {
				HttpURLConnection urlc = (HttpURLConnection)(new URL(OpenMRS.getInstance().getServerUrl()).openConnection
						());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				Log.e("Error", "Error: ", e);
			}
		} else {
			Log.d("error", "No network present");
		}
		return false;
	}

	private NetworkInfo getNetworkInfo() {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) OpenMRS.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo();
	}

	/**
	 * Get the connection speed based on the current network connection
	 * @return An estimate of the connection speed based on typical range estimates
	 */
	public int getCurrentConnectionSpeed(){
		if (!hasNetwork()) {
			return 0;
		}
		NetworkInfo networkInfo = getNetworkInfo();
		int type = networkInfo.getType();
		int subType = networkInfo.getSubtype();

		if (type == ConnectivityManager.TYPE_WIFI) {
			return 20000;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch(subType){
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return 50; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return 20; // ~ 14-64 kbps
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return 50; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					return 500; // ~ 400-1000 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					return 700; // ~ 600-1400 kbps
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return 100; // ~ 100 kbps
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return 3000; // ~ 2-14 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return 800; // ~ 700-1700 kbps
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return 4000; // ~ 1-23 Mbps
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return 800; // ~ 400-7000 kbps
				case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
					return 1000; // ~ 1-2 Mbps
				case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
					return 5000; // ~ 5 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
					return 10000; // ~ 10-20 Mbps
				case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
					return 25; // ~25 kbps
				case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
					return 10000; // ~ 10+ Mbps
				// Unknown
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				default:
					return 0;
			}
		} else {
			return 0;
		}
	}
}
