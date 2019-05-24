package com.rain.library.loader;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;


/**
 * Describe :自定义图片加载框架
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public interface ImageLoader extends Serializable {
    void displayImage(Context context, String originalImagePath, String thumbnailsImagePath, ImageView imageView, boolean resize, boolean loadThumbnailsImage);

    void displayImage(Context context, String originalImagePath, ImageView imageView, boolean resize);


    void clearMemoryCache();

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