package com.chinabsc.telemedicine.expert.expertActivity;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.LisEntity;
import com.chinabsc.telemedicine.expert.myAdapter.LisAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_lis)
public class LisActivity extends BaseActivity {
    RecyclerView lisRecycleRV;
    LisAdapter lisAdapter;
    private String TAG = "LisActivity";
    LisEntity.DataBean date;

    @ViewInject(R.id.time)
    private TextView timeTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        getLisIntent();
        initView();
        setData();
    }
    @Event(value = R.id.BackImageView, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.BackImageView:
                onBackPressed();
        }
    }

        private void initView() {
        List<LisEntity.DataBean.RisExamItemReturnListBean> list = new ArrayList<>();
        lisRecycleRV = findViewById(R.id.lisRecycle);
        //为recyclerView设置布局管理器
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lisRecycleRV.setLayoutManager(linearLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.devider_shape));
        lisRecycleRV.addItemDecoration(divider);
        if (date != null && date.getRisExamItemReturnList().size() > 0) {
            list.addAll(date.getRisExamItemReturnList());
        }
        lisAdapter = new LisAdapter(this, list);
        lisRecycleRV.setAdapter(lisAdapter);
    }

    private void setData() {
        if (date != null) {
            timeTV.setText("报告时间: "+date.getSampleTime());
 /*           testIdTV.setText(date.getTestId());
            hisIdTV.setText(date.getHisId());
            sndDepartTV.setText(date.getSndDepart());
            sndDoctorTV.setText(date.getSndDoctor());
            sampleTV.setText(date.getSample());*/

        }

    }

    private void getLisIntent() {
        Bundle bundle = getIntent().getExtras();
        date = (LisEntity.DataBean) bundle.getSerializable("middleMsg");
        Log.i(TAG, "list size()" + date.getRisExamItemReturnList().size() + " ----hisId " + date.getHisId());
    }
}
