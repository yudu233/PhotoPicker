package com.rain.library.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.rain.library.R;
import com.rain.library.bean.Photo;
import com.rain.library.bean.PhotoDirectory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.HEIGHT;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.WIDTH;

public class Data {
    public final static int INDEX_ALL_PHOTOS = 0;

    public static List<PhotoDirectory> getDataFromCursor(Context context, Cursor data, boolean checkImageStatus) {
        List<PhotoDirectory> directories = new ArrayList<>();
        PhotoDirectory photoDirectoryAll = new PhotoDirectory();
        photoDirectoryAll.setName(context.getString(R.string.all_photo));
        photoDirectoryAll.setId("ALL");

        while (data.moveToNext()) {
            //Original image id
            int originalImageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            //BucketId：equals path.toLowerCase.hashCode(), see MediaProvider.computeBucketValues()
            String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            //Original image directory name
            String directoryName = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            //Original image path (absolute path)
            String originalImagePath = data.getString(data.getColumnIndexOrThrow(DATA));
            //Byte is the size unit of the original file
            long originalImageSize = data.getLong(data.getColumnIndexOrThrow(SIZE));
            //image width
            String imageWidth = data.getString(data.getColumnIndexOrThrow(WIDTH));
            //image height
            String imageHeight = data.getString(data.getColumnIndexOrThrow(HEIGHT));
            //photo directoryPath
            String photoDirectoryPath = originalImagePath.substring(0, originalImagePath.lastIndexOf(File.separator));
            //Thumbnails image path(absolute path)
            String thumbnailsImagePath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));

            Photo photo = getPhoto(originalImageId, originalImagePath, originalImageSize, imageWidth, imageHeight, thumbnailsImagePath);

            if (checkImageStatus) {
                PhotoDirectory photoDirectory = new PhotoDirectory();
                photoDirectory.setId(bucketId);
                photoDirectory.setName(directoryName);
                photoDirectory.setDirPath(photoDirectoryPath);
                if (!directories.contains(photoDirectory)) {
                    photoDirectory.setCoverPath(originalImagePath);
                    photoDirectory.addPhoto(originalImageId, originalImagePath);
                    photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                    directories.add(photoDirectory);
                } else {
                    directories.get(directories.indexOf(photoDirectory)).addPhoto(photo);
                }
                photoDirectoryAll.addPhoto(photo);
            }
        }
        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
            photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }
        directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);

        return directories;
    }

    @NonNull
    private static Photo getPhoto(int originalImageId, String originalImagePath, long originalImageSize, String imageWidth, String imageHeight, String thumbnailsImagePath) {
        Photo photo = new Photo();
        photo.setOriginalImageId(originalImageId);
        photo.setOriginalImagePath(originalImagePath);
        photo.setOriginalImageSize(originalImageSize);
        photo.setThumbnailsImagePath(thumbnailsImagePath);
        photo.setImageWidth(imageWidth);
        photo.setImageHeight(imageHeight);
        return photo;
    }


}
