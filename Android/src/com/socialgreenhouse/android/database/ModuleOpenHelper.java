package com.socialgreenhouse.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.socialgreenhouse.android.database.ModuleContract.ModuleTable;

/**
 * Manages creation and version changes of the SQLite database.
 * 
 * @author Melvin
 */
public class ModuleOpenHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "module.db";

	public ModuleOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + ModuleTable.TABLE_NAME + " ("
				+ ModuleTable.COLUMN_SERIAL_NO + " INTEGER, " + ModuleTable.COLUMN_NAME
				+ " TEXT, " + ModuleTable.COLUMN_SENSOR_TYPE + " TEXT, "
				+ ModuleTable.COLUMN_VALUE + " TEXT, "
				+ ModuleTable.COLUMN_VALUE_TIMESTAMP + " INTEGER, "
				+ ModuleTable.COLUMN_TRIGGER_DATA + " TEXT, " + "PRIMARY KEY ("
				+ ModuleTable.COLUMN_SERIAL_NO + ") ON CONFLICT REPLACE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
