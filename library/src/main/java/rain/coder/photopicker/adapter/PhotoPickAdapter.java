package rain.coder.photopicker.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rain.coder.library.R;
import rain.coder.photopicker.bean.Photo;
import rain.coder.photopicker.bean.PhotoPickBean;
import rain.coder.photopicker.controller.PhotoPickConfig;
import rain.coder.photopicker.controller.PhotoPreviewConfig;
import rain.coder.photopicker.loader.ImageLoader;
import rain.coder.photopicker.ui.PhotoPickActivity;
import rain.coder.photopicker.utils.ImageUtils;
import rain.coder.photopicker.utils.PermissionHelper;
import rain.coder.photopicker.utils.UCropUtils;
import rain.coder.photopicker.weidget.GalleryImageView;


/**
 * Describe : 本地所有照片列表
 * Created by Rain on 17-4-28.
 */
public class PhotoPickAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Photo> photos = new ArrayList<>();
    private ArrayList<String> selectPhotos = new ArrayList<>();
    private int maxPickSize;
    private int pickMode;
    private int imageSize;
    private boolean clipCircle;
    private boolean showCamera;
    private boolean isClipPhoto;
    private boolean isOriginalPicture;
    private ImageLoader imageLoader;
    private Uri cameraUri;

    public PhotoPickAdapter(Context context, PhotoPickBean pickBean) {
        this.context = context;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        this.imageSize = metrics.widthPixels / pickBean.getSpanCount();
        this.pickMode = pickBean.getPickMode();
        this.maxPickSize = pickBean.getMaxPickSize();
        this.clipCircle = pickBean.getClipMode();
        this.showCamera = pickBean.isShowCamera();
        this.isClipPhoto = pickBean.isClipPhoto();
        this.isOriginalPicture = pickBean.isOriginalPicture();
        this.imageLoader = pickBean.getImageLoader();
    }

    public void refresh(List<Photo> photos) {
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
        return showCamera ? (photos == null ? 0 : photos.size() + 1) : (photos == null ? 0 : photos.size());
    }

    private Photo getItem(int position) {
        return showCamera ? photos.get(position - 1) : photos.get(position);
    }

    private class PhotoPickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GalleryImageView imageView;
        private CheckBox checkbox;

        public PhotoPickViewHolder(View view) {
            super(view);
            imageView = (GalleryImageView) itemView.findViewById(R.id.imageView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            imageView.getLayoutParams().height = imageSize;
            imageView.getLayoutParams().width = imageSize;
            checkbox.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void showData(int position) {
            if (showCamera && position == 0) {
                checkbox.setVisibility(View.GONE);
                imageView.setImageResource(R.mipmap.take_photo);
            } else {
                Photo photo = getItem(position);
                if (isClipPhoto) {
                    checkbox.setVisibility(View.GONE);
                } else {
                    checkbox.setVisibility(View.VISIBLE);
                    checkbox.setChecked(selectPhotos.contains(photo.getPath()));
                }
                String url = photo.getPath();
                imageLoader.displayImage(context, url, imageView, true);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == R.id.checkbox) {
                if (selectPhotos.contains(getItem(position).getPath())) {
                    checkbox.setChecked(false);
                    selectPhotos.remove(getItem(position).getPath());
                } else {
                    if (selectPhotos.size() == maxPickSize) {
                        checkbox.setChecked(false);
                        return;
                    } else {
                        checkbox.setChecked(true);
                        selectPhotos.add(getItem(position).getPath());
                    }
                }
                if (onUpdateListener != null) {
                    onUpdateListener.updateToolBarTitle(getTitle());
                }
            } else if (view.getId() == R.id.photo_pick_rl) {
                if (showCamera && position == 0) {
                    //权限检查
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!PermissionHelper.checkPermission((Activity) context, Manifest.permission.CAMERA))
                            PermissionHelper.requestPermission((Activity) context, PhotoPickActivity.REQUEST_CODE_CAMERA, Manifest.permission.CAMERA);
                        else selectPicFromCamera();
                    } else selectPicFromCamera();
                } else if (isClipPhoto) {
                    //头像裁剪
                    startClipPic(getItem(position).getPath());
                } else {
                    //查看大图
                    new PhotoPreviewConfig.Builder((Activity) context)
                            .setPosition(showCamera ? position - 1 : position)
                            .setMaxPickSize(maxPickSize)
                            .setPhotos(photos)
                            .setSelectPhotos(selectPhotos)
                            .setOriginalPicture(isOriginalPicture)
                            .build();
                }
            }
        }
    }


    /**
     * TODO 裁剪图片
     *
     * @param picPath
     */
    public void startClipPic(String picPath) {
        String imagePath = ImageUtils.getImagePath(context, "/Crop/");
        File corpFile = new File(imagePath + ImageUtils.createFile());
        UCropUtils.start((Activity) context, new File(picPath), corpFile, clipCircle);
    }

    /**
     * 启动Camera拍照
     */
    public void selectPicFromCamera() {
        if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.cannot_take_pic, Toast.LENGTH_SHORT).show();
            return;
        }
        // 直接将拍到的照片存到手机默认的文件夹
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        cameraUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        ((Activity) context).startActivityForResult(intent, PhotoPickActivity.REQUEST_CODE_SHOW_CAMERA);
    }

    public Uri getCameraUri() {
        return cameraUri;
    }

    //如果是多选title才会变化，要不然单选的没有变
    public String getTitle() {
        String title = context.getString(R.string.select_photo);
        if (pickMode == PhotoPickConfig.MODE_PICK_MORE && selectPhotos.size() >= 1) {//不是单选，更新title
            title = selectPhotos.size() + "/" + maxPickSize;
        }
        return title;
    }

    /**
     * 获取已经选择了的图片
     *
     * @return selected photos
     */
    public ArrayList<String> getSelectPhotos() {
        return selectPhotos;
    }

    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void updateToolBarTitle(String title);
    }
}
