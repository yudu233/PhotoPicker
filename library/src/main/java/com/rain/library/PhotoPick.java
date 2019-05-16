package com.rain.library;

import android.content.Context;

import com.rain.library.impl.CommonResult;
import com.rain.library.utils.ExternalStorage;
import com.rain.library.utils.Rlog;

import java.io.File;
import java.util.ArrayList;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public final class PhotoPick {

    private static Context mContext;

    public static void init(Context context) {
        init(context, PhotoPickOptions.DEFAULT);
    }

    public static void init(Context context, PhotoPickOptions options) {
        //说明已经初始化过了,不用重复初始化
        if (mContext != null) return;
        PhotoPickOptions.DEFAULT = options;
        ExternalStorage.getInstance().init(context, options.filePath);
        mContext = context.getApplicationContext();
    }

    public static int getToolbarBackGround() {
        return mContext.getResources().getColor(PhotoPickOptions.DEFAULT.photoPickThemeColor);
    }

    public static Context getContext() {
        return mContext;
    }

    public static void checkInit() {
        if (mContext == null) {
            throw new NullPointerException("photoLibrary was not initialized,please init in your Application");
        }
    }

    public static void startCompression(Context context, ArrayList<String> paths, final CommonResult result) {
        Luban.with(context)
                .load(paths)
                .setTargetDir(PhotoPickOptions.DEFAULT.imagePath)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Rlog.e("Rain", "Luban compression start");
                    }

                    @Override
                    public void onSuccess(File file) {
                        result.onSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Rlog.e("Rain", "onError:" + e.getMessage());

                    }
                }).launch();
    }
}
