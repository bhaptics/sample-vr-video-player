package com.lordpeara.samplevrplayer.models;

import com.bhaptics.ble.client.TactosyClient;
import com.bhaptics.ble.model.Device;
import com.bhaptics.ble.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnConnectListener implements TactosyClient.ConnectCallback, TactosyClient.DataCallback {

    private static final int INDEX_POSITION = 2;
    private static List<DeviceWrapper> sDevices = new ArrayList<>();

    public static List<DeviceWrapper> getDevices() {
        return sDevices;
    }

    @Override
    public void onConnect(String addr) {
        List<Device> unwrappedDevices = TactosyClient.getInstance(null).getConnectedDevices();

        for (Device _device: unwrappedDevices) {
            if (_device.getAddress().equals(addr)) {
                DeviceWrapper device = new DeviceWrapper(_device, DeviceWrapper.POSITION_BOTH);

                sDevices.add(device);

                TactosyClient.getInstance(null).getMotorConfig(_device.getAddress());
                return;
            }
        }
    }

    @Override
    public void onDisconnect(String addr) {
        for (DeviceWrapper device: sDevices) {
            if (device.device.getAddress().equals(addr)) {
                sDevices.remove(device);
                return;
            }
        }
    }

    @Override
    public void onConnectionError(String addr) {}

    @Override
    public void onRead(String address, UUID charUUID, byte[] data, int status) {
        if (!charUUID.equals(Constants.MOTOR_CONFIG_CUST)) {
            return;
        }

        for (DeviceWrapper device: sDevices) {
            if (device.device.getAddress().equals(address)) {
                device.position = data[INDEX_POSITION];
            }
        }
    }

    @Override
    public void onWrite(String address, UUID charUUID, int status) {}

    @Override
    public void onDataError(String address, String charId, int errCode) {}
}
