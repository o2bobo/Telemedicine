package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinabsc.telemedicine.expert.R;

import java.util.List;

public abstract class MiddleCommenAdapter<T> extends RecyclerView.Adapter {
    private Context context;
    private List<T> list;

    public MiddleCommenAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.middleware_commen_info_item, parent, false);
        CommenViewHolder holder = new CommenViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindData(holder,position);
    }
    public  abstract void bindData(RecyclerView.ViewHolder holder,int position);
    @Override
    public int getItemCount() {

        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
