package com.rain.photopicker.ui.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.rain.photopicker.PhotoPick;
import com.rain.photopicker.PhotoPickOptions;
import com.rain.photopicker.R;
import com.rain.photopicker.bean.MediaData;
import com.rain.photopicker.bean.PhotoPickBean;
import com.rain.photopicker.controller.PhotoPickConfig;
import com.rain.photopicker.controller.PhotoPreviewConfig;
import com.rain.photopicker.utils.MimeType;
import com.rain.photopicker.utils.PhotoPickerHelper;
import com.rain.photopicker.utils.UCropUtils;
import com.rain.photopicker.utils.UtilsHelper;
import com.rain.photopicker.weidget.GalleryImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe : 本地所有照片列表
 * Created by Rain on 17-4-28.
 */
public class PhotoPickAdapter extends RecyclerView.Adapter {

    private AppCompatActivity context;
    private ArrayList<MediaData> photos = new ArrayList<>();
    private ArrayList<MediaData> selectPhotosInfo = new ArrayList<>();

    private int imageSize;

    private PhotoPickBean photoPickBean;
    private PhotoPreviewConfig.Builder previewBuilder;

    public PhotoPickAdapter(AppCompatActivity context, PhotoPickBean pickBean) {
        this.context = context;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        this.imageSize = metrics.widthPixels / pickBean.getSpanCount();
        this.photoPickBean = pickBean;
        previewBuilder = new PhotoPreviewConfig.Builder((Activity) context);
    }

    public void refresh(List<MediaData> photos) {
        this.photos.clear();
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_pick, null);
        return new PhotoPickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PhotoPickViewHolder) holder).showData(position);
    }

    @Override
    public int getItemCount() {
        return photoPickBean.isShowCamera() ? (photos == null ? 0 : photos.size() + 1) : (photos == null ? 0 : photos.size());
    }

    private MediaData getItem(int position) {
        return photoPickBean.isShowCamera() ? photos.get(position - 1) : photos.get(position);
    }

    public void notifyPreviewConfig(boolean isSelectOrigin) {
        previewBuilder.isSelectOrigin(isSelectOrigin);
    }

    private class PhotoPickViewHolder extends RecyclerView.ViewHolder {

        private final GalleryImageView imageView;
        private final CheckBox checkbox;
        private final TextView mIsGif, mLongChart, mDuration;

        public PhotoPickViewHolder(View view) {
            super(view);
            imageView = itemView.findViewById(R.id.imageView);
            checkbox = itemView.findViewById(R.id.checkbox);
            mIsGif = itemView.findViewById(R.id.txv_isGif);
            mLongChart = itemView.findViewById(R.id.txv_long_chart);
            mDuration = itemView.findViewById(R.id.txv_duration);

            imageView.getLayoutParams().height = imageSize;
            imageView.getLayoutParams().width = imageSize;
            checkbox.setOnClickListener(v -> changeBoxState(getItem(getAdapterPosition())));
            itemView.findViewById(R.id.photo_pick_rl).setOnClickListener(v -> {
                if (PhotoPick.isTimeEnabled()) {
                    doSomeThing();
                }
            });
        }

        private void doSomeThing() {
            if (photoPickBean.isShowCamera() && getAdapterPosition() == 0) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, PhotoPickerHelper.REQUEST_CODE_CAMERA);
                } else {
                    PhotoPickerHelper.startCamera(context);
                }
            } else if (photoPickBean.isClipPhoto()) {
                //头像裁剪
                PhotoPickerHelper.startClipPic(context, photoPickBean, getItem(getAdapterPosition()).getOriginalPath());
            } else {
                //查看大图
                previewBuilder.setPosition(photoPickBean.isShowCamera() ? getAdapterPosition() - 1 : getAdapterPosition())
                        .setMaxPickSize(photoPickBean.getMaxPickSize())
                        .setSelectPhotosInfo(selectPhotosInfo)
                        .setShowOriginalButton(photoPickBean.isShowOriginalButton())
                        .build();
            }
        }

        private void changeBoxState(MediaData data) {
            String mimeType = selectPhotosInfo.size() > 0 ? selectPhotosInfo.get(0).getImageType() : "";
            if (MimeType.isGif(getItem(getAdapterPosition()).getImageType())) {
                previewBuilder.isSelectOrigin(true);
            }
            if (!checkbox.isChecked()) {
                selectPhotosInfo.remove(data);
            } else {
                if (!TextUtils.isEmpty(mimeType)) {
                    boolean toEqual = MimeType.mimeToEqual(mimeType, data.getImageType());
                    if (!toEqual) {
                        PhotoPick.toast(R.string.tips_rule);
                        checkbox.setChecked(false);
                        return;
                    }
                }
                if (selectPhotosInfo.size() == photoPickBean.getMaxPickSize()) {
                    checkbox.setChecked(false);
                    PhotoPick.toast(context.getString(R.string.tips_max_num, photoPickBean.getMaxPickSize()));
                    return;
                }
                checkbox.setChecked(true);
                selectPhotosInfo.add(data);
            }

            if (onUpdateListener != null) {
                onUpdateListener.updateToolBarTitle(getTitle());
            }
        }

        public void showData(int position) {
            if (photoPickBean.isShowCamera() && position == 0) {
                checkbox.setVisibility(View.GONE);
                imageView.setImageResource(R.mipmap.take_photo);
            } else {
                MediaData photo = getItem(position);
                if (photoPickBean.isClipPhoto()) {
                    checkbox.setVisibility(View.GONE);
                } else {
                    checkbox.setVisibility(View.VISIBLE);
                    checkbox.setChecked(selectPhotosInfo.contains(photo));
                }

                mIsGif.setVisibility(MimeType.isGif(photo.getImageType()) ? View.VISIBLE : View.GONE);
                mLongChart.setVisibility(MimeType.isLongImage(photo) ? View.VISIBLE : View.GONE);
                mDuration.setVisibility(MimeType.isVideo(photo.getImageType()) ? View.VISIBLE : View.GONE);
                mDuration.setText(UtilsHelper.parseDuration(photo.getDuration()));

                photoPickBean.getImageLoader().displayImage(context, photo.getOriginalPath(), imageView, true);
            }
        }
    }

    //如果是多选title才会变化，要不然单选的没有变
    public String getTitle() {
        String title = context.getString(R.string.send);
        if (photoPickBean.getPickMode() == PhotoPickConfig.MODE_PICK_MORE && selectPhotosInfo.size() >= 1) {//不是单选，更新title
            title = context.getString(R.string.sends, selectPhotosInfo.size(), photoPickBean.getMaxPickSize());
        }
        return title;
    }

    /**
     * get selected photos info
     *
     * @return
     */
    public ArrayList<MediaData> getSelectPhotosInfo() {
        return selectPhotosInfo;
    }

    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void updateToolBarTitle(String title);
    }
}
