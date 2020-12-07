package com.rain.photopicker.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.rain.photopicker.R;
import com.rain.photopicker.loader.ImageLoader;

/**
 * Describe :GlideImageLoader
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */

public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Context context, String originalImagePath, String thumbnailsImagePath, final ImageView imageView, boolean resize, boolean loadThumbnailsImage) {
        RequestOptions options = new RequestOptions();
        RequestBuilder<Drawable> load;
        if (loadThumbnailsImage && !TextUtils.isEmpty(thumbnailsImagePath)) {
            if (resize) options.centerCrop();
            load = Glide.with(context).load(originalImagePath).thumbnail(Glide.with(context).load(thumbnailsImagePath).apply(options));
        } else {
            load = Glide.with(context).load(originalImagePath);
        }

        if (resize) options.centerCrop();
        options.error(context.getResources().getDrawable(R.mipmap.error_image));
        load.apply(options).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
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