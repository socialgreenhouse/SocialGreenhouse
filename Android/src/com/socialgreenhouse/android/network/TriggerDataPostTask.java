package com.socialgreenhouse.android.network;

import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.socialgreenhouse.android.R;

public class TriggerDataPostTask extends RemoteCallTask<JSONObject> {

	private static final int RESULT_FAILURE = 0;
	private static final int RESULT_SUCCESS = 1;

	public TriggerDataPostTask(Context context) {
		super(context);
	}

	@Override
	protected Integer doInBackground(JSONObject... params) {
		Context context = getContext();
		DataManager dataManager = getDataManager();
		try {
			dataManager.postTriggerData(context, params[0]);
			dataManager.updateModules(context);
			return RESULT_SUCCESS;
		} catch (IOException e) {
			e.printStackTrace();
			return RESULT_FAILURE;
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		int message = result == RESULT_SUCCESS
				? R.string.phrase_trigger_data_save_success
				: R.string.error_io_failure;
		
		Toast.makeText(getContext(), getString(message), Toast.LENGTH_SHORT)
				.show();
	}
}
