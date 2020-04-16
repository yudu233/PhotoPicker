package com.rain.crow.weidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Describe :自定义 Image 用来兼容 fresco
 * Email:baossrain99@163.com
 * Created by Rain on 17-5-3.
 */
public class GalleryImageView extends AppCompatImageView {

    private static final String TAG = "GalleryImageView";

    private OnImageViewListener mOnImageViewListener;

    public GalleryImageView(Context context) {
        super(context);
    }

    public GalleryImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnImageViewListener(OnImageViewListener listener) {
        mOnImageViewListener = listener;
    }

    public static interface OnImageViewListener {
        void onDetach();

        void onAttach();

        boolean verifyDrawable(Drawable dr);

        void onDraw(Canvas canvas);

        boolean onTouchEvent(MotionEvent event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mOnImageViewListener != null) {
            mOnImageViewListener.onDetach();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOnImageViewListener != null) {
            mOnImageViewListener.onAttach();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable dr) {
        if (mOnImageViewListener != null) {
            if (mOnImageViewListener.verifyDrawable(dr)) {
                return true;
            }
        }
        return super.verifyDrawable(dr);
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (mOnImageViewListener != null) {
            mOnImageViewListener.onDetach();
        }
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (mOnImageViewListener != null) {
            mOnImageViewListener.onAttach();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOnImageViewListener != null) {

            mOnImageViewListener.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnImageViewListener == null) {
            return super.onTouchEvent(event);
        }
        return mOnImageViewListener.onTouchEvent(event) || super.onTouchEvent(event);
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
