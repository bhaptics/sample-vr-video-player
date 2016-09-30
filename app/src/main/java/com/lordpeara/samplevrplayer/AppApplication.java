package com.lordpeara.samplevrplayer;

import android.app.Application;

import com.bhaptics.ble.core.TactosyManager;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TactosyManager.instantiate(this);
        TactosyManager.getInstance().bindService();
    }
}
