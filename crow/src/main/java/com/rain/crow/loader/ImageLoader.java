package com.rain.crow.loader;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * @author:duyu
 * @org :   www.yudu233.com
 * @email : yudu233@gmail.com
 * @date :  2017/05/03
 * @filename : LocalMediaLoader
 * @describe : 自定义图片加载框架支持Glide、Picasso等框架
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