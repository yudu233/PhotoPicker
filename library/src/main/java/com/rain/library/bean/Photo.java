package com.rain.library.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe :全部照片
 * Created by Rain on 17-4-28.
 */
public class Photo implements Parcelable {

    private int originalImageId;
    private String originalImagePath;
    private long originalImageSize;

    private String thumbnailsImagePath;

    private String compressionImagePath;

    private String clipImagePath;

    private String cameraImagePath;

    public Photo(int id, String path) {
        this.originalImageId = id;
        this.originalImagePath = path;
    }

    public Photo(String path, int type) {
        switch (type) {
            case 0:     //压缩图片路径
                compressionImagePath = path;
                break;
            case 1:     //裁剪图片路径
                clipImagePath = path;
                break;
            case 2:     //相机图片路径
                cameraImagePath = path;
                break;
        }
    }

    public Photo(int id, String path, long size, String thumbnailsImagePath) {
        this.originalImageId = id;
        this.originalImagePath = path;
        this.originalImageSize = size;
        this.thumbnailsImagePath = thumbnailsImagePath;
    }

    public Photo() {
    }

    private Photo(Parcel in) {
        originalImageId = in.readInt();
        originalImagePath = in.readString();
        originalImageSize = in.readLong();
        thumbnailsImagePath = in.readString();
        compressionImagePath = in.readString();
        clipImagePath = in.readString();
        cameraImagePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(originalImageId);
        dest.writeString(originalImagePath);
        dest.writeLong(originalImageSize);
        dest.writeString(thumbnailsImagePath);
        dest.writeString(compressionImagePath);
        dest.writeString(clipImagePath);
        dest.writeString(cameraImagePath);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return originalImageId == photo.originalImageId;
    }

    @Override
    public int hashCode() {
        return originalImageId;
    }

    public String getOriginalImagePath() {
        return originalImagePath;
    }

    public void setOriginalImagePath(String path) {
        this.originalImagePath = path;
    }

    public int getOriginalImageId() {
        return originalImageId;
    }

    public void setOriginalImageId(int id) {
        this.originalImageId = id;
    }

    public long getOriginalImageSize() {
        return originalImageSize;
    }

    public void setOriginalImageSize(long size) {
        this.originalImageSize = size;
    }

    public String getThumbnailsImagePath() {
        return thumbnailsImagePath;
    }

    public void setThumbnailsImagePath(String thumbnailsImagePath) {
        this.thumbnailsImagePath = thumbnailsImagePath;
    }

    public String getCompressionImagePath() {
        return compressionImagePath;
    }

    public void setCompressionImagePath(String compressionImagePath) {
        this.compressionImagePath = compressionImagePath;
    }

    public String getClipImagePath() {
        return clipImagePath;
    }

    public void setClipImagePath(String clipImagePath) {
        this.clipImagePath = clipImagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
