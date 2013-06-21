package com.socialgreenhouse.android.network;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;

import com.socialgreenhouse.android.SocialGreenhouse;

public class SyncService extends IntentService {

	public SyncService() {
		super(SyncService.class.getName());
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			((SocialGreenhouse) getApplication())
				.getDataManager().updateModules(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
