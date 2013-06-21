package com.socialgreenhouse.android.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.socialgreenhouse.android.database.ModuleContract.ModuleTable;

public class ModuleProvider extends ContentProvider {

	private static final UriMatcher sUriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final int MATCH_MODULES = 0;
	private static final int MATCH_MODULE_ROW_ID = 1;

	static {
		final String authority = ModuleContract.CONTENT_AUTHORITY;
		sUriMatcher.addURI(authority, ModuleTable.PATH, MATCH_MODULES);
		sUriMatcher.addURI(authority, ModuleTable.PATH + "/#",
				MATCH_MODULE_ROW_ID);
	}

	private SQLiteOpenHelper mOpenHelper;
	private ContentResolver mContentResolver;

	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new ModuleOpenHelper(context);
		mContentResolver = context.getContentResolver();
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
			case MATCH_MODULE_ROW_ID : {
				final String clause = ModuleTable.COLUMN_SERIAL_NO + " = "
						+ ModuleTable.getRowIdFromModuleUri(uri);
				selection = (selection == null) ? clause : selection + " AND "
						+ clause;
				break;
			}
		}

		final int count = mOpenHelper.getWritableDatabase().delete(
				ModuleTable.TABLE_NAME, selection, selectionArgs);

		mContentResolver.notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case MATCH_MODULES : {
				return ModuleTable.TYPE_CONTENT;
			}
			case MATCH_MODULE_ROW_ID : {
				return ModuleTable.TYPE_CONTENT_ITEM;
			}
			default : {
				throw new UnsupportedOperationException("Unknown URI " + uri);
			}
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		switch (sUriMatcher.match(uri)) {
			case MATCH_MODULES : {
				long id = db
						.insertOrThrow(ModuleTable.TABLE_NAME, null, values);
				mContentResolver.notifyChange(uri, null);
				return ModuleTable.buildModuleUri(id);
			}
			default : {
				throw new UnsupportedOperationException("Invalid URI " + uri);
			}
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(ModuleTable.TABLE_NAME);

		if (selection != null) {
			builder.appendWhere(selection);
		}

		switch (sUriMatcher.match(uri)) {
			case MATCH_MODULE_ROW_ID : {
				builder.appendWhere(ModuleTable.COLUMN_SERIAL_NO + " = "
						+ ModuleTable.getRowIdFromModuleUri(uri));
				break;
			}
		}

		if (sortOrder == null) {
			sortOrder = ModuleTable.DEFAULT_SORT_ORDER;
		}

		final Cursor cursor = builder.query(mOpenHelper.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(mContentResolver, uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
			case MATCH_MODULE_ROW_ID : {
				final String clause = ModuleTable.COLUMN_SERIAL_NO + " = "
						+ ModuleTable.getRowIdFromModuleUri(uri);
				selection = (selection == null) ? clause : selection + " AND "
						+ clause;
				break;
			}
		}

		final int count = mOpenHelper.getWritableDatabase().update(
				ModuleTable.TABLE_NAME, values, selection, selectionArgs);
		mContentResolver.notifyChange(uri, null);
		return count;
	}
}
