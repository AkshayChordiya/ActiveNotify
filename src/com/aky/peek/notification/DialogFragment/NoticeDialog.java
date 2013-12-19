package com.aky.peek.notification.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.aky.peek.notification.R;

/** @author Akshay Chordiya
 * @category DialogFragment
 * @version API v11 **/
@SuppressLint("NewApi")
public class NoticeDialog extends DialogFragment{
	
	public static final String LOG_TAG = "NoticeDialog";
	boolean isCompatible = false;
	
	public static NoticeDialog newInstance(int title) {
        return new NoticeDialog();
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			isCompatible = true;
		AlertDialog.Builder mAlert = new AlertDialog.Builder(getActivity());
		mAlert.setTitle(R.string.hint);
		mAlert.setIcon(R.drawable.author);
		if(isCompatible)
			mAlert.setMessage(R.string.accessibility_intro);
		else
			mAlert.setMessage(R.string.accessibility_intro_pre);
		mAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub.
				if(!isCompatible){
					Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
					startActivityForResult(intent, 0);
				}
				else{
					startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
				}
				//SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
				//mEditor.putBoolean("start_notice", false);
				//mEditor.apply();
			}
		});
		return mAlert.create();
	}
	
}
