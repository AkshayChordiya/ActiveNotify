
package com.aky.peek.notification;

import java.util.List;


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
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class NotificationListener extends NotificationListenerService{
	
	public static final String LOG_TAG = "NotificationListener";
	
	static String mPackage = "";
	static String mName = "";
	static String mTitle = "";
	static int icon = 0;
	static Long post_time = 0L;
	static PendingIntent mPendingIntent;
	//public static int mUserId;
	
	//public static List<NotificationAdapter> data;
	//NotificationAdapter mNotificationAdapter;
	
	// messages sent to the handler for processing
    //private static final int MSG_SHOW_NOTIFICATION_VIEW = 1000;
    //private static final int MSG_HIDE_NOTIFICATION_VIEW = 1001;
    //private static final int MSG_SHOW_NOTIFICATION = 1002;
    //private static final int MSG_DISMISS_NOTIFICATION = 1003;
    
    private static final int HIDE_NOTIFICATIONS_BELOW_SCORE = Notification.PRIORITY_LOW;
    
    private boolean mHideLowPriorityNotifications = false;
    
    private PowerManager mPM;
    
    protected float ProximityValue;
	boolean isBreathAcess = true;
	
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mHideLowPriorityNotifications = pref.getBoolean("hide_low_priority", false);
		boolean invert_disable = pref.getBoolean("invert_disable_app", false);
		invert_disable = matchApp(sbn,invert_disable);
		if(invert_disable){
			Log.d(LOG_TAG, "Notification Posted");
			// need to make sure either the screen is off or the user is currently
            // viewing the notifications
            if (!isScreenOn() && isValidNotification(sbn) && getAccess())
				showNotification(sbn);
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
	* @param sbn StatusBarNotification to check.
	* @return True if it should be used, false otherwise.
	*/
	private boolean isValidNotification(StatusBarNotification sbn) {
		return (!isOnCall() && (!sbn.isOngoing())
				&& !(mHideLowPriorityNotifications && sbn.getNotification().priority < HIDE_NOTIFICATIONS_BELOW_SCORE));
	}
	
	/**
	* Determine if a call is currently in progress.
	* @return True if a call is in progress.
	*/
	private boolean isOnCall() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getCallState() != TelephonyManager.CALL_STATE_IDLE;
	}
	
	
	private boolean matchApp(StatusBarNotification sbn, boolean invert_disable) {
		// TODO Auto-generated method stub
		SharedPreferences prefer = getBaseContext().getSharedPreferences("packages", MODE_PRIVATE);
		if(invert_disable)
			return prefer.contains(sbn.getPackageName().toString());
		else
			return !prefer.contains(sbn.getPackageName().toString());
	}
	
	private void showNotification(StatusBarNotification sbn){
		// TODO Auto-generated method stub
		if(sbn != null){
			mTitle = (String) sbn.getNotification().tickerText;
			mPackage = sbn.getPackageName();
			mPendingIntent = sbn.getNotification().contentIntent;
			
			post_time = sbn.getNotification().when;
			if(post_time == 0L)
				post_time = sbn.getPostTime();
			
			obtainNotification(sbn);
			//startNotification();
		}
	}
	
	/*
	private void startNotification() {
		// TODO Auto-generated method stub
		
		Log.d(LOG_TAG, "Starting Notification");
		
		mNotificationAdapter = new NotificationAdapter();
		mNotificationAdapter.setTitle(mTitle);
		mNotificationAdapter.setIcon(icon);
		mNotificationAdapter.setTime(post_time);
		mNotificationAdapter.setPackage(mPackage);
		mNotificationAdapter.setIntent(mPendingIntent);
		
		try{
			data.add(0, mNotificationAdapter);
		}catch (UnsupportedOperationException  e) {
			Log.d(LOG_TAG, "Error Adding Notifications Data" + e);
		}
		catch (ClassCastException e) {
			Log.d(LOG_TAG, "Error Adding Notifications Data" + e);
		}
		catch (IllegalArgumentException e) {
			Log.d(LOG_TAG, "Error Adding Notifications Data" + e);
		}
		catch (IndexOutOfBoundsException e) {
			Log.d(LOG_TAG, "Error Adding Notifications Data" + e);
		}
		
		Intent peek = new Intent(this , PeekMode.class);
		peek.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(peek);
		
		Log.d(LOG_TAG, "Done");
	} */

	private void obtainNotification(StatusBarNotification sbn) {
		// TODO Auto-generated method stub
		try{
			Log.d(LOG_TAG, "Obtaining Notifications Data");
			mName = getAppName(mPackage);
			//RemoteViews r = sbn.getNotification().contentView;
			// Sometimes there is no description text to notifications
			if(mTitle.isEmpty())
				mTitle = "";
			mTitle = mName + "\n" + mTitle;
			Log.d(LOG_TAG, "Text = " + mTitle + "\nPackage = " + mPackage);
			
			Intent peek = new Intent(this , PeekMode.class);
			peek.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(peek);
		}
		catch (Exception e){
			Log.d(LOG_TAG, "Error getting notification extra title/text" + e);
		}
		/*
		Notification notification = (Notification) sbn.getNotification();
		RemoteViews views = notification.contentView;
		Class secretClass = views.getClass();

		try {
		    Map<String,Integer> text = new HashMap<String,Integer>();

		    Field outerFields[] = secretClass.getDeclaredFields();
		    for (int i = 0; i < outerFields.length; i++) {
		        if (!outerFields[i].getName().equals("mActions")) continue;

		        outerFields[i].setAccessible(true);

		        @SuppressWarnings("unchecked")
				ArrayList<Object> actions = (ArrayList<Object>) outerFields[i].get(views);
		        for (Object action : actions) {
		            Field innerFields[] = action.getClass().getSuperclass().getDeclaredFields();

		            Object value = null;
		            Integer type = null;
		            Integer viewId = null;
		            for (Field field : innerFields) {
		                field.setAccessible(true);
		                if (field.getName().equals("value")) {
		                    value = field.get(action);
		                } else if (field.getName().equals("type")) {
		                    type = field.getInt(action);
		                } else if (field.getName().equals("viewId")) {
		                    viewId = field.getInt(action);
		                }
		            }

		            text.put(value.toString(), viewId);
		        }

		        System.out.println("title is: " + text.get(16908310));
		        System.out.println("info is: " + text.get(16909082));
		        System.out.println("text is: " + text.get(16908358));
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		*/
	}


	private String getAppName(String packages) {
		// TODO Auto-generated method stub
		String mName = "";
		try{
			List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packageInfoList.size(); i++) {
				PackageInfo packageInfo = packageInfoList.get(i);
				if(packageInfo.packageName.equals(packages)){
					icon = packageInfo.applicationInfo.icon;
					mName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.d(LOG_TAG, "Error getting app name " + e);
		}
		return mName;
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		// TODO Auto-generated method stub
		icon = 0;
		post_time = 0L;
		mTitle = "";
		mName = "";
		mPackage = "";
		mPendingIntent = null;
	}
	
	protected String getRunningApp() {
		// TODO Auto-generated method stub
		final ActivityManager activityManager  =  (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		String service  =  activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
		return service;
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
		isBreathAcess = isAccess;
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

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sensorSwitch();
	}
	
	
	

}
