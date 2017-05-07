package rain.coder.photopicker.show;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import rain.coder.photopicker.R;
import rain.coder.photopicker.Rain;


public class AddPicLayout extends ViewGroup {
    private int rowSize = 3;
    private int childPadding = 0;
    private int maxSize = 9;
    private LayoutParams childParam;
    private OnPreviewListener listener;
    private ImageView addImage;
    private static final String PACKAGE_URL_SCHEME = "package:";//权限方案

    public AddPicLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.addPic);
        rowSize = a.getInt(R.styleable.addPic_row_size, 3);     //每行可显示子View个数
        maxSize = a.getInt(R.styleable.addPic_max_size, 9);     //最多可显示子View个数
        childPadding = (int) a.getDimension(R.styleable.addPic_child_padding, 0);
    }

    public View getChildById(int id) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (id == child.getId()) {
                return child;
            }
        }
        return null;
    }

    /**
     * 展示图片
     *
     * @param paths
     */
    public void setPaths(ArrayList<String> paths) {
        clear();
        for (int i = 0; i < paths.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setPadding(0, 0, 10, 0);
            addView(imageView);
            imageView.setId(i);
            Uri uri = Uri.fromFile(new File(paths.get(i)));
            Glide.with(getContext())
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(imageView);

        }

        if (paths.size() < maxSize) {
            addPlusPic(maxSize);
        }
    }

    /**
     * 设置进入相册的控件
     *
     * @param index
     */
    private void addPlusPic(int index) {
        addImage = new ImageView(getContext());
        addImage.setPadding(0, 0, 10, 0);
        addImage.setId(index);
        addImage.setAdjustViewBounds(true);
        addImage.setMaxHeight(30);
        addImage.setMaxWidth(30);
        addImage.setImageResource(R.mipmap.comment_pics);
        addView(addImage);
        addImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null &&
                        ContextCompat.checkSelfPermission(getContext(),
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    listener.onPick();
                } else {
                    showMissingPermissionDialog();
                }
            }
        });
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("温馨提示");
        builder.setMessage("拒绝访问将无法使用相机功能,点击设置进入“应用权限打开相关权限");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + Rain.context.getPackageName()));
                Rain.context.startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * OnResume -> onAttachedToWindow
     * 修改view视图
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.getChildCount() == 0)
            addPlusPic(1);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        child.setLayoutParams(getChildParam());
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onPreview(view.getId(), true);
                }
            }
        });
    }

    public void clear() {
        this.removeAllViews();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(getClass().getName(), "onMeasure");
        int count = getChildCount();
        if (count > maxSize) {
            count = maxSize;
        }
        if (count > 0) {
            View child = getChildAt(0);
            LayoutParams params = child.getLayoutParams();
            int cHeight = params.height;
            int lineNum = count / rowSize;
            lineNum = count % rowSize == 0 ? lineNum : lineNum + 1;
            int maxH = (2 * childPadding + cHeight) * lineNum;
            setMeasuredDimension(resolveSize(getMeasuredWidth(), widthMeasureSpec),
                    resolveSize(maxH, heightMeasureSpec));
        }
    }

    /**
     * @param changed
     * @param l,t     子控件左边缘相对于父控件的左、上边缘间距
     * @param r,b     子控件左边缘相对于父控件的左、上边缘间距
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(getClass().getName(), "onLayout");
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (i >= maxSize) {
                child.setVisibility(View.GONE);
            } else {
                child.setVisibility(View.VISIBLE);
                LayoutParams params = child.getLayoutParams();
                int cWidth = params.width;
                int cHeight = params.height;
                int cl = childPadding + (i % rowSize) * (cWidth + childPadding);
                int cr = cl + cWidth;
                int ct = childPadding + (i / rowSize) * (cHeight + childPadding);
                int cb = ct + cHeight;
                child.layout(cl, ct, cr, cb);
            }
        }
    }

    /**
     * 设置子View的视图大小
     *
     * @return
     */
    public LayoutParams getChildParam() {
        if (childParam == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int childWidth = (dm.widthPixels - (rowSize + 1) * childPadding) / rowSize;
            childParam = new LayoutParams(childWidth, childWidth);
        }
        return childParam;
    }

    public void setOnPreviewListener(OnPreviewListener listener) {
        this.listener = listener;
    }

}