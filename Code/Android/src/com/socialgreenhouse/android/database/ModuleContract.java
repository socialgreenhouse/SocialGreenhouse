package com.socialgreenhouse.android.database;

import android.net.Uri;

public class ModuleContract {
	
	public static final String CONTENT_AUTHORITY = "com.socialgreenhouse.android.module";
	
	private static final Uri CONTENT_URI_BASE = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	public static class ModuleTable {
		
		public static final String PATH = "modules";

		public static final Uri CONTENT_URI = CONTENT_URI_BASE.buildUpon()
				.appendPath(PATH).build();

		public static Uri buildModuleUri(long moduleSerialNo) {
			return CONTENT_URI.buildUpon().appendPath(moduleSerialNo + "").build();
		}

		public static long getRowIdFromModuleUri(Uri moduleUri) {
			return Long.parseLong(moduleUri.getPathSegments().get(1));
		}

		public static final String TYPE_CONTENT = "vnd.android.cursor.dir/vnd.socialgreenhouse.module";
		public static final String TYPE_CONTENT_ITEM = "vnd.android.cursor.item/vnd.socialgreenhouse.module";

		public static final String TABLE_NAME = "Module";

		public static final String COLUMN_SERIAL_NO = "SerialNo";
		public static final String COLUMN_SENSOR_TYPE = "SensorType";
		public static final String COLUMN_NAME = "Name";
		public static final String COLUMN_VALUE = "Value";
		public static final String COLUMN_VALUE_TIMESTAMP = "ValueTimestamp";
		public static final String COLUMN_TRIGGER_DATA = "TriggerData";

		public static final String DEFAULT_SORT_ORDER = COLUMN_NAME + " ASC, "
				+ COLUMN_SENSOR_TYPE + " ASC";

		private ModuleTable() {

		}
	}
	
	public static class TriggerData {
		
		public static final String KEY_ACTION = "action";
		public static final String KEY_ACTION_NAME = "name";
		public static final String KEY_ACTION_ARGUMENTS = "arguments";
		
		public static final String KEY_CONDITIONS = "conditions";
		public static final String KEY_CONDITION_ARGUMENTS = "arguments";
		public static final String KEY_CONDITION_NAME = "name";
		public static final String KEY_ENABLED = "enabled";
		
		public static final String KEY_COOLDOWN = "cooldown";
		
		public static final String CONDITION_LESS_THAN = "LessThan";
		public static final String CONDITION_GREATER_THAN = "GreaterThan";
		
		public static final String ACTION_TWEET = "Tweet";
		
		public static final int COOLDOWN_MINUTE = 1;
		public static final int COOLDOWN_HOUR = 60 * COOLDOWN_MINUTE;
		
		private TriggerData() {
			
		}
	}

	private ModuleContract() {

	}
}
