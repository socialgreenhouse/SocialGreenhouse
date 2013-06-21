package com.socialgreenhouse.android;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.socialgreenhouse.android.database.ModuleContract.ModuleTable;
import com.socialgreenhouse.android.database.SensorType;
import com.socialgreenhouse.android.network.DataManager;
import com.socialgreenhouse.android.network.RemoteCallTask;
import com.socialgreenhouse.android.network.TriggerDataPostTask;
import com.socialgreenhouse.android.triggers.RangeTriggerDataAdapter;
import com.socialgreenhouse.android.triggers.TriggerDataAdapter;

/**
 * A fragment representing a single module detail screen. This fragment is
 * either contained in a {@link ModuleListActivity} in two-pane mode (on
 * tablets) or a {@link ModuleDetailActivity} on handsets.
 */
public class ModuleDetailFragment extends Fragment
		implements
			LoaderCallbacks<Cursor> {

	/**
	 * The fragment argument representing the serial no. of the module that this
	 * fragment represents.
	 */
	public static final String ARG_MODULE_SERIAL_NO = "module_serial_no";

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of events
	 * related to this fragment.
	 */
	public interface Callbacks {
		public void onDelete(Fragment fragment);
	}

	/**
	 * Describes the query used to fetch the module this fragment is presenting.
	 */
	private interface ModuleQuery {

		public static final String[] PROJECTION = new String[]{
				ModuleTable.COLUMN_SENSOR_TYPE, ModuleTable.COLUMN_VALUE,
				ModuleTable.COLUMN_VALUE_TIMESTAMP,
				ModuleTable.COLUMN_TRIGGER_DATA};

		public static final int SENSOR_TYPE = 0;
		public static final int VALUE = 1;
		public static final int VALUE_TIMESTAMP = 2;
		public static final int TRIGGER_DATA = 3;
	}

	private static final Map<String, TriggerDataAdapter> sTriggerDataAdapterMap = new HashMap<String, TriggerDataAdapter>();
	static {
		TriggerDataAdapter adapter = new RangeTriggerDataAdapter();
		sTriggerDataAdapterMap.put(SensorType.TEMPERATURE, adapter);
		sTriggerDataAdapterMap.put(SensorType.SOIL_MOISTURE, adapter);
	}

	private TriggerDataAdapter mTriggerDataAdapter;

	/**
	 * The serial no. of the module this fragment is presenting.
	 */
	private long mModuleSerialNo;

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static final Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onDelete(Fragment fragment) {
		}
	};

	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * Date format used to display {@link ModuleTable#COLUMN_VALUE_TIMESTAMP}.
	 */
	private DateFormat mDateFormat = DateFormat.getDateTimeInstance(
			DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ModuleDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (getArguments().containsKey(ARG_MODULE_SERIAL_NO)) {
			mModuleSerialNo = getArguments().getLong(ARG_MODULE_SERIAL_NO);
			getLoaderManager().initLoader(0, null, this);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_delete :
				new ModuleDeleteTask(getActivity()).execute(mModuleSerialNo);
				return true;
			case R.id.action_save :
				onSave();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.module_detail, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_module_detail, container,
				false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
				ModuleTable.buildModuleUri(mModuleSerialNo),
				ModuleQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		// The module may have been deleted.
		if (cursor.getCount() != 1) {
			return;
		}

		cursor.moveToFirst();
		String sensorType = cursor.getString(ModuleQuery.SENSOR_TYPE);
		String value = cursor.getString(ModuleQuery.VALUE);
		long valueTimestamp = cursor.getLong(ModuleQuery.VALUE_TIMESTAMP);
		String triggerData = cursor.getString(ModuleQuery.TRIGGER_DATA);

		View view = getView();
		// Display the sensor name, or 'unknown' if it's of an unknown type.
		int sensorTypeResId = SensorType.RESOURCE_MAP.containsKey(sensorType)
				? SensorType.RESOURCE_MAP.get(sensorType)
				: R.string.sensor_unknown;
		((TextView) view.findViewById(R.id.textView_module_detail_sensor_type))
				.setText(sensorTypeResId);
		// A timestamp of 0 means the module doesn't have a value yet, so
		// a 'no data' message will be displayed.
		if (valueTimestamp != 0) {
			((TextView) view.findViewById(R.id.textView_module_detail_value))
					.setText(value);
			((TextView) view
					.findViewById(R.id.textView_module_detail_value_timestamp))
					.setText(mDateFormat.format(new Date(valueTimestamp)));
		} else {
			((TextView) view
					.findViewById(R.id.textView_module_detail_value_timestamp))
					.setText(R.string.phrase_no_data);
		}

		// Don't reload trigger data, in case the user is editing it.
		if (mTriggerDataAdapter != null) {
			return;
		}

		mTriggerDataAdapter = sTriggerDataAdapterMap.get(sensorType);
		if (mTriggerDataAdapter == null) {
			// Unknown sensor; no view to create.
			return;
		}

		ViewGroup container = ((ViewGroup) view
				.findViewById(R.id.container_module_detail_trigger_data));
		View triggerDataView = mTriggerDataAdapter.newView(getActivity(),
				container);
		try {
			mTriggerDataAdapter.bindView(triggerDataView, new JSONArray(
					triggerData));
		} catch (JSONException e) {
			e.printStackTrace();
			// The trigger data is corrupt; the view will remain 'clean'.
		}

		container.addView(triggerDataView);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	private void onSave() {

		// Unsupported sensor type; do nothing.
		if (mTriggerDataAdapter == null) {
			return;
		}

		View view = ((ViewGroup) getView().findViewById(
				R.id.container_module_detail_trigger_data)).getChildAt(0);
		JSONArray triggerData = mTriggerDataAdapter.getTriggerData(view);
		if (triggerData == null) {
			// Something didn't validate; don't save.
			return;
		}

		try {
			JSONObject command = new JSONObject();
			command.put("SerialNo", mModuleSerialNo);
			command.put("TriggerData", triggerData.toString());
			new TriggerDataPostTask(getActivity()).execute(command);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	private class ModuleDeleteTask extends RemoteCallTask<Long> {

		private static final int RESULT_SUCCESS = 1;
		private static final int RESULT_FAILURE = 0;

		public ModuleDeleteTask(Context context) {
			super(context);
		}

		@Override
		protected Integer doInBackground(Long... params) {
			try {
				DataManager dataManager = getDataManager();
				Context context = getContext();
				dataManager.deleteModule(context, params[0]);
				dataManager.updateModulesAsync(context);
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
					? R.string.phrase_module_delete_success
					: R.string.error_io_failure;

			if (null != getActivity().getWindowManager()) { // TODO work this out
				Toast.makeText(getActivity(), getString(message),
						Toast.LENGTH_SHORT).show();
			}

			mCallbacks.onDelete(ModuleDetailFragment.this);
		}
	}

}
