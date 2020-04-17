package com.rain.crow.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe :媒体文件信息
 * Created by Rain on 17-4-28.
 */
public class MediaData implements Parcelable {

    private int mediaId;

    /**
     * 原始路径
     */
    private String originalPath;

    /**
     * 原图大小
     */
    private long originalSize;

    /**
     * 压缩图片路径
     */
    private String compressionPath;

    /**
     * 裁剪图片路径
     */
    private String clipImagePath;

    /**
     * 相机图片路径
     */
    private String cameraImagePath;

    /**
     * 图片- 宽
     */
    private int imageWidth;

    /**
     * 图片 - 高
     */
    private int imageHeight;

    /**
     * 文件大小
     */
    private long mediaSize;

    /**
     * 媒体文件类型
     */
    private int mimeType;

    /**
     * 图片类型
     */
    private String imageType;

    /**
     * 媒体文件时长（音视频）
     */
    private long duration;


    /**
     * 是否压缩
     */
    private boolean isCompressed;

    /**
     * 是否裁剪
     */
    private boolean isClip;

    /**
     * 是否是相机图片
     */
    private boolean isCamera;


    public MediaData(int id, String path) {
        this.originalPath = path;
        this.mediaId = id;
    }

    public MediaData() {
    }


    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public long getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }

    public String getCompressionPath() {
        return compressionPath;
    }

    public void setCompressionPath(String compressionPath) {
        this.compressionPath = compressionPath;
    }

    public String getClipImagePath() {
        return clipImagePath;
    }

    public void setClipImagePath(String clipImagePath) {
        this.clipImagePath = clipImagePath;
    }

    public String getCameraImagePath() {
        return cameraImagePath;
    }

    public void setCameraImagePath(String cameraImagePath) {
        this.cameraImagePath = cameraImagePath;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public long getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(long mediaSize) {
        this.mediaSize = mediaSize;
    }

    public int getMimeType() {
        return mimeType;
    }

    public void setMimeType(int mimeType) {
        this.mimeType = mimeType;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    public boolean isClip() {
        return isClip;
    }

    public void setClip(boolean clip) {
        isClip = clip;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setCamera(boolean camera) {
        isCamera = camera;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaData)) return false;

        MediaData mediaData = (MediaData) o;

        return mediaId == mediaData.mediaId;
    }

    @Override
    public int hashCode() {
        return mediaId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mediaId);
        dest.writeString(this.originalPath);
        dest.writeLong(this.originalSize);
        dest.writeString(this.compressionPath);
        dest.writeString(this.clipImagePath);
        dest.writeString(this.cameraImagePath);
        dest.writeInt(this.imageWidth);
        dest.writeInt(this.imageHeight);
        dest.writeLong(this.mediaSize);
        dest.writeInt(this.mimeType);
        dest.writeString(this.imageType);
        dest.writeLong(this.duration);
        dest.writeByte(this.isCompressed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isClip ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCamera ? (byte) 1 : (byte) 0);

    }

    protected MediaData(Parcel in) {
        this.mediaId = in.readInt();
        this.originalPath = in.readString();
        this.originalSize = in.readLong();
        this.compressionPath = in.readString();
        this.clipImagePath = in.readString();
        this.cameraImagePath = in.readString();
        this.imageWidth = in.readInt();
        this.imageHeight = in.readInt();
        this.mediaSize = in.readLong();
        this.mimeType = in.readInt();
        this.imageType = in.readString();
        this.duration = in.readLong();
        this.isCompressed = in.readByte() != 0;
        this.isClip = in.readByte() != 0;
        this.isCamera = in.readByte() != 0;
    }

    public static final Creator<MediaData> CREATOR = new Creator<MediaData>() {
        @Override
        public MediaData createFromParcel(Parcel source) {
            return new MediaData(source);
        }

        @Override
        public MediaData[] newArray(int size) {
            return new MediaData[size];
        }
    };
}
