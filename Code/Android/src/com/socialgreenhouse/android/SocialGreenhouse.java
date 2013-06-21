package com.socialgreenhouse.android;

import android.app.Application;

import com.socialgreenhouse.android.network.DataManager;

public class SocialGreenhouse extends Application {
	
	private DataManager mDataManager = new DataManager();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public DataManager getDataManager() {
		return mDataManager;
	}
}
