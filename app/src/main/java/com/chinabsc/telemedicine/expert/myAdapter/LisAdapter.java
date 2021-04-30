package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.LisEntity;

import java.util.List;

public class LisAdapter extends RecyclerView.Adapter<LisAdapter.ViewHolder> {
    Context context;
    List<LisEntity.DataBean.RisExamItemReturnListBean> list;

    public LisAdapter(Context context, List<LisEntity.DataBean.RisExamItemReturnListBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_middle_recycle_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemNameTV.setText(list.get(position).getItemName().trim());
        holder.itemUnitTV.setText(" " + list.get(position).getItemUnit().trim());
        StringBuffer tempS = new StringBuffer();
        if (!TextUtils.isEmpty(list.get(position).getLowerLimit())) {
            tempS.append(list.get(position).getLowerLimit() + " - ");
        }
        if (!TextUtils.isEmpty(list.get(position).getUpperLimit())) {
            tempS.append(list.get(position).getUpperLimit());
        }
        if (!TextUtils.isEmpty(list.get(position).getLowerLimit()) && !TextUtils.isEmpty(list.get(position).getUpperLimit())) {
            holder.limitTV.setText(tempS.toString());
        } else {
            holder.limitTV.setText(" ");
        }
        holder.itemResultTv.setText(list.get(position).getItemResult());
        holder.itemValueTv.setText(" "+list.get(position).getItemValue());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemValueTv;
        TextView itemResultTv;
        TextView limitTV;
        TextView itemUnitTV;
        TextView itemNameTV;

        public ViewHolder(View itemView) {
            super(itemView);
            itemValueTv = itemView.findViewById(R.id.itemValue);
            limitTV = itemView.findViewById(R.id.limit);
            itemUnitTV = itemView.findViewById(R.id.itemUnit);
            itemNameTV = itemView.findViewById(R.id.itemName);
            itemResultTv=itemView.findViewById(R.id.itemResult);
        }
    }

    /**
     * 针对TextView显示中文中出现的排版错乱问题，通过调用此方法得以解决
     *
     * @param str
     * @return 返回全部为全角字符的字符串
     */
    /*public String toDBC(String str) {
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }

        }
        return new String(c);
    }*/
}
