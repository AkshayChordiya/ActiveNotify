package com.aky.peek.notification;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BreathingService extends Service{
	
	// String for collecting all the LOGS of that class
	private final static String LOG_TAG = "BreathingService";
	
	PowerManager mPM;
	
	int mBreathTimer;
	protected float ProximityValue;
	boolean isBreathAcess = true;
	
	private Handler mHandler;;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		getBreathDelay();
		mHandler = new Handler();
		mHandler.post(mBreath);
		sensorSwitch();
		return START_STICKY;
	}
	
	private void getBreathDelay() {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mBreathTimer = pref.getInt("breath_timeout", 15);
		mBreathTimer *= 1000;
	}

	private void setupBreath() {
		// TODO Auto-generated method stub
		if(!isScreenOn() && getAccess() && !isOnCall()){
			Intent peek = new Intent(getBaseContext() , PeekMode.class);
			peek.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(peek);
		}
	}
	
	Runnable mBreath = new Runnable() {
	    @Override 
	    public void run() {
	    	setupBreath();
	    	mHandler.postDelayed(mBreath, mBreathTimer);
	    }
	};
	
	/**
	* Determine i a call is currently in progress.
	* @return True if a call is in progress.
	*/
	private boolean isOnCall() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getCallState() != TelephonyManager.CALL_STATE_IDLE;
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

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

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
		destroyTimer();
	}

	private void destroyTimer() {
		// TODO Auto-generated method stub
		if(mHandler != null){
			mHandler.removeCallbacks(mBreath);
			mHandler = null;
		}
	}


}
