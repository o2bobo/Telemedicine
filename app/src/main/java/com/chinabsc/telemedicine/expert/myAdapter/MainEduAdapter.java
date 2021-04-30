package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.home.MainEduEntity;
import com.chinabsc.telemedicine.expert.expertinterfaces.OnItemInterfaceClick;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

public class MainEduAdapter extends RecyclerView.Adapter {
    Context context;
    public List<MainEduEntity.DataBean> mPlayingItem;
    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();
    OnItemInterfaceClick onItemInterfaceClick;

    public void setOnItemInterfaceClick(OnItemInterfaceClick onItemInterfaceClick) {
        this.onItemInterfaceClick = onItemInterfaceClick;
    }

    public MainEduAdapter(Context context, List<MainEduEntity.DataBean> mPlayingItem) {
        this.context = context;
        this.mPlayingItem = mPlayingItem;
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context.getApplicationContext()));
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
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.education_schedule_item, parent, false);
        EduItemViewHolder holder = new EduItemViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof EduItemViewHolder) {
            ((EduItemViewHolder) holder).courseclass.setText(mPlayingItem.get(position).getCourseclass());

            String time = PublicUrl.stampToDate(mPlayingItem.get(position).getStarttime());
            ((EduItemViewHolder) holder).starttime.setText(time);
            String status = mPlayingItem.get(position).getStatue();
            if (status.equals("preparing")) {
                status = "等待中";
                ((EduItemViewHolder) holder).statue.setBackgroundResource(R.drawable.education_status_preparing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_preparing, ((EduItemViewHolder) holder).pic, mOptions);
            } else if (status.equals("stopped")) {
                status = "已结束";
                ((EduItemViewHolder) holder).statue.setBackgroundResource(R.drawable.education_status_completed);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_completed, ((EduItemViewHolder) holder).pic, mOptions);
            } else {
                status = "正在直播";
                ((EduItemViewHolder) holder).statue.setBackgroundResource(R.drawable.education_status_playing);
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.education_playing, ((EduItemViewHolder) holder).pic, mOptions);
            }
            ((EduItemViewHolder) holder).statue.setText(status);
            ((EduItemViewHolder) holder).rcvsiteid.setText(mPlayingItem.get(position).getRcvsiteid() + " " + mPlayingItem.get(position).getCommondepartmentcode());
            if (mPlayingItem.get(position).getJobtitlecode()==null){
                ((EduItemViewHolder) holder).expertname.setText(mPlayingItem.get(position).getExpertname());
            }else {
                ((EduItemViewHolder) holder).expertname.setText(mPlayingItem.get(position).getExpertname() + " " + mPlayingItem.get(position).getJobtitlecode());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemInterfaceClick.onclick(position+"",position+"");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.i("MainEduAdapter","mPlayingItem.size()=="+mPlayingItem.size());
        return mPlayingItem.size();
    }

    class EduItemViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView courseclass;
        TextView starttime;
        TextView statue;
        TextView rcvsiteid;
        TextView expertname;

        public EduItemViewHolder(View view) {
            super(view);
            pic = (ImageView) view.findViewById(R.id.pic);
            courseclass = (TextView) view.findViewById(R.id.courseclass);
            starttime = (TextView) view.findViewById(R.id.starttime);
            statue = (TextView) view.findViewById(R.id.statue);
            rcvsiteid = (TextView) view.findViewById(R.id.rcvsiteid);
            expertname = (TextView) view.findViewById(R.id.expertname);
        }
    }
}
