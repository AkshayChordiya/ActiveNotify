package com.aky.peek.notification;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class PrefCategory extends PreferenceCategory{
	
    public PrefCategory(Context context) {
        super(context);
    }

    public PrefCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrefCategory(Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(view.getResources().getColor(R.color.androidblue));
    }
}
