package rain.coder.photopicker.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import rain.coder.library.R;
import rain.coder.photopicker.bean.PhotoDirectory;
import rain.coder.photopicker.utils.FileUtils;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;

public class Data {
    public final static int INDEX_ALL_PHOTOS = 0;

    public static List<PhotoDirectory> getDataFromCursor(Context context, Cursor data, boolean checkImageStatus) {
        List<PhotoDirectory> directories = new ArrayList<>();
        PhotoDirectory photoDirectoryAll = new PhotoDirectory();
        photoDirectoryAll.setName(context.getString(R.string.all_photo));
        photoDirectoryAll.setId("ALL");

        while (data.moveToNext()) {

            int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
            String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
            String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
            String path = data.getString(data.getColumnIndexOrThrow(DATA));
            long size = data.getLong(data.getColumnIndexOrThrow(SIZE));

            if (checkImageStatus) {
                if (!FileUtils.checkImgCorrupted(path)) {
                    PhotoDirectory photoDirectory = new PhotoDirectory();
                    photoDirectory.setId(bucketId);
                    photoDirectory.setName(name);

                    if (!directories.contains(photoDirectory)) {
                        photoDirectory.setCoverPath(path);
                        photoDirectory.addPhoto(imageId, path);
                        photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                        directories.add(photoDirectory);
                    } else {
                        directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path, size);
                    }

                    photoDirectoryAll.addPhoto(imageId, path, size);
                }
            } else {

                PhotoDirectory photoDirectory = new PhotoDirectory();
                photoDirectory.setId(bucketId);
                photoDirectory.setName(name);

                if (!directories.contains(photoDirectory)) {
                    photoDirectory.setCoverPath(path);
                    photoDirectory.addPhoto(imageId, path);
                    photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                    directories.add(photoDirectory);
                } else {
                    directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path, size);
                }

                photoDirectoryAll.addPhoto(imageId, path, size);
            }


        }
        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
            photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }
        directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);

        return directories;
    }

}
