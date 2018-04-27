package com.rain.photopicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rain.library.controller.PhotoPickConfig;
import com.rain.library.utils.FileSizeUtils;
import com.rain.photopicker.glide.GlideImageLoader;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoPickConfig
                        .Builder(MainActivity.this)
                        .imageLoader(new GlideImageLoader())                //图片加载方式，支持任意第三方图片加载库
                        .spanCount(PhotoPickConfig.GRID_SPAN_COUNT)         //相册列表每列个数，默认为3
                        .pickMode(PhotoPickConfig.MODE_PICK_SINGLE)           //设置照片选择模式为单选，默认为单选
                        .maxPickSize(PhotoPickConfig.DEFAULT_CHOOSE_SIZE)   //多选时可以选择的图片数量，默认为1张
                        .showCamera(true)           //是否展示相机icon，默认展示
                        .clipPhoto(true)            //是否开启裁剪照片功能，默认关闭
                        .clipCircle(false)          //是否裁剪方式为圆形，默认为矩形
                        .build();
            }
        });

        findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoPickConfig
                        .Builder(MainActivity.this)
                        .imageLoader(new GlideImageLoader())
                        .pickMode(PhotoPickConfig.MODE_PICK_MORE)
                        .maxPickSize(3)
                        .build();
            }
        });

        mContent = findViewById(R.id.txv_content);

    }

    StringBuilder builder = new StringBuilder();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PhotoPickConfig.PICK_SINGLE_REQUEST_CODE:      //单选不裁剪
                String path = data.getStringExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO);
                Log.e("单选", path);
                mContent.setText(path);
                break;
            case PhotoPickConfig.PICK_MORE_REQUEST_CODE:        //多选
                ArrayList<String> photoLists = data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
                if (photoLists != null && !photoLists.isEmpty()) {
                    for (int i = 0; i < photoLists.size(); i++) {
                        Log.e("多选", photoLists.get(i) + "========");
                        mContent.setText(builder.append(photoLists.get(i) + "\n"));
                    }
                }
                break;

            case PhotoPickConfig.PICK_CLIP_REQUEST_CODE:    //裁剪
                Uri resultUri = Uri.parse(data.getStringExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO));
                try {
                    InputStream input = getContentResolver().openInputStream(resultUri);
                    int size = input.available();
                    Log.e("裁剪", resultUri.toString() + "========" + FileSizeUtils.FormetFileSize(size));
                    mContent.setText(resultUri.toString() + FileSizeUtils.FormetFileSize(size));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
