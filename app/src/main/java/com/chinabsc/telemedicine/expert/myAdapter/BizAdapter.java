package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.BizItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

public class BizAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_FINISH = 2;
    private Context context;
    private ArrayList<BizItem> data;

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();


    public BizAdapter(Context context, ArrayList<BizItem> data) {
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

        void onItemChildClick(int position, String tag);
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
            View view = LayoutInflater.from(context).inflate(R.layout.bizcloud_info_item, parent,
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
            String modality = data.get(position).modality;
            String bodyPart = data.get(position).bodyPart;
            String instanceNum = data.get(position).instanceNum;
            if (((ItemViewHolder) holder).date != null)
                ((ItemViewHolder) holder).date.setText(data.get(position).studyTime);
            if (((ItemViewHolder) holder).ModalityView != null)
                ((ItemViewHolder) holder).ModalityView.setText(modality);
            if (((ItemViewHolder) holder).BodyPartView != null)
                ((ItemViewHolder) holder).BodyPartView.setText(bodyPart);
            if (((ItemViewHolder) holder).InstanceNumView != null)
                ((ItemViewHolder) holder).InstanceNumView.setText(instanceNum);
            if (((ItemViewHolder) holder).HistoryView != null && data.get(position).history.length() < 151) {
                ((ItemViewHolder) holder).HistoryView.setText(data.get(position).history);
            } else {
                String history = data.get(position).history.substring(0, 145) + "......";
                ((ItemViewHolder) holder).HistoryView.setText(history);
            }
            if (((ItemViewHolder) holder).DiagnosisView != null && data.get(position).diagnosis.length() < 151) {
                ((ItemViewHolder) holder).DiagnosisView.setText(data.get(position).diagnosis);
            } else {
                String diagnosis = data.get(position).diagnosis.substring(0, 145) + "......";
                ((ItemViewHolder) holder).DiagnosisView.setText(diagnosis);
            }
            if (((ItemViewHolder) holder).DescriptionView != null && data.get(position).description.length() < 151) {
                ((ItemViewHolder) holder).DescriptionView.setText(data.get(position).description);
            } else {
                String description = data.get(position).description.substring(0, 145) + "......";
                ((ItemViewHolder) holder).DescriptionView.setText(description);
            }
            if (((ItemViewHolder) holder).OpinionsView != null && data.get(position).opinions.length() < 151) {
                ((ItemViewHolder) holder).OpinionsView.setText(data.get(position).opinions);
            } else {
                String opinions = data.get(position).opinions.substring(0, 145) + "......";
                ((ItemViewHolder) holder).OpinionsView.setText(opinions);
            }
            if (((ItemViewHolder) holder).ExpertNameView != null)
                ((ItemViewHolder) holder).ExpertNameView.setText(data.get(position).expertName);
            if (onItemClickListener != null) {
                ((ItemViewHolder) holder).InstanceClickText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemChildClick(position, "InstanceClickText");
                    }
                });
                ((ItemViewHolder) holder).HistoryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemChildClick(position, "HistoryView");
                    }
                });
                ((ItemViewHolder) holder).DiagnosisView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemChildClick(position, "DiagnosisView");
                    }
                });
                ((ItemViewHolder) holder).DescriptionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemChildClick(position, "DescriptionView");
                    }
                });
                ((ItemViewHolder) holder).OpinionsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemChildClick(position, "OpinionsView");
                    }
                });
            }
        }
    }

    static class ItemViewHolder extends ViewHolder {

        TextView date;
        TextView ModalityView;
        TextView BodyPartView;
        TextView InstanceNumView;
        TextView InstanceClickText;
        TextView HistoryView;
        TextView DiagnosisView;
        TextView DescriptionView;
        TextView OpinionsView;
        TextView ExpertNameView;

        public ItemViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.DateTextView);
            BodyPartView = (TextView) view.findViewById(R.id.BodyPartText);
            ModalityView = (TextView) view.findViewById(R.id.ModalityText);
            InstanceNumView = (TextView) view.findViewById(R.id.InstanceNumText);
            InstanceClickText = (TextView) view.findViewById(R.id.InstanceClickText);
            HistoryView = (TextView) view.findViewById(R.id.HistoryView);
            DiagnosisView = (TextView) view.findViewById(R.id.DiagnosisView);
            DescriptionView = (TextView) view.findViewById(R.id.DescriptionView);
            OpinionsView = (TextView) view.findViewById(R.id.OpinionsView);
            ExpertNameView = (TextView) view.findViewById(R.id.ExpertNameView);
        }
    }

    static class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}