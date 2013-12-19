package com.aky.peek.notification;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/** @author Akshay Chordiya
 * @category DeviceAdminReceiver
 * {@code} To receive Device Administrator request **/
public class DeviceAdmin extends DeviceAdminReceiver {

	/* Method for showing toast */
    void showToast(Context context, String msg) {
        String status = context.getString(R.string.admin_receiver_status, msg);
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
    	// When Administrator is enabled
        showToast(context, context.getString(R.string.admin_receiver_status_enabled));
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    	// After disabling administrator
        showToast(context, context.getString(R.string.admin_receiver_status_disabled));
    }
}