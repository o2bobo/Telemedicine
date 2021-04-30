package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;

import android.widget.RelativeLayout;

import android.widget.TextView;


import com.chinabsc.telemedicine.expert.myAdapter.CommenViewHolder;
import com.chinabsc.telemedicine.expert.myAdapter.MiddleCommenAdapter;
import com.chinabsc.telemedicine.expert.utils.T;
import com.chinabsc.telemedicine.expert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_middleware_emr)
public class MiddlewareEmrActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";
    private String address;
    @ViewInject(R.id.EmrListView)
    private RecyclerView mEmrListView;
    @ViewInject(R.id.LoadingLayout)
    private RelativeLayout mLoadingLayout;
     MiddleCommenAdapter mEmrInfoListAdapter;
    ArrayList<DateInfo> itemList=new ArrayList<>();
    @Event(value = {
            R.id.BackImageView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
        }
    }

    public class DateInfo {
        public String sn;
        public String recordType;
        public String recordName;
        public String recordTime;
        public String content;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            address = getIntent().getStringExtra("address");
        } else {
            finish();
        }
        mEmrInfoListAdapter = new MiddleCommenAdapter(this,itemList) {
            @Override
            public void bindData(RecyclerView.ViewHolder holder, final int position) {

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MiddlewareEmrActivity.this, ElecMedicalRecordActivity.class);
                        intent.putExtra("medicalContent", itemList.get(position).content);
                        MiddlewareEmrActivity.this.startActivity(intent);
                    }
                });
                if(holder instanceof CommenViewHolder){
                    String dateTime[]=itemList.get(position).recordTime.split("\\s+");
                    if (position==0){
                        ((CommenViewHolder) holder).date.setText(dateTime[0]);
                    }else {
                        String tempTime[]=itemList.get(position-1).recordTime.split("\\s+");
                        if (tempTime[0].equals(dateTime[0])){
                            ((CommenViewHolder) holder).date.setText(" ");
                        }else {
                            ((CommenViewHolder) holder).date.setText(dateTime[0]);
                        }
                    }

                    ((CommenViewHolder) holder).time.setText(dateTime[1]);
                    ((CommenViewHolder) holder).msg.setText(itemList.get(position).recordType);
                }

            }


        };
        mEmrListView.setLayoutManager(new LinearLayoutManager(this));
        mEmrListView.setAdapter(mEmrInfoListAdapter);
        getEmr(mTelemedicineInfoId);
    }

    private void getEmr(String tId) {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(address + "/api/medical_record?hisId=" + tId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseJson(result);
                Log.i("zzw","mEmrItem size"+itemList.size());
                mEmrInfoListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i("onError", "onError:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                mLoadingLayout.setVisibility(View.GONE);
            }
        });
    }


    private void parseJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("code")) {
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray array = new JSONArray(data);
                        itemList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject k = array.getJSONObject(i);
                            DateInfo item = new DateInfo();
                            if (k.has("sn")) {
                                item.sn = k.getString("sn");
                            }
                            if (k.has("recordType")) {
                                item.recordType = k.getString("recordType");
                            }
                            if (k.has("recordName")) {
                                item.recordName = k.getString("recordName");
                            }
                            if (k.has("recordTime")) {
                                item.recordTime = k.getString("recordTime");
                            }
                            if (k.has("content")) {
                                item.content = k.getString("content");
                            }

                            itemList.add(item);
                        }
                    }
                } else if (resultCode.equals("401")) {
                    T.showMessage(getApplicationContext(), getString(R.string.login_timeout));
                    delToken();
                    doLogout();
                } else {
                    T.showMessage(getApplicationContext(), getString(R.string.api_error) + resultCode);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
