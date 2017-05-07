package rain.coder.photopicker.utils;

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

import rain.coder.library.R;

/**
 * Describe :
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";


    /**
     * 保存图片的路径
     *
     * @return
     */
    public static String getImagePath(Context context, String dir) {
        String path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String appName = context.getResources().getString(R.string.app_name);
            path = Environment.getExternalStorageDirectory() + "/" + appName + dir;
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
     * 创建图片文件
     *
     * @return
     */
    public static String createFile() {
        String fileName = UtilsHelper.getNowTime() + ".png";
        return fileName;
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
