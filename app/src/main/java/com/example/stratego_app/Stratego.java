package com.example.stratego_app;

import android.app.Application;
import android.content.Context;

public class Stratego extends Application {
    private static Stratego instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Stratego getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Application instance not initialized yet.");
        }
        return instance;
    }

    public Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static void setInstance(Stratego instance) {
        Stratego.instance = instance;
    }
}
