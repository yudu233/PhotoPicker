package com.rain.library.loader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rain.library.R;
import com.rain.library.bean.MediaData;
import com.rain.library.bean.MediaDirectory;
import com.rain.library.data.Data;
import com.rain.library.utils.MimeType;
import com.rain.library.utils.Rlog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MediaStoreHelper {

    /**
     * 第一种方式
     *
     * @param context        Activity
     * @param resultCallback PhotosResultCallback
     */
    public static void getPhotoDirs(final Activity context, final PhotosResultCallback resultCallback) {
        getPhotoDirs(context, resultCallback, true);
    }

    public static void getPhotoDirs(final Activity context, final PhotosResultCallback resultCallback, final boolean checkImageStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PhotoCursorLoader loader = new PhotoCursorLoader();
                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(loader.getUri(), loader.getProjection(), loader.getSelection(), loader.getSelectionArgs(), loader.getSortOrder());

                Cursor thumbnailCursor = contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA}, null, null, null);
                if (cursor == null) return;

                List<MediaDirectory> directories = Data.getDataFromCursor(context, cursor, checkImageStatus);
                cursor.close();
                if (resultCallback != null) {
                    resultCallback.onResultCallback(directories);
                }
            }
        }).start();
    }

    /**
     * 第二种方式
     *
     * @param activity       AppCompatActivity
     * @param resultCallback PhotosResultCallback
     */
    public static void getData(final AppCompatActivity activity, int type, boolean showGif, final PhotosResultCallback resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(type, null, new PhotoDirLoaderCallbacks(activity, type, showGif, resultCallback));

    }

    static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private Context context;
        private boolean showGif;        //是否展示gif
        private int mineType;           //文件类型
        private PhotosResultCallback resultCallback;

        public PhotoDirLoaderCallbacks(Context context, int type, boolean showGif, PhotosResultCallback resultCallback) {
            this.context = context;
            this.resultCallback = resultCallback;
            this.showGif = showGif;
            this.mineType = type;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new LocalMediaLoader(context, mineType, showGif);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null || data.isClosed()) return;
            List<MediaDirectory> directories = new ArrayList<>();
            MediaDirectory photoDirectoryAll = new MediaDirectory();


            while (data.moveToNext()) {
                long media_duration;
                Log.e("Rain", data.getInt(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[1])) + "---FileColumns._WIDTH---");
                Log.e("Rain", data.getInt(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[2])) + "---FileColumns._HEIGHT---");
                Log.e("Rain", data.getLong(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[3])) + "---FileColumns._DURATION---");
                Log.e("Rain", data.getString(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[4])) + "---FileColumns._DATA---");
                Log.e("Rain", data.getString(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[5])) + "---FileColumns.MIME_TYPE---");
                Log.e("Rain", data.getLong(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[6])) + "---FileColumns.SIZE---");
                Log.e("Rain", data.getString(data.getColumnIndexOrThrow(LocalMediaLoader.FILE_PROJECTION[8])) + "---BUCKET_DISPLAY_NAME---");
                Log.e("Rain", "------------------------------------------------------");

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

                if (MimeType.isVideo(media_type)) {
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    media.setDataSource(media_path);
                    String duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    media_duration = Long.parseLong(duration);
                    media.release();
                } else {
                    media_duration = 0;
                }

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

            if (resultCallback != null) {
                resultCallback.onResultCallback(directories);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
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
