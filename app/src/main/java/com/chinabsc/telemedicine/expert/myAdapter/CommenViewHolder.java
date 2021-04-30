package com.chinabsc.telemedicine.expert.myAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;

public class CommenViewHolder extends RecyclerView.ViewHolder {
    public TextView date;
    public TextView time;
    public TextView msg;
    public TextView addMsg;
    public CommenViewHolder(View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.DateTextView);
        time = itemView.findViewById(R.id.TimeTextView);
        msg = itemView.findViewById(R.id.MessageView);
        addMsg=itemView.findViewById(R.id.addContentView);

    }
}

