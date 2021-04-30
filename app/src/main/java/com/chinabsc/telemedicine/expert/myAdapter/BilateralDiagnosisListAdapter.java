package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.BilateralDiagnosis;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;

import java.util.ArrayList;

public class BilateralDiagnosisListAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_FINISH = 2;
    private Context context;
    private ArrayList<BilateralDiagnosis> data;

    public BilateralDiagnosisListAdapter(Context context, ArrayList<BilateralDiagnosis> data) {
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
            View view = LayoutInflater.from(context).inflate(R.layout.bilateral_diagnosis_item, parent,
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
            if (((ItemViewHolder) holder).createtime != null) {
                String createtime = PublicUrl.stampToDate(data.get(position).createtime + "");
                ((ItemViewHolder) holder).createtime.setText(createtime);
            }
            if (((ItemViewHolder) holder).reftype != null) {
                ((ItemViewHolder) holder).reftype.setText(data.get(position).reftype);
            }
            if (((ItemViewHolder) holder).updatetime != null) {
                String updatetime = PublicUrl.stampToDate(data.get(position).updatetime + "");
                ((ItemViewHolder) holder).updatetime.setText(updatetime);
            }
            if (((ItemViewHolder) holder).statusname != null) {
                ((ItemViewHolder) holder).statusname.setText(data.get(position).statusname);
            }
            if (((ItemViewHolder) holder).sndsitename != null) {
                ((ItemViewHolder) holder).sndsitename.setText(data.get(position).sndsitename);
            }
            if (((ItemViewHolder) holder).name != null) {
                String patientGender = data.get(position).gender;
                String sex = "";
                if (patientGender.equals("0") || patientGender.equals("男")) {
                    sex = "男";
                } else if (patientGender.equals("1") || patientGender.equals("女")) {
                    sex = "女";
                } else {
                    sex = "未知";
                }
                ((ItemViewHolder) holder).name.setText(data.get(position).name + "    " + sex + "    " + data.get(position).age + "岁");
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
    }

    static class ItemViewHolder extends ViewHolder {
        TextView createtime;
        TextView reftype;
        TextView updatetime;
        TextView statusname;
        TextView sndsitename;
        TextView name;

        public ItemViewHolder(View view) {
            super(view);
            createtime = (TextView) view.findViewById(R.id.createtime);
            reftype = (TextView) view.findViewById(R.id.reftype);
            updatetime = (TextView) view.findViewById(R.id.updatetime);
            statusname = (TextView) view.findViewById(R.id.statusname);
            sndsitename = (TextView) view.findViewById(R.id.sndsitename);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    static class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}