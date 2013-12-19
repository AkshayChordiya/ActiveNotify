package com.aky.peek.notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.aky.peek.notification.R;
import com.aky.peek.notification.Cache.ImageLoader;
import com.aky.peek.notification.GlowPad.GlowPadView;
import com.aky.peek.notification.GlowPad.GlowPadView.OnTriggerListener;

public class PeekMode extends Activity implements OnTriggerListener{
	
	public static final String LOG_TAG = "PeekMode";

	private static final int SWIPE_LEFT = 2;

	// Booleans
	private boolean isCompatible = false;
	boolean tabletSize = false;
	public static boolean isRunning = false;
	boolean isGrabbed = false;
	public boolean isActive;
	
	// UI elements
	GlowPadView mGlowPadView;
	TextView mTime;
	TextView message;
	TextView mCarrier;
	ImageView mProfile;
	
	BroadcastReceiver timeReceiver;
	private DevicePolicyManager mDPM;
	
	Handler mTimer;
	Runnable mLocker = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			turnScreenOff();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTheme();
		setFlags();
		setBrightness();
		setContentView(R.layout.activity_peek_mode);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mGlowPadView = (GlowPadView)findViewById(R.id.glow_pad_view);
		glowPadStuff(pref);
		mGlowPadView.setOnTriggerListener(this);
		
		getDPM();
		getCompatibility();
		
		mTime = (TextView)findViewById(R.id.time);
		mCarrier = (TextView)findViewById(R.id.carrier_label);
		setCarrierLabel(pref);
		setClockCosmetics(pref);
		
		if(tabletSize)
			mProfile = (ImageView)findViewById(R.id.profile_pic);
		message = (TextView)findViewById(R.id.message);
		
		setBackStuff(pref);
		updateTime();
		updateDrawable();
		activateTimeReceiver();
		startSleep();
	}
	
	private void setCarrierLabel(SharedPreferences pref) {
		// TODO Auto-generated method stub
		boolean carrier_label = pref.getBoolean("carrier_label", false);
		if(carrier_label){
			TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			String carrierName = manager.getNetworkOperatorName();
			mCarrier.setText(carrierName);
		}
		else
			mCarrier.setVisibility(View.GONE);
			
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setBackStuff(SharedPreferences pref) {
		// TODO Auto-generated method stub
		String back_stuff = pref.getString("back_stuff", "default_back");
		if(back_stuff.contentEquals("custom_color")){
			// Now get a handle to any View contained 
			// within the main layout you are using
			View mView = findViewById(R.id.peek_layout);
			// Find the root view
			View root = mView.getRootView();
			int color = pref.getInt("back_color", getResources().getColor(android.R.color.black));
			root.setBackgroundColor(color);
		}
		/*
		else if (back_stuff.contentEquals("custom_image")){
			// Now get a handle to any View contained 
			// within the main layout you are using
			View mView = findViewById(R.id.peek_layout);
			// Find the root view
			View root = mView.getRootView();
			String uri = pref.getString("custom_wall", null);
			try{
				Uri mUri = Uri.parse(uri);
				InputStream inputStream = getContentResolver().openInputStream(mUri);
				Drawable mDrawable = Drawable.createFromStream(inputStream, mUri.toString());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
					root.setBackground(mDrawable);
				else
					root.setBackgroundDrawable(mDrawable);
			}catch (NullPointerException e) {
				// TODO: handle exception
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} */
		else if(back_stuff.contentEquals("hs_image")){
			// Now get a handle to any View contained 
			// within the main layout you are using
			View mView = findViewById(R.id.peek_layout);
			// Find the root view
			View root = mView.getRootView();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				root.setBackground(getWallpaper());
			else
				root.setBackgroundDrawable(getWallpaper());
		}
	}

	/** Clock Cosmetic stuff **/
	private void setClockCosmetics(SharedPreferences pref) {
		// TODO Auto-generated method stub
		boolean mini_font = pref.getBoolean("clock_font", true);
		if(mini_font){
			Typeface mini = Typeface.createFromAsset(getAssets(), "fonts/AndroidClockMono-Thin.ttf");
			mTime.setTypeface(mini);
		}
		int color = pref.getInt("clock_color", getResources().getColor(android.R.color.white));
		mTime.setTextColor(color);
	}

	/** Getting Device configs **/
	private void getCompatibility() {
		// TODO Auto-generated method stub
		// If the device is Tablet
		tabletSize = getResources().getBoolean(R.bool.isTablet);
		if (!tabletSize)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			isCompatible = true;
	}

	/** Starting Time Change Receiver **/
	private void activateTimeReceiver() {
		// TODO Auto-generated method stub
		timeReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0){
					if(!isGrabbed)
						updateTime();
				}
			}
		};
		registerReceiver(timeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
	}

	/** Glow Pad Cosmetic stuff **/
	private void glowPadStuff(SharedPreferences pref) {
		// TODO Auto-generated method stub
		int haptic_vibration = pref.getInt("glowpad_vibrate", 0);
		if(haptic_vibration != 0){
			mGlowPadView.setVibrateEnabled(true);
			mGlowPadView.setVibrationDuration(haptic_vibration);
		}
		boolean dot_cloud = pref.getBoolean("dot_cloud", false);
		if(dot_cloud)
			mGlowPadView.setCloud(false);
		boolean outer_ring = pref.getBoolean("outer_ring", false);
		if(outer_ring)
			mGlowPadView.setOuterRing(false);
	}

	/**
	 * Restarts the timeout timer used to turn the screen off.
	 */
	private void startSleep() {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int sleep = pref.getInt("custom_timeout", 0);
		if(sleep != 0){
			Log.d(LOG_TAG, "Starting Timer");
			mTimer = new Handler();
			mTimer.postDelayed(mLocker, sleep * 1000);
		}
	}

	/** Getting Device Policy Manager **/
	private void getDPM() {
		// TODO Auto-generated method stub
		// Get Device Administrator Manager
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName DeviceAdmin = new ComponentName(this, DeviceAdmin.class);
        // Check if device administrator is active
		isActive = mDPM.isAdminActive(DeviceAdmin);
	}

	/** Set Lock-screen Flags to Window Manager **/
	@SuppressLint("InlinedApi")
	private void setFlags() {
		// TODO Auto-generated method stub
		// Running Flag
		isRunning = true;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		//getWindow().addFlags(WindowManager.LayoutParams.TYPE_KEYGUARD);
		//getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean status_bar = pref.getBoolean("status_bar", true);
		if(status_bar)
			/** Toggle Status Bar depending upon Preference **/
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		boolean hide_bav = pref.getBoolean("hide_nav", false);
		if(hide_bav){
			/** Toggle Soft-keys depending upon Preference **/
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}

	/** Dynamically apply theme to Activity **/
	private void setTheme() {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String theme = pref.getString("lock_theme", "black_theme");
		if(theme.contentEquals("holo_dark_theme")){
			setTheme(android.R.style.Theme_Holo_NoActionBar);
			Log.d(LOG_TAG, "Applying Theme");
		}
		
	}
	
	/** Setting Brightness to Activity **/
	private void setBrightness() {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int bright = pref.getInt("custom_brightness", 0);
		if(bright != 0){
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.screenBrightness = bright;
			getWindow().setAttributes(lp);
		}
	}


	private void updateProfile() {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Updating Profile");
		if(mProfile.getVisibility() == View.INVISIBLE)
			mProfile.setVisibility(View.VISIBLE);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String profile_detection = pref.getString("profile_detection", "none");
		if(profile_detection.contentEquals("gallery")){
			String uri = pref.getString("pic_loc", "");
			if(!TextUtils.isEmpty(uri)){
				Log.d(LOG_TAG, "Setting image from Gallery");
				mProfile.setImageURI(Uri.parse(uri));
			}
		}
		else if(profile_detection.contentEquals("google")){
			Log.d(LOG_TAG, "Loading image from Google");
			String url = pref.getString("pic_url", "");
			// ImageLoader class instance
	        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
	         
	        // whenever you want to load an image from url
	        // call DisplayImage function
	        // url - image url to load
	        // loader - loader image, will be displayed before getting image
	        // image - ImageView
	        imgLoader.DisplayImage(url, R.drawable.author, mProfile);
		}
	}


	/** Changing Drawables **/
	private void updateDrawable() {
		// TODO Auto-generated method stub
		int icon = 0;
		if(isCompatible)
			icon = NotificationListener.icon;
		else
			icon = NotificationListenerB.icon;
		if(icon != 0){
			try{
				String packages = "";
				if(isCompatible)
					packages = NotificationListener.mPackage;
				else
					packages = NotificationListenerB.mPackage;
				Drawable drawable1 = resizeDrawable(createPackageContext(packages, CONTEXT_IGNORE_SECURITY),icon, 1.2F);
				mGlowPadView.setHandleDrawable(drawable1);
				mGlowPadView.replaceTargetDrawablesByPosition(drawable1, SWIPE_LEFT);
			}catch (Exception e) {
				// TODO: handle exception
				Log.d(LOG_TAG, "Problem in Notification icon = " + e);
				setHandlerAndTargetDrawable(R.drawable.ic_lockscreen_handle_normal, R.drawable.ic_action_camera,1.0F);
			}
		}
	}


	public void updateTime() {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Updating Time");
		String time = "";
		Calendar cal = Calendar.getInstance();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean time_format = pref.getBoolean("time_format", false);
		if(time_format){
			// 24 hrs
			SimpleDateFormat df = new SimpleDateFormat("HH:mm",Locale.getDefault());
			time = df.format(cal.getTime());
		}
		else{
			SimpleDateFormat df = new SimpleDateFormat("hh:mm",Locale.getDefault());
			time = df.format(cal.getTime());
		}
		mTime.setText(time);
		applyFade(mTime , 250);
		
		//Spannable spannable = (Spannable)mTime.getText();
		//StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
		//spannable.setSpan(boldSpan, 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		//mTime.setTypeface(Typeface.DEFAULT_BOLD);
		//mTime.setTypeface(Typeface.DEFAULT);
		//mTime.setText(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
	}
	
	@Override
	public void onGrabbed(View v, int handle) {
		// TODO Auto-generated method stub
		String notify_message = "";
		if(isCompatible){
			String time = ""; 
			String title = "";
			if(!TextUtils.isEmpty(NotificationListener.mTitle)){
				title = NotificationListener.mTitle;
				time = Utils.TimeConvert(NotificationListener.post_time);
			}
			notify_message = title + "\n" + time;
		}
		else{
			String time = "";
			String title = "";
			if(!TextUtils.isEmpty(NotificationListenerB.mTitle)){
				title = NotificationListenerB.mTitle;
				time = Utils.TimeConvert(NotificationListenerB.post_time);
			}
			notify_message = title + "\n" + time;
		}
		message.setText(notify_message);
		applyFade(message,120);
		//if(!TextUtils.isEmpty(NotificationListener.mTitle))
		mTime.setText("");
		applyFade(mTime,250);
		if(tabletSize)
			updateProfile();
		updateDrawable();
		isGrabbed = true;
		destroyTimer();
	}

	@Override
	public void onReleased(View v, int handle) {
		// TODO Auto-generated method stub
		mGlowPadView.ping();
		isGrabbed = false;
	}

	@Override
	public void onTrigger(View v, int target) {
		// TODO Auto-generated method stub
		destroyTimer();
		final int resId = mGlowPadView.getResourceIdForTarget(target);
		switch (resId) {
		case R.drawable.ic_item_camera:
			launchPendingIntent();
			break;
		case R.drawable.ic_item_unlock:
			break;
		default:
			// Code should never reach here.
		}
		mGlowPadView.reset(false);
		finish();
	}

	private void launchPendingIntent() {
		// TODO Auto-generated method stub
		PendingIntent mPendingIntent;
		if(isCompatible)
			mPendingIntent = NotificationListener.mPendingIntent;
		else
			mPendingIntent = NotificationListenerB.mPendingIntent;
		if(mPendingIntent != null)
			try {
				mPendingIntent.send();
			} catch (CanceledException e) {
				// TODO Auto-generated catch block
				Log.d(LOG_TAG, "Problem in launching Notification = " + e);
			}
		else{
			Intent cameraIntent = new Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
			startActivity(cameraIntent);
		}
	}

	@Override
	public void onGrabbedStateChange(View v, int handle) {
		// TODO Auto-generated method stub
		if(handle == 0){
			message.setText("");
			applyFade(message , 120);
			if(tabletSize)
				mProfile.setVisibility(View.INVISIBLE);
			//updateTime();
			updateDrawable();
			startSleep();
		}
	}

	@Override
	public void onFinishFinalAnimation() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Cancels the timeout timer used to turn the screen off.
	 */
	private void destroyTimer() {
		// TODO Auto-generated method stub
		if(mTimer != null){
			Log.d(LOG_TAG, "Timer is destoryed");
			mTimer.removeCallbacks(mLocker);
			mTimer = null;
		}
	}
	
	/** Applying fading animation to text **/
	protected void applyFade(View mView , long timeout) {
		AlphaAnimation fadeIn = new AlphaAnimation( 1.0f , 0.0f ); 
		AlphaAnimation fadeOut = new AlphaAnimation(0.0f , 1.0f );
		mView.startAnimation(fadeIn);
		mView.startAnimation(fadeOut);
		fadeIn.setDuration(timeout);
		fadeIn.setFillAfter(true);
		fadeOut.setDuration(timeout);
		fadeOut.setFillAfter(true);
		fadeOut.setStartOffset(timeout + fadeIn.getStartOffset());
	}

	private Drawable resizeDrawable(Context mContext, int paramInt, float paramFloat){
	    BitmapDrawable localBitmapDrawable = (BitmapDrawable)mContext.getResources().getDrawable(paramInt);
	    Bitmap localBitmap = Bitmap.createScaledBitmap(localBitmapDrawable.getBitmap(), (int)(paramFloat * localBitmapDrawable.getIntrinsicHeight()), (int)(paramFloat * localBitmapDrawable.getIntrinsicWidth()), false);
	    return new BitmapDrawable(mContext.getResources(), localBitmap);
	}
	
	private void setHandlerAndTargetDrawable(int image_id1, int image_id2, float paramFloat){
	    Drawable localDrawable = resizeDrawable(this,image_id1, paramFloat);
	    mGlowPadView.setHandleDrawable(localDrawable);
	    mGlowPadView.replaceTargetDrawablesByPosition(getResources(), 2, image_id2);
	}
	
	/** Open an activity to display Device Administrator screen **/
	private void StartAdmin() {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Device Administrator is not activated");
    	// Starting Dummy Activity for starting Device Admin
    	Intent dummy_admin = new Intent(this , DummyAdmin.class);
    	dummy_admin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(dummy_admin);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			startSleep();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			destroyTimer();
			break;
		default:
			break;
		}
		return gestureDetector.onTouchEvent(ev);
	}

    SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener(){

     @Override
     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
      DisplayMetrics dm = getResources().getDisplayMetrics();
      float SWIPE_THRESHOLD_VELOCITY = (int)(200.0f * dm.densityDpi / 160.0f + 0.5);
      float SWIPE_DIVEDER = (float) 1.5;
      SWIPE_THRESHOLD_VELOCITY = SWIPE_THRESHOLD_VELOCITY / SWIPE_DIVEDER;
      
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
      if(pref.getBoolean("gesture_toggle", false)){
      
    	  // Swipe Down
    	  if((e2.getY() - e1.getY()) > SWIPE_THRESHOLD_VELOCITY){
    		  Log.d(LOG_TAG, "Swipe Down");
    		  
    	  }
    	  // Swipe Right
    	  else if(e1.getX() - e2.getX() > SWIPE_THRESHOLD_VELOCITY && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
    		  Log.d(LOG_TAG, "Swipe Right");
    		  String swipe_right = pref.getString("swipe_right", "unlock");
    		  if(swipe_right.contentEquals("lock"))
    			  turnScreenOff();
    		  else if (swipe_right.contentEquals("open_notify"))
    			  launchPendingIntent();
    		  else if(swipe_right.contentEquals("unlock"))
    			  finish();
    	  }
    	  // Swipe Left
    	  else if(e2.getX() - e1.getX() > SWIPE_THRESHOLD_VELOCITY && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
    		  Log.d(LOG_TAG, "Swipe Left");
    		  String swipe_left = pref.getString("swipe_left", "unlock");
    		  if(swipe_left.contentEquals("lock"))
    			  turnScreenOff();
    		  else if (swipe_left.contentEquals("open_notify"))
    			  launchPendingIntent();
    		  else if(swipe_left.contentEquals("unlock"))
    			  finish();
    	  }
    	  // Swipe Up
    	  else if((e1.getY() - e2.getY()) > SWIPE_THRESHOLD_VELOCITY){
    		  Log.d(LOG_TAG, "Swipe Up");
    	  
    	  }
      }
      return super.onFling(e1, e2, velocityX, velocityY);
     }

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean double_tab = pref.getBoolean("double_tap_lock", true);
		if(double_tab)
			turnScreenOff();
		return super.onDoubleTap(e);
	}
    };
    
    @SuppressWarnings("deprecation")
	GestureDetector gestureDetector= new GestureDetector(mGestureListener);
    
    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
        return;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    	if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)||(keyCode == KeyEvent.KEYCODE_POWER)||(keyCode == KeyEvent.KEYCODE_VOLUME_UP))
    		return true;
        if((keyCode == KeyEvent.KEYCODE_HOME))
        	return true;
        if(keyCode == KeyEvent.KEYCODE_BACK)
        	return backKey();
        return false;
    }
    
    private boolean backKey() {
		// TODO Auto-generated method stub
    	Intent get = getIntent();
    	if(get.hasExtra("demo_mode")){
    		boolean demo = get.getBooleanExtra("demo_mode", false);
    		if(demo)
    			finish();
    	}
		return true;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
    	if (event.getKeyCode() == KeyEvent.KEYCODE_POWER ||(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)||(event.getKeyCode() == KeyEvent.KEYCODE_POWER))
    		return false;
    	if((event.getKeyCode() == KeyEvent.KEYCODE_HOME))
    		return true;
    	if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
    		return backKey();
    	return false;
    }
    
	/** Turns screen OFF if Device Administrator is active */
    private void turnScreenOff() {
    	Log.d(LOG_TAG, "Device Locked");
    	if(isActive){
    		finish();
    		mDPM.lockNow();
    	}
    	else
    		StartAdmin();
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isRunning = false;
		if(timeReceiver != null)
			unregisterReceiver(timeReceiver);
		destroyTimer();
		super.onDestroy();
	}

	
}