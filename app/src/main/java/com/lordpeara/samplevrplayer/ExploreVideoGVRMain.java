package com.lordpeara.samplevrplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.IPickEvents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;

import com.lordpeara.samplevrplayer.models.VideoManager;

public class ExploreVideoGVRMain extends GVRMain {

    private static final float THUMB_WIDTH = 2.0f;
    private static final float THUMB_HEIGHT = 2.0f;

    private Context mMainContext;

    // NOTE this is never used but should not be collected by gc.
    private GVRPicker mPicker;

    private VideoPickHandler mPickHandler;

    private ArrayList<GVRSceneObject> mSceneObjects = new ArrayList<>();
    private ArrayList<File> mVideos = new ArrayList<>();

    private class VideoPickHandler implements IPickEvents {

        public GVRSceneObject mPickedObject = null;

        @Override
        public void onPick(GVRPicker picker) {
            GVRPicker.GVRPickedObject picked = picker.getPicked()[0];
            mPickedObject = picked.hitObject;
        }

        @Override
        public void onNoPick(GVRPicker picker) {
            mPickedObject = null;
        }

        @Override
        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject collision) {}

        @Override
        public void onExit(GVRSceneObject sceneObj) {}

        @Override
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject collision) {}
    }

    public ExploreVideoGVRMain(Context context) {
        mMainContext = context;
    }

    @Override
    public void onInit(GVRContext gvrContext) {
        List<File> videos = VideoManager.getVideoList();

        GVRScene mainScene = gvrContext.getNextMainScene();

        mainScene.getMainCameraRig().getLeftCamera().setBackgroundColor(1f, 1f, 1f, 1f);
        mainScene.getMainCameraRig().getRightCamera().setBackgroundColor(1f, 1f, 1f, 1f);

        mPickHandler = new VideoPickHandler();
        mainScene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(gvrContext, mainScene);

        GVRSceneObject eyeTracker = new GVRSceneObject(gvrContext,
                gvrContext.createQuad(0.1f, 0.1f),
                gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.tracker))
        );

        eyeTracker.getTransform().setPosition(0f, 0f, -1f);
        eyeTracker.getRenderData().setDepthTest(false);
        eyeTracker.getRenderData().setRenderingOrder(100000);

        mainScene.getMainCameraRig().addChildObject(eyeTracker);

        for (int i = 0; i < videos.size(); i++) {
            File video = videos.get(i);

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(
                    video.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND
            );

            GVRBitmapTexture texture = new GVRBitmapTexture(gvrContext, thumb);

            GVRSceneObject sceneObject = new GVRSceneObject(
                    gvrContext, THUMB_WIDTH, THUMB_HEIGHT, texture
            );

            float start = 1f - THUMB_WIDTH * (11 * videos.size() - 1) / 20;

            sceneObject.setPickingEnabled(true);
            sceneObject.getTransform().setPosition(start + THUMB_WIDTH * i * 1.1f, 0.0f, -2.0f);

            mSceneObjects.add(sceneObject);
            mVideos.add(video);

            // add the scene object to the scene graph
            gvrContext.getNextMainScene().addSceneObject(sceneObject);
        }
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPickHandler.mPickedObject != null) {
                    onPickedObjectHandled(mPickHandler.mPickedObject);
                }
                break;
            default:
                break;
        }
    }

    private void onPickedObjectHandled(GVRSceneObject picked) {
        int targetIdx = -1;
        for (int i=0; i<mSceneObjects.size(); i++) {
            if (picked == mSceneObjects.get(i)) {
                targetIdx = i;
                break;
            }
        }

        if (targetIdx != -1) {
            Intent intent = new Intent(mMainContext, VideoActivity.class);
            File video = mVideos.get(targetIdx);

            intent.putExtra(VideoActivity.EXTRA_FILE_NAME, video.getAbsolutePath());

            mMainContext.startActivity(intent);
        }
    }

    @Override
    public void onStep() {}
}
