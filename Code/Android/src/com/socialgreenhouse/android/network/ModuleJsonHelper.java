package com.socialgreenhouse.android.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

import com.socialgreenhouse.android.database.ModuleContract.ModuleTable;

public class ModuleJsonHelper {

	public static final String KEY_SERIAL_NO = "SerialNo";
	public static final String KEY_NAME = "Name";
	public static final String KEY_SENSOR_TYPE = "SensorType";
	public static final String KEY_TRIGGER_DATA = "TriggerData";
	public static final String KEY_VALUE = "Value";
	public static final String KEY_VALUE_TIMESTAMP = "ValueTimestamp";

	public static ContentValues toContentValues(JSONObject jsonObject)
			throws JSONException {

		// Fetch each value from the JSON object
		String serialNo = jsonObject.getString(KEY_SERIAL_NO);
		String sensorType = jsonObject.getString(KEY_SENSOR_TYPE);
		String name = jsonObject.getString(KEY_NAME);
		String value = jsonObject.getString(KEY_VALUE);
		long valueTimestamp = jsonObject.optLong(KEY_VALUE_TIMESTAMP, 0);
		String triggerData = jsonObject.getString(KEY_TRIGGER_DATA);

		// Create the ContentValues object
		ContentValues contentValues = new ContentValues();
		contentValues.put(ModuleTable.COLUMN_SERIAL_NO, serialNo);
		contentValues.put(ModuleTable.COLUMN_SENSOR_TYPE, sensorType);
		contentValues.put(ModuleTable.COLUMN_NAME, name);
		contentValues.put(ModuleTable.COLUMN_VALUE, value);
		contentValues.put(ModuleTable.COLUMN_VALUE_TIMESTAMP, valueTimestamp);
		contentValues.put(ModuleTable.COLUMN_TRIGGER_DATA, triggerData);
		return contentValues;
	}
	
	public static ContentValues[] toContentValues(JSONArray jsonArray)
			throws JSONException {
		
		int length = jsonArray.length();
		ContentValues[] contentValuesArray = new ContentValues[length];
		for (int i = 0; i < length; i++) {
			contentValuesArray[i] = toContentValues(jsonArray.getJSONObject(i));
		}

		return contentValuesArray;
	}
}
