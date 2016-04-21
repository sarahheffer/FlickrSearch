package com.sarahheffer.flickrsearch.dagger;

import com.sarahheffer.flickrsearch.interfaces.FlickrApiInterface;
import com.sarahheffer.flickrsearch.services.ImageService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ApiModule {

    public <T> T createApiInterface(Retrofit retrofit, Class<T> cls) {
        return retrofit.create(cls);
    }

    @Provides
    @Singleton
    FlickrApiInterface provideFlickrApiInterface(Retrofit retrofit) {
        return createApiInterface(retrofit, FlickrApiInterface.class);
    }

    @Provides
    @Singleton
    ImageService provideImageService(FlickrApiInterface flickrApiInterface){
        return new ImageService(flickrApiInterface);
    }

}
