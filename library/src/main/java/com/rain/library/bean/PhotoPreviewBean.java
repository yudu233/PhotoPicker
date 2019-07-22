package com.rain.library.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rain.library.impl.PhotoSelectCallback;

import java.util.ArrayList;

public class PhotoPreviewBean implements Parcelable {

    private int position;
    private ArrayList<MediaData> photos;
    private ArrayList<String> selectPhotos;
    private ArrayList<MediaData> selectPhotosInfo;
    private int maxPickSize;
    private boolean originalPicture;//是否选择的是原图
    private PhotoSelectCallback callback;

    public PhotoPreviewBean(){}


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<MediaData> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<MediaData> photos) {
        this.photos = photos;
    }

    public ArrayList<String> getSelectPhotos() {
        return selectPhotos;
    }

    public void setSelectPhotos(ArrayList<String> selectPhotos) {
        this.selectPhotos = selectPhotos;
    }

    public ArrayList<MediaData> getSelectPhotosInfo() {
        return selectPhotosInfo;
    }

    public void setSelectPhotosInfo(ArrayList<MediaData> selectPhotosInfo) {
        this.selectPhotosInfo = selectPhotosInfo;
    }

    public int getMaxPickSize() {
        return maxPickSize;
    }

    public void setMaxPickSize(int maxPickSize) {
        this.maxPickSize = maxPickSize;
    }

    public boolean isOriginalPicture() {
        return originalPicture;
    }

    public void setOriginalPicture(boolean originalPicture) {
        this.originalPicture = originalPicture;
    }

    public PhotoSelectCallback getCallback() {
        return callback;
    }

    public void setCallback(PhotoSelectCallback callback) {
        this.callback = callback;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeTypedList(this.photos);
        dest.writeStringList(this.selectPhotos);
        dest.writeTypedList(this.selectPhotosInfo);
        dest.writeInt(this.maxPickSize);
        dest.writeByte(this.originalPicture ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.callback, flags);
    }

    protected PhotoPreviewBean(Parcel in) {
        this.position = in.readInt();
        this.photos = in.createTypedArrayList(MediaData.CREATOR);
        this.selectPhotos = in.createStringArrayList();
        this.selectPhotosInfo = in.createTypedArrayList(MediaData.CREATOR);
        this.maxPickSize = in.readInt();
        this.originalPicture = in.readByte() != 0;
        this.callback = in.readParcelable(PhotoSelectCallback.class.getClassLoader());
    }

    public static final Creator<PhotoPreviewBean> CREATOR = new Creator<PhotoPreviewBean>() {
        @Override
        public PhotoPreviewBean createFromParcel(Parcel source) {
            return new PhotoPreviewBean(source);
        }

        @Override
        public PhotoPreviewBean[] newArray(int size) {
            return new PhotoPreviewBean[size];
        }
    };
}
