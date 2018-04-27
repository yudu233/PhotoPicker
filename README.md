# PhotoPicker
[![](https://jitpack.io/v/yudu233/PhotoPicker.svg)](https://jitpack.io/#yudu233/PhotoPicker)

Android 照片选择器 

简书：[PhotoPicker](http://www.jianshu.com/p/a6b5831797d0)

### 各位读者来点Star支持一下吧

参考项目：
- https://github.com/donglua/PhotoPicker
- https://github.com/YancyYe/GalleryPick

### PhotoPicker功能介绍
- 自定义图片加载方式
- 图片单选、多选
- 图片预览
- 自定义主题颜色
- 集成UCrop裁剪

### 效果预览
![01.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/01.png) ![02.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/02.png)
![03.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/03.png) ![04.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/04.png)
![send.gif](https://github.com/yudu233/PhotoPicker/blob/master/picture/send.gif) ![circleimage.gif](https://github.com/yudu233/PhotoPicker/blob/master/picture/circleimage.gif)
![lookbigimage.gif](https://github.com/yudu233/PhotoPicker/blob/master/picture/lookbigimage.gif)

### 用法
```java
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
        jcenter()
    }
}

```

在APP目录下的build.gradle中添加依赖

```java
    compile 'com.github.yudu233:PhotoPicker:1.5.0'
    
```

### AndroidManifest.xml 配置
```java
       <activity
                   android:name="com.rain.library.ui.PhotoPickActivity"
                   android:theme="@style/PhoAppTheme.AppTheme"/>

               <activity
                   android:name="com.rain.library.ui.PhotoPreviewActivity"
                   android:theme="@style/PhoAppTheme.AppTheme"/>

               <activity
                   android:name="com.yalantis.ucrop.UCropActivity"
                   android:screenOrientation="portrait"
                   android:theme="@style/Theme.AppCompat.Light.NoActionBar"/>

               <activity
                   android:name=".lookBigImage.ViewBigImageActivity"
                   android:screenOrientation="portrait"
                   android:theme="@style/Theme.AppCompat.Light.NoActionBar"/>

               //适配android7.0
               <provider
                   android:name="android.support.v4.content.FileProvider"
                   android:authorities="@string/file_provider_authorities"
                   android:exported="false"
                   android:grantUriPermissions="true">
                   <meta-data
                       android:name="android.support.FILE_PROVIDER_PATHS"
                       android:resource="@xml/file_provider_paths"/>
               </provider>
                  
```

### 照片选择器配置
```java
   new PhotoPickConfig
       .Builder(MainActivity.this)
       .imageLoader(new GlideImageLoader())                //图片加载方式，支持任意第三方图片加载库
       .spanCount(PhotoPickConfig.GRID_SPAN_COUNT)         //相册列表每列个数，默认为3
       .pickMode(PhotoPickConfig.MODE_PICK_SINGLE)         //设置照片选择模式为单选，默认为单选
       .maxPickSize(PhotoPickConfig.DEFAULT_CHOOSE_SIZE)   //多选时可以选择的图片数量，默认为1张
       .showCamera(true)           //是否展示相机icon，默认展示
       .clipPhoto(true)            //是否开启裁剪照片功能，默认关闭
       .clipCircle(false)          //是否裁剪方式为圆形，默认为矩形
       .build();

```

### 配置说明
>  imageLoader(new GlideImageLoader())
    图片加载方式，可以自定义支持Glide、Piscasso、Fresco

> spanCount()
    设置相册列表每列展示个数，默认为3列

> pickMode()
    设置照片选择模式(单选、多选)，默认为单选
> maxPickSize()
    多选时可以选择的图片数量，当pickMode为多选时默认为9张，单选默认为1

> showCamera()
    是否展示相机icon，默认展示，当pickMode为多选时默认不展示

> clipPhoto()
    是否开启裁剪照片功能，默认关闭,需手动开启

> clipCircle()
    裁剪方式为是否圆形，默认为矩形，设置圆形适用于设置头像

>
    
### 自定义主题颜色，同步App主题
```java
        在Application里初始化PhotoPick
        //默认颜色：橘色android.R.color.holo_red_light
        PhotoPick.init(getApplicationContext());
        //自定义主题颜色
        PhotoPick.init(context, android.R.color.holo_red_light);    
        
```

### Activity 回调
```java
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
    
```

### 使用的第三方库
[PhotoView](https://github.com/chrisbanes/PhotoView)
[slidinguppanel](https://github.com/umano/AndroidSlidingUpPanel)
[ucrop](https://github.com/Yalantis/uCrop)

有问题可以添加QQ：19880794 


