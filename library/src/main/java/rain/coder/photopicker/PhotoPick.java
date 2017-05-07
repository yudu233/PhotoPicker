package rain.coder.photopicker;

import android.content.Context;


public final class PhotoPick {

    private static PhotoPick photoPick;

    public static PhotoPick getInstance() {
        if (photoPick == null) photoPick = new PhotoPick();
        return photoPick;
    }

    private static int toolbarBackGround;
    private static Context mContext;


    public static void init(Context context) {
        init(context, android.R.color.holo_red_light);
    }


    public static void init(Context context, int toolbarBackGroundId) {
        //说明已经初始化过了,不用重复初始化
        if (mContext != null) return;
        toolbarBackGround = toolbarBackGroundId;
        mContext = context.getApplicationContext();
    }

    public static int getToolbarBackGround() {
        return mContext.getResources().getColor(toolbarBackGround);
    }

    public static Context getContext() {
        return mContext;
    }


    public static void setToolbarBackGround(int toolbarBackGroundId) {
        toolbarBackGround = toolbarBackGroundId;
    }


    public static void checkInit() {
        if (mContext == null) {
            throw new NullPointerException("photoLibrary was not initialized,please init in your Application");
        }
    }
}
