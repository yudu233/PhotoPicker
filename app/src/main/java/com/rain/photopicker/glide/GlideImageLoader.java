package com.rain.photopicker.glide;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rain.library.loader.ImageLoader;
import com.rain.photopicker.R;

/**
 * Describe :GlideImageLoader
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */

public class GlideImageLoader implements ImageLoader {

    private final static String TAG = "GlideImageLoader";

    @Override
    public void displayImage(Context context, String originalImagePath, String thumbnailsImagePath, ImageView imageView, boolean resize, boolean loadThumbnailsImage) {
        DrawableRequestBuilder builder;
        if (loadThumbnailsImage && !TextUtils.isEmpty(thumbnailsImagePath)) {
            DrawableTypeRequest<String> load = Glide.with(context).load(thumbnailsImagePath);
            if (resize) load.centerCrop();
            builder = Glide.with(context)
                    .load(originalImagePath)
                    .thumbnail(load);
        } else {
            builder = Glide.with(context)
                    .load(originalImagePath);
        }
        if (resize) builder.centerCrop();
        builder.crossFade()
                .error(context.getResources().getDrawable(R.mipmap.error_image))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    @Override
    public void displayImage(Context context, String originalImagePath, ImageView imageView, boolean resize) {
        displayImage(context, originalImagePath, null, imageView, resize, false);
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