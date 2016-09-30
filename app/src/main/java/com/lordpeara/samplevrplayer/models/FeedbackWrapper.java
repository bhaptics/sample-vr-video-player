package com.lordpeara.samplevrplayer.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class FeedbackWrapper {
    @SerializedName("intervalMillis")
    public int mInterval;

    @SerializedName("size")
    public int mArraySize;

    @SerializedName("durationMillis")
    public int mDuration;

    @SerializedName("feedback")
    public Map<String, List<Feedback>> mFeedbacks;
}
