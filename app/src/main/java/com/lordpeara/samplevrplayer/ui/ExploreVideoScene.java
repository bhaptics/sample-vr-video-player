package com.lordpeara.samplevrplayer.ui;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.lordpeara.samplevrplayer.R;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.IPickEvents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExploreVideoScene extends GVRScene implements PickHandler {

    private static final String TAG = "ExploreVideoScene";

    private class VideoPickHandler implements IPickEvents {

        GVRSceneObject mPickedObject = null;

        @Override
        public void onPick(GVRPicker picker) {
            GVRPicker.GVRPickedObject picked = picker.getPicked()[0];
            mPickedObject = picked.hitObject;

            int targetIdx = -1;
            for (int i=0; i<mSceneObjects.size(); i++) {
                if (mPickHandler.mPickedObject == mSceneObjects.get(i)) {
                    targetIdx = i;
                    break;
                }
            }

            if (targetIdx != -1) {
                Log.e(TAG, "pickedObject: " + mVideos.get(targetIdx).getAbsolutePath());
            }
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

    private static final float THUMB_WIDTH = 2.0f;
    private static final float THUMB_HEIGHT = 2.0f;

    // NOTE this is never used but should not be collected by gc.
    private GVRPicker mPicker;

    private VideoPickHandler mPickHandler;

    private ArrayList<GVRSceneObject> mSceneObjects = new ArrayList<>();
    private ArrayList<File> mVideos = new ArrayList<>();

    public ExploreVideoScene(GVRContext gvrContext, List<File> videos) {
        super(gvrContext);

        getMainCameraRig().getLeftCamera().setBackgroundColor(1f, 1f, 1f, 1f);
        getMainCameraRig().getRightCamera().setBackgroundColor(1f, 1f, 1f, 1f);

        GVRSceneObject eyeTracker = new GVRSceneObject(gvrContext,
                gvrContext.createQuad(0.1f, 0.1f),
                gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.tracker))
        );

        eyeTracker.getTransform().setPosition(0f, 0f, -1f);
        eyeTracker.getRenderData().setDepthTest(false);
        eyeTracker.getRenderData().setRenderingOrder(100000);

        getMainCameraRig().addChildObject(eyeTracker);

        mPickHandler = new VideoPickHandler();
        getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(gvrContext, this);

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
            addSceneObject(sceneObject);
        }
    }

    @Override
    public File onPicked() {
        int targetIdx = -1;

        for (int i=0; i<mSceneObjects.size(); i++) {
            if (mPickHandler.mPickedObject == mSceneObjects.get(i)) {
                targetIdx = i;
                break;
            }
        }

        if (targetIdx != -1) {
            Log.e(TAG, "onPicked: " + mVideos.get(targetIdx).getAbsolutePath());
            return mVideos.get(targetIdx);
        }

        return null;
    }
}
