package com.socialgreenhouse.android;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Justin
 */
public class AdkActivity extends Activity implements OnClickListener {

	/**
	 * Action string for identifying permission requests.
	 */
	private static final String ACTION_USB_PERMISSION = "com.example.adkdemo.action.USB_PERMISSION";
	/**
	 * Provides access to USB accessories.
	 */
	private UsbManager usbManager;
	/**
	 * Used for requesting permission to connect with a USB device.
	 */
	private PendingIntent pendingIntent;
	/**
	 * Indicates whether permission has been requested or not.
	 */
	private boolean permissionPending = false;
	/**
	 * Contains information about the currently connected accessory.
	 */
	private UsbAccessory accessory;
	/**
	 * Used to open and close streams to the currently connected accessory.
	 */
	private ParcelFileDescriptor handle;
	/**
	 * Input stream from the currently connected accessory.
	 */
	private FileInputStream in;
	/**
	 * Output stream to the currently connected accessory.
	 */
	private FileOutputStream out;
	/**
	 * All the fields
	 */
	private EditText ssidInput;
	private EditText phraseInput;
	private Spinner connectionTypeSpinner;
	private EditText serverIpInput;
	private EditText serverPortInput;
	/**
	 * Receiver that responds to:
	 * 
	 * - USB permission requests - USB accessory detachment
	 */
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(ACTION_USB_PERMISSION)) {
				synchronized (this) {
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						open((UsbAccessory) intent
								.getParcelableExtra(UsbManager.EXTRA_ACCESSORY));
					}

					permissionPending = false;
				}
			} else if (action.equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)) {
				UsbAccessory accessory = (UsbAccessory) intent
						.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

				if (accessory != null) {
					closeAccessory(accessory);
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// GUI set-up.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adk);

		// Obtain the USB manager and permission broadcast.
		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);

		// Register the broadcast receiver with appropriate
		// filters.
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(receiver, filter);

		ssidInput = (EditText) findViewById(R.id.editText_adk_ssid);
		phraseInput = (EditText) findViewById(R.id.editText_adk_phrase);
		connectionTypeSpinner = (Spinner) findViewById(R.id.spinner_adk_security);
		serverIpInput = (EditText) findViewById(R.id.editText_adk_ip);
		serverPortInput = (EditText) findViewById(R.id.editText_adk_port);

		((Button) findViewById(R.id.button_adk_set_network))
				.setOnClickListener(this);
		
		((Button) findViewById(R.id.button_adk_set_server))
				.setOnClickListener(this);

		connectionTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (connectionTypeSpinner.getSelectedItemId() == 0) {
							phraseInput.setVisibility(View.INVISIBLE);
						} else {
							phraseInput.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();

		if (in != null && out != null) // <- ?
		{
			return;
		}

		UsbAccessory[] accessories = usbManager.getAccessoryList();

		if (accessories != null) {
			UsbAccessory accessory = accessories[0];

			if (usbManager.hasPermission(accessory)) {
				open(accessory);
			} else {
				synchronized (receiver) // <- ?
				{
					if (!permissionPending) {
						usbManager.requestPermission(accessory, pendingIntent);
						permissionPending = true;
					}
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		closeAccessory(accessory);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void open(UsbAccessory accessory) {
		handle = usbManager.openAccessory(accessory);

		if (handle != null) {
			// The accessory is stored for later identification.
			this.accessory = accessory;

			// Create in- and output streams.
			FileDescriptor file = handle.getFileDescriptor();
			in = new FileInputStream(file);
			out = new FileOutputStream(file);
		}
	}

	private void closeAccessory(UsbAccessory accessory) {
		if (accessory != this.accessory) {
			return;
		}

		try {
			if (handle != null) {
				handle.close();
			}
		} catch (IOException e) {
			// Ignore.
		} finally {
			handle = null;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adk_set_network :
				setNetwork();
				return;
			case R.id.button_adk_set_server :
				setServer();
				return;
		}
	}

	public void setNetwork() {

		String ssid = ssidInput.getText().toString();
		String phrase = phraseInput.getText().toString();

		if (ssid.contains("$") || ssid.contains("#") || ssid.length() < 4) {
			ssidInput.setError(getString(R.string.error_ssid_invalid));
			return;
		}

		if (connectionTypeSpinner.getSelectedItemId() != 0
				&& (phrase.contains("$") || phrase.contains("#") || (phrase
						.length() < 8))) {
			ssidInput.setError(getString(R.string.error_phrase_invalid));
			return;
		}

		String security = connectionTypeSpinner.getSelectedItemId() == 0
				? "OPEN+"
				: "WPA+" + phrase;

		int result = R.string.error_adk;
		if (out != null) {
			try {
				String output = "#connect " + ssid + "+" + security + "$";
				out.write(output.getBytes());
				result = R.string.phrase_adk_set_network_success;
			} catch (IOException e) {
			}
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	public void setServer() {

		String ip = serverIpInput.getText().toString();
		int port = Integer.parseInt(serverPortInput.getText().toString());

		if (!Patterns.IP_ADDRESS.matcher(ip).matches()) {
			serverIpInput.setError(getString(R.string.error_ip_invalid));
			return;
		}

		if (port < 1 || port > 60000) {
			serverPortInput.setError(getString(R.string.error_port_invalid));
			return;
		}

		int result = R.string.error_adk;
		if (out != null) {
			try {
				String output = "#server " + ip + ":" + port + "$";
				out.write(output.getBytes());
				result = R.string.phrase_adk_set_server_success;
			} catch (IOException e) {
			}
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}
}