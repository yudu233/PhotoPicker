package com.rain.crow.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe :相册
 * Created by Rain on 17-4-28.
 */
public class MediaDirectory {

    /**
     * 相册id
     */
    private String id;

    /**
     * 封面图片
     */
    private String coverPath;

    /**
     * 相册名称
     */
    private String name;

    /**
     * 相册路径
     */
    private String dirPath;

    /**
     * 单个相册内所有文件
     */
    private ArrayList<MediaData> mediaDatas = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaDirectory)) return false;

        MediaDirectory directory = (MediaDirectory) o;

        if (!id.equals(directory.id)) return false;
        if (name == null) return false;
        return name.equals(directory.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public ArrayList<MediaData> getMediaData() {
        return mediaDatas;
    }

    public void setMediaData(ArrayList<MediaData> mediaDatas) {
        this.mediaDatas = mediaDatas;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(mediaDatas.size());
        for (MediaData mediaData : mediaDatas) {
            paths.add(mediaData.getOriginalPath());
        }
        return paths;
    }

    public void addMediaData(int id, String path) {
        mediaDatas.add(new MediaData(id, path));
    }

    public void addMediaData(MediaData mediaData) {
        mediaDatas.add(mediaData);
    }
}
