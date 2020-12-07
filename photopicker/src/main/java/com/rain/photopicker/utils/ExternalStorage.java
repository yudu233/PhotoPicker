package com.rain.photopicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;


import com.rain.photopicker.PhotoPickOptions;

import java.io.File;
import java.io.IOException;

/**
 * package
 */
public class ExternalStorage {
    /**
     * 外部存储根目录
     */
    private String sdkStorageRoot = null;

    private static ExternalStorage instance;

    private static final String TAG = "ExternalStorage";

    private boolean hasPermission = true; // 是否拥有存储卡权限

    private Context context;

    private ExternalStorage() {

    }

    synchronized public static ExternalStorage getInstance() {
        if (instance == null) {
            instance = new ExternalStorage();
        }
        return instance;
    }

    public void init(Context context, String sdkStorageRoot) {
        this.context = context;
        // 判断权限
        hasPermission = checkPermission(context);

        if (!TextUtils.isEmpty(sdkStorageRoot)) {
            File dir = new File(sdkStorageRoot);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (dir.exists() && !dir.isFile()) {
                this.sdkStorageRoot = sdkStorageRoot;
                if (!sdkStorageRoot.endsWith("/")) {
                    this.sdkStorageRoot = sdkStorageRoot + "/";
                }
            }
        }

        if (TextUtils.isEmpty(this.sdkStorageRoot)) {
            loadStorageState(context);
        }

        createSubFolders();
    }

    private void loadStorageState(Context context) {
        String externalPath = Environment.getExternalStorageDirectory().getPath();
        this.sdkStorageRoot = externalPath + "/" + context.getPackageName() + "/";
    }

    private void createSubFolders() {
        boolean result = true;
        File root = new File(sdkStorageRoot);
        if (root.exists() && !root.isDirectory()) {
            root.delete();
        }
        result &= makeDirectory(PhotoPickOptions.DEFAULT.imagePath);
        if (result) {
            createNoMediaFile(sdkStorageRoot);
        }
    }

    /**
     * 创建目录
     *
     * @param path
     * @return
     */
    private boolean makeDirectory(String path) {
        File file = new File(path);
        boolean exist = file.exists();
        if (!exist) {
            exist = file.mkdirs();
        }
        return exist;
    }

    protected static String NO_MEDIA_FILE_NAME = ".nomedia";

    private void createNoMediaFile(String path) {
        File noMediaFile = new File(path + "/" + NO_MEDIA_FILE_NAME);
        try {
            if (!noMediaFile.exists()) {
                noMediaFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSdkStorageReady() {
        String externalRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (this.sdkStorageRoot.startsWith(externalRoot)) {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } else {
            return true;
        }
    }

    /**
     * 获取外置存储卡剩余空间
     *
     * @return
     */
    public long getAvailableExternalSize() {
        return getResidualSpace(sdkStorageRoot);
    }

    /**
     * 获取目录剩余空间
     *
     * @param directoryPath
     * @return
     */
    private long getResidualSpace(String directoryPath) {
        try {
            StatFs sf = new StatFs(directoryPath);
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            long availCountByte = availCount * blockSize;
            return availCountByte;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * SD卡存储权限检查
     */
    private boolean checkPermission(Context context) {
        if (context == null) {
            Log.e(TAG, "checkMPermission context null");
            return false;
        }

        // 写权限有了默认就赋予了读权限
        PackageManager pm = context.getPackageManager();
        if (PackageManager.PERMISSION_GRANTED !=
                pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getApplicationInfo().packageName)) {
            Log.e(TAG, "without permission to access storage");
            return false;
        }

        return true;
    }

    /**
     * 有效性检查
     */
    public boolean checkStorageValid() {
        if (hasPermission) {
            return true; // M以下版本&授权过的M版本不需要检查
        }

        hasPermission = checkPermission(context); // 检查是否已经获取权限了
        if (hasPermission) {
            Log.i(TAG, "get permission to access storage");

            // 已经重新获得权限，那么重新检查一遍初始化过程
            createSubFolders();
        }
        return hasPermission;
    }

    /**
     * 判断文件是否损坏
     *
     * @param width
     * @param media_path
     * @return
     */
    public boolean checkImageIsDamage(int width, String media_path) {
        if (width == 0) {
            BitmapFactory.Options options = null;
            if (options == null) options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(media_path, options); //filePath代表图片路径
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                //表示图片已损毁
                Rlog.e("表示图片已损毁:" + media_path);
                return true;
            }

        }
        return false;
    }
}
