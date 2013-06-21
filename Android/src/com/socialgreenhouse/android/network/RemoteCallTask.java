package com.socialgreenhouse.android.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.socialgreenhouse.android.R;
import com.socialgreenhouse.android.SocialGreenhouse;

public abstract class RemoteCallTask<T> extends AsyncTask<T, Void, Integer> {

	private Context mContext;

	private ProgressDialog mProgressDialog;

	public RemoteCallTask(Context context) {
		mContext = context;
	}

	protected DataManager getDataManager() {
		return ((SocialGreenhouse) mContext.getApplicationContext())
				.getDataManager();
	}
	
	protected String getString(int resId) {
		return mContext.getString(resId);
	}
	
	protected String getString(int resId, Object... formatArgs) {
		return mContext.getString(resId, formatArgs);
	}

	protected Context getContext() {
		return mContext;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(getContext(), null,
				getString(R.string.phrase_connecting));
	}
	
	@Override
	protected void onCancelled(Integer result) {
		mProgressDialog.dismiss();
	}

	@Override
	protected void onPostExecute(Integer result) {
		mProgressDialog.dismiss();
	}
}
