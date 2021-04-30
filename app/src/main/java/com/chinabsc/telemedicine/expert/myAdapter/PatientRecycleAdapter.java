package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.MiddlePatientEntity;
import com.chinabsc.telemedicine.expert.expertActivity.MiddlewareActivity;
import com.chinabsc.telemedicine.expert.expertinterfaces.OnItemInterfaceClick;

public class PatientRecycleAdapter extends RecyclerView.Adapter<PatientRecycleAdapter.ViewHolder> {
    Context context;
    MiddlePatientEntity middlePatientEntity;
    OnItemInterfaceClick onItemInterfaceClick;
    public void setOnItemClickListener(OnItemInterfaceClick onItemInterfaceClick) {
        this.onItemInterfaceClick = onItemInterfaceClick;
    }
    public PatientRecycleAdapter(Context context, MiddlePatientEntity middlePatientEntity) {
        this.context = context;
        this.middlePatientEntity = middlePatientEntity;
        Log.i("MiddleSelectActivity","size1111=="+middlePatientEntity.getData().size());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recycle_middle_patient, parent,false);
        ViewHolder holder=new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i("MiddleSelectActivity","position=="+position);
        if (holder instanceof  ViewHolder){
            ((ViewHolder) holder).nameTv.setText(middlePatientEntity.getData().get(position).getPatientName()+" / "+middlePatientEntity.getData().get(position).getGender());
            ((ViewHolder) holder).birthTv.setText(middlePatientEntity.getData().get(position).getBirthDate());
            ((ViewHolder) holder).inpatientDateTv.setText(middlePatientEntity.getData().get(position).getInpatientDate());
            ((ViewHolder) holder).sickbedNumberTv.setText(middlePatientEntity.getData().get(position).getSickbedNumber());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                     onItemInterfaceClick.onclick(middlePatientEntity.getData().get(position).getHisId(),"");

                }
            });

        }
    }


    @Override
    public int getItemCount() {
        Log.i("MiddleSelectActivity","size=="+middlePatientEntity.getData().size());
        return middlePatientEntity.getData().size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        TextView birthTv;
        TextView inpatientDateTv;
        TextView sickbedNumberTv;



        public ViewHolder(View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.name);
            birthTv=itemView.findViewById(R.id.birth);
            inpatientDateTv= itemView.findViewById(R.id.inpatientDate);
            sickbedNumberTv=itemView.findViewById(R.id.sickbedNumber);

        }
    }

}
