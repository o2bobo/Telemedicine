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
import com.chinabsc.telemedicine.expert.entity.SchedulingInfo;

import java.util.ArrayList;

import static com.chinabsc.telemedicine.expert.utils.PublicUrl.dateToWeek;
import static com.chinabsc.telemedicine.expert.utils.PublicUrl.getNowTime;

public class SchedulingListAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_FINISH = 2;
    private Context context;
    private ArrayList<SchedulingInfo> data;

    public SchedulingListAdapter(Context context, ArrayList<SchedulingInfo> data) {
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
            View view = LayoutInflater.from(context).inflate(R.layout.scheduling_list_item, parent,
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
                String day = data.get(position).formattime;
                String week = dateToWeek(day);
                ((ItemViewHolder) holder).DateTextView.setText(data.get(position).formattime + "\n" + week);
                if (position > 0) {
                    if (data.get(position - 1).formattime.equals(data.get(position).formattime)) {
                        ((ItemViewHolder) holder).DateTextView.setText("");
                    }
                }
            }
            if (((ItemViewHolder) holder).TimeTextView != null)
                ((ItemViewHolder) holder).TimeTextView.setText(data.get(position).se);
            Long now = getNowTime();
            Long stime = data.get(position).starttime;
            Long etime = data.get(position).endtime;
            Long regusable = data.get(position).regusable;
            String status = "";
            if (((ItemViewHolder) holder).StatusTextView != null) {
                if (stime > now && regusable > 0) {
                    ((ItemViewHolder) holder).StatusTextView.setTextColor(Color.parseColor("#49d288"));
                    status = "预约中";
                    ((ItemViewHolder) holder).StatusTextView.setText(status);
                } else if (stime > now && regusable == 0) {
                    ((ItemViewHolder) holder).StatusTextView.setTextColor(Color.parseColor("#ff2600"));
                    status = "预约满员";
                    ((ItemViewHolder) holder).StatusTextView.setText(status);
                } else if (stime <= now && etime > now) {
                    status = "进行中";
                    ((ItemViewHolder) holder).StatusTextView.setTextColor(Color.parseColor("#af7b78"));
                    ((ItemViewHolder) holder).StatusTextView.setText(status);
                } else if (etime <= now) {
                    status = "已结束";
                    ((ItemViewHolder) holder).StatusTextView.setTextColor(Color.parseColor("#ff2600"));
                    ((ItemViewHolder) holder).StatusTextView.setText(status);
                }
            }
            if (((ItemViewHolder) holder).RcvsiteNameView != null)
                ((ItemViewHolder) holder).RcvsiteNameView.setText(data.get(position).rcvsitename);
            if (((ItemViewHolder) holder).ExpertNameView != null)
                ((ItemViewHolder) holder).ExpertNameView.setText(data.get(position).department + "    " + data.get(position).expertname);
            if (((ItemViewHolder) holder).RegusableView != null)
                ((ItemViewHolder) holder).RegusableView.setText("余号：" + data.get(position).regusable);
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
        TextView DateTextView;
        TextView TimeTextView;
        TextView RcvsiteNameView;
        TextView ExpertNameView;
        TextView RegusableView;
        TextView StatusTextView;

        public ItemViewHolder(View view) {
            super(view);
            DateTextView = (TextView) view.findViewById(R.id.DateTextView);
            TimeTextView = (TextView) view.findViewById(R.id.TimeTextView);
            RcvsiteNameView = (TextView) view.findViewById(R.id.RcvsiteNameView);
            ExpertNameView = (TextView) view.findViewById(R.id.ExpertNameView);
            RegusableView = (TextView) view.findViewById(R.id.RegusableView);
            StatusTextView = (TextView) view.findViewById(R.id.StatusTextView);
        }
    }

    static class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}