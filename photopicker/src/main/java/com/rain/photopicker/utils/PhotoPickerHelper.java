package com.rain.photopicker.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.rain.photopicker.PhotoPick;
import com.rain.photopicker.PhotoPickOptions;
import com.rain.photopicker.R;
import com.rain.photopicker.bean.MediaData;
import com.rain.photopicker.bean.PhotoPickBean;
import com.rain.photopicker.controller.PhotoPickConfig;
import com.rain.photopicker.impl.CommonResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * @Author : Rain
 * @CreateDate : 2020/8/26 11:54
 * @Version : 1.0
 * @Descroption :
 */
public class PhotoPickerHelper {

    //相机图片Uri
    private static Uri cameraUri;

    //相机图片路径
    private static String cameraImagePath;

    //裁剪图片路径
    private static String clipImagePath;

    //权限相关
    public static final int REQUEST_CODE_SDCARD = 100;             //读写权限请求码
    public static final int REQUEST_CODE_CAMERA = 200;             //拍照权限请求码


    public static final int CAMERA_REQUEST_CODE = 10001;


    /**
     * 开启拍照
     *
     * @param context
     */
    public static void startCamera(Activity context) {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }

        //保存到自定义目录
        String cameraImageName = "camera_" + (System.currentTimeMillis() / 1000) + ".jpg";
        File imageFile = new File(PhotoPickOptions.DEFAULT.imagePath, cameraImageName);
        cameraImagePath = imageFile.getAbsolutePath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            cameraUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", imageFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件,私有目录读写权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        context.startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }


    /**
     * 裁剪图片
     *
     * @param picPath
     */
    public static void startClipPic(AppCompatActivity activity, PhotoPickBean photoPickBean, String picPath) {
        String clipImageName = "clip_" + (System.currentTimeMillis() / 1000) + ".jpg";
        File clipImage = new File(PhotoPickOptions.DEFAULT.imagePath, clipImageName);
        clipImagePath = clipImage.getAbsolutePath();
        UCropUtils.start(activity, new File(picPath), clipImage, photoPickBean.getClipMode());
    }

    /**
     * 压缩并发送
     *
     * @param context
     */
    public static void startCompress(Activity context, PhotoPickBean pickBean) {
        MediaData photo = new MediaData();
        photo.setCamera(true);
        photo.setCameraImagePath(PhotoPickerHelper.getCameraImagePath());
        photo.setImageType(MimeType.createImageType(PhotoPickerHelper.getCameraImagePath()));
        photo.setMimeType(MimeType.TYPE_IMAGE);
        PhotoPick.startCompression(context,
                new ArrayList<>(Arrays.asList(photo)), (CommonResult<File>) (data, success) -> {
                    photo.setCompressed(success ? true : false);
                    photo.setCompressionPath(success ? data.getAbsolutePath() :
                            PhotoPickerHelper.getCameraImagePath());
                    sendImages(context, pickBean, new ArrayList<>(Arrays.asList(photo)));
                });
    }


    /**
     * 检测图片是否存在/损坏
     */
    public static void checkImages(ArrayList<MediaData> data) {
        ListIterator<MediaData> iterator = data.listIterator();
        while (iterator.hasNext()) {
            String mediaPath;
            MediaData media = iterator.next();
            if (media.isClip()) {
                mediaPath = media.getClipImagePath();
            } else if (media.isCamera()) {
                mediaPath = media.getCameraImagePath();
            } else if (media.isCompressed()) {
                mediaPath = media.getCompressionPath();
            } else {
                mediaPath = media.getOriginalPath();
            }

            if (!new File(mediaPath).exists()) {
                iterator.remove();
            }
        }
    }

    /**
     * 发送图片
     */
    public static void sendImages(Activity context, PhotoPickBean pickBean, ArrayList<MediaData> data) {
        if (!pickBean.isStartCompression()) {
            PhotoPickerHelper.checkImages(data);
        }

        if (pickBean.getCallback() != null) {
            pickBean.getCallback().selectResult(data);
        } else {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SELECT_PHOTOS, data);
            context.setResult(Activity.RESULT_OK, intent);
        }
        context.finish();
    }

    /**
     * 发送相机拍摄照片 无压缩裁剪
     *
     * @param context
     * @param cameraImagePath
     */
    public static void sendCameraImage(Activity context, PhotoPickBean pickBean, String cameraImagePath) {
        MediaData mediaData = new MediaData();
        mediaData.setCamera(true);
        mediaData.setMimeType(MimeType.TYPE_IMAGE);
        mediaData.setImageType(MimeType.createImageType(cameraImagePath));
        mediaData.setCameraImagePath(cameraImagePath);
        sendImages(context, pickBean, new ArrayList<>(Arrays.asList(mediaData)));
    }

    /**
     * 发送裁剪照片
     *
     * @param context
     */
    public static void sendClipImage(Activity context, PhotoPickBean pickBean) {
        MediaData mediaData = new MediaData();
        mediaData.setClip(true);
        mediaData.setMimeType(MimeType.TYPE_IMAGE);
        mediaData.setImageType(MimeType.createImageType(getClipImagePath()));
        mediaData.setClipImagePath(getClipImagePath());
        sendImages(context, pickBean, new ArrayList<>(Arrays.asList(mediaData)));
    }


    public static Uri getCameraUri() {
        return cameraUri;
    }

    public static String getCameraImagePath() {
        return cameraImagePath;
    }

    public static String getClipImagePath() {
        return clipImagePath;
    }
}
