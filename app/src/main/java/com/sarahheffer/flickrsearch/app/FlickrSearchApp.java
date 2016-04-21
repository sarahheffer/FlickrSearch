package com.sarahheffer.flickrsearch.app;

import android.app.Application;

import com.sarahheffer.flickrsearch.dagger.ApiModule;
import com.sarahheffer.flickrsearch.dagger.AppComponent;
import com.sarahheffer.flickrsearch.dagger.DaggerAppComponent;
import com.sarahheffer.flickrsearch.dagger.NetModule;
import com.sarahheffer.flickrsearch.interfaces.AppContext;

public class FlickrSearchApp extends Application implements AppContext {

    AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initModules();
    }

    private void initModules() {

        ApiModule apiModule = new ApiModule();
        NetModule netModule = new NetModule(this);

        appComponent = DaggerAppComponent.builder()
                .apiModule(apiModule)
                .netModule(netModule)
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
