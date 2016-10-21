package com.lordpeara.samplevrplayer;

import android.app.Application;

import com.bhaptics.ble.client.TactosyClient;
import com.lordpeara.samplevrplayer.models.OnConnectListener;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TactosyClient.getInstance(this).bindService(this);

        OnConnectListener callback = new OnConnectListener();

        TactosyClient.getInstance(this).addConnectCallback(callback);
        TactosyClient.getInstance(this).addDataCallback(callback);
    }
}
