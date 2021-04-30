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

public abstract class MiddleBingQuBingFangAdapter<T> extends RecyclerView.Adapter {
    Context context;
    List<T> list;

    public MiddleBingQuBingFangAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spinner_bingqu_layout, parent, false);
        BQFViewHolder bqfViewHolder = new BQFViewHolder(view);
        return bqfViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bingView(holder, position);
    }

    public abstract void bingView(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        Log.i("zzw","size()========="+list.size());
        return list.size();
    }
}

