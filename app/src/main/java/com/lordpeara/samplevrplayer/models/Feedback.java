package com.lordpeara.samplevrplayer.models;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Feedback {

    @SerializedName("position")
    public String mPosition;

    @SerializedName("mode")
    public String mType;

    @SerializedName("values")
    public byte[] mValues;

    @Override
    public String toString() {
        return "Feedback{" +
                "mPosition='" + mPosition + '\'' +
                ", mType='" + mType + '\'' +
                ", mValues=" + Arrays.toString(mValues) +
                '}';
    }
}
