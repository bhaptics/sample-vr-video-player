package com.lordpeara.samplevrplayer;

import android.app.Application;

import com.bhaptics.ble.core.TactosyManager;
import com.lordpeara.samplevrplayer.models.OnConnectListener;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TactosyManager.instantiate(this);
        TactosyManager.getInstance().bindService();

        OnConnectListener callback = new OnConnectListener();

        TactosyManager.getInstance().addConnectCallback(callback);
        TactosyManager.getInstance().addDataCallback(callback);
    }
}
