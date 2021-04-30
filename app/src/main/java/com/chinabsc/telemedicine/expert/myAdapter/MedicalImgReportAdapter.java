package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.MiddlePatientEntity;
import com.chinabsc.telemedicine.expert.entity.MiddlewareMedicalImgEntity;
import com.chinabsc.telemedicine.expert.expertinterfaces.OnItemInterfaceClick;

public class MedicalImgReportAdapter extends RecyclerView.Adapter<MedicalImgReportAdapter.ViewHolder> {
    Context context;
    MiddlewareMedicalImgEntity entity;
    OnItemInterfaceClick onItemInterfaceClick;

    public void setOnItemClickListener(OnItemInterfaceClick onItemInterfaceClick) {
        this.onItemInterfaceClick = onItemInterfaceClick;
    }

    public MedicalImgReportAdapter(Context context, MiddlewareMedicalImgEntity entity) {
        this.context = context;
        this.entity = entity;
        if (entity.getData()!=null){
            Log.i("MiddleSelectActivity", "size1111==" + entity.getData().size());
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.middleware_imgcheck_info_item, parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i("MiddleSelectActivity", "position==" + position);
        if (entity.getData()==null){
            return;
        }
        if (holder instanceof ViewHolder) {
            String[] dateTime=entity.getData().get(position).getReportTime().split("\\s+");
            if (position==0){
                holder.date.setText(dateTime[0]);
            }else {
                String[] temp=entity.getData().get(position-1).getReportTime().split("\\s+");
                if (dateTime[0].equals(temp[0])){
                    holder.date.setText(" ");
                } else{
                    holder.date.setText(dateTime[0]);
                }
            }
            holder.time.setText(dateTime[1]);
            holder.msg.setText(entity.getData().get(position).getReportName()+" / "+entity.getData().get(position).getCheckPoint());

            holder.seeReportTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemInterfaceClick.onclick(entity.getData().get(position).getReportAddress(), "seeReport");

                }
            });
            holder.seeMedicalImgTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url="user_name=vuem&password=vuem123&force_all_browsers=true&accession_number="+entity.getData().get(position).getAccessionNumber();
                    onItemInterfaceClick.onclick(url, "seeMedical");

                }
            });

        }
    }


    @Override
    public int getItemCount() {
        if (entity.getData()==null){
            return 0;
        }
        Log.i("MiddleSelectActivity", "size==" + entity.getData().size());
        return entity.getData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView msg;
        TextView seeReportTv;
        TextView seeMedicalImgTv;
        public ViewHolder(View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.DateTextView);
            time=itemView.findViewById(R.id.TimeTextView);
            msg=itemView.findViewById(R.id.MessageView);
            seeReportTv=itemView.findViewById(R.id.seeReport);
            seeMedicalImgTv=itemView.findViewById(R.id.seeMedicalImg);
        }
    }

}
