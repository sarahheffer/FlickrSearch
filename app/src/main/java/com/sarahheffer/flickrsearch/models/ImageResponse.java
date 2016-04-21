package com.sarahheffer.flickrsearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResponse {

    @SerializedName("photos")
    ImageMetaData data;

    public List<Image> getPhotos() {
        return data.getPhotos();
    }

}
