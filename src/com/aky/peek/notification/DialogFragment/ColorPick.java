package com.aky.peek.notification.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.aky.peek.notification.R;
import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.colorpicker.SaturationBar;
import com.larswerkman.colorpicker.ValueBar;

/** @author Akshay Chordiya
 * @category DialogFragment
 * @version API v11 **/
public class ColorPick extends DialogFragment implements OnColorChangedListener{
	
	public final String LOG_TAG = "ColorPick";
	int color;
	protected static String key;
	
	
	public static ColorPick newInstance(String title) {
		key = title;
        return new ColorPick();
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "Color Pick");
		LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View color_picker = inflater.inflate(R.layout.color_picker, null);
        
        ColorPicker picker = (ColorPicker) color_picker.findViewById(R.id.picker);
        ValueBar valueBar = (ValueBar) color_picker.findViewById(R.id.valuebar);
        SaturationBar saturationBar = (SaturationBar) color_picker.findViewById(R.id.saturationbar);

        picker.addValueBar(valueBar);
        picker.addSaturationBar(saturationBar);
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(key.contentEquals("clock_color"))
        	color = pref.getInt(key, getResources().getColor(android.R.color.white));
        else if(key.contentEquals("back_color"))
        	color = pref.getInt(key, getResources().getColor(android.R.color.black));
		picker.setColor(color);
        // To get the color
        picker.getColor();
        //To set the old selected color u can do it like this
        picker.setOldCenterColor(picker.getColor());
        // Adds listener to the colorpicker which is implemented
        picker.setOnColorChangedListener(this);
        
        return new AlertDialog.Builder(getActivity())
        .setTitle(R.string.clock_color_title)
		.setView(color_picker)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				setPref();
			}
		})
		.setNegativeButton(android.R.string.cancel, null)
		.create();
        
    }

	protected void setPref() {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor edit = pref.edit();
		edit.putInt(key, color);
		// Commit the data
		edit.apply();
	}

	@Override
	public void onColorChanged(int color) {
		// TODO Auto-generated method stub
		this.color = color;
	}
	
}
