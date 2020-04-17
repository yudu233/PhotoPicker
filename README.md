# PhotoPicker

[![](https://jitpack.io/v/yudu233/PhotoPicker.svg)](https://jitpack.io/#yudu233/PhotoPicker)

Android 照片选择器 ，支持单图、多图、视频、gif选择，内置鲁班压缩和Ucrop裁剪功能，以及主题自定义配置等功能

简书：[PhotoPicker](http://www.jianshu.com/p/a6b5831797d0)

### 各位读者来点Star支持一下吧

参考项目：
- https://github.com/donglua/PhotoPicker
- https://github.com/YancyYe/GalleryPick

## 目录
-- [功能介绍](#功能介绍)</br>
-- [集成方式](#集成方式)</br>
-- [照片选择器配置](#照片选择器配置)</br>
-- [配置说明](#配置说明)</br>
-- [初始化](#初始化)</br>
-- [回调方式](#回调方式)</br>
-- [使用的第三方库](#使用的第三方库)</br>
-- [更新日志](#更新日志)</br>
-- [混淆配置](#混淆配置)</br>
-- [联系方式](#联系方式)</br>
-- [效果预览](#效果预览)</br>


### 功能介绍
- 自定义图片加载方式
- 动态权限加载
- 图片、视频单选、多选
- 图片、视频预览
- 自定义主题颜色
- 集成UCrop裁剪
- 圆形头像裁剪
- 自定义裁剪比例
- 鲁班压缩


### 集成方式
```java
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
        jcenter()
    }
}
```

**在APP目录下的build.gradle中添加依赖**

```java
//support版本
implementation 'com.github.yudu233:PhotoPicker:2.0.4'
//androidx版本
implementation 'com.github.yudu233:PhotoPicker:3.0.0'
```

**AndroidManifest.xml 配置**
```java
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
    .pickMode(PhotoPickConfig.MODE_PICK_SINGLE)           //设置照片选择模式为单选，默认为单选
    .maxPickSize(PhotoPickConfig.DEFAULT_CHOOSE_SIZE)   //多选时可以选择的图片数量，默认为1张
    .setMimeType(MimeType.TYPE_ALL)     //显示文件类型，默认全部（全部、图片、视频）
    .showCamera(true)           //是否展示相机icon，默认展示
    .clipPhoto(true)            //是否开启裁剪照片功能，默认关闭
    .clipCircle(false)          //是否裁剪方式为圆形，默认为矩形
    .showOriginal(true)         //是否显示原图按钮，默认显示
    .startCompression(true)     //是否开启压缩，默认true
    .selectedMimeType(data)     //选择后返回的文件（用于判断下次进入是否可展示其他类型文件）
    .setCallback(new PhotoSelectCallback())     //回调
    .build();

```

### 配置说明
> **imageLoader(new GlideImageLoader())**</br>
    图片加载方式，可以自定义支持Glide、Piscasso、Fresco

> **setMimeType(MimeType.TYPE_ALL)**</br>
    显示文件类型，默认全部（全部、图片、视频）

> **spanCount(3)**</br>
    设置相册列表每列展示个数，默认为3列

> **pickMode(PhotoPickConfig.MODE_PICK_SINGLE)**</br>
    设置照片选择模式(单选、多选)，默认为单选
    
> **maxPickSize(9)**</br>
    多选时可以选择的图片数量，当pickMode为多选时默认为9张，单选默认为1

> **showCamera(true)**</br>
    是否展示相机icon，默认展示，当pickMode为多选时默认不展示

> **clipPhoto(false)**</br>
    是否开启裁剪照片功能，默认关闭,需手动开启，开启后多选模式自动变为单选

> **clipCircle(true)**</br>
    裁剪方式为是否圆形，默认为矩形，设置圆形适用于设置头像

> **showOriginal(true)**</br>
    是否显示原图按钮，默认显示，选中后若开启了图片压缩将不再压缩图片，返回原图
    
> **startCompression(true)**</br>
    是否启动图片压缩功能，默认开启(目前图片裁剪后没有再做压缩处理)
    
> **selectedMimeType(List<MediaData> data)**</br>
    传入上一次选中的数据，用于判断下次进入是否可展示其他类型文件。如类似朋友圈，第一次选择了图片，此时数据类型为MimeType.TYPE_ALL，打开时将过滤视频文件。</br>
    **==注：如果没有多类型的文件限制，可以不加该api==**
    
> **setCallback(new PhotoSelectCallback())**</br>
    回调方式1：setCallback();回调方式2：onActivityResult()
    
### 初始化
> 在Application中初始化：
```
 PhotoPick.init(getApplicationContext(), PhotoPickOptionsConfig.getPhotoPickOptions(context));
```
>配置
```java
    public static PhotoPickOptions getPhotoPickOptions(Context context) {
        PhotoPickOptions options = new PhotoPickOptions();
        //自定义文件存储路径
        options.filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "eWorld/";
        //自定义文件夹名称
        options.imagePath = options.filePath +  "cytx/";
        //自定义FileProvider地址
        options.photoPickAuthority = context.getString(R.string.file_provider_authorities);
        //自定义主题颜色 同步APP
        options.photoPickThemeColor = R.color.colorAccent;
        return options;
    }   
        
```

###  回调方式
#### 方式一：
```
new PhotoPickConfig
    .Builder(MainActivity.this)
    ...
    .setCallback(new PhotoSelectCallback() {
        @Override
        public void selectResult(ArrayList<MediaData> photos) {
            
        }).build();
```
#### 方式二：
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) return;
    if (requestCode == PhotoPickConfig.PICK_SELECT_REQUEST_CODE) {
        ArrayList<MediaData> datas = data.getParcelableArrayListExtra(PhotoPickConfig.EXTRA_SELECT_PHOTOS);
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
```


### 使用的第三方库
[PhotoView](https://github.com/chrisbanes/PhotoView)
[slidinguppanel](https://github.com/umano/AndroidSlidingUpPanel)
[ucrop](https://github.com/Yalantis/uCrop)

## 更新日志
# 当前版本：v3.0.0
* 支持AndroidX
* 修复已知的问题
* 移除无效代码


# 历史版本
### v2.0.4
* 新增文件夹有效性校验
---
### v2.0.3(紧急修复)
* 修复部分小米手机打开相册直接崩溃问题
---
### v2.0.2
* 优化裁剪时不显示其他类型文件（video、gif）
* 优化选择gif时默认原图，非原图时不压缩
---
### v2.0.1
* 代码优化
* 新增损坏文件判断过滤
---
### v2.0.0
* 新增视频、gif选择功能
* 鲁班压缩优化
* 部分机型视频
* 获取资源方式变更
* 修复选择-取消后返回多条数据
* 修复快速双击问题
* UI更新
---
### v1.6.0
* 新增鲁班压缩
* 新增长截图预览功能
* 新增选择原图功能
* 更改存储路径及方式
* 已知问题修复
* 代码优化
---
#### v1.5.0
---
#### v1.2.0
---
#### v1.1.0
---


### 混淆配置
```
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
```

### 联系方式
有问题可以添加QQ：19880794(不常上线) 微信:dadada0413


### 效果预览
![01.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/01.png) ![02.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/02.png)
![03.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/03.png) ![04.png](https://github.com/yudu233/PhotoPicker/blob/master/picture/04.png)
![send.gif](https://github.com/yudu233/PhotoPicker/blob/master/picture/send.gif) ![circleimage.gif](https://github.com/yudu233/PhotoPicker/blob/master/picture/circleimage.gif)
![lookbigimage.gif](https://github.com/yudu233/PhotoPicker/blob/master/picture/lookbigimage.gif)
