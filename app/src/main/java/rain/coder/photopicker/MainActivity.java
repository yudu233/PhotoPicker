package rain.coder.photopicker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import rain.coder.photopicker.NineGridImageView.NineGridImageView;
import rain.coder.photopicker.NineGridImageView.NineGridImageViewAdapter;
import rain.coder.photopicker.controller.PhotoPickConfig;
import rain.coder.photopicker.imageLoader.GlideImageLoader;
import rain.coder.photopicker.lookBigImage.ViewBigImageActivity;
import rain.coder.photopicker.show.AddPicLayout;
import rain.coder.photopicker.show.OnPreviewListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPreviewListener {
    private AddPicLayout mAddPicture;
    private ImageView mPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        images.add("http://img.hb.aicdn.com/6dbf111bcbb9ec58ba4f99910e79dc40d3b513941de90-4hXdfL_fw658");
        images.add("http://img.hb.aicdn.com/9a0103690aede0255533ad783fd732528ceec60931a13-qjwDRn_fw658");
        images.add("http://img.hb.aicdn.com/5ca7ab06a0014d4e95ff560328fac65c37fc511e1d3cd-8iGw9D_fw658");
        images.add("http://img.hb.aicdn.com/119d9fc57978a8bc7691e9f0fb73c928a2794fa7106ef-zVUSCZ_fw658");
        images.add("http://img.hb.aicdn.com/f5be3efc5eb157e0a1ba994e18ae5b7f9f8ec2d511e15-hYNZqp_fw658");
        images.add("http://img.hb.aicdn.com/b4a1b2a43892b29066a4b5885cae4001083355ba1ac97-Bkz2mX_fw658");
        images.add("http://img.hb.aicdn.com/dc282aa286def0bf0804f895bc9b9bf86f35c3eb21a0a-GIyoXP_fw658");
        images.add("http://img.hb.aicdn.com/06b65abdb7b0e5b53c5a70284dc3e22d4aca663a2cb93-egcHNs_fw658");
        images.add("http://img.hb.aicdn.com/1fc254d57b5ac880fc4fe4bc906b559790f95f64392ff-vQ91wD_fw658");

        mAddPicture = (AddPicLayout) findViewById(R.id.addPicture);
        mAddPicture.setOnPreviewListener(this);
        mPic = (ImageView) findViewById(R.id.imvPic);

        findViewById(R.id.btn01).setOnClickListener(this);
        findViewById(R.id.btn02).setOnClickListener(this);
        findViewById(R.id.btn03).setOnClickListener(this);
        //照片选择器配置说明
/*        new PhotoPickConfig.Builder(this)
                .imageLoader(new GlideImageLoader())        //图片加载方式（必须）
                .showCamera(true)                           //是否显示拍照按钮（默认false）
                .clipPhoto(false)                           //是否裁剪图片（默认false）
                .clipCircle(true)                           //裁剪方式（默认矩形）
                .maxPickSize(9)                             //最多可选择图片个数（默认9张）
                .pickMode(PhotoPickConfig.MODE_PICK_MORE)   //手动设置照片多选还是单选（1单选2多选）
                .spanCount(3)                               //手动设置GridView列数（默认3列）
                .build();*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn01:
                sendTimeLine();
                break;
            case R.id.btn02:
                showNineGridImage();
                break;
            case R.id.btn03:
                clipPhoto();
                break;

        }
    }

    private void clipPhoto() {
        new PhotoPickConfig.Builder(this)
                .imageLoader(new GlideImageLoader())
                .showCamera(true)
                .clipPhoto(true)
                .clipCircle(true)
                .build();
    }

    private ArrayList<String> images = new ArrayList<>();

    private void showNineGridImage() {
        NineGridImageView nineGridImageView = (NineGridImageView) findViewById(R.id.ngl_images);
        nineGridImageView.setVisibility(View.VISIBLE);
        nineGridImageView.setAdapter(mGridImageViewAdapter);
        nineGridImageView.setImagesData(images);
    }

    private NineGridImageViewAdapter<String> mGridImageViewAdapter = new NineGridImageViewAdapter<String>() {
        @Override
        protected void onDisplayImage(Context context, ImageView imageView, String s) {
            //图片展示
            Glide.with(MainActivity.this).load(s).into(imageView);
        }

        @Override
        protected ImageView generateImageView(Context context) {
            return super.generateImageView(context);
        }

        @Override
        protected void onItemImageClick(Context context, ImageView imageView, int index, List<String> list) {
            Bundle bundle = new Bundle();
            bundle.putInt("select", 2);
            bundle.putInt("code", index);
            bundle.putStringArrayList("image_uri", images);
            Intent intent = new Intent(MainActivity.this, ViewBigImageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    private void sendTimeLine() {
        mAddPicture.setVisibility(View.VISIBLE);
        new PhotoPickConfig.Builder(this)
                .imageLoader(new GlideImageLoader())
                .showCamera(true)
                .maxPickSize(9)
                .build();
    }

    //图片预览
    // TODO: 2017/5/4 0004 发布动态选择照片一般要有删除照片的选择还未实现
    @Override
    public void onPreview(int pos, boolean showDelete) {
        Bundle bundle = new Bundle();
        bundle.putInt("select", 2);
        bundle.putInt("code", pos);
        bundle.putStringArrayList("image_uri", mImagePaths);
        Intent intent = new Intent(MainActivity.this, ViewBigImageActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //照片选择
    @Override
    public void onPick() {
        new PhotoPickConfig.Builder(this)
                .imageLoader(new GlideImageLoader())
                .showCamera(true)
                .pickMode(PhotoPickConfig.MODE_PICK_MORE)
                .maxPickSize(9)
                .build();
    }

    ArrayList<String> mImagePaths = new ArrayList<>();   //选择的照片路径集合

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PhotoPickConfig.PICK_REQUEST_CODE:
                if (PhotoPickConfig.photoPickBean.isClipPhoto()){
                    Uri resultUri = Uri.parse(data.getStringExtra(PhotoPickConfig.EXTRA_CLIP_PHOTO));
                    Glide.with(MainActivity.this).load(resultUri)
                            .transform(new GlideCircleTransform(this))
                            .into(mPic);
                }else {
                    ArrayList<String> photoLists = data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST);
                    if (photoLists != null && !photoLists.isEmpty()) {
                        for (int i = 0; i < photoLists.size(); i++)
                            if (mImagePaths.size() < 9)
                                mImagePaths.add(photoLists.get(i));
                    }
                    mAddPicture.setPaths(mImagePaths);
                }
                break;
        }
    }
}
