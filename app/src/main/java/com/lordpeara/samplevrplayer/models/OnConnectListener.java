package com.lordpeara.samplevrplayer.models;

import com.bhaptics.ble.core.TactosyManager;
import com.bhaptics.ble.model.Device;

import java.util.ArrayList;
import java.util.List;

public class OnConnectListener implements TactosyManager.ConnectCallback {

    private static List<DeviceWrapper> sDevices = new ArrayList<>();

    private static final int INDEX_LED_COLOR = 2;

    private static final byte[] DEFAULT_CONFIG = new byte[] {
            0x64, 0x04, 0x00
    };

    private static int LAST_POSITION = DeviceWrapper.POSITION_LEFT;

    public static List<DeviceWrapper> getDevices() {
        return sDevices;
    }

    @Override
    public void onConnect(String addr) {
        List<Device> unwrappedDevices = TactosyManager.getInstance().getConnectedDevices();

        for (Device _device: unwrappedDevices) {
            if (_device.getMacAddress().equals(addr)) {
                DeviceWrapper device = new DeviceWrapper(_device, LAST_POSITION);

                LAST_POSITION = LAST_POSITION == DeviceWrapper.POSITION_RIGHT ?
                        DeviceWrapper.POSITION_LEFT : DeviceWrapper.POSITION_RIGHT;

                sDevices.add(device);

                byte[] config = DEFAULT_CONFIG.clone();
                config[INDEX_LED_COLOR] = (byte) device.position;

                TactosyManager.getInstance().setMotorConfig(_device.getMacAddress(), config);
            }
        }
    }

    @Override
    public void onDisconnect(String addr) {
        for (DeviceWrapper device: sDevices) {
            if (device.device.getMacAddress().equals(addr)) {
                sDevices.remove(device);
                return;
            }
        }
    }

    @Override
    public void onConnectionError(String addr) {}
}
