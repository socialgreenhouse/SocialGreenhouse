package com.socialgreenhouse.android.database;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;

import com.socialgreenhouse.android.R;

/**
 * List of all available sensor types.
 * 
 * @author Melvin
 */
public class SensorType {
	
	public static final String TEMPERATURE = "temperature";
	public static final String SOIL_MOISTURE = "soil_moisture";

	/**
	 * Sensor types mapped to resource IDs. Use this to fetch localized strings
	 * for sensor types.
	 */
	public static final Map<String, Integer> RESOURCE_MAP = new HashMap<String, Integer>();

	/**
	 * 
	 */
	public static final Map<String, Class<Fragment>> FRAGMENT_MAP = new HashMap<String, Class<Fragment>>();
	
	static {
		RESOURCE_MAP.put(TEMPERATURE, R.string.sensor_temperature);
		RESOURCE_MAP.put(SOIL_MOISTURE, R.string.sensor_soil_moisture);
	}
}
