package rain.coder.photopicker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.ucrop.UCrop;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rain.coder.library.R;
import rain.coder.photopicker.BaseActivity;
import rain.coder.photopicker.PhotoPick;
import rain.coder.photopicker.adapter.PhotoGalleryAdapter;
import rain.coder.photopicker.adapter.PhotoPickAdapter;
import rain.coder.photopicker.bean.Photo;
import rain.coder.photopicker.bean.PhotoDirectory;
import rain.coder.photopicker.bean.PhotoPickBean;
import rain.coder.photopicker.controller.PhotoPickConfig;
import rain.coder.photopicker.controller.PhotoPreviewConfig;
import rain.coder.photopicker.loader.MediaStoreHelper;
import rain.coder.photopicker.utils.PermissionHelper;
import rain.coder.photopicker.utils.UtilsHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick);

        Bundle bundle = getIntent().getBundleExtra(PhotoPickConfig.EXTRA_PICK_BUNDLE);
        if (bundle == null) {
            throw new NullPointerException("bundle is null,please init it");
        }
        pickBean = bundle.getParcelable(PhotoPickConfig.EXTRA_PICK_BEAN);
        if (pickBean == null) {
            finish();
            return;
        }

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermission();
        else init();
    }

    private void init() {
        //设置ToolBar
        toolbar.setTitle(R.string.select_photo);
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());

        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, pickBean.getSpanCount()));
        adapter = new PhotoPickAdapter(this, pickBean);
        recyclerView.setAdapter(adapter);

        RecyclerView gallery_rv = (RecyclerView) this.findViewById(R.id.gallery_rcl);
        gallery_rv.setLayoutManager(new LinearLayoutManager(this));
        galleryAdapter = new PhotoGalleryAdapter(this);
        gallery_rv.setAdapter(galleryAdapter);

        adapter.setOnUpdateListener(new PhotoPickAdapter.OnUpdateListener() {
            @Override
            public void updateToolBarTitle(String title) {
                toolbar.setTitle(title);
            }
        });

        galleryAdapter.setOnItemClickListener(new PhotoGalleryAdapter.OnItemClickListener() {
            @Override
            public void onClick(List<Photo> photos) {
                if (adapter != null) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    adapter.refresh(photos);
                }
            }
        });

        MediaStoreHelper.getPhotoDirs(this, new MediaStoreHelper.PhotosResultCallback() {
            @Override
            public void onResultCallback(final List<PhotoDirectory> directories) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refresh(directories.get(0).getPhotos());
                        galleryAdapter.refresh(directories);
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
        if (!PermissionHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            PermissionHelper.requestPermission(this, REQUEST_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        else init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //读写权限请求成功
    @PermissionGrant(REQUEST_CODE_SDCARD)
    public void requestSdcardSuccess() {
        init();
    }

    //读写权限请求失败
    @PermissionDenied(REQUEST_CODE_SDCARD)
    public void requestSdcardFailed() {
        PermissionHelper.showSystemSettingDialog(this, getString(R.string.permission_tip_SD));
    }

    //相机权限请求成功
    @PermissionGrant(REQUEST_CODE_CAMERA)
    public void requestCameraSuccess() {
        selectPicFromCamera();
    }

    //相机权限请求失败
    @PermissionDenied(REQUEST_CODE_CAMERA)
    public void requestCameraFailed() {
        PermissionHelper.showSystemSettingDialog(this, getString(R.string.permission_tip_SD));
    }

    /**
     * 启动Camera拍照
     */
    public void selectPicFromCamera() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }
        // 直接将拍到的照片存到手机默认的文件夹
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!pickBean.isClipPhoto()) {
            getMenuInflater().inflate(R.menu.menu_ok, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok) {
            if (adapter != null && !adapter.getSelectPhotos().isEmpty()) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, adapter.getSelectPhotos());
                setResult(Activity.RESULT_OK, intent);
                finish();
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
                findPhoto(adapter.getCameraUri());
                break;
            case UCrop.REQUEST_CROP:    //裁剪
                findClipPhoto(UCrop.getOutput(data));
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
                    if (photoLists == null) {
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

    private void findClipPhoto(Uri uri) {
        Intent intent = new Intent();
        intent.putExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO, uri.toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void findPhoto(Uri imageUri) {
        String filePath = UtilsHelper.getRealPathFromURI(imageUri, this);
        if (imageUri == null) {
            Toast.makeText(this, R.string.unable_find_pic, Toast.LENGTH_LONG).show();
        } else {
            if (pickBean.isClipPhoto()) {//拍完照之后，如果要启动头像裁剪，则去裁剪再把地址传回来
                adapter.startClipPic(filePath);
            } else {
                ArrayList<String> pic = new ArrayList<>();
                pic.add(filePath);
                Intent intent = new Intent();
                intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, pic);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
    }

}
