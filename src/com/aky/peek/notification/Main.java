package com.aky.peek.notification;

import org.jraf.android.backport.switchwidget.Switch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.aky.peek.notification.SettingsActivity.Gestures;
import com.aky.peek.notification.Adapters.DrawerAdapter;
import com.aky.peek.notification.DialogFragment.NoticeDialog;
import com.aky.peek.notification.DialogFragment.ResetDialog;

public class Main extends Activity{
	
	public static final String LOG_TAG = "Main";
	
	ImageView mAppView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// If the device is Tablet
		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
		if (!tabletSize)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setView();
		initMenu();
		firstrun();
	}
	
	private void setView() {
		// TODO Auto-generated method stub
		String service = NotificationListenerB.class.getName();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			service = NotificationListener.class.getName();
		
		Switch switch_peek = (Switch)findViewById(R.id.active_notify);
		//mAppView = (ImageView)findViewById(R.id.imageView1);
		//Drawable mDrawable = getResources().getDrawable(R.drawable.ic_launcher);
		//Bitmap bitmap;
		//bitmap = makeCircular(bitmap);
		
		switch_peek.setChecked(isServiceRunning(service));
		switch_peek.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Intent service;
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					service = new Intent(getBaseContext(),NotificationListener.class);
				else
					service = new Intent(getBaseContext(),NotificationListenerB.class);
				
				if(isChecked)
					startService(service);
				else
					stopService(service);
			}
		});
		
		Switch switch_sensor = (Switch)findViewById(R.id.breath_notify);
		switch_sensor.setChecked(isServiceRunning(BreathingService.class.getName()));
		switch_sensor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Intent detection = new Intent(getBaseContext(),BreathingService.class);
				if(isChecked)
					startService(detection);
				else
					stopService(detection);
			}
		});
	}
	
	@SuppressLint("NewApi")
	private void initMenu() {
		// TODO Auto-generated method stub
		//mPlanetTitles = getResources().getStringArray(R.array.auto_battery_entries);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        DrawerAdapter mAdapter = new DrawerAdapter(this);
        mAdapter = AddItem(mAdapter);
        // Set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.activenotify_ic_navigation_drawer, R.string.extra_toggle_text, R.string.app_name) {            
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // For slider icon
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}

	
	private DrawerAdapter AddItem(DrawerAdapter mAdapter) {
		// TODO Auto-generated method stub
		
		// Adding 1st Item
        mAdapter.addItem(R.string.action_settings,R.drawable.pref);
        
        // Adding Header
        mAdapter.addHeader(R.string.category_shortcut);
        mAdapter.addItem(R.string.check_peek,android.R.drawable.ic_menu_view);
        mAdapter.addItem(R.string.gestures_title,R.drawable.hand_gesture);
        mAdapter.addItem(R.string.disable_app_title,R.drawable.time_delay);
        
        // Adding Header
		mAdapter.addHeader(R.string.category_handy);
		
        mAdapter.addItem(R.string.reset_setting_title,android.R.drawable.ic_menu_revert);
        
        // Adding Header
        mAdapter.addHeader(R.string.category_misc);
        mAdapter.addItem(R.string.share_app,R.drawable.ic_share);
        mAdapter.addItem(R.string.remove_app, android.R.drawable.ic_menu_delete);
        
        return mAdapter;
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			 Log.d(LOG_TAG, "Menu Key Pressed");
	            if(mDrawerLayout.isDrawerOpen(mDrawerList))
	            	mDrawerLayout.closeDrawers();
	            else
	            	mDrawerLayout.openDrawer(mDrawerList);
			break;
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		}
        return false;
    }
	
	private class DrawerItemClickListener implements OnItemClickListener {
		
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }

	    /** Swaps fragments in the main content view */
	    @SuppressLint("InlinedApi")
		private void selectItem(int position) {
	    	
	    	switch(position){
	    	case 0:
	    		startActivity(new Intent(getBaseContext(),SettingsActivity.class));
	    		break;
	    		// Case 1 is Header
	    	case 2:
	    		Intent peek = new Intent(getBaseContext(),PeekMode.class);
	    		peek.putExtra("demo_mode", true);
	    	    startActivity(peek);
	    		break;
	    	case 3:
	    		Intent gesture = new Intent(getBaseContext(), SettingsActivity.class);
	    		gesture.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Gestures.class.getName());
	    		gesture.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, false);
	    		startActivity(gesture);
	    		break;
	    	case 4:
	    	    startActivity(new Intent(getBaseContext(),AppSelector.class));
	    		break;
	    	case 6:
	    		ResetDialog.newInstance(0).show(getFragmentManager(), LOG_TAG);
	    		break;
	    	case 8: // Share
	    		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    	    sharingIntent.setType("text/plain");
	    	    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
	    	    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body) + "\n" + 
	    	    		"https://play.google.com/store/apps/details?id=" + getPackageName());
	    	    startActivity(Intent.createChooser(sharingIntent, "Share via"));
	    		break;
	    	case 9:
	    		// Getting package name of app
				Uri packageURI = Uri.parse("package:" + getPackageName());
				Intent uninstallIntent;
				// Setting intent to uninstall app
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				else
                	uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageURI);
				// Get Device Administrator Manager
		        DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		        ComponentName DeviceAdmin = new ComponentName(Main.this, DeviceAdmin.class);
				// Removing Device Admin
		        mDPM.removeActiveAdmin(DeviceAdmin);
				startActivity(uninstallIntent);	
				Log.d(LOG_TAG, "Uninstalled Successfully");
	    		break;
	    	}

	        // Highlight the selected item, update the title, and close the drawer
	        mDrawerList.setItemChecked(position, true);
	        mDrawerLayout.closeDrawer(mDrawerList);
	    }
	}
	
	private void firstrun() {
		// TODO Auto-generated method stub
		boolean isCompatible = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			isCompatible = true;
		if(!isCompatible){
			if(!isAccessibilityEnabled())
				NoticeDialog.newInstance(0).show(getFragmentManager(), LOG_TAG);
		}
		else{
			if(!isServiceRunning(NotificationListener.class.getName()))
				NoticeDialog.newInstance(0).show(getFragmentManager(), LOG_TAG);
		}
	}
	
	public boolean isAccessibilityEnabled(){
	    int accessibilityEnabled = 0;
	    final String ACCESSIBILITY_SERVICE = "com.aky.peek.notification/com.aky.peek.notification.NotificationService";
	    boolean accessibilityFound = false;
	    try {
	        accessibilityEnabled = Settings.Secure.getInt(getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
	        Log.d(LOG_TAG, "Accessibility: " + accessibilityEnabled);
	    } catch (SettingNotFoundException e) {
	        Log.d(LOG_TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
	    }

	    TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

	    if (accessibilityEnabled==1){
	    	Log.d(LOG_TAG, "Accessibility is enabled");


	         String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
	         Log.d(LOG_TAG, "Setting: " + settingValue);
	         if (settingValue != null) {
	             TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
	             splitter.setString(settingValue);
	             while (splitter.hasNext()) {
	                 String accessabilityService = splitter.next();
	                 Log.d(LOG_TAG, "Setting: " + accessabilityService);
	                 if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)){
	                     Log.d(LOG_TAG, "Accessibility is switched on!");
	                     return true;
	                 }
	             }
	         }
	    }
	    else{
	        Log.d(LOG_TAG, "Accessibility is disabled");
	    }
	    return accessibilityFound;
	}
	
	
	public boolean isServiceRunning(String service){
		 ActivityManager manager = (ActivityManager)getSystemService("activity");
		 for (RunningServiceInfo running_service : manager.getRunningServices(Integer.MAX_VALUE)) {
			 if(service.equals(running_service.service.getClassName()))
				 return true;
		 }
		 return false;
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prefs, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
       if (mDrawerToggle.onOptionsItemSelected(item)) {
           return true;
       }
       switch (item.getItemId()){
       case R.id.action_exit:
    	   Log.d(LOG_TAG, "Closing Main Activity");
    	   finish();
    	   return true;
    	}
       return super.onOptionsItemSelected(item);
	}
	

}
