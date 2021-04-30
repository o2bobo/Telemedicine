package com.chinabsc.telemedicine.expert.myView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chinabsc.telemedicine.expert.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public class ImageDialog extends Dialog {
    Context mContext;
    public Activity activity;
    public TouchImageView mTicketImg;
    public RelativeLayout mDialoglayout;
    private DisplayImageOptions mOptions;
    private String mImageErWei;


    public ImageDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public ImageDialog(Activity someactivity, int theme, String imageurl) {
        super(someactivity, theme);
        activity = someactivity;
        this.setContentView(R.layout.dialgo_image);
        mDialoglayout = (RelativeLayout) findViewById(R.id.dialoglayout);
        mTicketImg = (TouchImageView) findViewById(R.id.ImageView);
        mTicketImg.setMaxZoom(4f);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_download) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.basic_image_error)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.basic_image_error)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成
        Log.e("imagedialog", imageurl);
        if (imageurl.startsWith("http://")) {
            ImageLoader.getInstance().displayImage(imageurl, mTicketImg, mOptions);
        } else {
            ImageLoader.getInstance().displayImage("file:///" + imageurl, mTicketImg, mOptions);
        }
        mTicketImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });
    }

}