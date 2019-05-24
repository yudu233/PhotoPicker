package com.rain.library.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.rain.library.BaseActivity;
import com.rain.library.PhotoPick;
import com.rain.library.PhotoPickOptions;
import com.rain.library.R;
import com.rain.library.bean.Photo;
import com.rain.library.bean.PhotoPreviewBean;
import com.rain.library.controller.PhotoPickConfig;
import com.rain.library.controller.PhotoPreviewConfig;
import com.rain.library.impl.CommonResult;
import com.rain.library.impl.PhotoSelectCallback;
import com.rain.library.utils.Rlog;
import com.rain.library.utils.UtilsHelper;
import com.rain.library.weidget.HackyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Describe :仿微信图片预览
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class PhotoPreviewActivity extends BaseActivity implements OnPhotoTapListener {

    private static final String TAG = "PhotoPreviewActivity";

    private ArrayList<Photo> photos;    //全部图片集合
    private ArrayList<String> selectPhotos;     //选中的图片集合
    private ArrayList<Photo> selectPhotosInfo;     //选中的图片集合信息

    private CheckBox checkbox;
    private RadioButton radioButton;
    private int pos;
    private int maxPickSize;            //最大选择个数
    private boolean isChecked = false;
    private boolean originalPicture;    //是否选择的是原图
    private PhotoSelectCallback callback;
    private int screenHeight;
    private int screenWidth;

    private static final int MAX_SIZE = 4096;
    private static final int MAX_SCALE = 8;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Bundle bundle = getIntent().getBundleExtra(PhotoPreviewConfig.EXTRA_BUNDLE);
        if (bundle == null) {
            throw new NullPointerException("bundle is null,please init it");
        }
        PhotoPreviewBean bean = bundle.getParcelable(PhotoPreviewConfig.EXTRA_BEAN);
        if (bean == null) {
            finish();
            return;
        }
        photos = PhotoPreviewConfig.getPhotos();
        if (photos == null || photos.isEmpty()) {
            finish();
            return;
        }
        originalPicture = bean.isOriginalPicture();
        maxPickSize = bean.getMaxPickSize();
        selectPhotos = bean.getSelectPhotos();
        selectPhotosInfo = bean.getSelectPhotosInfo();
        callback = bean.getCallback();
        final int beginPosition = bean.getPosition();
        setContentView(R.layout.activity_photo_select);

        radioButton = (RadioButton) findViewById(R.id.radioButton);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        HackyViewPager viewPager = (HackyViewPager) findViewById(R.id.pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
        toolbar.setTitle((beginPosition + 1) + "/" + photos.size());
        toolbar.setNavigationIcon(PhotoPickOptions.DEFAULT.backIcon);
        setSupportActionBar(toolbar);

        screenHeight = UtilsHelper.getScreenHeight(this);
        screenWidth = UtilsHelper.getScreenWidth(this);

        //照片滚动监听，更改ToolBar数据
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                position++;
                toolbar.setTitle(position + "/" + photos.size());
                if (selectPhotos != null && selectPhotos.contains(photos.get(pos).getOriginalImagePath())) {
                    checkbox.setChecked(true);
                    if (pos == 1 && selectPhotos.contains(photos.get(pos - 1).getOriginalImagePath())) {
                        checkbox.setChecked(true);
                    }
                } else {
                    checkbox.setChecked(false);
                }
                if (originalPicture && radioButton.isChecked()) {
                    radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(pos).getOriginalImageSize())));
                } else {
                    radioButton.setText(getString(R.string.original_image));

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //选中
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPhotos == null) {
                    selectPhotos = new ArrayList<>();
                }
                String path = photos.get(pos).getOriginalImagePath();
                if (selectPhotos.contains(path)) {
                    selectPhotos.remove(path);
                    checkbox.setChecked(false);
                } else {
                    if (maxPickSize == selectPhotos.size()) {
                        checkbox.setChecked(false);
                        return;
                    }
                    selectPhotos.add(path);
                    checkbox.setChecked(true);
                }
                updateMenuItemTitle();
            }
        });

        //原图
        if (originalPicture) {
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChecked) {
                        radioButton.setChecked(false);
                        isChecked = false;
                        radioButton.setText(getString(R.string.original_image));
                    } else {
                        radioButton.setChecked(true);
                        isChecked = true;
                        radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(beginPosition).getOriginalImageSize())));
                    }
                }
            });
        } else {
            radioButton.setVisibility(View.GONE);
        }

        viewPager.setAdapter(new ImagePagerAdapter());
        viewPager.setCurrentItem(beginPosition);
    }

    private void updateMenuItemTitle() {
        if (selectPhotos.isEmpty()) {
            menuItem.setTitle(R.string.send);
        } else {
            menuItem.setTitle(getString(R.string.sends, String.valueOf(selectPhotos.size()), String.valueOf(maxPickSize)));
        }
    }

    private MenuItem menuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        menuItem = menu.findItem(R.id.ok);
        updateMenuItemTitle();
        return true;
    }

    private List<String> imageFilePath = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok && !selectPhotos.isEmpty()) {
            if (isChecked) {


                Intent intent = new Intent();
                if (selectPhotos.size() != 1) {
                    if (callback != null) {
                        callback.moreSelect(selectPhotosInfo);
                    } else
                        intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotosInfo);
                    // intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotos);
                } else {
                    if (callback != null)
                        callback.singleSelect(selectPhotosInfo);
                    else
                        intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, selectPhotosInfo);
                }
                //intent.putExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, selectPhotos.get(0));
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else
                PhotoPick.startCompression(PhotoPreviewActivity.this, selectPhotos, new CommonResult<File>() {
                    @Override
                    public void onSuccess(File file) {
                        if (file.exists()) {
                            Rlog.e("Rain", "onSuccess:" + file.getAbsolutePath() + "--- length = " + file.length());
                            imageFilePath.add(file.getAbsolutePath());
                            if (imageFilePath != null && imageFilePath.size() > 0 && imageFilePath.size() == selectPhotos.size()) {
                                Rlog.e("Rain", "所有图片压缩完成!");
                                Intent intent = new Intent();
                                if (selectPhotos.size() != 1) {
                                    if (callback != null) {
                                        callback.moreSelect(selectPhotosInfo);
                                    } else
                                        intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotosInfo);
                                } else {
                                    if (callback != null)
                                        callback.singleSelect(selectPhotosInfo);
                                    else
                                        intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO, selectPhotosInfo);
                                }
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                });
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            backTo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backTo() {
        Intent intent = new Intent();
        intent.putExtra("isBackPressed", true);
        intent.putStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotos);
        intent.putExtra(PhotoPreviewConfig.EXTRA_ORIGINAL_PIC, originalPicture);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backTo();
        super.onBackPressed();
    }

    private boolean toolBarStatus = true;

    //隐藏ToolBar
    private void hideViews() {
        toolBarStatus = false;
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    //显示ToolBar
    private void showViews() {
        toolBarStatus = true;
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    //单击图片时操作
    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        finish();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view;
            View longView = LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_photo_preview_long, container, false);
            View simpleView = LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_photo_preview, container, false);

            String originalImagePath = photos.get(position).getOriginalImagePath();
            String thumbnailsImagePath = photos.get(position).getThumbnailsImagePath();
            int imageWidth = Integer.parseInt(photos.get(position).getImageWidth());
            int imageHeight = Integer.parseInt(photos.get(position).getImageHeight());

            if (imageHeight > MAX_SIZE || imageHeight / imageWidth > MAX_SCALE) {
                //加载长截图
                view = longView;
                SubsamplingScaleImageView imageView = longView.findViewById(R.id.iv_media_image);
                float scale = UtilsHelper.getImageScale(PhotoPreviewActivity.this, originalImagePath);
                imageView.setImage(ImageSource.uri(originalImagePath),
                        new ImageViewState(scale, new PointF(0, 0), 0));
            } else {
                view = simpleView;
                PhotoView imageView = (PhotoView) simpleView.findViewById(R.id.iv_media_image);
                imageView.setOnPhotoTapListener(PhotoPreviewActivity.this);
                PhotoPickConfig.imageLoader.displayImage(PhotoPreviewActivity.this, originalImagePath, thumbnailsImagePath, imageView, false, true);
            }
            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.image_pager_exit_animation);
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
