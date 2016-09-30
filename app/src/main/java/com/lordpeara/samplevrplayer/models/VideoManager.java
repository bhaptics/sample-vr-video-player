package com.lordpeara.samplevrplayer.models;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoManager {

    private static final String DEFAULT_PATH = "Videos";
    private static final String SUPPORTED_EXT = ".mp4";

    private static final ArrayList<File> EMPTY_LIST = new ArrayList<>();

    public static List<File> getVideoList() {
        return getVideoList(DEFAULT_PATH);
    }

    public static List<File> getVideoList(String path) {

        String sdCardState = Environment.getExternalStorageState();

        if (!(sdCardState.equals(Environment.MEDIA_MOUNTED) ||
              sdCardState.equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {

            return EMPTY_LIST;
        }

        File root = Environment.getExternalStorageDirectory();
        File mediaDir = new File(root, path);

        File files[] = mediaDir.listFiles();

        if (files == null) {
            return EMPTY_LIST;
        }

        ArrayList<File> videoList = new ArrayList<>();
        for (File file: files) {
            if (file.getName().toLowerCase().endsWith(SUPPORTED_EXT)) {
                videoList.add(file);
            }
        }

        return videoList;
    }
}
