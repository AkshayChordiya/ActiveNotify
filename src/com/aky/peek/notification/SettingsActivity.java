package com.aky.peek.notification;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aky.peek.notification.DialogFragment.ColorPick;
import com.aky.peek.notification.DialogFragment.PlannedDialog;
import com.aky.peek.notification.DialogFragment.ResetDialog;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity{

	private static final String LOG_TAG = "SettingsActivity";
	protected static final int SELECT_PICTURE = 1912;
	
	// Unique Keys of Preference
	public static final String CHANGELOG_KEY = "changelog";
	public static final String PLANNEDLOG_KEY = "plannedlog";
	public static final String EMAIL_KEY = "email_support";
	public static final String RESET_SETTINGS = "reset_setting";	
	public static final String FAQ = "faq_dialog";
		
	public static final int CHANGELOG = 0;
	public static final int PLANNEDLOG = 1;
	public static final int EMAIL_SUPPORT = 2;
	public static final int RESET_DIALOG = 7;
	public static final int FAQ_DIALOG = 11;
	
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
	}
	
	@Override
	protected boolean isValidFragment(String fragmentName) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
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

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		
		if (!isSimplePreferences(this)) {
			return;
		}
		
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB;
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB && isXLargeTablet(this)){
				Log.d(LOG_TAG, "Modern Two-Pane Preferences Layout Set");
				loadHeadersFromResource(R.xml.preference_headers, target);
			}
			else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
				Log.d(LOG_TAG, "Modern Preferences Layout Set");
				loadHeadersFromResource(R.xml.preference_headers, target);
			}
		}
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == Activity.RESULT_OK){
			if (requestCode == SELECT_PICTURE){
	             Uri selectedImageUri = data.getData();
	             //Intent cropper = new Intent(getBaseContext(),ImageCrop.class);
	             //cropper.putExtra("image_crop", selectedImageUri.toString());
	             //startActivity(cropper);
	             SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
	             mEditor.putString("pic_loc", selectedImageUri.toString());
	             mEditor.apply();
	         }
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prefs_advance, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
       switch (item.getItemId()){
       case android.R.id.home:
       case R.id.action_exit:
    	   Log.d(LOG_TAG, "Closing Main Activity");
    	   finish();
    	   return true;
       case R.id.action_preview:
    	   Intent peek = new Intent(getBaseContext(),PeekMode.class);
    	   peek.putExtra("demo_mode", true);
    	   startActivity(peek);
    	   return true;
    	}
       return super.onOptionsItemSelected(item);
	}
	
	 @SuppressLint("NewApi")
	    public static class Basic extends PreferenceFragment {
	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	            super.onCreate(savedInstanceState);
	            

	            // Make sure default values are applied.  In a real app, you would
	            // want this in a shared function that is used to retrieve the
	            // SharedPreferences wherever they are needed.
	            PreferenceManager.setDefaultValues(getActivity(),
	                    R.xml.pref_basic, false);
	            
	            // Load the preferences from an XML resource
	            addPreferencesFromResource(R.xml.pref_basic);
	            
	            final ListPreference profile_detection = (ListPreference)findPreference("profile_detection");
	    		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    		profile_detection.setDefaultValue(pref.getString("profile_detection", "none"));
	    		profile_detection.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	    			
	    			@Override
	    			public boolean onPreferenceChange(Preference preference, Object newValue) {
	    				// TODO Auto-generated method stub
	    				Log.d(LOG_TAG, "Profile Detection");
	    				
	    				SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
	    				mEditor.putString("profile_detection", newValue.toString());
	    				mEditor.apply();
	    				profile_detection.setDefaultValue(newValue.toString());
	    				
	    				if(newValue.toString().contentEquals("google")){
	    					startActivity(new Intent(getActivity(),SignInActivity.class));
	    				}
	    				else if(newValue.toString().contentEquals("gallery")){
	    					Intent intent = new Intent();
	    					intent.setType("image/*");
	    					intent.setAction(Intent.ACTION_GET_CONTENT);
	    					startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
	    				}
	    				return false;
	    			}
	    		});
	    		
	    		// If the device is Tablet
	    		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
	    		if (!tabletSize)
	    			profile_detection.setEnabled(false);
	        }
	 }
	 
	 @SuppressLint("NewApi")
	    public static class Cosmetic extends PreferenceFragment {
	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	            super.onCreate(savedInstanceState);
	            
	            // Make sure default values are applied.  In a real app, you would
	        	// want this in a shared function that is used to retrieve the
	            // SharedPreferences wherever they are needed.
	            PreferenceManager.setDefaultValues(getActivity(),
	                    R.xml.pref_cosmetic, false);
	            
	            // Load the preferences from an XML resource
	            addPreferencesFromResource(R.xml.pref_cosmetic);
	            
	            
	            final ListPreference back_stuff = (ListPreference)findPreference("back_stuff");
	            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	            back_stuff.setDefaultValue(pref.getString("back_stuff", "default_back"));
	            
	            back_stuff.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	    			
	    			@Override
	    			public boolean onPreferenceChange(Preference preference, Object newValue) {
	    				// TODO Auto-generated method stub
	    				Log.d(LOG_TAG, "Background Stuff Dialog");
	    				
	    				SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
	    				mEditor.putString("back_stuff", newValue.toString());
	    				mEditor.apply();
	    				back_stuff.setDefaultValue(newValue.toString());
	    				
	    				if(newValue.toString().contentEquals("custom_color"))
	    					ColorPick.newInstance("back_color").show(getFragmentManager(), LOG_TAG);
	    				else if(newValue.toString().contentEquals("custom_image")){
	    					Intent intent = new Intent();
	    					intent.setType("image/*");
	    					intent.setAction(Intent.ACTION_GET_CONTENT);
	    					startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
	    				}
	    				
	    				return false;
	    			}
	    		});
	        }
	        
	        @Override
	        public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
	        	super.onPreferenceTreeClick(prefScreen, pref);
	        	if(pref.getKey().equals("clock_color"))
	        		ColorPick.newInstance(pref.getKey()).show(getFragmentManager(), LOG_TAG);
	        	return false;
	        }
	    }
	 
	 @SuppressLint("NewApi")
	    public static class Advance extends PreferenceFragment {
	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	        	super.onCreate(savedInstanceState);
	            

	            // Make sure default values are applied.  In a real app, you would
	        	// want this in a shared function that is used to retrieve the
	            // SharedPreferences wherever they are needed.
	            PreferenceManager.setDefaultValues(getActivity(),
	                    R.xml.pref_advance, false);
	            
	            // Load the preferences from an XML resource
	            addPreferencesFromResource(R.xml.pref_advance);
	            
	            final Preference disable_app = (Preference)findPreference("disable_app");
	    		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    		boolean invert_app = pref.getBoolean("invert_disable_app", false);
	    		if(invert_app)
	    			disable_app.setSummary(R.string.disable_app_summary_off);
	        }
	 }
	 
	 @SuppressLint("NewApi")
	    public static class Gestures extends PreferenceFragment {
		 @Override
		 public void onCreate(Bundle savedInstanceState) {
			 super.onCreate(savedInstanceState);
			 
			// Make sure default values are applied.  In a real app, you would
	        	// want this in a shared function that is used to retrieve the
	            // SharedPreferences wherever they are needed.
	            PreferenceManager.setDefaultValues(getActivity(),
	                    R.xml.pref_gesture, false);
			 
			 // Load the preferences from an XML resource
			 addPreferencesFromResource(R.xml.pref_gesture);
		 }
	 }
	
	@SuppressLint("NewApi")
    public static class About extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_about);
            
        }
        
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        	super.onPreferenceTreeClick(prefScreen, pref);
        	if(pref.getKey().equals("reset_setting"))
        		ResetDialog.newInstance(0).show(getFragmentManager(), LOG_TAG);
        	else if(pref.getKey().equals("plannedlog"))
        		PlannedDialog.newInstance(0).show(getFragmentManager(), LOG_TAG);
        	else if(pref.getKey().equals("email_support")){
        		PackageInfo pInfo = null;
    			try {
    				pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
    				/* Create the Email Intent */
    				Intent emailIntent = new Intent(Intent.ACTION_SEND);
    				/* Fill it with Data */
    				emailIntent.setType("plain/text");
    				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"akshaychordiya2@gmail.com"});
    				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Regarding " + getString(R.string.app_name)
    						+ " " + pInfo.versionName);
    				//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

    				/* Send it off to the Activity-Chooser */
    				startActivity(Intent.createChooser(emailIntent, "Send mail"));
    			} catch (NameNotFoundException e) {
    				e.printStackTrace();
    			}
        	}
        	else if(pref.getKey().equals("changelog")){
        		DialogCreator cl = new DialogCreator(getActivity());
    			cl.show();
        	}
        	return false;
        }
    }
    
    @SuppressLint("NewApi")
    public static class Credits extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_credits);
        }
    }

}
