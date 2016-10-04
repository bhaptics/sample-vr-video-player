package com.lordpeara.samplevrplayer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.lordpeara.samplevrplayer.models.FeedbackWrapper;

import org.gearvrf.GVRActivity;
import org.gearvrf.scene_objects.GVRVideoSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObjectPlayer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

public class VideoActivity extends GVRActivity {

    public static final String EXTRA_FILE_NAME = "com.bhaptics.vr.VideoActivity.EXTRA_FILE_NAME";

    private GVRVideoSceneObjectPlayer<MediaPlayer> mVideoSceneObjectPlayer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String videoPath = intent.getStringExtra(EXTRA_FILE_NAME);

        mVideoSceneObjectPlayer = makeMediaPlayer(this, videoPath);

        String splt[] = videoPath.split("\\.");
        String ext = "." + splt[splt.length-1];

        String tactosyFilePath = videoPath.replace(ext, ".tactosy");

        VideoSceneGVRMain main;

        try {
            FileReader fReader = new FileReader(tactosyFilePath);

            Gson gson = new Gson();
            Type type = new TypeToken<FeedbackWrapper>() {}.getType();

            JsonReader reader = new JsonReader(fReader);

            FeedbackWrapper wrapper = gson.fromJson(reader, type);

            main = new VideoSceneGVRMain(mVideoSceneObjectPlayer, wrapper);

        } catch (FileNotFoundException e) {
            main = new VideoSceneGVRMain(mVideoSceneObjectPlayer);
            e.printStackTrace();
        }

        if (mVideoSceneObjectPlayer != null) {
            setMain(main, "settings.xml");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoSceneObjectPlayer != null) {
            final Object player = mVideoSceneObjectPlayer.getPlayer();
            MediaPlayer mediaPlayer = (MediaPlayer) player;
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoSceneObjectPlayer != null) {
            final Object player = mVideoSceneObjectPlayer.getPlayer();
            MediaPlayer mediaPlayer = (MediaPlayer) player;
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mVideoSceneObjectPlayer != null) {
            final Object player = mVideoSceneObjectPlayer.getPlayer();
            MediaPlayer mediaPlayer = (MediaPlayer) player;
            mediaPlayer.release();
        }
    }

    private static GVRVideoSceneObjectPlayer<MediaPlayer> makeMediaPlayer(Activity context, String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        mediaPlayer.setLooping(false);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            context.finish();
            return null;
        }

        return GVRVideoSceneObject.makePlayerInstance(mediaPlayer);
    }
}
