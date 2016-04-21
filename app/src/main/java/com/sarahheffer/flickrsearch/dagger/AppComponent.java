package com.sarahheffer.flickrsearch.dagger;

import com.sarahheffer.flickrsearch.activities.MainActivity;
import com.sarahheffer.flickrsearch.adapters.RecyclerGridAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApiModule.class, NetModule.class})
public interface AppComponent {

    void inject(MainActivity activity);
    void inject(RecyclerGridAdapter adapter);

}