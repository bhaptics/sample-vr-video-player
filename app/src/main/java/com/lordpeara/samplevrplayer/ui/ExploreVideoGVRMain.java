package com.lordpeara.samplevrplayer.ui;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRScene;

import android.view.MotionEvent;

import com.lordpeara.samplevrplayer.models.VideoManager;

import java.io.File;

public class ExploreVideoGVRMain extends GVRMain {

    private GVRScene mMainScene;

    public ExploreVideoGVRMain() {}

    @Override
    public void onInit(GVRContext gvrContext) {
        mMainScene = new ExploreVideoScene(gvrContext, VideoManager.getVideoList());

        gvrContext.setMainScene(mMainScene);
    }

    public void onTouchEvent(MotionEvent event) {
        GVRScene mainScene = getGVRContext().getMainScene();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mainScene instanceof PickHandler) {
                    File result = ((PickHandler) mainScene).onPicked();

                    if (result != null) {
                        VideoScene videoScene = new VideoScene(getGVRContext(), result);
                        getGVRContext().setMainScene(videoScene);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void switchToMainScene() {
        GVRScene currentScene = getGVRContext().getMainScene();
        if (currentScene instanceof VideoScene) {
            ((VideoScene) currentScene).destroy();
        }

        getGVRContext().setMainScene(mMainScene);
    }

    @Override
    public void onStep() {
        GVRScene mainScene = getGVRContext().getMainScene();

        if (!(mainScene instanceof VideoScene)) {
            return;
        }

        ((VideoScene) mainScene).onStep();
    }
}
