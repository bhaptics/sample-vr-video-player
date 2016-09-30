package com.lordpeara.samplevrplayer.models;

import com.google.gson.annotations.SerializedName;

public class Feedback {

    @SerializedName("position")
    public String mPosition;

    @SerializedName("mode")
    public String mType;

    @SerializedName("values")
    public byte[] mValues;
}
