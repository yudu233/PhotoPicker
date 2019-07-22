package com.rain.library.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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


/**
 * Descriptions :照片选择器
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class PhotoPickActivity extends BaseActivity {

    //权限相关
    public static final int REQUEST_CODE_SDCARD = 100;             //读写权限请求码
    public static final int REQUEST_CODE_CAMERA = 200;             //拍照权限请求码

    public static final int REQUEST_CODE_SHOW_CAMERA = 0;// 拍照
    public static final int REQUEST_CODE_CLIP = 1;//裁剪头像

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PhotoGalleryAdapter galleryAdapter;
    private PhotoPickAdapter adapter;
    private PhotoPickBean pickBean;
    private Uri cameraUri;


    private ArrayList<MediaData> photoList = new ArrayList<>();
    private ArrayList<MediaDirectory> photoDirectoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick, true);

//        Bundle bundle = getIntent().getBundleExtra(PhotoPickConfig.EXTRA_PICK_BUNDLE);
//        if (bundle == null) {
//            throw new NullPointerException("bundle is null,please init it");
//        }
//        pickBean = bundle.getParcelable(PhotoPickConfig.EXTRA_PICK_BEAN);
        pickBean = PhotoPickConfig.getInstance();
        if (pickBean == null) {
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
                toolbar.setTitle(title);
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

        //获取全部媒体文件
        MediaStoreHelper.getData(this, pickBean.getMimeType(), true, new MediaStoreHelper.PhotosResultCallback() {
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


    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_SDCARD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage(getString(R.string.permission_tip_SD));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adapter.selectPicFromCamera();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示");
                builder.setMessage(getString(R.string.permission_tip_video));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!pickBean.isClipPhoto()) {
            getMenuInflater().inflate(R.menu.menu_ok, menu);
        }
        return true;
    }

    private ArrayList<String> imageFilePath = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok) {
            final Intent intent = new Intent();
            if (adapter != null && !adapter.getSelectPhotosInfo().isEmpty()) {

                if (pickBean.isStartCompression()) {
                    PhotoPick.startCompression(PhotoPickActivity.this, adapter.getSelectPhotos(), new CommonResult<File>() {
                        @Override
                        public void onSuccess(File file) {
                            if (file.exists()) {
                                Rlog.e("Rain", "Luban compression success:" + file.getAbsolutePath() + " ; image length = " + file.length());
                                adapter.getSelectPhotosInfo().get(imageFilePath.size()).setCompressionPath(file.getAbsolutePath());
                                imageFilePath.add(file.getAbsolutePath());
                                if (imageFilePath != null && imageFilePath.size() > 0 && imageFilePath.size() == adapter.getSelectPhotos().size()) {
                                    Rlog.e("Rain", "all select image compression success!");
                                    if (adapter.getSelectPhotos().size() != 1) {
                                        if (pickBean.getCallback() != null)
                                            pickBean.getCallback().moreSelect(adapter.getSelectPhotosInfo());
                                        else
                                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, adapter.getSelectPhotosInfo());
                                        // intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, imageFilePath);
                                    } else {
                                        if (pickBean.getCallback() != null)
                                            pickBean.getCallback().singleSelect(adapter.getSelectPhotosInfo());
                                        else
                                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotosInfo());
                                        //intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, imageFilePath.get(0));
                                    }
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            }
                        }
                    });

                } else {
                    //不做压缩处理 直接发送原图信息
                    if (adapter.getSelectPhotos().size() != 1) {
                        if (pickBean.getCallback() != null)
                            pickBean.getCallback().moreSelect(adapter.getSelectPhotosInfo());
                        else
                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, adapter.getSelectPhotosInfo());
                        // intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, imageFilePath);
                    } else {
                        if (pickBean.getCallback() != null)
                            pickBean.getCallback().singleSelect(adapter.getSelectPhotosInfo());
                        else
                            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, adapter.getSelectPhotosInfo());
                        //intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, imageFilePath.get(0));
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                if (!isBackPressed) {//如果上个activity不是按了返回键的，就是按了"发送"按钮
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {//用户按了返回键，合并用户选择的图片集合
                    ArrayList<String> photoLists = data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
                    if (photoLists == null || photoLists.size() == 0) {
                        return;
                    }
                    ArrayList<String> selectedList = adapter.getSelectPhotos();//之前已经选了的图片
                    List<String> deleteList = new ArrayList<>();//这是去图片预览界面需要删除的图片
                    for (String s : selectedList) {
                        if (!photoLists.contains(s)) {
                            deleteList.add(s);
                        }
                    }
                    selectedList.removeAll(deleteList);//删除预览界面取消选择的图片
                    deleteList.clear();
                    //合并相同的数据
                    HashSet<String> set = new HashSet<>(photoLists);
                    for (String s : selectedList) {
                        set.add(s);
                    }
                    selectedList.clear();
                    selectedList.addAll(set);
                    toolbar.setTitle(adapter.getTitle());
                    adapter.notifyDataSetChanged();
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
        // String filePath = UtilsHelper.getRealPathFromURI(imageUri, this);
        if (adapter.getCameraUri() == null || TextUtils.isEmpty(adapter.getCameraImagePath())) {
            Toast.makeText(this, R.string.unable_find_pic, Toast.LENGTH_LONG).show();
        } else {
            if (pickBean.isClipPhoto()) {//拍完照之后，如果要启动裁剪，则去裁剪再把地址传回来
                adapter.startClipPic(adapter.getCameraImagePath());
            } else {
                if (pickBean.isStartCompression()) {
                    PhotoPick.startCompression(PhotoPickActivity.this, new ArrayList<>(Arrays.asList(adapter.getCameraImagePath())), new CommonResult<File>() {
                        @Override
                        public void onSuccess(File data) {
                            MediaData photo = new MediaData();
                            photo.setCompressionPath(data.getAbsolutePath());
                            photo.setOriginalPath(adapter.getCameraImagePath());
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

}
