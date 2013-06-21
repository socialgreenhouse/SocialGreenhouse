package com.socialgreenhouse.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.socialgreenhouse.android.network.SyncService;

/**
 * An activity representing a list of Modules. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ModuleDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ModuleListFragment} and the item details (if present) is a
 * {@link ModuleDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ModuleListFragment.Callbacks} interface to listen for item selections.
 */
public class ModuleListActivity extends FragmentActivity
		implements
			ModuleListFragment.Callbacks,
			ModuleDetailFragment.Callbacks {
	
	public static final int SYNC_INTERVAL = 60 * 1000;
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!ServerSetupActivity.hasSetupServer(this)) {
			startActivity(new Intent(this, ServerSetupActivity.class));
			finish();
			return;
		}

		// Start auto-sync
		((AlarmManager) getSystemService(ALARM_SERVICE)).setRepeating(
				AlarmManager.RTC, System.currentTimeMillis(), SYNC_INTERVAL,
				PendingIntent.getService(this, 0, new Intent(this,
						SyncService.class), 0));

		setContentView(R.layout.activity_module_list);

		if (findViewById(R.id.module_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ModuleListFragment) getSupportFragmentManager().findFragmentById(
					R.id.module_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link ModuleListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(long id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putLong(ModuleDetailFragment.ARG_MODULE_SERIAL_NO, id);
			ModuleDetailFragment fragment = new ModuleDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.module_detail_container, fragment).commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ModuleDetailActivity.class);
			detailIntent.putExtra(ModuleDetailFragment.ARG_MODULE_SERIAL_NO, id);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_server_setup:
				startActivity(new Intent(this, ServerSetupActivity.class));
				return true;
			case R.id.action_twitter_setup:
				startActivity(new Intent(this, TwitterSetupActivity.class));
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.module_list, menu);
		return true;
	}

	/**
	 * Called when a module is deleted while it's being displayed.
	 */
	@Override
	public void onDelete(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.popBackStack();		
		fragmentManager.beginTransaction().remove(fragment).commit();
	}
}
