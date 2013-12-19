package com.aky.peek.notification;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/** @author Akshay Chordiya
 * @category Activity
 * {@code} Dummy Activity to show Device Administrator */
public class DummyAdmin extends Activity {
	
	private static final int ACTIVATION_REQUEST = 0;

	// String for collecting all the LOGS of that class
	private static final String LOG_TAG = "DummyActivity";
	
	DevicePolicyManager mDPM;
	ComponentName DeviceAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get Device Administrator Manager
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		DeviceAdmin = new ComponentName(this, DeviceAdmin.class);
		requestAdmin();
	}
	
	public void requestAdmin() {
		// TODO Auto-generated method stub
		// Launch the activity to have the user enable our admin.
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, DeviceAdmin);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.admin_description);
		intent.putExtra("force-lock", DeviceAdminInfo.USES_POLICY_FORCE_LOCK);
		startActivityForResult(intent, ACTIVATION_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case ACTIVATION_REQUEST:
	            if (resultCode == Activity.RESULT_OK)
	                Log.d(LOG_TAG, "Administration enabled!");
	            else
	                Log.i(LOG_TAG, "Administration enable FAILED!");
	            finish();	    	
	            return;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
