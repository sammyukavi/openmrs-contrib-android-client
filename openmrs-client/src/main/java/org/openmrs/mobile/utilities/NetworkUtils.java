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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.openmrs.mobile.application.OpenMRS;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetworkUtils {

	public static boolean hasNetwork() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager)OpenMRS.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public static boolean isOnline() {

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
		boolean toggle = prefs.getBoolean("sync", true);

		if (toggle) {
			ConnectivityManager connectivityManager
					= (ConnectivityManager)OpenMRS.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
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

	public static boolean checkIfServerOnline() {
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
}
