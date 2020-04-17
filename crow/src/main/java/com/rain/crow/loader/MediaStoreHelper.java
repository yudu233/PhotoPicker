package com.rain.crow.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.rain.crow.bean.MediaData;
import com.rain.crow.bean.MediaDirectory;
import com.rain.crow.utils.ExternalStorage;
import com.rain.crow.utils.MimeType;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2019/7/10 21:13
 * @filename : LocalMediaLoader
 * @describe : 媒体资源获取
 */

public class MediaStoreHelper implements LoaderManager.LoaderCallbacks<Cursor> {

    private LoaderManager mLoaderManager;
    private WeakReference<Context> mContext;
    private boolean showGif;        //是否展示gif
    private int mineType;           //文件类型
    private PhotosResultCallback mResultCallback;

    /**
     * 获取媒体资源
     *
     * @param activity       上下文
     * @param type           媒体类型
     * @param showGif        是否获取Gif资源
     * @param resultCallback 回调
     */
    public void getData(final AppCompatActivity activity, int type, boolean showGif, final PhotosResultCallback resultCallback) {
        this.mContext = new WeakReference<>(activity);
        this.mResultCallback = resultCallback;
        this.showGif = showGif;
        this.mineType = type;
        this.mLoaderManager = activity.getSupportLoaderManager();
        this.mLoaderManager.initLoader(type, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        return new LocalMediaLoader(context, mineType, showGif);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) return;
        if (data == null || data.isClosed()) return;
        List<MediaDirectory> directories = new ArrayList<>();
        MediaDirectory photoDirectoryAll = new MediaDirectory();


        while (data.moveToNext()) {
            int media_id = data.getInt(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[0]));
            int media_width = data.getInt(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[1]));
            int media_height = data.getInt(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[2]));
            // 使用DURATION获取的时长不准确
            // media_duration = data.getLong(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[3]));
            String media_path = data.getString(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[4]));
            String media_type = data.getString(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[5]));
            long media_size = data.getLong(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[6]));
            String media_dirId = data.getString(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[7]));
            String media_dirName = data.getString(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[8]));
            String media_directoryPath = media_path.substring(0, media_path.lastIndexOf(File.separator));

            long media_duration = MimeType.isVideo(media_type) ? MimeType.getVideoDuration(media_path) : 0;

            //判断文件是否损坏
            boolean isDamage = ExternalStorage.getInstance().checkImageIsDamage(media_width, media_path);
            if (isDamage) continue;
            MediaData mediaData = getMediaData(media_id, media_path, media_size, media_duration, mineType, media_type, media_width, media_height);
            MediaDirectory mediaDirectory = new MediaDirectory();
            mediaDirectory.setId(media_dirId);
            mediaDirectory.setDirPath(media_directoryPath);
            mediaDirectory.setName(media_dirName);

            if (!directories.contains(mediaDirectory)) {
                mediaDirectory.setCoverPath(media_path);
                mediaDirectory.addMediaData(mediaData);
                directories.add(mediaDirectory);
            } else {
                directories.get(directories.indexOf(mediaDirectory)).addMediaData(mediaData);
            }
            photoDirectoryAll.addMediaData(mediaData);
        }

        photoDirectoryAll.setName(MimeType.getTitle(mineType, context));
        photoDirectoryAll.setId("ALL");
        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
            photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }
        directories.add(0, photoDirectoryAll);
        data.close();

        if (mResultCallback != null) {
            mResultCallback.onResultCallback(directories);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onDestory() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(mineType);
        }
        mResultCallback = null;
    }


    public interface PhotosResultCallback {
        void onResultCallback(List<MediaDirectory> directories);
    }

    private static MediaData getMediaData(int mediaId, String mediaPath, long mediaSize, long duration, int mimeType, String mediaType, int mediaWidth, int mediaHeight) {
        MediaData mediaData = new MediaData();
        mediaData.setMediaId(mediaId);
        mediaData.setOriginalPath(mediaPath);
        mediaData.setOriginalSize(mediaSize);
        mediaData.setDuration(duration);
        mediaData.setMimeType(mimeType);
        mediaData.setImageType(mediaType);
        mediaData.setImageWidth(mediaWidth);
        mediaData.setImageHeight(mediaHeight);
        return mediaData;
    }

}
