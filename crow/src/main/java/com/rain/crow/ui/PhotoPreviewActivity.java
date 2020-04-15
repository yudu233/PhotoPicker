package com.rain.crow.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.rain.crow.BaseActivity;
import com.rain.crow.PhotoPick;
import com.rain.crow.PhotoPickOptions;
import com.rain.crow.R;
import com.rain.crow.bean.MediaData;
import com.rain.crow.bean.PhotoPreviewBean;
import com.rain.crow.controller.PhotoPickConfig;
import com.rain.crow.controller.PhotoPreviewConfig;
import com.rain.crow.impl.CommonResult;
import com.rain.crow.impl.PhotoSelectCallback;
import com.rain.crow.observer.UpdateUIObserver;
import com.rain.crow.utils.MimeType;
import com.rain.crow.utils.Rlog;
import com.rain.crow.utils.UtilsHelper;
import com.rain.crow.weidget.HackyViewPager;
import com.rain.crow.weidget.LoadingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;


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
    private PhotoSelectCallback callback;

    private static final int MAX_SCALE = 3;
    private LoadingDialog loadingDialog;
    private PhotoPreviewBean photoPreviewBean;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        loadingDialog = new LoadingDialog(this);
        Bundle bundle = getIntent().getBundleExtra(PhotoPreviewConfig.EXTRA_BUNDLE);
        if (bundle == null) {
            throw new NullPointerException("bundle is null,please init it");
        }
        photoPreviewBean = bundle.getParcelable(PhotoPreviewConfig.EXTRA_BEAN);
        if (photoPreviewBean == null) {
            finish();
            return;
        }
        photos = PhotoPreviewConfig.getPhotos();
        if (photos == null || photos.isEmpty()) {
            finish();
            return;
        }

        maxPickSize = photoPreviewBean.getMaxPickSize();
        selectPhotosInfo = photoPreviewBean.getSelectPhotosInfo();
        callback = PhotoPickConfig.getInstance().getCallback();
        setContentView(R.layout.activity_photo_select);

        radioButton = findViewById(R.id.radioButton);
        checkbox = findViewById(R.id.checkbox);
        HackyViewPager viewPager = findViewById(R.id.pager);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(PhotoPick.getToolbarBackGround());
        toolbar.setTitle((photoPreviewBean.getPosition() + 1) + "/" + photos.size());
        toolbar.setNavigationIcon(PhotoPickOptions.DEFAULT.backIcon);
        setSupportActionBar(toolbar);

        //照片滚动监听，更改ToolBar数据
        viewPager.addOnPageChangeListener(onPageChangeListener);
        //选中按钮监听
        checkbox.setOnClickListener(mCheckBoxClickListener);
        //原图按钮监听
        if (photoPreviewBean.isShowOriginalButton()) {
            radioButton.setOnClickListener(mRadioButtonClickListener);
        } else {
            radioButton.setVisibility(View.GONE);
        }
        radioButton.setChecked(photoPreviewBean.isSelectOrigin());

        viewPager.setAdapter(new ImagePagerAdapter());
        viewPager.setCurrentItem(photoPreviewBean.getPosition());
        if (photoPreviewBean.getPosition() == 0) {
            onPageChangeListener.onPageSelected(photoPreviewBean.getPosition());
        }
        if (MimeType.isGif(photos.get(photoPreviewBean.getPosition()).getImageType())) {
            radioButton.setVisibility(View.GONE);
        }
    }

    private RadioButton.OnClickListener mRadioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (photoPreviewBean.isSelectOrigin()) {
                radioButton.setChecked(false);
                photoPreviewBean.setSelectOrigin(false);
                radioButton.setText(getString(R.string.original_image));
            } else {
                radioButton.setChecked(true);
                photoPreviewBean.setSelectOrigin(true);
                radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(pos).getOriginalSize())));
                UpdateUIObserver.getInstance().sendUpdateUIMessage(pos, photos.get(pos), checkbox.isChecked(), radioButton.isChecked());
            }
        }
    };

    private CheckBox.OnClickListener mCheckBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectPhotosInfo == null) {
                selectPhotosInfo = new ArrayList<>();
            }
            if (MimeType.isGif(photos.get(pos).getImageType())) {
                radioButton.setChecked(true);
                photoPreviewBean.setShowOriginalButton(true);
            }
            if (!checkbox.isChecked()) {
                selectPhotosInfo.remove(photos.get(pos));
                updateMenuItemTitle();
                UpdateUIObserver.getInstance().sendUpdateUIMessage(pos, photos.get(pos), checkbox.isChecked(), radioButton.isChecked());
            } else {
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
                //判断最大选择数量
                if (selectPhotosInfo.size() == maxPickSize && checkbox.isChecked()) {
                    checkbox.setChecked(false);
                    PhotoPick.toast(getString(R.string.tips_max_num, maxPickSize));
                    return;
                }
                selectPhotosInfo.add(photos.get(pos));
                updateMenuItemTitle();
                UpdateUIObserver.getInstance().sendUpdateUIMessage(pos, photos.get(pos), checkbox.isChecked(), radioButton.isChecked());
            }
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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
            if (!photoPreviewBean.isShowOriginalButton()) {
                radioButton.setVisibility(View.GONE);
                return;
            }

            if (photoPreviewBean.isShowOriginalButton() && radioButton.isChecked()) {
                radioButton.setText(getString(R.string.image_size, UtilsHelper.formatFileSize(photos.get(pos).getOriginalSize())));
            } else {
                radioButton.setText(getString(R.string.original_image));
            }
            int pictureType = MimeType.isPictureType(photos.get(position).getImageType());
            if (pictureType == MimeType.ofVideo() || MimeType.isGif(photos.get(position).getImageType())) {
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

    /**
     * 更新发送按钮UI
     */
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
        if (PhotoPick.isTimeEnabled()) {
            if (item.getItemId() == R.id.ok) {
                checkImages();
                if (!selectPhotosInfo.isEmpty()) {
                    if (PhotoPickConfig.getInstance().isStartCompression() &&
                            !photoPreviewBean.isSelectOrigin() && !MimeType.isVideo(selectPhotosInfo.get(0).getImageType())) {
                        if (loadingDialog != null) {
                            loadingDialog.show();
                        }
                        if (selectPhotosInfo.size() > 0) {
                            PhotoPick.startCompression(PhotoPreviewActivity.this, selectPhotosInfo, compressResult);
                        } else {
                            PhotoPick.toast(getString(R.string.tips_no));
                            finish();
                        }
                    } else {
                        sendImage();
                    }
                } else {
                    PhotoPick.toast(getString(R.string.tips_no));
                    finish();
                }
            } else {
                backTo();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendImage() {
        if (callback != null) {
            callback.selectResult(selectPhotosInfo);
            setResult(RESULT_OK, new Intent());
        } else {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SELECT_PHOTOS, selectPhotosInfo);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    private void checkImages() {
        //检测图片是否存在/损坏
        ListIterator<MediaData> iterator = selectPhotosInfo.listIterator();
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
                Rlog.e("文件不存在");
                iterator.remove();

            }

        }
    }

    private int index = 0;

    private CommonResult<File> compressResult = new CommonResult<File>() {
        @Override
        public void onSuccess(File file, boolean success) {

            if (success && file.exists()) {
                Rlog.e("Rain", "Luban compression success:" + file.getAbsolutePath() + " ; image length = " + file.length());
                MediaData photo = selectPhotosInfo.get(index);
                photo.setCompressed(true);
                if (MimeType.isGif(photo.getImageType())) {
                    photo.setCompressionPath(photo.getOriginalPath());
                } else {
                    photo.setCompressionPath(file.getAbsolutePath());
                }
                index++;

                if (index > 0 && index == selectPhotosInfo.size()) {
                    Rlog.e("Rain", "all select image compression success!");
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                    Intent intent = new Intent();
                    if (callback != null) {
                        callback.selectResult(selectPhotosInfo);
                    } else {
                        intent.putParcelableArrayListExtra(PhotoPickConfig.EXTRA_SELECT_PHOTOS, selectPhotosInfo);
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            } else {
                MediaData photo = selectPhotosInfo.get(index);
                photo.setCompressed(true);
                photo.setCompressionPath(photo.getOriginalPath());
                index++;
            }
        }
    };


    private void backTo() {
        if (photoPreviewBean.isSelectOrigin()) {
            PhotoPickConfig.getInstance().setStartCompression(false);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        backTo();
        super.onBackPressed();
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
            View view;
            View longView = LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_photo_preview_long, container, false);
            View simpleView = LayoutInflater.from(PhotoPreviewActivity.this).inflate(R.layout.item_photo_preview, container, false);

            String originalImagePath = photos.get(position).getOriginalPath();
            int imageWidth = photos.get(position).getImageWidth() == 0 ? UtilsHelper.getScreenWidth(PhotoPreviewActivity.this) :
                    photos.get(position).getImageWidth();
            int imageHeight = photos.get(position).getImageHeight() == 0 ? UtilsHelper.getScreenHeight(PhotoPreviewActivity.this) :
                    photos.get(position).getImageHeight();
            if (imageHeight / imageWidth > MAX_SCALE) {
                //加载长截图
                view = longView;
                SubsamplingScaleImageView imageView = longView.findViewById(R.id.iv_media_image);
                float scale = UtilsHelper.getImageScale(PhotoPreviewActivity.this, originalImagePath);
                imageView.setImage(ImageSource.uri(originalImagePath),
                        new ImageViewState(scale, new PointF(0, 0), 0));
            } else {
                view = simpleView;
                PhotoView imageView = simpleView.findViewById(R.id.iv_media_image);
                imageView.setOnPhotoTapListener(PhotoPreviewActivity.this);
                PhotoPickConfig.getInstance().getImageLoader().displayImage(PhotoPreviewActivity.this, originalImagePath, imageView, false);
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
