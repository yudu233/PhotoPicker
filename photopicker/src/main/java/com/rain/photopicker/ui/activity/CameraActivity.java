package com.rain.photopicker.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rain.photopicker.PhotoPick;
import com.rain.photopicker.R;
import com.rain.photopicker.controller.CameraConfig;
import com.rain.photopicker.utils.PhotoPickerHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;

/**
 * @Author : Rain
 * @CreateDate : 2020/8/26 11:49
 * @Version : 1.0
 * @Descroption :
 */
public class CameraActivity extends BaseActivity {

    public static final int REQUEST_CODE_CAMERA = 200;             //拍照权限请求码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PhotoPickerHelper.REQUEST_CODE_CAMERA);
        } else {
            PhotoPickerHelper.startCamera(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PhotoPickerHelper.CAMERA_REQUEST_CODE:
                findCameraPhoto();
                break;
            case UCrop.REQUEST_CROP:    //裁剪
                PhotoPickerHelper.sendClipImage(this, CameraConfig.photoPickBean);
                finish();
                break;
        }
    }

    private void findCameraPhoto() {
        if (PhotoPickerHelper.getCameraUri() == null ||
                !new File(PhotoPickerHelper.getCameraImagePath()).exists()) {
            PhotoPick.toast(R.string.unable_find_pic);
        } else {
            if (CameraConfig.photoPickBean.isClipPhoto()) {
                //裁剪照片
                PhotoPickerHelper.startClipPic(this,
                        CameraConfig.photoPickBean, PhotoPickerHelper.getCameraImagePath());
            } else {
                if (CameraConfig.photoPickBean.isStartCompression()) {
                    //压缩
                    PhotoPickerHelper.startCompress(this, CameraConfig.photoPickBean);
                } else {
                    //发送相机图片
                    PhotoPickerHelper.sendCameraImage(this, CameraConfig.photoPickBean, PhotoPickerHelper.getCameraImagePath());
                }
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PhotoPickerHelper.startCamera(this);
            } else {
                PhotoPick.showDialog(this, R.string.permission_tip_video).show();
            }
        }
    }
}
