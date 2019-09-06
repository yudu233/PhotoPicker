package com.rain.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rain.library.bean.MediaData;
import com.rain.library.controller.PhotoPickConfig;
import com.rain.library.impl.PhotoSelectCallback;
import com.rain.library.utils.MimeType;
import com.rain.photopicker.glide.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mContent;

    private List<MediaData> data = new ArrayList<>();

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
                        .setMimeType(MimeType.TYPE_ALL)     //显示文件类型，默认全部（全部、图片、视频）
                        .showCamera(true)           //是否展示相机icon，默认展示
                        .clipPhoto(true)            //是否开启裁剪照片功能，默认关闭
                        .clipCircle(false)          //是否裁剪方式为圆形，默认为矩形
                        .showOriginal(true)         //是否显示原图按钮，默认显示
                        .startCompression(true)     //是否开启压缩，默认true
                        .selectedMimeType(data)     //选择后返回的文件（用于判断下次进入是否可展示其他类型文件）
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
                        .setMimeType(MimeType.TYPE_ALL)
                        .maxPickSize(9)
                        .selectedMimeType(data)
                        .setCallback(new PhotoSelectCallback() {
                            @Override
                            public void selectResult(ArrayList<MediaData> photos) {
                                if (photos != null && photos.size() != 0) {
                                    data = photos;
                                    MediaData mediaData = photos.get(0);
                                    if (mediaData.isCompressed()) {
                                        //压缩
                                        for (int i = 0; i < photos.size(); i++) {
                                            Log.e("压缩路径:", photos.get(i).getCompressionPath());
                                            mContent.setText(builder.append(photos.get(i).getCompressionPath() + "\n"));
                                        }
                                    } else {
                                        //原图
                                        for (int i = 0; i < photos.size(); i++) {
                                            Log.e("原图路径:", photos.get(i).getOriginalPath());
                                            mContent.setText(builder.append(photos.get(i).getOriginalPath() + "\n"));
                                        }
                                    }
                                }
                            }
                        }).build();
            }
        });

        mContent = findViewById(R.id.txv_content);

    }

    StringBuilder builder = new StringBuilder();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;
        if (requestCode == PhotoPickConfig.PICK_SELECT_REQUEST_CODE) {
            List<MediaData> datas = data.getParcelableArrayListExtra(PhotoPickConfig.EXTRA_SELECT_PHOTOS);
            if (datas != null) {
                MediaData mediaData = datas.get(0);
                if (mediaData.isClip()) {
                    Log.e("裁剪:", mediaData.getClipImagePath());
                    mContent.setText(builder.append(mediaData.getClipImagePath() + "\n"));
                    return;
                }
                if (mediaData.isCamera()) {
                    Log.e("相机:", mediaData.getCameraImagePath());
                    mContent.setText(builder.append(mediaData.getCameraImagePath() + "\n"));
                    return;
                }

                if (mediaData.isCompressed()) {
                    Log.e("压缩后:", mediaData.getCompressionPath());
                    mContent.setText(builder.append(mediaData.getCompressionPath() + "\n"));
                    return;
                }
                Log.e("原始地址：:", mediaData.getOriginalPath());
                mContent.setText(builder.append(mediaData.getOriginalPath() + "\n"));
            }
        }
    }
}
