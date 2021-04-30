package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.SubscribeInfo;

import java.util.ArrayList;


public class SubscribeListAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_FINISH = 2;
    private Context context;
    private ArrayList<SubscribeInfo> data;

    public SubscribeListAdapter(Context context, ArrayList<SubscribeInfo> data) {
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
            View view = LayoutInflater.from(context).inflate(R.layout.subscribe_list_item, parent,
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
            if (((ItemViewHolder) holder).DateTextView != null) {
                ((ItemViewHolder) holder).DateTextView.setText(data.get(position).schedulingtime);
            }
            String status = data.get(position).statusname;
            if (((ItemViewHolder) holder).StatusTextView != null) {
                //((ItemViewHolder) holder).StatusTextView.setTextColor(Color.parseColor("#49d288"));
                // TODO: 2018-12-27 颜色随状态变更 
                ((ItemViewHolder) holder).StatusTextView.setText(status);
            }
            if (((ItemViewHolder) holder).SndsiteTextView != null)
                ((ItemViewHolder) holder).SndsiteTextView.setText(data.get(position).sndsitename + "    " + data.get(position).doctorname);
            if (((ItemViewHolder) holder).PatientNameView != null)
                ((ItemViewHolder) holder).PatientNameView.setText(data.get(position).name + "    " + data.get(position).gender + "    " + data.get(position).age + "岁");
        }
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

    static class ItemViewHolder extends ViewHolder {
        TextView DateTextView;
        TextView StatusTextView;
        TextView SndsiteTextView;
        TextView PatientNameView;

        public ItemViewHolder(View view) {
            super(view);
            DateTextView = (TextView) view.findViewById(R.id.DateTextView);
            StatusTextView = (TextView) view.findViewById(R.id.StatusTextView);
            SndsiteTextView = (TextView) view.findViewById(R.id.SndsiteTextView);
            PatientNameView = (TextView) view.findViewById(R.id.PatientNameView);
        }
    }

    static class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}