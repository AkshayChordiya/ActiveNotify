package com.aky.peek.notification.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.aky.peek.notification.R;

/** @author Akshay Chordiya
 * @category DialogFragment
 * @version API v11
 * {@code} For showing Reset Dialog on Tablets **/
@SuppressLint("NewApi")
public class ResetDialog extends DialogFragment{
	
	public static ResetDialog newInstance(int title) {
        return new ResetDialog();
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return new AlertDialog.Builder(getActivity())
		.setTitle(R.string.reset_setting_title)
		.setIcon(R.drawable.author)
		.setMessage(R.string.reset_message)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor edit = pref.edit();
				edit.clear();
				// Commit the data
				edit.apply();
				// Get editor & shared preferences
				Editor editor = getActivity().getSharedPreferences("packages", Context.MODE_PRIVATE).edit();
				// Clear everything
				editor.clear();
				// Commit the data
				editor.apply();
				// Tell user about restarting app
				Toast.makeText(getActivity(), R.string.reset_settings_toast, Toast.LENGTH_LONG).show();
			}
		})		
		.setNegativeButton(android.R.string.no, null)
		.create();
	}
	
	

}
