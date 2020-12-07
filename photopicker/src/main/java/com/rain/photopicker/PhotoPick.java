package com.rain.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


import com.rain.photopicker.bean.MediaData;
import com.rain.photopicker.controller.CameraConfig;
import com.rain.photopicker.controller.PhotoPickConfig;
import com.rain.photopicker.impl.CommonResult;
import com.rain.photopicker.utils.ExternalStorage;
import com.rain.photopicker.utils.Rlog;
import com.rain.photopicker.utils.UtilsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public static PhotoPickConfig.Builder from(Activity activity) {
        return new PhotoPickConfig.Builder(activity);
    }

    public static CameraConfig.Builder useCamera(Activity activity){
        return new CameraConfig.Builder(activity);
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

    public static void startCompression(Context context, ArrayList<MediaData> mediaData, final CommonResult result) {
        Rlog.d("compression image size is " + mediaData.size());
        List<String> paths = new ArrayList<>();
        for (MediaData data : mediaData) {
            if (data.isCamera()) {
                paths.add(data.getCameraImagePath());
            } else if (data.isClip()) {
                paths.add(data.getClipImagePath());
            } else {
                paths.add(data.getOriginalPath());
            }
        }
        ExternalStorage.getInstance().checkStorageValid();
        Luban.with(context)
                .load(paths)
                .setTargetDir(PhotoPickOptions.DEFAULT.imagePath)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Rlog.d("Luban compression start");
                    }

                    @Override
                    public void onSuccess(File file) {
                        result.onSuccess(file, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        result.onSuccess(null, false);
                        Rlog.d("onError:" + e.getMessage());

                    }
                }).launch();
    }

    public static AlertDialog.Builder showDialog(final Activity activity, int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(UtilsHelper.getString(R.string.permission_tip_title));
        builder.setMessage(UtilsHelper.getString(resId));
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> activity.finish());
        builder.setPositiveButton(R.string.settings, (dialogInterface, i) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        });
        builder.setCancelable(false);
        return builder;
    }

    public static void toast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private static long lastClickTime;
    private final static long TIME = 800;

    public static boolean isTimeEnabled() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime > TIME) {
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }


}
