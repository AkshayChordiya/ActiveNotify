package com.aky.peek.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aky.peek.notification.Adapters.PackageAdapter;
import com.aky.peek.notification.Adapters.PackageItem;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

/** @author Akshay Chordiya
 * @param <AppCollector>
 * @category Activity
 * {@code} To get all installed apps & storing them */
public class AppSelector extends Activity implements OnItemClickListener{
	
	private static final String LOG_TAG = "AppSelector";

	private PackageAdapter adapter;
    private static List<PackageItem> data;
	
    ProgressBar mProgressBar;
	
	int app_count = 0;
	
    EditText inputSearch;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_list_selector);
		setupActionBar();
		SetId();
	}

	private void SetId() {
		// TODO Auto-generated method stub
		ListView mAppsList = (ListView) findViewById(R.id.appslist);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		mProgressBar = (ProgressBar)findViewById(R.id.loader);
		data = new ArrayList<PackageItem>();
		adapter = new PackageAdapter(this , data);
	    mAppsList.setOnItemClickListener(this);
	    mAppsList.setAdapter(adapter);
	    new AppLoaderTask().execute();
        inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				AppSelector.this.adapter.getFilter().filter(s.toString());
				adapter.notifyDataSetChanged();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				adapter.notifyDataSetChanged();
			}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_selector, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
            case R.id.exit:
            	finish();
                return true;    
            case R.id.clear_packs:
            	// Clearing Preferences
				ClearDatabase();
    			// Telling user about changes made
            	Toast.makeText(getBaseContext(), "All apps removed from ignore list", Toast.LENGTH_LONG).show();
            	ManualRestart();
            	break;

        }
        return super.onOptionsItemSelected(item);
        }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		final PackageItem app = (PackageItem) parent.getItemAtPosition(position);
		/** Get Position gives -1 , Get Column gives 2 as only 2 columns present in database
		 * Get Count gives number of ROWS i.e number of elements present in the list
		 * Get Column name returns the NAME of COLOUM at that position in database
		 * Get Column Index returns the position of COLOUM by entering name
		 * Get String is some what useless*/
		SharedPreferences pref = getSharedPreferences("packages", MODE_PRIVATE);
		/** For removing app from list **/
		if(pref.contains(app.getPackageName())){
			app.setCheck(false);
			RemoveApp(app.getPackageName());
			// Telling user
			Toast.makeText(getBaseContext(), "Removed " + app.getTitle() + " from list", Toast.LENGTH_SHORT).show();
			adapter.notifyDataSetChanged();
		}
		/** For adding app to list */
		else{
			app.setCheck(true);
			AddData(app.getPackageName());
			Toast.makeText(getBaseContext(), "Added " + app.getTitle() + " to list", Toast.LENGTH_SHORT).show();
			adapter.notifyDataSetChanged();
		}
	}

	private void AddData(String packageName) {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("packages", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(packageName, "");
		editor.commit();
	}
	
	private void RemoveApp(String packageName) {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("packages", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(packageName);
		editor.commit();
	}

	private void ClearDatabase() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("packages", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	@SuppressLint("NewApi")
	private void ManualRestart() {
		// TODO Auto-generated method stub
		Log.d("AppSelector", "Manually Restarting app");
		if (android.os.Build.VERSION.SDK_INT >= 11)
			// For Android 3.0 above calling recreate function
            recreate();
        else{
        	// For below 3.0 restarting activity using basic functions
        	Intent restart = getIntent();
        	finish();
        	startActivity(restart);
        }				
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public class AppLoaderTask extends AsyncTask<Void, Void, List<PackageItem>> {

		protected List<PackageItem> doInBackground(Void... args) {
            PackageManager appInfo = getPackageManager();
            SharedPreferences pref = getSharedPreferences("packages", MODE_PRIVATE);
            List<ApplicationInfo> listInfo = appInfo.getInstalledApplications(0);
            Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(appInfo));
            List<PackageItem> data = new ArrayList<PackageItem>();
            for (int index = 0; index < listInfo.size(); index++) {
            	ApplicationInfo content = listInfo.get(index);
            	PackageItem app_item = new PackageItem();
            	app_item.setTitle(content.loadLabel(appInfo).toString());
            	app_item.setPackageName(content.packageName);
            	app_item.setIcon(content.loadIcon(appInfo));
            	if(pref.contains(app_item.getPackageName()))
            		app_item.setCheck(true);
            	else
            		app_item.setCheck(false);
            	data.add(app_item);
            	app_count++;
            }
            return data;
        }

        protected void onPostExecute(List<PackageItem> result) {
        	data.clear();
            data.addAll(result);
            adapter.notifyDataSetChanged();
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
                Log.d(LOG_TAG, "Stopped Progress Bar");
                setSubtitle();
                mProgressBar = null;
            }
        }
    }

	@SuppressLint("NewApi")
	public void setSubtitle() {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setSubtitle("Total " + app_count +  " apps");
			Log.d(LOG_TAG, "Action Bar subtitle set");
		}
	}
	
}
