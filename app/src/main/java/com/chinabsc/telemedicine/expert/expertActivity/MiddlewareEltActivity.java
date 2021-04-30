package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.entity.LisEntity;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_middleware_elt)
public class MiddlewareEltActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";
    private String address;
    @ViewInject(R.id.EltListView)
    private RecyclerView mEltListView;
    @ViewInject(R.id.LoadingLayout)
    private RelativeLayout mLoadingLayout;
    MiddleCommenAdapter middleCommenAdapter;
    LisEntity lisEntity;

    public ArrayList<DateInfo> mEltItem = new ArrayList<DateInfo>();

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
        public String sampleTime;
        public String diagnosis;
        public String testId;
        public String purpose;
        public String sample;
        public String sampleMemo;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            Log.i("Elt Bundle", "Telemedicine" + mTelemedicineInfoId);
            address = getIntent().getStringExtra("address");
        } else {
            Log.i("Elt Bundle", "bundle == null");
            finish();
        }
        middleCommenAdapter = new MiddleCommenAdapter(this, mEltItem) {
            @Override
            public void bindData(RecyclerView.ViewHolder holder, final int position) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lisEntity == null) {
                            return;
                        }
                        Log.i("MiddlewareEltActivity", " click position=" + position);
                        Intent intent = new Intent(MiddlewareEltActivity.this, LisActivity.class);
                        Bundle bundle = new Bundle();
                        LisEntity.DataBean dateBean = lisEntity.getData().get(position);
                        bundle.putSerializable("middleMsg", (Serializable) dateBean);
                        intent.putExtras(bundle);
                        MiddlewareEltActivity.this.startActivity(intent);
                    }
                });
                if (holder instanceof CommenViewHolder) {
                    String dateTime[] = mEltItem.get(position).sampleTime.split("\\s+");
                    if (position == 0) {
                        ((CommenViewHolder) holder).date.setText(dateTime[0]);
                    } else {
                        String tempTime[] = mEltItem.get(position - 1).sampleTime.split("\\s+");
                        if (tempTime[0].equals(dateTime[0])) {
                            ((CommenViewHolder) holder).date.setText(" ");
                        } else {
                            ((CommenViewHolder) holder).date.setText(dateTime[0]);
                        }
                    }

                    ((CommenViewHolder) holder).time.setText(dateTime[1]);
                    ((CommenViewHolder) holder).msg.setText(mEltItem.get(position).purpose);
                }
            }
        };
        mEltListView.setLayoutManager(new LinearLayoutManager(this));
        mEltListView.setAdapter(middleCommenAdapter);
        getElt(mTelemedicineInfoId);
    }

    private void getElt(String tId) {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(address + "/api/lis_test?hisId=" + tId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseJson(result);
                middleCommenAdapter.notifyDataSetChanged();
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
        Log.i("zzw", "result==" + result);
        lisEntity = com.alibaba.fastjson.JSONObject.parseObject(result, LisEntity.class);


        try {
            JSONObject j = new JSONObject(result);
            if (j.has("code")) {
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray array = new JSONArray(data);
                        mEltItem.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject k = array.getJSONObject(i);
                            DateInfo item = new DateInfo();
                            if (k.has("testId")) {
                                item.testId = k.getString("testId");
                            }
                            if (k.has("sampleTime")) {
                                item.sampleTime = k.getString("sampleTime");
                            }
                            if (k.has("purpose")) {
                                item.purpose = k.getString("purpose");
                            }
                            if (k.has("sample")) {
                                item.sample = k.getString("sample");
                            }
                            if (k.has("sampleMemo")) {
                                item.sampleMemo = k.getString("sampleMemo");
                            }
                            if (k.has("diagnosis")) {
                                item.diagnosis = k.getString("diagnosis");
                            }
                            mEltItem.add(item);
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
