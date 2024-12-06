package com.example.vanner;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("loginperfilimg")
                .setApplicationId("1:1030729774309:android:f989dcbcc3c39bb40e500c")
                .setApiKey("AIzaSyAgfeveBQYjthGmRU8S6QacpH4LHDOxaUg")
                .setStorageBucket("loginperfilimg.appspot.com")
                .build();

        FirebaseApp.initializeApp(this, options, "proyectoStorage");
    }
}

