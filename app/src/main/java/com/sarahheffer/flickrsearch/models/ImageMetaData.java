package com.sarahheffer.flickrsearch.models;

import java.util.List;

public class ImageMetaData {

    int page;
    int pages;
    int perpage;
    int total;
    List<Image> photo;

    public List<Image> getPhotos() {
        return photo;
    }
}
