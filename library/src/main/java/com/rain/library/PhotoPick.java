package com.rain.library;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.rain.library.bean.MediaData;
import com.rain.library.impl.CommonResult;
import com.rain.library.utils.ExternalStorage;
import com.rain.library.utils.MimeType;
import com.rain.library.utils.Rlog;
import com.rain.library.utils.UtilsHelper;

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
        List<String> paths = new ArrayList<>();
        for (MediaData data : mediaData) {
            if (data.isCamera()) {
                paths.add(data.getCameraImagePath());
            } else if (data.isClip()) {
                paths.add(data.getClipImagePath());
            } else{
                paths.add(data.getOriginalPath());
            }
        }

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
                        result.onSuccess(file, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        result.onSuccess(null, false);
                        Rlog.e("Rain", "onError:" + e.getMessage());

                    }
                }).launch();
    }

    public static AlertDialog.Builder showDialog(final Activity activity, int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(UtilsHelper.getString(R.string.permission_tip_title));
        builder.setMessage(UtilsHelper.getString(resId));
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            }
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
