package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertActivity.PhotoPagerActivity;
import com.chinabsc.telemedicine.expert.myView.TouchImageView;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class PhotoPagerAdapter extends PagerAdapter {

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();
    public TouchImageView mImageView;

    private PhotoPagerActivity activity;
    private ArrayList<String> list;
    private LayoutInflater inflater;

    public PhotoPagerAdapter(PhotoPagerActivity context, ArrayList<String> list) {
        this.activity = context;
        this.list = list;

        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_download) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.basic_image_error)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.basic_image_error)  //设置图片加载/解码过程中错误时候显示的图片
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.photo_pager_image, container, false);

        mImageView = (TouchImageView) itemView.findViewById(R.id.imageView);
        mImageView.setMaxZoom(4f);
        ImageLoader.getInstance().displayImage(list.get(position), mImageView, mOptions);

        (container).addView(itemView);

        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewGroup) container).removeView((View) object);
        object=null;
    }
}