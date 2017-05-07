package rain.coder.photopicker.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import rain.coder.library.R;

/**
 * Descriptions : ToolBar帮助类
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */

public class ToolBarHelper {

    private Context mContext;

    //base view
    private LinearLayout mContentView;

    //用户定义的view
    private View mUserView;

    //toolbar
    private Toolbar mToolBar;

    //视图构造器
    private LayoutInflater mInflater;

    /**
     * toolbar两个属性
     * toolbar是否悬浮在窗口之上
     * toolbar的高度获取
     */
    private static int[] ATTRS = {
            R.attr.windowActionBarOverlay,
            R.attr.actionBarSize
    };

    public ToolBarHelper(Context context, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        initContentView();
        initToolBar(R.layout.toolbar_layout);
        initUserView(layoutId);
    }

    /**
     * 初始化整个内容
     */
    private void initContentView() {
        //直接创建一个LinearLayout，作为视图容器的父容器
        mContentView = new LinearLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setOrientation(LinearLayout.VERTICAL);
        mContentView.setLayoutParams(params);

    }

    /**
     * 初始化toolbar
     *
     * @param id
     */
    private void initToolBar(int id) {
        //通过inflater获取toolbar的布局文件
        View toolbar = mInflater.inflate(id, mContentView);
        mToolBar = (Toolbar) toolbar.findViewById(R.id.toolbar);
    }

    /**
     * 初始化用户定义的布局
     *
     * @param id
     */
    private void initUserView(int id) {
        mUserView = mInflater.inflate(id, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(ATTRS);
        //获取主题中定义的悬浮标志
        boolean overly = typedArray.getBoolean(0, false);
        //获取主题中定义的toolbar的高度
        int toolBarSize = (int) typedArray.getDimension(1, (int) mContext.getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
        typedArray.recycle();
        //如果是悬浮状态，则不需要设置间距
        params.topMargin = overly ? 0 : toolBarSize;
        mContentView.addView(mUserView, params);

    }

    public LinearLayout getContentView() {
        return mContentView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }
}
