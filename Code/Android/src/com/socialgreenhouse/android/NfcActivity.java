package com.socialgreenhouse.android;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.socialgreenhouse.android.database.SensorType;
import com.socialgreenhouse.android.network.DataManager;
import com.socialgreenhouse.android.network.ModuleJsonHelper;
import com.socialgreenhouse.android.network.RemoteCallTask;

public class NfcActivity extends Activity implements OnClickListener {

	private static final String SEPARATOR = ":";

	private static final int INDEX_SERIAL_NO = 0;
	private static final int INDEX_SENSOR_TYPE = 1;

	private String mSensorType;
	private long mSerialNo;

	private CheckBox mCustomNameCheckBox;
	private EditText mCustomNameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Can't do anything if the server isn't configured.
		if (!ServerSetupActivity.hasSetupServer(this)) {
			startActivity(new Intent(this, ServerSetupActivity.class));
			finish();
			return;
		}

		// Fetch the serial no. from the NFC tag.
		NdefMessage message = (NdefMessage) getIntent()
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];
		NdefRecord[] records = message.getRecords();
		boolean valid = true;

		if (records.length == 2) {
			byte[] payload = records[0].getPayload();
			String[] parts = new String(payload).split(SEPARATOR);
			if (parts.length == 2) {
				mSerialNo = Long.parseLong(parts[INDEX_SERIAL_NO]);
				mSensorType = parts[INDEX_SENSOR_TYPE];
			} else {
				valid = false;
			}
		} else {
			valid = false;
		}

		if (!valid) {
			Toast.makeText(this, getString(R.string.error_nfc_tag_unreadable),
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		setContentView(R.layout.activity_nfc);

		((TextView) findViewById(R.id.textView_nfc_sensor_type))
				.setText(SensorType.RESOURCE_MAP.get(mSensorType));

		((Button) findViewById(R.id.button_nfc_add)).setOnClickListener(this);

		mCustomNameCheckBox = (CheckBox) findViewById(R.id.checkBox_nfc_custom_name);
		mCustomNameCheckBox.setOnClickListener(this);
		mCustomNameEditText = (EditText) findViewById(R.id.editText_nfc_custom_name);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.checkBox_nfc_custom_name :
				mCustomNameEditText.setEnabled(((CheckBox) view).isChecked());
				return;
			case R.id.button_nfc_add :
				onAdd();
				return;
		}
	}

	protected void onAdd() {

		String name = mCustomNameCheckBox.isChecked() ? mCustomNameEditText
				.getText().toString() : "";

		JSONObject module = new JSONObject();
		try {
			module.put(ModuleJsonHelper.KEY_NAME, name);
			module.put(ModuleJsonHelper.KEY_SERIAL_NO, mSerialNo);
			module.put(ModuleJsonHelper.KEY_SENSOR_TYPE, mSensorType);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		
		new ModulePostTask(this).execute(module);
	}
	
	private class ModulePostTask extends RemoteCallTask<JSONObject> {

		private static final int RESULT_SUCCESS = 1;
		private static final int RESULT_FAILURE = 0;

		public ModulePostTask(Context context) {
			super(context);
		}

		@Override
		protected Integer doInBackground(JSONObject... params) {
			try {
				DataManager dataManager = getDataManager();
				dataManager.postModule(NfcActivity.this, params[0]);
				dataManager.updateModules(NfcActivity.this);
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
					? R.string.phrase_module_save_success
					: R.string.error_io_failure;

			Toast.makeText(NfcActivity.this, getString(message), Toast.LENGTH_SHORT)
					.show();
			
			startActivity(new Intent(NfcActivity.this, ModuleListActivity.class));
			finish();
		}
	}

}
