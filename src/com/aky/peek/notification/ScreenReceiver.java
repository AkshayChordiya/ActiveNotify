package com.aky.peek.notification;


import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/** @author Akshay Chordiya
 * @category Broadcast Receiver
 * {@code} To receive screen ON/OFF status for implementing LOCK & UNLOCK **/
@SuppressWarnings("deprecation")
public class ScreenReceiver extends BroadcastReceiver {
	
	public static final String LOG_TAG = "ScreenReceiver";
	
	// Boolean for storing screen is ON or OFF for further LOCK & UNLOCK procedure
	public static int screen_state = 1;
	
	@Override
    public void onReceive(Context context, Intent intent) {
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean replace_lockscreen = pref.getBoolean("replace_lockscreen", false);
		
		KeyguardManager km = (KeyguardManager) context.getSystemService("keyguard");
		KeyguardLock kl = km.newKeyguardLock("Aky");
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
			// When Screen is ON
        	Log.d(LOG_TAG, "Device/Screen ON");
        	if(replace_lockscreen && !PeekMode.isRunning){
        		kl.disableKeyguard();
        		Intent peek = new Intent(context , PeekMode.class);
        		peek.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		context.startActivity(peek);
        	}
        	screen_state = 0;
		}
		else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
			// When user is present i.e some activities
         	Log.d(LOG_TAG, "User present / active");
         	screen_state = 1;
		}
		else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
        	 // When Screen is OFF
        	 Log.d(LOG_TAG, "Device/Screen OFF");
        	 screen_state = 2;
		}
		else{
			Log.d(LOG_TAG, "Something went wrong");
		}
		
		
    }

}
