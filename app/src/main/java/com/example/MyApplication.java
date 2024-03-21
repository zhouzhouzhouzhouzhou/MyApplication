package com.example;

import android.app.Application;

import com.example.log.Logger;
import com.example.log.NLog;

public class MyApplication extends Application implements IApplication {
    private static final String TAG = "MyApplication";
    @Override
    public Application getApp() {
        return this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NLog.i(TAG, "onCreate: ");
        WgApp.init(this);
        NLog.setDebug(true, Logger.VERBOSE);

    }
}
