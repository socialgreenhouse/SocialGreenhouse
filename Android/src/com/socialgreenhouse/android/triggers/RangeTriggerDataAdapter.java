package com.socialgreenhouse.android.triggers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.socialgreenhouse.android.R;
import com.socialgreenhouse.android.database.ModuleContract.TriggerData;

/**
 * Trigger data adapter that presents a 'minimum' and a 'maximum' form trigger.
 */
public class RangeTriggerDataAdapter implements TriggerDataAdapter {

	@Override
	public View newView(Context context, ViewGroup parent) {
		ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.trigger_data_range, parent, false);

		for (int i = 0; i < 2; i++) {
			View container = view.getChildAt(i);
			((CheckBox) container
					.findViewById(R.id.checkBox_trigger_data_threshold))
					.setText(i == 0
							? R.string.phrase_minimum
							: R.string.phrase_maximum);
		}

		return view;
	}

	@Override
	public void bindView(View view, JSONArray triggerData) {

		try {
			for (int i = 0; i < triggerData.length(); i++) {
				View container = ((ViewGroup) view).getChildAt(i);
				JSONObject trigger = triggerData.getJSONObject(i);

				// Checkbox; check if needed, set appropriate label
				boolean enabled = trigger.getBoolean(TriggerData.KEY_ENABLED);
				CheckBox checkBox = (CheckBox) container
						.findViewById(R.id.checkBox_trigger_data_threshold);
				checkBox.setChecked(enabled);

				// Tweet
				String tweet = trigger.getJSONObject(TriggerData.KEY_ACTION)
						.getJSONArray(TriggerData.KEY_ACTION_ARGUMENTS)
						.getString(0);
				((EditText) container
						.findViewById(R.id.editText_trigger_data_threshold_tweet))
						.setText(tweet);

				// Threshold; display if present
				JSONArray arguments = trigger
						.getJSONArray(TriggerData.KEY_CONDITIONS)
						.getJSONObject(0)
						.getJSONArray(TriggerData.KEY_CONDITION_ARGUMENTS);
				if (!arguments.isNull(0)) {
					((EditText) container
							.findViewById(R.id.editText_trigger_data_threshold))
							.setText("" + arguments.getDouble(0));
				}
			}
		} catch (JSONException e) {
			// Nothing to do here. Leave the view as is.
		}
	}
	@Override
	public JSONArray getTriggerData(View view) {

		JSONArray triggerData = new JSONArray();

		try {
			for (int i = 0; i < 2; i++) {
				View container = ((ViewGroup) view).getChildAt(i);

				String tweet = ((EditText) container
						.findViewById(R.id.editText_trigger_data_threshold_tweet))
						.getText().toString();
				JSONObject action = new JSONObject();
				action.put(TriggerData.KEY_ACTION_NAME, "Tweet");
				JSONArray actionArgs = new JSONArray();
				actionArgs.put(tweet);
				action.put(TriggerData.KEY_ACTION_ARGUMENTS, actionArgs);

				JSONObject condition = new JSONObject();
				condition.put(TriggerData.KEY_CONDITION_NAME, i == 0
						? "LessThan"
						: "GreaterThan");

				boolean enabled = ((CheckBox) container
						.findViewById(R.id.checkBox_trigger_data_threshold))
						.isChecked();

				EditText thresholdView = ((EditText) container
						.findViewById(R.id.editText_trigger_data_threshold));
				String threshold = thresholdView.getText().toString();

				JSONArray conditionArgs = new JSONArray();

				if (threshold.length() == 0) {
					if (enabled) {
						thresholdView.setError(view.getContext().getString(
								R.string.error_threshold_missing));
						thresholdView.requestFocus();
						return null;
					}
				} else {
					conditionArgs.put(Double.parseDouble(threshold));
				}

				condition.put(TriggerData.KEY_CONDITION_ARGUMENTS,
						conditionArgs);

				JSONArray conditions = new JSONArray();
				conditions.put(condition);

				JSONObject trigger = new JSONObject();
				trigger.put(TriggerData.KEY_ENABLED, enabled);
				trigger.put(TriggerData.KEY_COOLDOWN, 60);
				trigger.put(TriggerData.KEY_ACTION, action);
				trigger.put(TriggerData.KEY_CONDITIONS, conditions);

				triggerData.put(trigger);
			}
		} catch (JSONException e) {
			// This shouldn't happen.
			throw new RuntimeException(e);
		}

		return triggerData;

	}

}
