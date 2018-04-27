package com.rain.photopicker;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.rain.library.PhotoPick;

/**
 * Describe:
 * Created by Rain on 2018/4/26.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        PhotoPick.init(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return context;
    }
}
