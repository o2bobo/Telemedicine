package com.chinabsc.telemedicine.expert.myAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;

public class BQFViewHolder extends RecyclerView.ViewHolder {
  public   TextView tv;
    public ImageView markImg;
    public BQFViewHolder(View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.bingqu_tv);
        markImg=itemView.findViewById(R.id.mark);
    }
}

