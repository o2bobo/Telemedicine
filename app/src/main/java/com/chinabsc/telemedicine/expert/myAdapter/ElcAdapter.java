package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.ElcItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

public class ElcAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_FINISH = 2;
    private Context context;
    private ArrayList<ElcItem> data;

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();


    public ElcAdapter(Context context, ArrayList<ElcItem> data) {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
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

        this.context = context;
        this.data = data;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            if (data.size() > 0 & position == data.get(0).total) {
                return TYPE_FINISH;
            } else {
                return TYPE_FOOTER;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.elc_info_item, parent,
                    false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.main_news_foot_item, parent,
                    false);
            return new FootViewHolder(view);
        } else {
            //TYPE_FINISH
            View view = new View(context);
            return new FootViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            if (((ItemViewHolder) holder).Diagnosis != null)
                ((ItemViewHolder) holder).Diagnosis.setText(data.get(position).diagnosis);
            if (((ItemViewHolder) holder).Purpose != null)
                ((ItemViewHolder) holder).Purpose.setText(data.get(position).purpose);
            if (((ItemViewHolder) holder).SndDepart != null)
                ((ItemViewHolder) holder).SndDepart.setText(data.get(position).snddepart);
            if (((ItemViewHolder) holder).SndDoctor != null)
                ((ItemViewHolder) holder).SndDoctor.setText(data.get(position).snddoctor);
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        }
    }

    static class ItemViewHolder extends ViewHolder {

        TextView Diagnosis;
        TextView Purpose;
        TextView SndDepart;
        TextView SndDoctor;

        public ItemViewHolder(View view) {
            super(view);
            Diagnosis = (TextView) view.findViewById(R.id.DiagnosisView);
            Purpose = (TextView) view.findViewById(R.id.PurposeView);
            SndDepart = (TextView) view.findViewById(R.id.SndDepartView);
            SndDoctor = (TextView) view.findViewById(R.id.SndDoctorView);
        }
    }

    static class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}