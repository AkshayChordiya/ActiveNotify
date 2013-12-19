package com.aky.peek.notification.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.aky.peek.notification.R;

/** @author Akshay Chordiya
 * @category DialogFragment
 * @version API v11 **/
@SuppressLint("NewApi")
public class RateDialog extends DialogFragment{
	
	public static RateDialog newInstance(int title) {
        return new RateDialog();
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return new AlertDialog.Builder(getActivity())
		.setTitle(R.string.rate_it)
    	.setIcon(android.R.drawable.btn_star_big_on)
    	.setMessage(getString(R.string.like_app_summary))
    	.setPositiveButton(R.string.rate_it, new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			// TODO Auto-generated method stub
    			Intent play = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));
    			startActivity(play);
    		}
    	})
    	.setNegativeButton(R.string.later, null)
		.create();
	}
	
	

}
