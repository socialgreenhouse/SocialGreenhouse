package com.socialgreenhouse.android;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.socialgreenhouse.android.network.RemoteCallTask;
import com.socialgreenhouse.android.network.ServerUrlHelper;

public class ServerSetupActivity extends Activity implements OnClickListener {

	public static final String HELP_URL = "http://www.socialgreenhouse.com/instructies.php";

	public static boolean hasSetupServer(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).contains(
				PREFERENCE_SERVER_SETUP_COMPLETE);
	}

	public static final String PREFERENCE_SERVER_SETUP_COMPLETE = "server_setup_complete";

	private EditText mServerAddressEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_setup);

		mServerAddressEditText = (EditText) findViewById(R.id.editText_server_setup_server_address);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_setup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_help :
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(HELP_URL)));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_server_setup_connect :
				onConnect();
				break;
		}
	}

	private void onConnect() {

		// Fetch / fix URL
		String serverUrl = mServerAddressEditText.getText().toString();
		if (!serverUrl.startsWith("http://")) {
			serverUrl = "http://" + serverUrl;
		}

		// Validate URL
		if (!Patterns.WEB_URL.matcher(serverUrl).matches()) {
			mServerAddressEditText
					.setError(getString(R.string.error_server_address_invalid));
			return;
		}

		ServerUrlHelper.saveToContext(this, serverUrl);
		new InitialSyncAsyncTask(this).execute();
	}

	private class InitialSyncAsyncTask extends RemoteCallTask<Void> {

		private static final int RESULT_SUCCESS = 1;
		private static final int RESULT_FAILURE = 0;

		public InitialSyncAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				getDataManager().updateModules(ServerSetupActivity.this);
				return RESULT_SUCCESS;
			} catch (IOException e) {
				e.printStackTrace();
				return RESULT_FAILURE;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {

			Context context = ServerSetupActivity.this;
			PreferenceManager.getDefaultSharedPreferences(context).edit()
					.putBoolean(PREFERENCE_SERVER_SETUP_COMPLETE, true)
					.commit();

			startActivity(new Intent(context, ModuleListActivity.class));
			finish();
		}
	}
}
