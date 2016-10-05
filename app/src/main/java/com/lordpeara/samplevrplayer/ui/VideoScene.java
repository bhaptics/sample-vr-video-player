package com.lordpeara.samplevrplayer.ui;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.bhaptics.ble.core.TactosyManager;
import com.bhaptics.ble.model.Device;
import com.bhaptics.ble.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.lordpeara.samplevrplayer.models.Feedback;
import com.lordpeara.samplevrplayer.models.FeedbackWrapper;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRScene;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VideoScene extends GVRScene {

    private static final byte[] EMPTY_BYTES = new byte[20];

    private Map<String, List<Feedback>> mFeedbacks = null;
    private List<DeviceWrapper> mDevices = new ArrayList<>();

    private GVRVideoSceneObjectPlayer<MediaPlayer> mGVRPlayer;
    private MediaPlayer mPlayer;

    public static GVRVideoSceneObjectPlayer<MediaPlayer> makeMediaPlayer(File video) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        mediaPlayer.setLooping(false);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        try {
            mediaPlayer.setDataSource(video.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return GVRVideoSceneObject.makePlayerInstance(mediaPlayer);
    }

    public VideoScene(GVRContext gvrContext, File video) {
        super(gvrContext);

        // set up camerarig position (default)
        getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);

        mPlayer = null;

        List<Device> devices = TactosyManager.getInstance().getConnectedDevices();
        for (int i = 0; i < devices.size(); i++) {
            mDevices.add(new DeviceWrapper(devices.get(i), i % 2));
        }

        loadVideo(video);
    }

    private void loadVideo(File video) {
        String videoPath = video.getAbsolutePath();

        String splt[] = videoPath.split("\\.");
        String ext = "." + splt[splt.length-1];

        String tactosyFilePath = videoPath.replace(ext, ".tactosy");

        try {
            FileReader fReader = new FileReader(tactosyFilePath);

            Gson gson = new Gson();
            Type type = new TypeToken<FeedbackWrapper>() {}.getType();

            JsonReader reader = new JsonReader(fReader);

            FeedbackWrapper wrapper = gson.fromJson(reader, type);
            mFeedbacks = wrapper.mFeedbacks;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        mGVRPlayer = makeMediaPlayer(video);
        mPlayer = mGVRPlayer.getPlayer();

        // create sphere / mesh
        GVRSphereSceneObject sphere = new GVRSphereSceneObject(getGVRContext(), 72, 144, false);
        GVRMesh mesh = sphere.getRenderData().getMesh();

        // create video scene
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(getGVRContext(),
                mesh, mPlayer, GVRVideoSceneObject.GVRVideoType.MONO);

        videoSceneObject.setName("video");

        // apply video to scene
        addSceneObject(videoSceneObject);
    }

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

    public void destroy() {
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    class DeviceWrapper {
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
