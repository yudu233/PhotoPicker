package com.rain.crow.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PhotoPreviewBean implements Parcelable {

    private int position;
    private ArrayList<MediaData> photos;
    private ArrayList<String> selectPhotos;
    private ArrayList<MediaData> selectPhotosInfo;
    private int maxPickSize;
    private boolean showOriginalButton;//是否显示原图按钮
    private boolean isSelectOrigin; //是否选中原图

    public PhotoPreviewBean() {
    }

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

    public boolean isShowOriginalButton() {
        return showOriginalButton;
    }

    public void setShowOriginalButton(boolean originalPicture) {
        this.showOriginalButton = originalPicture;
    }

    public boolean isSelectOrigin() {
        return isSelectOrigin;
    }

    public void setSelectOrigin(boolean selectOrigin) {
        isSelectOrigin = selectOrigin;
    }

    protected PhotoPreviewBean(Parcel in) {
        position = in.readInt();
        photos = in.createTypedArrayList(MediaData.CREATOR);
        selectPhotos = in.createStringArrayList();
        selectPhotosInfo = in.createTypedArrayList(MediaData.CREATOR);
        maxPickSize = in.readInt();
        showOriginalButton = in.readByte() != 0;
        isSelectOrigin = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeTypedList(photos);
        dest.writeStringList(selectPhotos);
        dest.writeTypedList(selectPhotosInfo);
        dest.writeInt(maxPickSize);
        dest.writeByte((byte) (showOriginalButton ? 1 : 0));
        dest.writeByte((byte) (isSelectOrigin ? 1 : 0));

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoPreviewBean> CREATOR = new Creator<PhotoPreviewBean>() {
        @Override
        public PhotoPreviewBean createFromParcel(Parcel in) {
            return new PhotoPreviewBean(in);
        }

        @Override
        public PhotoPreviewBean[] newArray(int size) {
            return new PhotoPreviewBean[size];
        }
    };
}
