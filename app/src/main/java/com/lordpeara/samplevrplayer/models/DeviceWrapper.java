package com.lordpeara.samplevrplayer.models;

import com.bhaptics.ble.model.Device;

public class DeviceWrapper {
    public static final int POSITION_BOTH = -1;
    public static final int POSITION_LEFT = 1;
    public static final int POSITION_RIGHT =  2;
    public static final int VEST_FRONT = 201;
    public static final int VEST_BACK =202;
    public Device device;
    public int position;

    public DeviceWrapper(Device device, int position) {
        this.device = device;
        this.position = position;
    }
}
