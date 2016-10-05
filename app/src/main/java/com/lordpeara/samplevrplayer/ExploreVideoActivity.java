package com.lordpeara.samplevrplayer;

import org.gearvrf.GVRActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.lordpeara.samplevrplayer.ui.ExploreVideoGVRMain;
import com.lordpeara.samplevrplayer.ui.VideoScene;

public class ExploreVideoActivity extends GVRActivity {

    private static final String TAG = "ExploreVideoActivity";

    private ExploreVideoGVRMain mGVRMain = new ExploreVideoGVRMain();

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyAction = event.getAction();
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && keyAction == KeyEvent.ACTION_DOWN
                && mGVRMain.getGVRContext().getMainScene() instanceof VideoScene) {
            mGVRMain.switchToMainScene();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
