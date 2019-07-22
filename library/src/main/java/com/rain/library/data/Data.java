package com.rain.library.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.rain.library.R;
import com.rain.library.bean.MediaData;
import com.rain.library.bean.MediaDirectory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.HEIGHT;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.WIDTH;

public class Data {
    public final static int INDEX_ALL_PHOTOS = 0;

    public static List<MediaDirectory> getDataFromCursor(Context context, Cursor data, boolean checkImageStatus) {
        List<MediaDirectory> directories = new ArrayList<>();
        MediaDirectory photoDirectoryAll = new MediaDirectory();
        photoDirectoryAll.setName(context.getString(R.string.all_photo));
        photoDirectoryAll.setId("ALL");

        while (data.moveToNext()) {
            int originalImageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String originalImagePath = data.getString(data.getColumnIndexOrThrow(DATA));
            String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            String directoryName = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            long originalImageSize = data.getLong(data.getColumnIndexOrThrow(SIZE));
            int imageWidth = data.getInt(data.getColumnIndexOrThrow(WIDTH));
            int imageHeight = data.getInt(data.getColumnIndexOrThrow(HEIGHT));
            String photoDirectoryPath = originalImagePath.substring(0, originalImagePath.lastIndexOf(File.separator));

            MediaData photo = getPhoto(originalImageId, originalImagePath, originalImageSize, imageWidth, imageHeight);

            if (checkImageStatus) {
                MediaDirectory mediaDirectory = new MediaDirectory();
                mediaDirectory.setId(bucketId);
                mediaDirectory.setName(directoryName);
                mediaDirectory.setDirPath(photoDirectoryPath);
                if (!directories.contains(mediaDirectory)) {
                    mediaDirectory.setCoverPath(originalImagePath);
                    mediaDirectory.addMediaData(originalImageId, originalImagePath);
                    directories.add(mediaDirectory);
                } else {
                    directories.get(directories.indexOf(mediaDirectory)).addMediaData(photo);
                }
                photoDirectoryAll.addMediaData(photo);
            }
        }
        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
            photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }
        directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);

        return directories;
    }

    @NonNull
    private static MediaData getPhoto(int originalImageId, String originalImagePath, long originalImageSize, int imageWidth, int imageHeight) {
        MediaData photo = new MediaData();
        photo.setMediaId(originalImageId);
        photo.setOriginalPath(originalImagePath);
        photo.setOriginalSize(originalImageSize);
        photo.setImageWidth(imageWidth);
        photo.setImageHeight(imageHeight);
        return photo;
    }
}
