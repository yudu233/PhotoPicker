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
import com.rain.library.utils.Rlog;
import com.rain.photopicker.glide.GlideImageLoader;

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
                        .showOriginal(true)
                        .startCompression(true)
                        .setCallback(new PhotoSelectCallback(){
                            @Override
                            public void clipImage(ArrayList<MediaData> photos) {
                                super.clipImage(photos);
                                Rlog.e(photos.get(0).getClipImagePath() + "---------------");
                            }
                        })
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
                        .maxPickSize(3)
                        .setCallback(new PhotoSelectCallback() {
                            @Override
                            public void moreSelect(ArrayList<MediaData> photoLists) {
                                if (PhotoPickConfig.DEFAULT_START_COMPRESSION) {
                                    for (int i = 0; i < photoLists.size(); i++) {
                                        Log.e("多选---压缩", photoLists.get(i).getCompressionPath() + "====Callback====");
                                        mContent.setText(builder.append(photoLists.get(i).getCompressionPath() + "\n"));
                                    }

                                } else {
                                    for (int i = 0; i < photoLists.size(); i++) {
                                        Log.e("多选---原图", photoLists.get(i).getOriginalPath() + "====Callback====");
                                        mContent.setText(builder.append(photoLists.get(i).getOriginalPath() + "\n"));
                                    }
                                }
                            }

                            @Override
                            public void singleSelect(ArrayList<MediaData> photos) {
                                Rlog.e(photos.get(0).getOriginalPath() + "-----------");
                            }

                            @Override
                            public void clipImage(ArrayList<MediaData> photos) {
                            }
                        }).build();
            }
        });

        mContent = findViewById(R.id.txv_content);

    }

    StringBuilder builder = new StringBuilder();

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK) return;
//        String path;
//
//        switch (requestCode) {
//            case PhotoPickConfig.PICK_SINGLE_REQUEST_CODE:      //单选不裁剪
//                ArrayList<MediaData> photos = data.getParcelableArrayListExtra(PhotoPickConfig.EXTRA_SINGLE_PHOTO);
//
//                if (PhotoPickConfig.DEFAULT_START_COMPRESSION) {
//                    path = photos.get(0).getCompressionPath();
//                    Log.e("单选---压缩 ：", photos.get(0).getCompressionPath());
//
//                } else {
//                    path = photos.get(0).getOriginalPath();
//                    Log.e("单选---原图 ：", photos.get(0).getOriginalPath());
//                }
//
//                mContent.setText(path);
//                break;
//            case PhotoPickConfig.PICK_MORE_REQUEST_CODE:        //多选
//                ArrayList<MediaData> photoLists = data.getParcelableArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
//
//                if (photoLists != null && !photoLists.isEmpty()) {
//
//                    if (PhotoPickConfig.DEFAULT_START_COMPRESSION) {
//                        for (int i = 0; i < photoLists.size(); i++) {
//                            Log.e("多选---压缩", photoLists.get(i).getCompressionPath() + "========");
//                            mContent.setText(builder.append(photoLists.get(i).getCompressionPath() + "\n"));
//                        }
//
//                    } else {
//                        for (int i = 0; i < photoLists.size(); i++) {
//                            Log.e("多选---原图", photoLists.get(i).getOriginalPath() + "========");
//                            mContent.setText(builder.append(photoLists.get(i).getOriginalPath() + "\n"));
//                        }
//                    }
//                }
//                break;
//
//            case PhotoPickConfig.PICK_CLIP_REQUEST_CODE:    //裁剪
//                ArrayList<MediaData> photoArrayList = data.getParcelableArrayListExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO);
//                Log.e("裁剪", photoArrayList.get(0).getClipImagePath());
//                break;
//        }
//    }
}
