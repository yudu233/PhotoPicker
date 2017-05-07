package rain.coder.photopicker.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

public class PhotoDirectoryLoader extends CursorLoader {

    private final static String IMAGE_JPEG = "image/jpeg";
    private final static String IMAGE_PNG = "image/png";
    private final static String IMAGE_GIF = "image/gif";

    final String[] IMAGE_PROJECTION = {
            Media._ID,
            Media.DATA,
            Media.BUCKET_ID,
            Media.BUCKET_DISPLAY_NAME,
            Media.DATE_ADDED,
            Media.SIZE
    };

    public PhotoDirectoryLoader(Context context){
        this(context,false);
    }

    public PhotoDirectoryLoader(Context context, boolean showGif) {
        super(context);

        setProjection(IMAGE_PROJECTION);
        setUri(Media.EXTERNAL_CONTENT_URI);
        setSortOrder(Media.DATE_ADDED + " DESC");

        setSelection(
                MIME_TYPE + "=? or " + MIME_TYPE + "=? " + (showGif ? ("or " + MIME_TYPE + "=?") : ""));
        String[] selectionArgs;
        if (showGif) {
            selectionArgs = new String[]{IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF};
        } else {
            selectionArgs = new String[]{IMAGE_JPEG, IMAGE_PNG};
        }
        setSelectionArgs(selectionArgs);
    }


    private PhotoDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }


}
