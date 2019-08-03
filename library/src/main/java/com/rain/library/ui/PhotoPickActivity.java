package com.rain.library.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rain.library.BaseActivity;
import com.rain.library.PhotoGalleryAdapter;
import com.rain.library.PhotoPick;
import com.rain.library.PhotoPickAdapter;
import com.rain.library.PhotoPickOptions;
import com.rain.library.R;
import com.rain.library.bean.MediaData;
import com.rain.library.bean.MediaDirectory;
import com.rain.library.bean.PhotoPickBean;
import com.rain.library.controller.PhotoPickConfig;
import com.rain.library.controller.PhotoPreviewConfig;
import com.rain.library.impl.CommonResult;
import com.rain.library.loader.MediaStoreHelper;
import com.rain.library.observer.UpdateUIObserver;
import com.rain.library.utils.MimeType;
import com.rain.library.utils.Rlog;
import com.rain.library.utils.UtilsHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Descriptions :照片选择器
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class PhotoPickActivity extends BaseActivity implements Observer {

    public static final String TAG = PhotoPickActivity.class.getSimpleName();

    //权限相关
    public static final int REQUEST_CODE_SDCARD = 100;             //读写权限请求码
    public static final int REQUEST_CODE_CAMERA = 200;             //拍照权限请求码

    public static final int REQUEST_CODE_SHOW_CAMERA = 0;// 拍照
    public static final int REQUEST_CODE_CLIP = 1;//裁剪头像

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoGalleryAdapter galleryAdapter;
    private PhotoPickAdapter adapter;
    private PhotoPickBean pickBean;

    private ArrayList<MediaData> photoList = new ArrayList<>();
    private ArrayList<MediaDirectory> photoDirectoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick, true);
        pickBean = PhotoPickConfig.getInstance();
        if (pickBean == null) {
            finish();
            return;
        }

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermission();
        else init();

        //获取全部媒体文件
        loadMediaData();
    }

    /**
     * 初始化控件
     */
    private void init() {
        //设置ToolBar
        toolbar.setTitle(MimeType.getTitle(pickBean.getMimeType(), this));
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
        toolbar.setNavigationIcon(PhotoPickOptions.DEFAULT.backIcon);

        //全部相册照片列表
        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, pickBean.getSpanCount()));
        adapter = new PhotoPickAdapter(this, pickBean);
        recyclerView.setAdapter(adapter);

        //相册列表
        RecyclerView gallery_rv = (RecyclerView) this.findViewById(R.id.gallery_rcl);
        gallery_rv.setLayoutManager(new LinearLayoutManager(this));
        galleryAdapter = new PhotoGalleryAdapter(this);
        gallery_rv.setAdapter(galleryAdapter);

        //当选择照片的时候更新toolbar的标题
        adapter.setOnUpdateListener(new PhotoPickAdapter.OnUpdateListener() {
            @Override
            public void updateToolBarTitle(String title) {
                menuItem.setTitle(title);
            }
        });

        //相册列表item选择的时候关闭slidingUpPanelLayout并更新照片adapter
        galleryAdapter.setOnItemClickListener(new PhotoGalleryAdapter.OnItemClickListener() {
            @Override
            public void onClick(ArrayList<MediaData> photos, int position) {
                if (adapter != null) {
                    PhotoPreviewConfig.setPreviewPhotos(photos);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    toolbar.setTitle(photoDirectoryList.get(position).getName());
                    adapter.refresh(photos);
                }
            }
        });

        slidingUpPanelLayout = (SlidingUpPanelLayout) this.findViewById(R.id.slidingUpPanelLayout);
        slidingUpPanelLayout.setAnchorPoint(0.5f);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        UpdateUIObserver.getInstance().addObserver(this);

    }

    /**
     * 获取媒体文件
     */
    private void loadMediaData() {
        MediaStoreHelper.getData(this, pickBean.getMimeType(), pickBean.isShowGif(), new MediaStoreHelper.PhotosResultCallback() {
            @Override
            public void onResultCallback(final List<MediaDirectory> directories) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
            }
        });
    }

    //请求权限(先检查)
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_SDCARD);
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
        if (requestCode == REQUEST_CODE_SDCARD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                PhotoPick.showDialog(PhotoPickActivity.this, R.string.permission_tip_SD).show();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adapter.selectPicFromCamera();
            } else {
                PhotoPick.showDialog(PhotoPickActivity.this, R.string.permission_tip_video).show();
            }
        }
    }


    private MenuItem menuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!pickBean.isClipPhoto()) {
            getMenuInflater().inflate(R.menu.menu_ok, menu);
            menuItem = menu.findItem(R.id.ok);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok) {
            Intent intent = new Intent();
            if (adapter != null && !adapter.getSelectPhotosInfo().isEmpty()) {
                if (pickBean.isStartCompression()) {
                    PhotoPick.startCompression(PhotoPickActivity.this, adapter.getSelectPhotos(), compressResult);
                } else {
                    //不做压缩处理 直接发送原图信息
                    if (adapter.getSelectPhotos().size() != 1) {
                        if (pickBean.getCallback() != null)
                            pickBean.getCallback().moreSelect(adapter.getSelectPhotosInfo());
                        else {
                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, adapter.getSelectPhotosInfo());
                            setResult(Activity.RESULT_OK, intent);
                        }
                    } else {
                        if (pickBean.getCallback() != null)
                            pickBean.getCallback().singleSelect(adapter.getSelectPhotosInfo());
                        else {
                            setResult(Activity.RESULT_OK, intent);
                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotosInfo());
                        }
                    }
                    finish();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int index = 0;

    private ArrayList<String> imageFilePath = new ArrayList<>();


    private CommonResult<File> compressResult = new CommonResult<File>() {
        @Override
        public void onSuccess(File file, boolean success) {
            if (success && file.exists()) {
                Rlog.e(TAG, "Luban compression success:" + file.getAbsolutePath() + " ; image length = " + file.length());
                adapter.getSelectPhotosInfo().get(imageFilePath.size()).setCompressionPath(file.getAbsolutePath());
                index++;
                if (index > 0 && index == adapter.getSelectPhotosInfo().size()) {
                    Rlog.e(TAG, "all select image compression success!");
                    Intent intent = new Intent();
                    if (adapter.getSelectPhotosInfo().size() != 1) {

                        if (pickBean.getCallback() != null)
                            pickBean.getCallback().moreSelect(adapter.getSelectPhotosInfo());
                        else
                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, adapter.getSelectPhotosInfo());
                    } else {
                        if (pickBean.getCallback() != null)
                            pickBean.getCallback().singleSelect(adapter.getSelectPhotosInfo());
                        else
                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotosInfo());
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            } else {
                index++;
                MediaData photo = adapter.getSelectPhotosInfo().get(index);
                photo.setCompressionPath(photo.getOriginalPath());
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

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_SHOW_CAMERA://相机
                findPhoto();
                break;
            case UCrop.REQUEST_CROP:    //裁剪
                findClipPhoto();
                break;
            case UCrop.RESULT_ERROR:
                Throwable cropError = UCrop.getError(data);
                break;
            case PhotoPreviewConfig.REQUEST_CODE:
                boolean isBackPressed = data.getBooleanExtra("isBackPressed", false);
                //如果上个activity不是按了返回键的，就是按了"发送"按钮
                if (!isBackPressed) {
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    //用户按了返回键，合并用户选择的图片集合
                    ArrayList<MediaData> photoLists = data.getParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
                    if (photoLists == null || photoLists.size() == 0) {
                        return;
                    }
                    //之前已经选了的图片
                    ArrayList<MediaData> selectedList = adapter.getSelectPhotosInfo();
                    //这是去图片预览界面需要删除的图片
                    List<MediaData> deleteList = new ArrayList<>();
                    for (MediaData mediaData : selectedList) {
                        if (!photoLists.contains(mediaData)) {
                            deleteList.add(mediaData);
                        }
                    }
                    //删除预览界面取消选择的图片
                    selectedList.removeAll(deleteList);
                    deleteList.clear();
                    //合并相同的数据
                    HashSet<MediaData> set = new HashSet<>(photoLists);
                    for (MediaData mediaData : selectedList) {
                        set.add(mediaData);
                    }
                    selectedList.clear();
                    selectedList.addAll(set);
                    adapter.notifyDataSetChanged();
                    menuItem.setTitle(adapter.getTitle());
                }
                break;
        }
    }

    private void findClipPhoto() {
        adapter.getSelectPhotosInfo().add(new MediaData(adapter.getClipImagePath(), 1));
        if (pickBean.getCallback() != null) {
            pickBean.getCallback().clipImage(adapter.getSelectPhotosInfo());
        } else {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO, adapter.getSelectPhotosInfo());
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }


    private void findPhoto() {
        if (adapter.getCameraUri() == null || TextUtils.isEmpty(adapter.getCameraImagePath())) {
            PhotoPick.toast(R.string.unable_find_pic);
        } else {
            if (pickBean.isClipPhoto()) {
                //拍完照之后，如果要启动裁剪，则去裁剪再把地址传回来
                adapter.startClipPic(adapter.getCameraImagePath());
            } else {
                if (pickBean.isStartCompression()) {
                    PhotoPick.startCompression(PhotoPickActivity.this, new ArrayList<>(Arrays.asList(adapter.getCameraImagePath())), new CommonResult<File>() {
                        @Override
                        public void onSuccess(File data, boolean success) {
                            MediaData photo = new MediaData();
                            if (success) {
                                photo.setCompressionPath(data.getAbsolutePath());
                                photo.setOriginalPath(adapter.getCameraImagePath());

                            } else {
                                photo.setCompressionPath(adapter.getCameraImagePath());
                                photo.setOriginalPath(adapter.getCameraImagePath());
                            }
                            adapter.getSelectPhotosInfo().add(photo);
                            if (pickBean.getCallback() != null) {
                                pickBean.getCallback().cameraImage(adapter.getSelectPhotosInfo());
                            } else {
                                Intent intent = new Intent();
                                intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotosInfo());
                                setResult(Activity.RESULT_OK, intent);
                            }
                            finish();
                        }
                    });
                } else {
                    adapter.getSelectPhotosInfo().add(new MediaData(adapter.getCameraImagePath(), 3));

                    if (pickBean.getCallback() != null) {
                        pickBean.getCallback().singleSelect(adapter.getSelectPhotosInfo());
                    } else {
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotosInfo());
                        setResult(Activity.RESULT_OK, intent);
                    }
                    finish();
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }

    @Override
    public void update(Observable observable, Object obj) {
        Rlog.e("update ui");
        UpdateUIObserver.NotifyCmd data = (UpdateUIObserver.NotifyCmd) obj;
        if (data.isChecked) {
            adapter.getSelectPhotos().add(data.mediaData.getOriginalPath());
        } else {
            adapter.getSelectPhotos().remove(data.mediaData.getOriginalPath());
        }
        adapter.notifyItemChanged(data.position);

    }
}
