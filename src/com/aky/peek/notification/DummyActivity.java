package com.aky.peek.notification;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class DummyActivity extends Activity {

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(new View(this));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            finish();
    }

}
