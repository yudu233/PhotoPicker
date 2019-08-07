package com.rain.library;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.rain.library.bean.MediaData;
import com.rain.library.bean.PhotoPickBean;
import com.rain.library.controller.PhotoPickConfig;
import com.rain.library.controller.PhotoPreviewConfig;
import com.rain.library.ui.PhotoPickActivity;
import com.rain.library.utils.MimeType;
import com.rain.library.utils.UCropUtils;
import com.rain.library.utils.UtilsHelper;
import com.rain.library.weidget.GalleryImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe : 本地所有照片列表
 * Created by Rain on 17-4-28.
 */
public class PhotoPickAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<MediaData> photos = new ArrayList<>();
    //    private ArrayList<String> selectPhotos = new ArrayList<>();
    private ArrayList<MediaData> selectPhotosInfo = new ArrayList<>();

    private int imageSize;
    private Uri cameraUri;
    private String cameraImagePath;
    private String clipImagePath;

    private PhotoPickBean photoPickBean;

    public PhotoPickAdapter(Context context, PhotoPickBean pickBean) {
        this.context = context;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        this.imageSize = metrics.widthPixels / pickBean.getSpanCount();
        this.photoPickBean = pickBean;
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
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeBoxState(getItem(getAdapterPosition()));
                }
            });
            itemView.findViewById(R.id.photo_pick_rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PhotoPick.isTimeEnabled()) {
                        doSomeThing();
                    }
                }
            });
        }

        private void doSomeThing() {
            if (photoPickBean.isShowCamera() && getAdapterPosition() == 0) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, PhotoPickActivity.REQUEST_CODE_CAMERA);
                } else {
                    selectPicFromCamera();
                }
            } else if (photoPickBean.isClipPhoto() &&
                    (!MimeType.isVideo(getItem(getAdapterPosition()).getImageType()))) {
                //头像裁剪
                startClipPic(getItem(getAdapterPosition()).getOriginalPath());
            } else {
                //查看大图
                new PhotoPreviewConfig.Builder((Activity) context)
                        .setPosition(photoPickBean.isShowCamera() ? getAdapterPosition() - 1 : getAdapterPosition())
                        .setMaxPickSize(photoPickBean.getMaxPickSize())
                        .setSelectPhotosInfo(selectPhotosInfo)
//                        .setSelectPhotos(selectPhotos)
                        .setOriginalPicture(photoPickBean.isOriginalPicture())
                        .build();
            }
        }

        private void changeBoxState(MediaData data) {
            String mimeType = selectPhotosInfo.size() > 0 ? selectPhotosInfo.get(0).getImageType() : "";

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


    /**
     * 裁剪图片
     *
     * @param picPath
     */
    public void startClipPic(String picPath) {
        String clipImageName = "clip_" + (System.currentTimeMillis() / 1000) + ".jpg";
        File clipImage = new File(PhotoPickOptions.DEFAULT.imagePath, clipImageName);
        clipImagePath = clipImage.getAbsolutePath();
        UCropUtils.start((Activity) context, new File(picPath), clipImage, photoPickBean.getClipMode());
    }

    /**
     * 启动Camera拍照
     */
    public void selectPicFromCamera() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }

        //保存到自定义目录
        String cameraImageName = "camera_" + (System.currentTimeMillis() / 1000) + ".jpg";
        File imageFile = new File(PhotoPickOptions.DEFAULT.imagePath, cameraImageName);
        cameraImagePath = imageFile.getAbsolutePath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            cameraUri = FileProvider.getUriForFile(context, PhotoPickOptions.DEFAULT.photoPickAuthority, imageFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件,私有目录读写权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        ((Activity) context).startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);
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
     * get selected photos path
     *
     * @return selected photos
     */
//    public ArrayList<String> getSelectPhotos() {
//        return selectPhotos;
//    }

    /**
     * get selected photos info
     *
     * @return
     */
    public ArrayList<MediaData> getSelectPhotosInfo() {
        return selectPhotosInfo;
    }

    /**
     * get camera image uri
     *
     * @return
     */
    public Uri getCameraUri() {
        return cameraUri;
    }

    /**
     * get clip image path
     *
     * @return
     */
    public String getClipImagePath() {
        return clipImagePath;
    }

    /**
     * get camera image path
     *
     * @return
     */
    public String getCameraImagePath() {
        return cameraImagePath;
    }


    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void updateToolBarTitle(String title);
    }
}
