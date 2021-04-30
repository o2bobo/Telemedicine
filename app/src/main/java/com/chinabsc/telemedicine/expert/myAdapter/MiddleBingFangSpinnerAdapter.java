package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.MiddleBingFangEntity;
import com.chinabsc.telemedicine.expert.entity.MiddleBingQuEntity;

public class MiddleBingFangSpinnerAdapter extends BaseAdapter {
    private Context ctx;
    MiddleBingFangEntity middleBingFangEntity;

    public MiddleBingFangSpinnerAdapter(Context ctx,MiddleBingFangEntity middleBingFangEntity) {
        this.ctx = ctx;
        this.middleBingFangEntity = middleBingFangEntity;
    }

    @Override
    public int getCount() {
        return middleBingFangEntity.getData().size();
    }

    @Override
    public Object getItem(int position) {
        return middleBingFangEntity.getData().get(position);
    }

    @Override
    public long getItemId(int position) {
       // Log.i("MiddleSelectActivity","position"+position);
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.item_spinner_bingqu_layout, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();// get convertView's holder

        holder.bingquTv.setText(middleBingFangEntity.getData().get(position).getWardName());


        return convertView;
    }
    class ViewHolder {
        TextView bingquTv;



        public ViewHolder(View convertView){
            bingquTv = (TextView) convertView.findViewById(R.id.bingqu_tv);
            convertView.setTag(this);//set a viewholder
        }
    }

}
