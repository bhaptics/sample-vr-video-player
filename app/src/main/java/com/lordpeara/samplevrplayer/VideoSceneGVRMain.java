package com.lordpeara.samplevrplayer;

import android.media.MediaPlayer;

import com.bhaptics.ble.core.TactosyManager;
import com.bhaptics.ble.model.Device;
import com.bhaptics.ble.util.Constants;
import com.lordpeara.samplevrplayer.models.Feedback;
import com.lordpeara.samplevrplayer.models.FeedbackWrapper;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRScene;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject.GVRVideoType;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VideoSceneGVRMain extends GVRMain {

    private final GVRVideoSceneObjectPlayer<MediaPlayer> mGVRPlayer;
    private Map<String, List<Feedback>> mFeedbacks = null;
    private MediaPlayer mPlayer;

    private static final byte[] EMPTY_BYTES = new byte[20];

    private List<DeviceWrapper> mDevices = new ArrayList<>();

    public VideoSceneGVRMain(GVRVideoSceneObjectPlayer<MediaPlayer> player) {
        this(player, null);
    }

    public VideoSceneGVRMain(GVRVideoSceneObjectPlayer<MediaPlayer> player, FeedbackWrapper wrapper) {
        mGVRPlayer = player;
        mPlayer = player.getPlayer();

        if (wrapper != null) {
            mFeedbacks = wrapper.mFeedbacks;
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onInit(GVRContext gvrContext) {

        GVRScene scene = gvrContext.getNextMainScene();

        // set up camerarig position (default)
        scene.getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);

        // create sphere / mesh
        GVRSphereSceneObject sphere = new GVRSphereSceneObject(gvrContext, 72, 144, false);
        GVRMesh mesh = sphere.getRenderData().getMesh();

        // create video scene
        GVRVideoSceneObject video = new GVRVideoSceneObject(gvrContext, mesh, mPlayer, GVRVideoType.MONO);
        video.setName("video");

        // apply video to scene
        scene.addSceneObject(video);

        List<Device> devices = TactosyManager.getInstance().getConnectedDevices();
        for (int i = 0; i < devices.size(); i++) {
            mDevices.add(new DeviceWrapper(devices.get(i), i % 2));
        }
    }

    @Override
    public void onStep() {
        if (!mPlayer.isPlaying() || mFeedbacks == null) {
            return;
        }

        int cur = mPlayer.getCurrentPosition();
        cur = cur / 20 * 20;

        byte[] bytes;
        List<Feedback> feedbacks = null;
        if (mFeedbacks.containsKey(String.valueOf(cur))) {
            feedbacks = mFeedbacks.get(String.valueOf(cur));
        }

        if (feedbacks == null || feedbacks.size() == 0) {
            for (DeviceWrapper device: mDevices) {
                TactosyManager.getInstance().setMotor(device.device.getMacAddress(), EMPTY_BYTES);
            }
            return;
        }

        for (Feedback f: feedbacks) {
            int pos = f.mPosition.equals("Left") ?
                    DeviceWrapper.POSITION_LEFT : DeviceWrapper.POSITION_RIGHT;

            UUID charUuid = f.mType.equals("DOT_MODE") ?
                    Constants.MOTOR_CHAR : Constants.MOTOR_CHAR_MAPP;

            for (DeviceWrapper device: mDevices) {
                if (pos == device.position) {
                    TactosyManager.getInstance()
                            .setMotor(device.device.getMacAddress(), f.mValues, charUuid);
                }
            }
        }
    }

    private class DeviceWrapper {
        public static final int POSITION_BOTH = -1;
        public static final int POSITION_LEFT = 0;
        public static final int POSITION_RIGHT = 1;

        public Device device;
        public int position;

        public DeviceWrapper(Device device, int position) {
            this.device = device;
            this.position = position;
        }
    }
}
