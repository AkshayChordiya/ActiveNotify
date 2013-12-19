package com.aky.peek.notification;

import java.util.List;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class NotificationListenerB extends AccessibilityService {
	
	public static final String LOG_TAG = "NotificationService";
	
	static String mPackage = "";
	static String mName = "";
	static String mTitle = "";
	static int icon = 0;
	static Long post_time = 0L;
	int flag = 0;
	static PendingIntent mPendingIntent;
	
	private PowerManager mPM;
	
	protected float ProximityValue;
	boolean isBreathAcess = true;
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		Log.d(LOG_TAG, "Notification Accessibility Event");
	    if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED && (event.getParcelableData() instanceof Notification))
	    	notificationEvent(event);
	}
	
	private void notificationEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean invert_disable = pref.getBoolean("invert_disable_app", false);
		invert_disable = matchApp(event,invert_disable);
		if(invert_disable){
			Notification notify = (Notification)event.getParcelableData();
			if (!isScreenOn() && isValidNotification(notify) && getAccess()){
				mPackage = event.getPackageName().toString();
				showNotification(notify);
			}
		}
	}
	
	/**
	* Determine if screen is ON
	* @return True if a screen is ON
	*/
	private boolean isScreenOn() {
		// TODO Auto-generated method stub
		mPM = (PowerManager)getSystemService(POWER_SERVICE);
		return mPM.isScreenOn();
	}

	/**
	* Determine if a given notification should be used.
	* @param notify StatusBarNotification to check.
	* @return True if it should be used, false otherwise.
	*/
	private boolean isValidNotification(Notification notify) {
		return (!isOnCall() && (!(notify.flags == Notification.FLAG_ONGOING_EVENT)));
	}
	
	/**
	* Determine if a call is currently in progress.
	* @return True if a call is in progress.
	*/
	private boolean isOnCall() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getCallState() != TelephonyManager.CALL_STATE_IDLE;
	}

	private boolean matchApp(AccessibilityEvent event, boolean invert_disable) {
		// TODO Auto-generated method stub
		SharedPreferences prefer = getBaseContext().getSharedPreferences("packages", MODE_PRIVATE);
		if(invert_disable)
			return prefer.contains(event.getPackageName().toString());
		else
			return !prefer.contains(event.getPackageName().toString());
	}

	private void showNotification(Notification notify) {
		// TODO Auto-generated method stub
		if(notify != null){
			try{
				mTitle = notify.tickerText.toString();
			}catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			icon = notify.icon;
			mPendingIntent = notify.contentIntent;
			post_time = notify.when;
			obtainNotification();
		}
	}


	private void obtainNotification() {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Obtaining Notifications");
		try{
			mName = getAppName(mPackage);
			// Sometimes there is no description text to notifications
			if(mTitle.isEmpty())
				mTitle = "";
			mTitle = mName + "\n" + mTitle;
			Log.d(LOG_TAG, " Text = " + mTitle + "\nPackage = " + mPackage);
			
			Intent peek = new Intent(this , PeekMode.class);
			peek.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(peek);
		}
		catch (Exception e){
			Log.d(LOG_TAG, "Error getting notification extra title/text" + e);
		}	
	}
	
	private String getAppName(String packages) {
		// TODO Auto-generated method stub
		String mName = "";
		try{
			List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packageInfoList.size(); i++) {
				PackageInfo packageInfo = packageInfoList.get(i);
				if(packageInfo.packageName.equals(packages))
					mName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d(LOG_TAG, "Error getting app name " + e);
		}
		return mName;
	}

	protected String getRunningApp() {
		// TODO Auto-generated method stub
		final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		String service = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
		return service;
	}
	
	@SuppressLint("InlinedApi")
	@Override
	protected void onServiceConnected() {
		Log.d(LOG_TAG, "Started Service");
	    AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	    info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
	    info.notificationTimeout = 100L;
	    // Set the type of feedback your service will provide.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        } else {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        }
        sensorSwitch();
	    setServiceInfo(info);
	}
	
	

	@Override
	public void onInterrupt() {
		Log.d(LOG_TAG, "Interrupt Service");
	}
	
	private void sensorSwitch() {
		// TODO Auto-generated method stub
		// Getting Sensor Manager
		SensorManager mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String auto_detection = pref.getString("detection_method", "none");
		if(auto_detection.contentEquals("pocket")){
			// Setting Sensor from Sensor Manager to Proximity
			Sensor Proximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			// Checking device has Proximity sensor
			if(Proximity != null){			
				Log.d(LOG_TAG, "Auto Detection: Proximity");
				// Registering Proximity Value Change Listener
				mSensorManager.registerListener(ProximitySensorListener, Proximity, SensorManager.SENSOR_DELAY_NORMAL);
			}
			else{
				// If the device doesn't have Proximity sensor
				Toast.makeText(getBaseContext(), R.string.proximity_sensor_error, Toast.LENGTH_LONG).show();
				stopSelf();
			}
		}
		else if(auto_detection.contentEquals("face_up")){
			// Setting Sensor from Sensor Manager to Accelerometer
			Sensor Accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			// Checking device has Accelerometer sensor
			if(Accelerometer != null){			
				Log.d(LOG_TAG, "Auto Detection: Accelerometer");
				// Registering Accelerometer Value Change Listener
				mSensorManager.registerListener(AccelerometerSensorListener, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			}
			else{
				// If the device doesn't have Accelerometer sensor
				Toast.makeText(getBaseContext(), R.string.accelerometer_sensor_error, Toast.LENGTH_LONG).show();
				stopSelf();
			}
		}
		else if(auto_detection.contentEquals("both")){
			// Setting Sensor from Sensor Manager to Proximity
			Sensor Proximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			// Checking device has Proximity sensor
			if(Proximity != null){			
				Log.d(LOG_TAG, "Auto Detection: Proximity");
				// Registering Proximity Value Change Listener
				mSensorManager.registerListener(ProximitySensorListener, Proximity, SensorManager.SENSOR_DELAY_NORMAL);
			}
			else{
				// If the device doesn't have Proximity sensor
				Toast.makeText(getBaseContext(), R.string.proximity_sensor_error, Toast.LENGTH_LONG).show();
				stopSelf();
			}
			// Setting Sensor from Sensor Manager to Accelerometer
			Sensor Accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			// Checking device has Proximity sensor
			if(Accelerometer != null){			
				Log.d(LOG_TAG, "Auto Detection: Accelerometer");
				// Registering Accelerometer Value Change Listener
				mSensorManager.registerListener(FuseAccelerometerSensorListener, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			}
			else{
				// If the device doesn't have Accelerometer sensor
				Toast.makeText(getBaseContext(), R.string.accelerometer_sensor_error, Toast.LENGTH_LONG).show();
				stopSelf();
			}
		}
		else
			setAccess(true);
	}
	
	/******* Event Listener for Proximity Sensor ********/		
	private SensorEventListener ProximitySensorListener = new SensorEventListener(){
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// Getting value of sensor
			ProximityValue = event.values[0];
			if(ProximityValue != 0.0)
				setAccess(true);
			else
				setAccess(false);
		}		
	};
	
	/******* Event Listener for Accelerometer Sensor ********/		
	private SensorEventListener AccelerometerSensorListener = new SensorEventListener(){
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// Getting value of sensor
			float AXIZ_Z = event.values[2];
			// Face up
			if (AXIZ_Z >9 && AXIZ_Z < 10){
				Log.d(LOG_TAG, "Face Up");
				setAccess(true);
			}
			// Face Down
            else if (AXIZ_Z > -10 && AXIZ_Z < -9){
            	Log.d(LOG_TAG, "Face Down");
            	setAccess(false);
            }
            else
            	setAccess(false);
		}		
	};
	
	/******* Event Listener for Accelerometer Sensor ********/		
	private SensorEventListener FuseAccelerometerSensorListener = new SensorEventListener(){
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// Getting value of sensor
			float AXIZ_Z = event.values[2];
			// Face up
			if (AXIZ_Z >9 && AXIZ_Z < 10){
				if(ProximityValue != 0.0){
					Log.d(LOG_TAG, "Face Up");
					setAccess(true);
				}
				else
					setAccess(false);
			}
			// Face Down
            else if (AXIZ_Z > -10 && AXIZ_Z < -9){
            	Log.d(LOG_TAG, "Face Down");
            	setAccess(false);
            }
            else
				setAccess(false);
		}		
	};

	protected void setAccess(boolean isAccess) {
		// TODO Auto-generated method stub
		isBreathAcess = true;
	}
	
	protected boolean getAccess() {
		// TODO Auto-generated method stub
		return isBreathAcess;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Getting Sensor Manager
		SensorManager mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String auto_detection = pref.getString("detection_method", "none");
		if(auto_detection.contentEquals("pocket")){
			mSensorManager.unregisterListener(ProximitySensorListener);
			Log.d(LOG_TAG, "Unregistered Proximity Sensor");
		}
		else if(auto_detection.contentEquals("face_up")){
			mSensorManager.unregisterListener(AccelerometerSensorListener);
			Log.d(LOG_TAG, "Unregistered Accelerometer Sensor");
		}
		else if(auto_detection.contentEquals("both")){
			mSensorManager.unregisterListener(ProximitySensorListener);
			mSensorManager.unregisterListener(FuseAccelerometerSensorListener);
			Log.d(LOG_TAG, "Unregistered Accelerometer Sensor");
			Log.d(LOG_TAG, "Unregistered Proximity Sensor");
		}
	}
	
	

}
