package com.rain.photopicker.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.rain.photopicker.PhotoPick;
import com.rain.photopicker.R;

/**
 * Descriptions : Activity基类
 * GitHub : https://github.com/Rain0413
 * Blog   : http://blog.csdn.net/sinat_33680954
 * Created by Rain on 16-12-7.
 */
public class BaseActivity extends AppCompatActivity {
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 5.0系统以上才开启沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //如果主题色为白色，设置状态栏字体为深色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && PhotoPick.getToolbarBackGround() == getResources().getColor(R.color.color_white)) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    protected void setContentView(int layoutId, boolean hasTitle) {
        setContentView(layoutId);
        if (hasTitle) {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
