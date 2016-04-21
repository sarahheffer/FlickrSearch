package com.sarahheffer.flickrsearch.interfaces;

import com.sarahheffer.flickrsearch.models.ImageResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface FlickrApiInterface {

    @GET("/services/rest/")
    Observable<ImageResponse> getImages(@Query("method") String method,
                                        @Query("api_key") String apikey,
                                        @Query("format") String format,
                                        @Query("nojsoncallback") int noJsonCallback,
                                        @Query("safe_search") int safeSearch,
                                        @Query("text") String searchTerm,
                                        @Query("page") int page);
}
