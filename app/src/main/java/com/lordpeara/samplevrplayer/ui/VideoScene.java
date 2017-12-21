package com.lordpeara.samplevrplayer.ui;

import android.media.MediaPlayer;
import android.util.Log;

import com.bhaptics.ble.client.TactosyClient;
import com.bhaptics.ble.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.lordpeara.samplevrplayer.models.DeviceWrapper;
import com.lordpeara.samplevrplayer.models.Feedback;
import com.lordpeara.samplevrplayer.models.FeedbackWrapper;
import com.lordpeara.samplevrplayer.models.OnConnectListener;

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

    private GVRVideoSceneObjectPlayer<MediaPlayer> mGVRPlayer;
    private MediaPlayer mPlayer;

    List<Feedback> feedbackList;

    public static final String TAG = "VideoScene";

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
        feedbackList = new ArrayList<>();
        // set up camerarig position (default)
        getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);

        mPlayer = null;

        loadVideo(video);
    }

    private void loadVideo(File video) {
        String videoPath = video.getAbsolutePath();

        String splt[] = videoPath.split("\\.");
        String ext = "." + splt[splt.length-1];

        String tactosyFilePath = videoPath.replace(ext, ".tact");

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
        Log.e(TAG,"CUR : "+cur);

        List<Feedback> feedbacks = null;
        if (mFeedbacks.containsKey(String.valueOf(cur))) {
            feedbacks = mFeedbacks.get(String.valueOf(cur));
        }


        for (Feedback f: feedbacks) {
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< f.mValues.length; i++ )
                sb.append(f.mValues[i]+" ");
            Log.e(TAG, sb.toString());

            feedbackList.add(f);


        }
        Log.e(TAG,feedbackList.toString());

        TactosyClient.getInstance(null)
                .setMotor(new Gson().toJson(feedbackList));
        feedbackList.clear();
    }

    public void destroy() {
        for (DeviceWrapper device: OnConnectListener.getDevices()) {
            TactosyClient.getInstance(null)
                    .setMotor(device.device.getAddress(), EMPTY_BYTES);
        }

        if (mPlayer != null) {
            mPlayer.release();
        }
    }
}
