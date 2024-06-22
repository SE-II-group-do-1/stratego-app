package com.example.stratego_app;

import android.app.Application;
import android.content.Context;

public class Stratego extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Stratego.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Stratego.context;
    }
}
