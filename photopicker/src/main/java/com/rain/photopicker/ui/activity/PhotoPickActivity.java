package com.rain.photopicker.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rain.photopicker.PhotoPick;
import com.rain.photopicker.PhotoPickOptions;
import com.rain.photopicker.R;
import com.rain.photopicker.bean.MediaData;
import com.rain.photopicker.bean.MediaDirectory;
import com.rain.photopicker.controller.CameraConfig;
import com.rain.photopicker.controller.PhotoPickConfig;
import com.rain.photopicker.controller.PhotoPreviewConfig;
import com.rain.photopicker.impl.CommonResult;
import com.rain.photopicker.loader.MediaStoreHelper;
import com.rain.photopicker.observer.UpdateUIObserver;
import com.rain.photopicker.ui.adapter.PhotoGalleryAdapter;
import com.rain.photopicker.ui.adapter.PhotoPickAdapter;
import com.rain.photopicker.utils.MimeType;
import com.rain.photopicker.utils.PhotoPickerHelper;
import com.rain.photopicker.utils.Rlog;
import com.rain.photopicker.utils.UtilsHelper;
import com.rain.photopicker.weidget.LoadingDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Descriptions :照片选择器
 * GitHub : https://github.com/yudu233
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class PhotoPickActivity extends BaseActivity implements Observer {

    public static final String TAG = PhotoPickActivity.class.getSimpleName();

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoGalleryAdapter galleryAdapter;
    private PhotoPickAdapter adapter;

    private ArrayList<MediaData> photoList = new ArrayList<>();
    private ArrayList<MediaDirectory> photoDirectoryList = new ArrayList<>();
    private LoadingDialog loadingDialog;
    private MediaStoreHelper mediaStoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick, true);
        if (PhotoPickConfig.getInstance() == null) {
            finish();
            return;
        }

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermission();
        else init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        mediaStoreHelper = new MediaStoreHelper();
        //获取全部媒体文件
        loadMediaData();

        loadingDialog = new LoadingDialog(this);

        //设置ToolBar
        toolbar.setTitle(MimeType.getTitle(PhotoPickConfig.getInstance().getMimeType(), this));
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
        toolbar.setNavigationIcon(PhotoPickOptions.DEFAULT.backIcon);

        //全部相册照片列表
        RecyclerView recyclerView = this.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, PhotoPickConfig.getInstance().getSpanCount()));
        adapter = new PhotoPickAdapter(this, PhotoPickConfig.getInstance());
        recyclerView.setAdapter(adapter);

        //相册列表
        RecyclerView gallery_rv = this.findViewById(R.id.gallery_rcl);
        gallery_rv.setLayoutManager(new LinearLayoutManager(this));
        galleryAdapter = new PhotoGalleryAdapter(this);
        gallery_rv.setAdapter(galleryAdapter);

        //当选择照片的时候更新toolbar的标题
        adapter.setOnUpdateListener(title -> menuItem.setTitle(title));

        //相册列表item选择的时候关闭slidingUpPanelLayout并更新照片adapter
        galleryAdapter.setOnItemClickListener((photos, position) -> {
            if (adapter != null) {
                PhotoPreviewConfig.setPreviewPhotos(photos);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                toolbar.setTitle(photoDirectoryList.get(position).getName());
                adapter.refresh(photos);
            }
        });

        slidingUpPanelLayout = this.findViewById(R.id.slidingUpPanelLayout);
        slidingUpPanelLayout.setAnchorPoint(0.5f);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(v -> slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));
        UpdateUIObserver.getInstance().addObserver(this);
    }

    /**
     * 获取媒体文件
     */
    private void loadMediaData() {
        mediaStoreHelper.getData(this, PhotoPickConfig.getInstance().getMimeType(),
                PhotoPickConfig.getInstance().isShowGif(), directories -> runOnUiThread(() -> {
                    List<MediaData> photos = directories.get(0).getMediaData();
                    for (int i = 0; i < photos.size(); i++) {
                        if (UtilsHelper.isFileExist(photos.get(i).getOriginalPath()))
                            photoList.add(photos.get(i));
                    }
                    photoDirectoryList.add(directories.get(0));
                    for (int i = 1; i < directories.size(); i++) {
                        if (UtilsHelper.isFileExist(directories.get(i).getDirPath())) {
                            photoDirectoryList.add(directories.get(i));
                        }
                    }
                    PhotoPreviewConfig.setPreviewPhotos(photoList);
                    adapter.refresh(photoList);
                    galleryAdapter.refresh(photoDirectoryList);
                }));
    }

    //请求权限(先检查)
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PhotoPickerHelper.REQUEST_CODE_SDCARD);
        } else {
            init();
        }
    }


    /**
     * 权限申请回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PhotoPickerHelper.REQUEST_CODE_SDCARD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                PhotoPick.showDialog(PhotoPickActivity.this, R.string.permission_tip_SD).show();
            }
        } else if (requestCode == PhotoPickerHelper.REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PhotoPickerHelper.startCamera(this);
            } else {
                PhotoPick.showDialog(PhotoPickActivity.this, R.string.permission_tip_video).show();
            }
        }
    }

    private MenuItem menuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!PhotoPickConfig.getInstance().isClipPhoto()) {
            getMenuInflater().inflate(R.menu.menu_ok, menu);
            menuItem = menu.findItem(R.id.ok);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (PhotoPick.isTimeEnabled()) {
            if (item.getItemId() == R.id.ok) {
                index = 0;
                if (adapter != null && !adapter.getSelectPhotosInfo().isEmpty()) {
                    PhotoPickerHelper.checkImages(adapter.getSelectPhotosInfo());
                    MediaData mediaData = adapter.getSelectPhotosInfo().get(0);
                    if (PhotoPickConfig.getInstance().isStartCompression() &&
                            !MimeType.isVideo(mediaData.getImageType())) {
                        if (loadingDialog != null) {
                            loadingDialog.show();
                        }
                        PhotoPick.startCompression(PhotoPickActivity.this, adapter.getSelectPhotosInfo(), compressResult);
                    } else {
                        //不做压缩处理 直接发送原图信息
                        sendImages();
                    }
                } else {
                    PhotoPick.toast(getString(R.string.tips_no));
                    finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private int index = 0;

    private CommonResult<File> compressResult = new CommonResult<File>() {
        @Override
        public void onSuccess(File file, boolean success) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            if (success && file.exists()) {
                Rlog.d(TAG, "Luban compression success:" + file.getAbsolutePath() + " ; image length = " + file.length());
                MediaData mediaData = adapter.getSelectPhotosInfo().get(index);
                mediaData.setCompressionPath(file.getAbsolutePath());
                mediaData.setCompressed(true);
                index++;
                if (index > 0 && index == adapter.getSelectPhotosInfo().size()) {
                    Rlog.d(TAG, "all select image compression success!");
                    sendImages();
                }
            } else {
                MediaData photo = adapter.getSelectPhotosInfo().get(index);
                photo.setCompressed(true);
                photo.setCompressionPath(photo.getOriginalPath());
                index++;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PhotoPickerHelper.CAMERA_REQUEST_CODE://相机
                findPhoto();
                break;
            case UCrop.REQUEST_CROP:    //裁剪
                PhotoPickerHelper.sendClipImage(this,PhotoPickConfig.getInstance());
                finish();
                break;
            case UCrop.RESULT_ERROR:
                Throwable cropError = UCrop.getError(data);
                PhotoPick.toast(cropError.getMessage());
                break;
            case PhotoPreviewConfig.REQUEST_CODE:
                setResult(Activity.RESULT_OK, data);
                finish();
                break;
        }
    }

    private void findPhoto() {
        if (PhotoPickerHelper.getCameraUri() == null || TextUtils.isEmpty(PhotoPickerHelper.getCameraImagePath())) {
            PhotoPick.toast(R.string.unable_find_pic);
        } else {
            if (PhotoPickConfig.getInstance().isClipPhoto()) {
                //裁剪照片
                PhotoPickerHelper.startClipPic(this,
                        CameraConfig.photoPickBean, PhotoPickerHelper.getCameraImagePath());
            } else {
                if (PhotoPickConfig.getInstance().isStartCompression()) {
                    //压缩
                    PhotoPickerHelper.startCompress(this,PhotoPickConfig.getInstance());
                } else {

                    PhotoPickerHelper.sendCameraImage(this,PhotoPickConfig.getInstance(),PhotoPickerHelper.getCameraImagePath());
                }
            }
        }
    }


    private void sendImages() {
        PhotoPickerHelper.sendImages(this,PhotoPickConfig.getInstance(),adapter.getSelectPhotosInfo());
        finish();
    }

    @Override
    public void update(Observable observable, Object obj) {
        if (!isFinishing()) {
            UpdateUIObserver.NotifyCmd data = (UpdateUIObserver.NotifyCmd) obj;
            if (data.isChecked) {
                adapter.getSelectPhotosInfo().add(data.mediaData);
            } else {
                adapter.getSelectPhotosInfo().remove(data.mediaData);
            }
            adapter.notifyItemChanged(data.position);
            toolbar.getMenu().findItem(R.id.ok).setTitle(adapter.getTitle());
            adapter.notifyPreviewConfig(data.isSelectOrigin);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }

}
