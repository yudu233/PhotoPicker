package com.rain.library.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Describe :
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    private static File filesDir = null;
    private static File imageFile = null;
    private static String imagePath;

    /**
     * 保存图片的路径
     *
     * @return
     */
    public static String getImagePath(Context context, String dir) {
        String path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String packageName = context.getPackageName();
            path = Environment.getExternalStorageDirectory() + "/" + packageName + dir;
        }
        if (TextUtils.isEmpty(path))
            context.getCacheDir().getPath();
        existsFolder(path);
        return path;
    }

    /**
     * 判断文件夹是否存在,不存在则创建
     *
     * @param path
     */
    public static void existsFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建保存的图片文件
     *
     * @param context
     * @param dir
     * @return
     * @throws IOException
     */
    public static File createImageFile(Context context, String dir) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //文件目录
            filesDir = context.getExternalFilesDir(dir).getAbsoluteFile();
        }else {
            filesDir = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + dir);
        }
        if (!filesDir.exists()) filesDir.mkdirs();
        imageFile = new File(filesDir, createFileName());
        //image = File.createTempFile(createFileName(), ".png", fileDir);
        setImagePath(imageFile.getAbsolutePath());
        return imageFile;
    }

    /**
     * 设置创建图片的路径
     *
     * @param path
     * @return
     */
    private static String setImagePath(String path) {
        imagePath = path;
        return imagePath;
    }

    /**
     * 获取图片路径
     * @return
     */
    public static String getImagePath() {
        return imagePath;
    }

    /**
     * 创建图片文件
     *
     * @return
     */
    public static String createFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date()) + ".png";
    }

    /**
     * 保存图片到本地并更新图库
     *
     * @param filePath
     * @param bmp
     * @param context
     * @return
     */
    public static boolean saveImageToGallery(String filePath, Bitmap bmp, Context context) {
        // 首先保存图片
        if (bmp == null)
            return false;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete(); // 删除原图片
        }
        String dir;
        if (!file.isFile()) {
            dir = filePath.substring(0, filePath.lastIndexOf("/"));
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    return false;
                }
            }
            FileOutputStream fOut = null;
            boolean isSuccess = false;
            try {
                file.createNewFile();
                fOut = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                // 最后通知图库更新
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                context.sendBroadcast(intent);
                isSuccess = true;
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                if (fOut != null) {
                    try {
                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return isSuccess;
        }
        return false;
    }

    /**
     * Uri转Bitmap
     *
     * @param uri
     * @param context
     * @return
     * @throws IOException
     */
    public static Bitmap UriToBitmap(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return bitmap;
    }

    /**
     * 按比例压缩图片
     *
     * @param path
     * @param w
     * @param h
     * @return
     */
    public static Bitmap getBitmap(String path, int w, int h) {
        Bitmap bit = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // opts.inJustDecodeBounds = true
        // ,设置该属性为true，不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你，
        // 这样就不会占用太多的内存，也就不会那么频繁的发生OOM了
        opts.inJustDecodeBounds = true;
        bit = BitmapFactory.decodeFile(path, opts);
        int test = 1;
        if (opts.outWidth > opts.outHeight) {
            if (opts.outWidth >= w)
                test = opts.outWidth / w;
            opts.inSampleSize = test; // 限制处理后的图片最大宽为w*2
        } else {
            if (opts.outHeight >= h)
                test = opts.outHeight / h;
            opts.inSampleSize = test; // 限制处理后的图片最大高为h*2
        }
        opts.inJustDecodeBounds = false;
        bit = BitmapFactory.decodeFile(path, opts);
        return bit;
    }
}

/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */