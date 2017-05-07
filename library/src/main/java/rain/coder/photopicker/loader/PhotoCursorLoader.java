package rain.coder.photopicker.loader;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

public class PhotoCursorLoader {

    @NonNull
    private Uri uri;
    @Nullable
    private String[] projection;
    @Nullable
    private String selection;
    @Nullable
    private String[] selectionArgs;
    @Nullable
    private String sortOrder;

    private boolean showGif;

    public PhotoCursorLoader() {
        //default ，默认配置
        setShowGif(true);//展示gif
        setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        setProjection(IMAGE_PROJECTION);
        setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? " + (showGif ? ("or " + MIME_TYPE + "=?") : ""));
        setShowGif(isShowGif());
        setSelectionArgs(selectionArgs);
        setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
//        setSortOrder(MediaStore.Images.Media.DATE_MODIFIED);
    }

    public PhotoCursorLoader(@NonNull Uri uri, @Nullable String[] projection,
                             @Nullable String selection, @Nullable String[] selectionArgs,
                             @Nullable String sortOrder) {
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    private final static String IMAGE_JPEG = "image/jpeg";
    private final static String IMAGE_PNG = "image/png";
    private final static String IMAGE_GIF = "image/gif";

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE
    };

    @NonNull
    public Uri getUri() {
        return uri;
    }

    public void setUri(@NonNull Uri uri) {
        this.uri = uri;
    }

    @Nullable
    public String[] getProjection() {
        return projection;
    }

    public void setProjection(@Nullable String[] projection) {
        this.projection = projection;
    }

    @Nullable
    public String getSelection() {
        return selection;
    }

    public void setSelection(@Nullable String selection) {
        this.selection = selection;
    }

    @Nullable
    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public void setSelectionArgs(@Nullable String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    @Nullable
    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(@Nullable String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
        if (showGif) {
            selectionArgs = new String[]{IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF};
        } else {
            selectionArgs = new String[]{IMAGE_JPEG, IMAGE_PNG};
        }
    }
}
