package com.example.vanner;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar el proyecto principal (proyectoA)
        FirebaseApp.initializeApp(this);

        // Configurar y registrar el segundo proyecto (proyectoStorage)
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("loginperfilimg")  // project_id de proyectoStorage
                .setApplicationId("1:1030729774309:android:f989dcbcc3c39bb40e500c") // mobilesdk_app_id de proyectoStorage
                .setApiKey("AIzaSyAgfeveBQYjthGmRU8S6QacpH4LHDOxaUg") // current_key de proyectoStorage
                .setStorageBucket("loginperfilimg.appspot.com") // storage_bucket de proyectoStorage
                .build();

        FirebaseApp.initializeApp(this, options, "proyectoStorage");
    }
}

