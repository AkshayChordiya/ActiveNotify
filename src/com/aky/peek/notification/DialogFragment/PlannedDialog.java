package com.aky.peek.notification.DialogFragment;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.aky.peek.notification.R;

/** @author Akshay Chordiya
 * @category DialogFragment
 * @version API v11
 * {@code} For showing Planned Log Dialog on Tablets **/
@SuppressLint("NewApi")
public class PlannedDialog extends DialogFragment{
	
	public static PlannedDialog newInstance(int title) {
        return new PlannedDialog();
    }


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//Launch planned feature dialog
		InputStream isF = getActivity().getResources().openRawResource(R.raw.featurelog);
        if (isF == null)
            return null;
        // Read the change log
        StringBuilder sb = new StringBuilder();
        int read = 0;
        byte[] data = new byte[512];
        try {
        	while ((read = isF.read(data, 0, 512)) != -1) {
        		sb.append(new String(data, 0, read));
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        // Show a dialog
        return new AlertDialog.Builder(getActivity())
        .setTitle(R.string.about_plan_title)
        .setIcon(R.drawable.author)
        .setMessage(sb.toString())
        .setNegativeButton(android.R.string.ok, null)
        .create();

		
	}
	
	

}
