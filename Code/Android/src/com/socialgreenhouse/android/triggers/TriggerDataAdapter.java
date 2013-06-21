package com.socialgreenhouse.android.triggers;

import org.json.JSONArray;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Handles the conversion from the JSON representation of a trigger
 * to a view, and back. Used to provide custom interfaces for different
 * types of modules.
 */
public interface TriggerDataAdapter {
	/**
	 * Should create a new view as it should be displayed to the user if no trigger
	 * data is present.
	 */
	public View newView(Context context, ViewGroup parent);
	
	/**
	 * Should populate a view with the user's trigger data.  
	 */
	public void bindView(View view, JSONArray triggerData);
	
	/**
	 * Should create a JSON object from the view.
	 */
	public JSONArray getTriggerData(View view);
}
