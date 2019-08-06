package com.rain.library.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.rain.library.bean.MediaData;
import com.rain.library.bean.PhotoPreviewBean;
import com.rain.library.controller.PhotoPickConfig;
import com.rain.library.controller.PhotoPreviewConfig;
import com.rain.library.impl.CommonResult;
import com.rain.library.impl.PhotoSelectCallback;
import com.rain.library.observer.UpdateUIObserver;
import com.rain.library.utils.MimeType;
import com.rain.library.utils.Rlog;
import com.rain.library.utils.UtilsHelper;
import com.rain.library.weidget.HackyViewPager;

import java.io.File;
import java.util.ArrayList;


/**
 * Describe :仿微信图片预览
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class PhotoPreviewActivity extends BaseActivity implements OnPhotoTapListener {

    private static final String TAG = "PhotoPreviewActivity";

    private ArrayList<MediaData> photos;            //全部图片集合
    private ArrayList<MediaData> selectPhotosInfo;  //选中的图片集合信息

    private CheckBox checkbox;
    private RadioButton radioButton;
    private int pos;                    //当前位置
    private int maxPickSize;            //最大选择个数
    private boolean isChecked = false;  //是否已选定
    private boolean originalPicture;    //是否选择的是原图
    private PhotoSelectCallback callback;

    private static final int MAX_SCALE = 3;
    public static final int DEFAULT_WIDTH = 1080;
    public static final int DEFAULT_HEIGHT = 1920;

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
        selectPhotosInfo = bean.getSelectPhotosInfo();
        callback = bean.getCallback();
        setContentView(R.layout.activity_photo_select);

        radioButton = (RadioButton) findViewById(R.id.radioButton);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        HackyViewPager viewPager = (HackyViewPager) findViewById(R.id.pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
        toolbar.setTitle((bean.getPosition() + 1) + "/" + photos.size());
        toolbar.setNavigationIcon(PhotoPickOptions.DEFAULT.backIcon);
        setSupportActionBar(toolbar);

        //照片滚动监听，更改ToolBar数据
        viewPager.addOnPageChangeListener(onPageChangeListener);

        //选中
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPhotosInfo == null) {
                    selectPhotosInfo = new ArrayList<>();
                }

                //判断是否同一类型文件
                String mimeType = selectPhotosInfo.size() > 0 ? selectPhotosInfo.get(0).getImageType() : "";
                if (!TextUtils.isEmpty(mimeType)) {
                    boolean toEqual = MimeType.mimeToEqual(mimeType, photos.get(pos).getImageType());
                    if (!toEqual) {
                        PhotoPick.toast(R.string.tips_rule);
                        checkbox.setChecked(false);
                        return;
                    }
                }

                if (selectPhotosInfo.size() == maxPickSize && checkbox.isChecked()) {
                    checkbox.setChecked(false);
                    PhotoPick.toast(getString(R.string.tips_max_num, maxPickSize));
                    return;
                }

                if (checkbox.isChecked()) {
                    selectPhotosInfo.add(photos.get(pos));
                } else {
                    selectPhotosInfo.remove(photos.get(pos));
                }

                updateMenuItemTitle();
                UpdateUIObserver.getInstance().sendUpdateUIMessage(pos, photos.get(pos), checkbox.isChecked());
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
                        radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(pos).getOriginalSize())));
                    }
                }
            });
        } else {
            radioButton.setVisibility(View.GONE);
        }

        viewPager.setAdapter(new ImagePagerAdapter());
        viewPager.setCurrentItem(bean.getPosition());
        if (bean.getPosition() == 0) {
            onPageChangeListener.onPageSelected(bean.getPosition());
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            pos = position;
            toolbar.setTitle(position + 1 + "/" + photos.size());

            if (isChecked(position)) {
                checkbox.setChecked(true);
            } else {
                checkbox.setChecked(false);
            }

            if (originalPicture && radioButton.isChecked()) {
                radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(pos).getOriginalSize())));
            } else {
                radioButton.setText(getString(R.string.original_image));
            }

            if (MimeType.isPictureType(photos.get(position).getImageType()) == MimeType.ofVideo()) {
                radioButton.setVisibility(View.GONE);
            } else {
                radioButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    /**
     * 判断当前图片是否选中
     *
     * @param position
     * @return
     */
    private boolean isChecked(int position) {
        if (selectPhotosInfo == null || selectPhotosInfo.size() == 0) {
            return false;
        } else {
            for (MediaData mediaData : selectPhotosInfo) {
                if (mediaData.getOriginalPath().equals(photos.get(position).getOriginalPath())) {
                    return true;
                }
            }
            return false;
        }
    }

    private void updateMenuItemTitle() {
        if (selectPhotosInfo.isEmpty()) {
            menuItem.setTitle(R.string.send);
        } else {
            menuItem.setTitle(getString(R.string.sends, selectPhotosInfo.size(), maxPickSize));
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ok && !selectPhotosInfo.isEmpty()) {
            if (isChecked) {
                Intent intent = new Intent();
                if (selectPhotosInfo.size() != 1) {
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
            } else {
                ArrayList<String> selectPath = new ArrayList<>();
                for (MediaData data :
                        selectPhotosInfo) {
                    selectPath.add(data.getOriginalPath());
                }
                PhotoPick.startCompression(PhotoPreviewActivity.this, selectPath, compressResult);
            }

            return true;
        } else if (item.getItemId() == android.R.id.home) {
            backTo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int index = 0;

    private CommonResult<File> compressResult = new CommonResult<File>() {
        @Override
        public void onSuccess(File file, boolean success) {
            if (success && file.exists()) {
                Rlog.e("Rain", "Luban compression success:" + file.getAbsolutePath() + " ; image length = " + file.length());
                MediaData photo = selectPhotosInfo.get(index);
                photo.setCompressionPath(file.getAbsolutePath());
                index++;

                if (index > 0 && index == selectPhotosInfo.size()) {
                    Rlog.e("Rain", "all select image compression success!");
                    Intent intent = new Intent();
                    if (selectPhotosInfo.size() != 1) {
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
            } else {
                index++;
                MediaData photo = selectPhotosInfo.get(index);
                photo.setCompressionPath(photo.getOriginalPath());
            }
        }
    };

    private void backTo() {
        Intent intent = new Intent();
        intent.putExtra("isBackPressed", true);
        intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST, selectPhotosInfo);
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
        if (MimeType.isPictureType(photos.get(pos).getImageType()) == MimeType.ofVideo()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String type = "video/*";
            Uri uri = FileProvider.getUriForFile(this, PhotoPickOptions.DEFAULT.photoPickAuthority, new File(photos.get(pos).getOriginalPath()));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, type);
            startActivity(intent);
        }
        finish();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = null;
            View longView = LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_photo_preview_long, container, false);
            View simpleView = LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_photo_preview, container, false);

            String originalImagePath = photos.get(position).getOriginalPath();
            int imageWidth = photos.get(position).getImageWidth() == 0 ? UtilsHelper.getScreenWidth(PhotoPreviewActivity.this) :
                    photos.get(position).getImageWidth();
            int imageHeight = photos.get(position).getImageHeight() == 0 ? UtilsHelper.getScreenHeight(PhotoPreviewActivity.this) :
                    photos.get(position).getImageHeight();
            if ( imageHeight / imageWidth > MAX_SCALE) {
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
                PhotoPickConfig.imageLoader.displayImage(PhotoPreviewActivity.this, originalImagePath, imageView, false);
            }
            if (MimeType.isVideo(photos.get(position).getImageType())) {
                simpleView.findViewById(R.id.imv_play).setVisibility(View.VISIBLE);
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
