package com.rain.photopicker;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.rain.crow.PhotoPickOptions;

import java.io.File;
import java.io.IOException;

/**
 * @author:duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2019/5/10 14:44
 * @filename : PhotoPickOptionsConfig
 * @describe :
 */
public class PhotoPickOptionsConfig {

    public static PhotoPickOptions getPhotoPickOptions(Context context) {
        PhotoPickOptions options = new PhotoPickOptions();
        options.filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "eWorld/";
        options.imagePath = options.filePath +  "cytx/";
        options.photoPickAuthority = context.getString(R.string.file_provider_authorities);
        options.photoPickThemeColor = R.color.colorAccent;
        return options;
    }

    /**
     * 配置 APP 保存图片数据的目录
     */
    public static String getAppCacheDir(Context context) {
        String storageRootPath = null;
        try {
            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
            if (context.getExternalCacheDir() != null) {
                storageRootPath = context.getExternalCacheDir().getCanonicalPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(storageRootPath)) {
            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
            storageRootPath = Environment.getExternalStorageDirectory() + "/" + context.getPackageName();
        }

        return storageRootPath;
    }

}
