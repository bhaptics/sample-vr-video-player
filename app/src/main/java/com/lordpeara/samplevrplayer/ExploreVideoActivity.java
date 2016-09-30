package com.lordpeara.samplevrplayer;

import org.gearvrf.GVRActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class ExploreVideoActivity extends GVRActivity {

    private ExploreVideoGVRMain mGVRMain = new ExploreVideoGVRMain(this);

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setMain(mGVRMain, "settings.xml");
        Log.e(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGVRMain.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
