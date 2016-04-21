package com.sarahheffer.flickrsearch.services;

import com.sarahheffer.flickrsearch.interfaces.FlickrApiInterface;
import com.sarahheffer.flickrsearch.models.ImageResponse;

import rx.Observable;

public class ImageService {

    FlickrApiInterface flickrApiInterface;

    public ImageService(FlickrApiInterface flickrApiInterface) {
        this.flickrApiInterface = flickrApiInterface;
    }

    public Observable<ImageResponse> getImages(String searchTerm, int page) {
        String method = "flickr.photos.search";
        String apikey = "3e7cc266ae2b0e0d78e279ce8e361736";
        String format = "json";
        int noJsonCallback = 1;
        int safeSearch = 1;
        return flickrApiInterface.getImages(method, apikey, format, noJsonCallback, safeSearch, searchTerm, page);
    }
}
