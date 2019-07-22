package com.rain.library.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;

import com.rain.library.utils.MimeType;

import java.util.Locale;

/**
 * @author:duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2019/7/10 21:13
 * @filename : LocalMediaLoader
 * @describe :
 */
public class LocalMediaLoader extends CursorLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";

    private static final String NOT_GIF = "!='image/gif'";

    public static final String BUCKET_ID = "bucket_id";

    public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";

    // 过滤掉小于500毫秒的录音
    private static final int AUDIO_DURATION = 500;

    private long videoMaxS = 0;
    private long videoMinS = 0;

    /**
     * 媒体文件数据库字段
     */
    public static final String[] FILE_PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.Video.Media.DURATION,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            BUCKET_ID,
            BUCKET_DISPLAY_NAME,
    };

    // 图片
    private static final String SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_NOT_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF;

    // 查询条件(音视频)
    private static String getSelectionArgsForSingleMediaCondition(String time_condition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + time_condition;
    }

    // 全部模式下条件
    private static String getSelectionArgsForAllMediaCondition(String time_condition, boolean isGif) {
        String condition = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + time_condition) + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        return condition;
    }

    // 获取图片or视频
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

    /**
     * 获取指定类型的文件
     *
     * @param mediaType
     * @return
     */
    private static String[] getMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }


    public LocalMediaLoader(@NonNull Context context, int mimeType, boolean showGif) {
        super(context);
        setUri(QUERY_URI);
        setProjection(FILE_PROJECTION);
        setSortOrder(ORDER_BY);

        switch (mimeType) {
            case MimeType.TYPE_ALL:
                String all_selection = getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), showGif);
                setSelection(all_selection);
                setSelectionArgs(SELECTION_ALL_ARGS);
                break;
            case MimeType.TYPE_IMAGE:
                String[] MEDIA_TYPE_IMAGE = getMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                setSelection(showGif ? SELECTION : SELECTION_NOT_GIF);
                setSelectionArgs(MEDIA_TYPE_IMAGE);
                break;
            case MimeType.TYPE_VIDEO:
                String video_selection = getSelectionArgsForSingleMediaCondition(getDurationCondition(0, 0));
                String[] MEDIA_TYPE_VIDEO = getMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                setSelection(video_selection);
                setSelectionArgs(MEDIA_TYPE_VIDEO);
                break;
            case MimeType.TYPE_AUDIO:
                String audio_selection = getSelectionArgsForSingleMediaCondition(getDurationCondition(0, AUDIO_DURATION));
                setSelection(audio_selection);
                String[] MEDIA_TYPE_AUDIO = getMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
                setSelectionArgs(MEDIA_TYPE_AUDIO);
                break;
        }
    }


    /**
     * 获取视频(最长或最小时间)
     *
     * @param exMaxLimit
     * @param exMinLimit
     * @return
     */
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = videoMaxS == 0 ? Long.MAX_VALUE : videoMaxS;
        if (exMaxLimit != 0) maxS = Math.min(maxS, exMaxLimit);

        return String.format(Locale.CHINA, "%d <%s duration and duration <= %d",
                Math.max(exMinLimit, videoMinS),
                Math.max(exMinLimit, videoMinS) == 0 ? "" : "=",
                maxS);
    }


}
