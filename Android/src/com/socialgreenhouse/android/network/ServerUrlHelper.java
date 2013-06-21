package com.socialgreenhouse.android.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Used to save and load the server URL from the default shared preferences.
 */
public class ServerUrlHelper {

	public static final String PREFERENCE_KEY = "server_url";

	public static void saveToContext(Context context, String serverURL) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		sharedPreferences.edit().putString(PREFERENCE_KEY, serverURL).commit();
	}

	public static String getFromContext(Context context) {
		SharedPreferences sharedPreferences = getSharedPreferences(context);
		if (!sharedPreferences.contains(PREFERENCE_KEY)) {
			throw new IllegalStateException("No server URL configured");
		}
		return sharedPreferences.getString(PREFERENCE_KEY, null);
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
