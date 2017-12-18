package com.lordpeara.samplevrplayer;

import org.gearvrf.GVRActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
        getPermission();
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
    public void getPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}
