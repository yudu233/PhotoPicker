package com.rain.crow.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.rain.crow.R;
import com.rain.crow.bean.MediaData;

import java.io.File;

/**
 * @author:duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2019/7/10 20:49
 * @filename : MimeType
 * @describe :
 */
public class MimeType {

    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;
    public final static int TYPE_AUDIO = 3;

    public static int ofAll() {
        return MimeType.TYPE_ALL;
    }

    public static int ofImage() {
        return MimeType.TYPE_IMAGE;
    }

    public static int ofVideo() {
        return MimeType.TYPE_VIDEO;
    }

    public static int ofAudio() {
        return MimeType.TYPE_AUDIO;
    }


    public static String getTitle(int type, Context context) {
        int redId = -1;

        if (type == ofAll()) {
            redId = R.string.all_media;
        }
        if (type == ofImage()) {
            redId = R.string.all_photo;
        }
        if (type == ofVideo()) {
            redId = R.string.all_video;
        }
        if (type == ofAudio()) {
            redId = R.string.all_audio;
        }
        return context.getString(redId);
    }

    public static int isPictureType(String pictureType) {
        if (UtilsHelper.isEmptyOrNull(pictureType)) {
            return MimeType.TYPE_IMAGE;
        }
        switch (pictureType) {
            case "image/png":
            case "image/PNG":
            case "image/jpeg":
            case "image/JPEG":
            case "image/webp":
            case "image/WEBP":
            case "image/gif":
            case "image/bmp":
            case "image/GIF":
            case "imagex-ms-bmp":
                return MimeType.TYPE_IMAGE;
            case "video/3gp":
            case "video/3gpp":
            case "video/3gpp2":
            case "video/avi":
            case "video/mp4":
            case "video/quicktime":
            case "video/x-msvideo":
            case "video/x-matroska":
            case "video/mpeg":
            case "video/webm":
            case "video/mp2ts":
                return MimeType.TYPE_VIDEO;
        }
        return MimeType.TYPE_IMAGE;
    }

    /**
     * 是否是gif
     *
     * @param pictureType
     * @return
     */
    public static boolean isGif(String pictureType) {
        if (UtilsHelper.isEmptyOrNull(pictureType)) {
            return false;
        }
        switch (pictureType) {
            case "image/gif":
            case "image/GIF":
                return true;
        }
        return false;
    }

    /**
     * 是否是视频
     *
     * @param pictureType
     * @return
     */
    public static boolean isVideo(String pictureType) {
        if (UtilsHelper.isEmptyOrNull(pictureType)) {
            return false;
        }
        switch (pictureType) {
            case "video/3gp":
            case "video/3gpp":
            case "video/3gpp2":
            case "video/avi":
            case "video/mp4":
            case "video/quicktime":
            case "video/x-msvideo":
            case "video/x-matroska":
            case "video/mpeg":
            case "video/webm":
            case "video/mp2ts":
                return true;
        }
        return false;
    }

    /**
     * 是否是长图
     *
     * @param media
     * @return
     */
    public static boolean isLongImage(MediaData media) {
        if (null != media) {
            int width = media.getImageWidth();
            int height = media.getImageHeight();
            int h = width * 3;
            return height > h;
        }
        return false;
    }

    /**
     * 判断文件类型是图片还是视频
     *
     * @param file
     * @return
     */
    public static String fileToType(File file) {
        if (file != null) {
            String name = file.getName();
            if (name.endsWith(".mp4") || name.endsWith(".avi")
                    || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.startsWith(".mov")) {
                return "video/mp4";
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                    || name.endsWith(".bmp")) {
                return "image/jpeg";
            } else if (name.endsWith(".mp3") || name.endsWith(".amr")
                    || name.endsWith(".aac") || name.endsWith(".war")
                    || name.endsWith(".flac") || name.endsWith(".lamr")) {
                return "audio/mpeg";
            }
        }
        return "image/jpeg";
    }

    /**
     * is type Equal
     *
     * @param p1
     * @param p2
     * @return
     */
    public static boolean mimeToEqual(String p1, String p2) {
        return isPictureType(p1) == isPictureType(p2);
    }

    public static String createImageType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last);
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    public static String createVideoType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last);
                return "video/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "video/mp4";
        }
        return "video/mp4";
    }

    /**
     * Picture or video
     *
     * @return
     */
    public static int pictureToVideo(String pictureType) {
        if (!TextUtils.isEmpty(pictureType)) {
            if (pictureType.startsWith("video")) {
                return MimeType.TYPE_VIDEO;
            }
        }
        return MimeType.TYPE_IMAGE;
    }

    /**
     * 获取图片后缀
     *
     * @param path
     * @return
     */
    public static String getLastImgType(String path) {
        try {
            int index = path.lastIndexOf(".");
            if (index > 0) {
                String imageType = path.substring(index);
                switch (imageType) {
                    case ".png":
                    case ".PNG":
                    case ".jpg":
                    case ".jpeg":
                    case ".JPEG":
                    case ".WEBP":
                    case ".bmp":
                    case ".BMP":
                    case ".webp":
                        return imageType;
                    default:
                        return ".png";
                }
            } else {
                return ".png";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".png";
        }
    }

    /**
     * 获取视频时长
     * @param videoPath
     * @return
     */
    public static long getVideoDuration(String videoPath) {
        long duration;
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            duration = Long.parseLong(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            media.release();
        } catch (Exception e) {
            return 0;
        }
        return duration;
    }
}
