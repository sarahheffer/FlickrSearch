package com.sarahheffer.flickrsearch.dagger;

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

@Module
public class NetModule {

    private Application mApplication;

    public NetModule(Application app) {
        this.mApplication = app;
    }

    @Provides
    @Singleton
    public Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        return client;
    }

    @Provides
    @Singleton
    RxJavaCallAdapterFactory provideRxJavaCallAdapterFactory() {
        return RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, RxJavaCallAdapterFactory rxJavaCallAdapterFactory,
                             OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .baseUrl("https://api.flickr.com")
                .client(okHttpClient)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    OkHttp3Downloader provideOkHttp3Downloader(OkHttpClient client) {
        return new OkHttp3Downloader(client);
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application application, OkHttp3Downloader okHttp3Downloader) {
        return new Picasso.Builder(application).downloader(okHttp3Downloader).build();
    }
}
