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

    private String imageWidth;

    private String imageHeight;

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

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.originalImageId);
        dest.writeString(this.originalImagePath);
        dest.writeLong(this.originalImageSize);
        dest.writeString(this.thumbnailsImagePath);
        dest.writeString(this.compressionImagePath);
        dest.writeString(this.clipImagePath);
        dest.writeString(this.cameraImagePath);
        dest.writeString(this.imageWidth);
        dest.writeString(this.imageHeight);
    }

    protected Photo(Parcel in) {
        this.originalImageId = in.readInt();
        this.originalImagePath = in.readString();
        this.originalImageSize = in.readLong();
        this.thumbnailsImagePath = in.readString();
        this.compressionImagePath = in.readString();
        this.clipImagePath = in.readString();
        this.cameraImagePath = in.readString();
        this.imageWidth = in.readString();
        this.imageHeight = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
