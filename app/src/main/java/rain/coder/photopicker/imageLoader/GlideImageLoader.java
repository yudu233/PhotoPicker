package rain.coder.photopicker.imageLoader;

import android.content.Context;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import rain.coder.photopicker.R;
import rain.coder.photopicker.loader.ImageLoader;
import rain.coder.photopicker.weidget.GalleryImageView;

/**
 * Describe :GlideImageLoader
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */

public class GlideImageLoader implements ImageLoader {

    private final static String TAG = "GlideImageLoader";

    @Override
    public void displayImage(Context context, String path, GalleryImageView galleryImageView, boolean resize) {
        DrawableRequestBuilder builder = null;

        builder = Glide.with(context)
                .load(path);
        if (resize)
            builder = builder.centerCrop();
        builder.crossFade()
                .error(context.getResources().getDrawable(R.mipmap.error_image))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(galleryImageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}
/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */