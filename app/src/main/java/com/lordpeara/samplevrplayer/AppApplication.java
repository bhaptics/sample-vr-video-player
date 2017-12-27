package com.lordpeara.samplevrplayer;

import android.app.Application;

import com.bhaptics.ble.client.HapticPlayer;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HapticPlayer.getInstance(this).bindService(this);
    }
}
