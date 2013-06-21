package com.socialgreenhouse.android.network;

import java.io.IOException;

import nl.mbosit.http.HttpClient;
import nl.mbosit.http.HttpClient.HttpResponseHandler;
import nl.mbosit.http.HttpMethod;
import nl.mbosit.http.HttpRequest;
import nl.mbosit.http.HttpURLConnectionClient;
import nl.mbosit.http.StringHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.socialgreenhouse.android.database.ModuleContract.ModuleTable;

/**
 * Handles interaction with the server.
 */
public class DataManager {

	private static final String CONTENT_CHARSET = "utf-8";
	private static final String CONTENT_TYPE = "application/json; charset=" + CONTENT_CHARSET;
	
	private static final String PATH_MODULES = "/modules";
	private static final String PATH_TRIGGERS = "/triggers";

	private HttpClient mHttpClient = new HttpURLConnectionClient();
	private HttpResponseHandler<String> mHttpResponseHandler = new StringHandler(
			CONTENT_CHARSET);

	/**
	 * Gets all modules from the server.
	 */
	public void updateModules(Context context) throws IOException {
		String serverURL = ServerUrlHelper.getFromContext(context);

		HttpRequest httpRequest = new HttpRequest(serverURL + PATH_MODULES)
				.setHeader("Accept", CONTENT_TYPE);
		
		ContentResolver contentResolver = context.getContentResolver();

		try {
			// Update / insert modules
			JSONArray jsonArray = new JSONArray(mHttpClient.execute(httpRequest,
					mHttpResponseHandler));
			
			Log.i("S", jsonArray.toString(4));
			
			contentResolver.bulkInsert(ModuleTable.CONTENT_URI,
					ModuleJsonHelper.toContentValues(jsonArray));
			
			// Delete removed modules.
			int length = jsonArray.length();
			StringBuilder inClause = new StringBuilder();
			String[] serials = new String[length];
			for (int i = 0; i < length; i++) {
				serials[i] = "" + jsonArray.getJSONObject(i).getLong(ModuleTable.COLUMN_SERIAL_NO);
				inClause.append((i == 0) ? "?" : ", ?");
			}
			contentResolver.delete(ModuleTable.CONTENT_URI, "SerialNo NOT IN("
					+ inClause + ")", serials);
			
		} catch (JSONException e) {
			throw new IOException("Response was not a valid JSON array", e);
		}
	}
	
	public void updateModulesAsync(final Context context) {
		new Thread() {
			@Override
			public void run() {
				try {
					updateModules(context);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Posts a module to the server.
	 */
	public void postModule(Context context, JSONObject moduleJsonObject)
			throws IOException {
		String serverURL = ServerUrlHelper.getFromContext(context);

		HttpRequest httpRequest = new HttpRequest(serverURL + PATH_MODULES)
				.setMethod(HttpMethod.POST)
				.setHeader("Content-Type", CONTENT_TYPE)
				.setContent(
						moduleJsonObject.toString().getBytes(CONTENT_CHARSET));

		mHttpClient.execute(httpRequest);
	}

	/**
	 * Deletes a module from the server.
	 */
	public void deleteModule(Context context, long serialNo) throws IOException {
		String serverURL = ServerUrlHelper.getFromContext(context);

		HttpRequest httpRequest = new HttpRequest(serverURL + PATH_MODULES
				+ "/" + serialNo).setMethod(HttpMethod.DELETE);

		mHttpClient.execute(httpRequest);
	}

	/**
	 * Posts trigger data to the server.
	 */
	public void postTriggerData(Context context, JSONObject triggerDataJsonObject)
		throws IOException {
		String serverURL = ServerUrlHelper.getFromContext(context);
		
		HttpRequest httpRequest = new HttpRequest(serverURL + PATH_TRIGGERS)
				.setMethod(HttpMethod.POST)
				.setHeader("Content-Type", CONTENT_TYPE)
				.setContent(triggerDataJsonObject.toString().getBytes());
		
		mHttpClient.execute(httpRequest);
	}
}
