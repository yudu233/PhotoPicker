package com.rain.crow.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rain.crow.PhotoPick;
import com.rain.crow.R;
import com.rain.crow.bean.MediaData;
import com.rain.crow.bean.PhotoPreviewBean;
import com.rain.crow.ui.activity.PhotoPreviewActivity;

import java.util.ArrayList;


/**
 * Describe : 仿微信图片预览
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class PhotoPreviewConfig {

    private static final String TAG = "PhotoPreviewConfig";

    public static final String EXTRA_BUNDLE = "extra_bundle";
    public static final String EXTRA_BEAN = "extra_bean";
    public final static int REQUEST_CODE = 10504;

    public PhotoPreviewConfig(Activity activity, Builder builder) {
        PhotoPreviewBean photoPreviewBean = builder.bean;
        if (photoPreviewBean == null) {
            throw new NullPointerException("Builder#photoPagerBean is null");
        }

        if (photoPreviewBean.getSelectPhotos() != null && (photoPreviewBean.getSelectPhotos().size() > photoPreviewBean.getMaxPickSize())) {
            throw new IndexOutOfBoundsException("seleced photo size out maxPickSize size,select photo size = " + photoPreviewBean.getSelectPhotos().size() + ",maxPickSize size = " + photoPreviewBean.getMaxPickSize());
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_BEAN, photoPreviewBean);
        startPreviewActivity(activity, bundle);
    }

    private void startPreviewActivity(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putExtra(EXTRA_BUNDLE, bundle);
        activity.startActivityForResult(intent, REQUEST_CODE);
        activity.overridePendingTransition(R.anim.image_pager_enter_animation, 0);
    }

    public static class Builder {
        private Activity context;
        private PhotoPreviewBean bean;

        public Builder(Activity context) {
            PhotoPick.checkInit();
            if (context == null) {
                throw new NullPointerException("context is null");
            }
            this.context = context;
            bean = new PhotoPreviewBean();
        }

        public Builder setPhotoPreviewBean(PhotoPreviewBean bean) {
            this.bean = bean;
            return this;
        }

        public Builder setPosition(int position) {
            if (position < 0) {
                position = 0;
            }
            bean.setPosition(position);
            return this;
        }

        public Builder isSelectOrigin(boolean isSelect){
            bean.setSelectOrigin(isSelect);
            return this;
        }

        public Builder setShowOriginalButton(boolean originalPicture) {//是否设置原图,默认false
            bean.setShowOriginalButton(originalPicture);
            return this;
        }

        public Builder setSelectPhotosInfo(ArrayList<MediaData> selectPhotosInfo) {
            bean.setSelectPhotosInfo(selectPhotosInfo);
            return this;
        }

        public Builder setMaxPickSize(int maxPickSize) {
            bean.setMaxPickSize(maxPickSize);
            return this;
        }

        public PhotoPreviewConfig build() {
            return new PhotoPreviewConfig(context, this);
        }
    }

    private static ArrayList<MediaData> photoArrayList = new ArrayList<>();

    public static void setPreviewPhotos(ArrayList<MediaData> photos) {
        photoArrayList = photos;
    }

    public static ArrayList<MediaData> getPhotos() {
        return photoArrayList;
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
