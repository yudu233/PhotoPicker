package rain.coder.photopicker.loader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import rain.coder.photopicker.bean.PhotoDirectory;
import rain.coder.photopicker.data.Data;

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
                if (cursor == null) return;

                List<PhotoDirectory> directories = Data.getDataFromCursor(context, cursor, checkImageStatus);
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
     * @param args           Bundle
     * @param resultCallback PhotosResultCallback
     */
    public static void getPhotoDirs(final AppCompatActivity activity, final Bundle args, final PhotosResultCallback resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(0, args, new PhotoDirLoaderCallbacks(activity, true, resultCallback));

    }

    static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private Context context;
        private PhotosResultCallback resultCallback;
        private boolean checkImageStatus;//是否检查图片已经损坏

        public PhotoDirLoaderCallbacks(Context context, boolean checkImageStatus, PhotosResultCallback resultCallback) {
            this.context = context;
            this.resultCallback = resultCallback;
            this.checkImageStatus = checkImageStatus;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoDirectoryLoader(context);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data == null) return;

            List<PhotoDirectory> directories = Data.getDataFromCursor(context, data, checkImageStatus);
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
        void onResultCallback(List<PhotoDirectory> directories);
    }

}
