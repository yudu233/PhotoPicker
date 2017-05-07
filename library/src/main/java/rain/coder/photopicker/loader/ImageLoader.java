package rain.coder.photopicker.loader;

import android.content.Context;

import java.io.Serializable;

import rain.coder.photopicker.weidget.GalleryImageView;

/**
 * Describe :自定义图片加载框架
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public interface ImageLoader extends Serializable {
    void displayImage(Context context, String path, GalleryImageView galleryImageView, boolean resize);

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