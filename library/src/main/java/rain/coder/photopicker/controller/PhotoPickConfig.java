package rain.coder.photopicker.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import rain.coder.library.R;
import rain.coder.photopicker.bean.PhotoPickBean;
import rain.coder.photopicker.loader.ImageLoader;
import rain.coder.photopicker.ui.PhotoPickActivity;

/**
 * Descriptions :
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class PhotoPickConfig {

    public static int DEFAULT_CHOOSE_SIZE = 9;         //默认可以选择的图片数目

    public static int MODE_PICK_SINGLE = 1;             //单选模式

    public static int MODE_PICK_MORE = 2;               //多选模式

    public static int GRID_SPAN_COUNT = 3;              //gridView的列数

    public static boolean CLIP_CIRCLE = false;         //裁剪方式 圆形

    public static boolean DEFAULT_SHOW_CAMERA = true;   //默认展示相机icon

    public static boolean DEFAULT_SHOW_CLIP = false;   //默认不开启裁剪图片功能

    public static ImageLoader imageLoader;              //图片加载方式

    public static PhotoPickBean photoPickBean;

    public final static String EXTRA_STRING_ARRAYLIST = "extra_string_array_list";
    public final static String EXTRA_PICK_BUNDLE = "extra_pick_bundle";
    public final static String EXTRA_PICK_BEAN = "extra_pick_bean";
    public static final String EXTRA_CLIP_PHOTO = "extra_clip_photo";
    public final static int PICK_REQUEST_CODE = 10001;
    public static final int CLIP_REQUEST_CODE = 10002;

    public PhotoPickConfig(Activity activity, Builder builder) {
        imageLoader = builder.imageLoader;
        if (builder.pickBean == null) {
            throw new NullPointerException("builder#pickBean is null");
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_PICK_BEAN, builder.pickBean);
        startPick(activity, bundle);
    }

    private void startPick(Activity activity, Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PICK_BUNDLE, bundle);
        intent.setClass(activity, PhotoPickActivity.class);
        activity.startActivityForResult(intent, PICK_REQUEST_CODE);
        activity.overridePendingTransition(R.anim.image_pager_enter_animation, 0);
    }

    public static class Builder {
        private Activity activity;
        private PhotoPickBean pickBean;
        private ImageLoader imageLoader;

        public Builder(Activity activity) {
            if (activity == null) {
                throw new NullPointerException("context is null");
            }
            this.activity = activity;
            photoPickBean = pickBean = new PhotoPickBean();
            pickBean.setSpanCount(GRID_SPAN_COUNT);             //默认gridView列数
            pickBean.setMaxPickSize(DEFAULT_CHOOSE_SIZE);       //默认可以选择的图片数目
            pickBean.setPickMode(MODE_PICK_MORE);               //默认图片多选
            pickBean.setShowCamera(DEFAULT_SHOW_CAMERA);        //默认展示拍照那个icon
            pickBean.setClipPhoto(DEFAULT_SHOW_CLIP);           //默认不开启图片裁剪
            pickBean.setClipMode(CLIP_CIRCLE);                  //默认裁剪方式非圆形
        }

        public Builder imageLoader(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            pickBean.setImageLoader(imageLoader);
            return this;
        }

        /**
         * 手动设置GridView列数
         *
         * @param spanCount
         * @return
         */
        public Builder spanCount(int spanCount) {
            pickBean.setSpanCount(spanCount);
            if (pickBean.getSpanCount() == 0) pickBean.setSpanCount(GRID_SPAN_COUNT);
            return this;
        }

        /**
         * 手动设置照片多选还是单选
         *
         * @param pickMode
         * @return
         */
        public Builder pickMode(int pickMode) {
            pickBean.setPickMode(pickMode);
            if (pickMode == MODE_PICK_SINGLE) pickBean.setMaxPickSize(1);
            else if (pickMode == MODE_PICK_MORE) {
                pickBean.setClipPhoto(false);
            } else throw new IllegalArgumentException("unKnow_pickMode : " + pickMode);
            return this;
        }

        /**
         * 默认可以选择的图片数目
         *
         * @param maxPickSize
         * @return
         */
        public Builder maxPickSize(int maxPickSize) {
            pickBean.setMaxPickSize(maxPickSize);
            if (maxPickSize == 0 || maxPickSize == 1) {
                pickBean.setMaxPickSize(1);
                pickBean.setPickMode(MODE_PICK_SINGLE);
            } else if (pickBean.getPickMode() == MODE_PICK_SINGLE)
                pickBean.setMaxPickSize(1);
            else pickBean.setPickMode(MODE_PICK_MORE);
            return this;
        }

        /**
         * 是否显示拍照icon
         * 默认显示
         *
         * @param showCamera
         * @return
         */
        public Builder showCamera(boolean showCamera) {
            pickBean.setShowCamera(showCamera);
            return this;
        }

        /**
         * 是否开启选择照片后开启裁剪功能
         *
         * @param clipPhoto
         * @return
         */
        public Builder clipPhoto(boolean clipPhoto) {
            pickBean.setClipPhoto(clipPhoto);
            return this;
        }

        /**
         * 设置裁剪方式（圆形，矩形）
         *
         * @param showClipCircle
         * @return
         */
        public Builder clipCircle(boolean showClipCircle) {
            pickBean.setClipMode(showClipCircle);
            return this;
        }

        public Builder setPhotoPickBean(PhotoPickBean bean) {
            this.pickBean = bean;
            return this;
        }

        public PhotoPickConfig build() {
            if (pickBean.isClipPhoto()) {
                pickBean.setMaxPickSize(1);
                pickBean.setPickMode(MODE_PICK_SINGLE);
            }
            return new PhotoPickConfig(activity, this);
        }
    }
}
