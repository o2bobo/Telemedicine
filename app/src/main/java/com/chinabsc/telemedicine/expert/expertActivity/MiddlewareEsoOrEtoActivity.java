package com.chinabsc.telemedicine.expert.expertActivity;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;


import android.widget.TextView;


import com.chinabsc.telemedicine.expert.R;

import com.chinabsc.telemedicine.expert.myAdapter.CommenViewHolder;
import com.chinabsc.telemedicine.expert.myAdapter.MiddleCommenAdapter;

import com.chinabsc.telemedicine.expert.utils.T;

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

@ContentView(R.layout.activity_middleware_eso_or_eto)
public class MiddlewareEsoOrEtoActivity extends BaseActivity {
    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public static String TYPE_ID = "TYPE_ID";
    public String mTelemedicineInfoId = "";
    public String mTypeId = "";
    private String address;
    @ViewInject(R.id.TitleTextView)
    private TextView mTitleTextView;
    @ViewInject(R.id.EsoListView)
    private RecyclerView mEsoListView;
    public MiddleCommenAdapter mEsoInfoListAdapter;

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
        public String implementTime;
        public String orderContent;
        public String specification;
        public String status;
    }

    public ArrayList<DateInfo> mEsoItem = new ArrayList<DateInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            mTypeId = bundle.getString(TYPE_ID);
            address = getIntent().getStringExtra("address");
            if (mTypeId.equals("standing_order")) {
                mTitleTextView.setText("长期医嘱");
            } else if (mTypeId.equals("temporary_order")) {
                mTitleTextView.setText("临时医嘱");
            }
        } else {
            finish();
        }
        mEsoInfoListAdapter = new MiddleCommenAdapter(this,mEsoItem) {
            @Override
            public void bindData(final RecyclerView.ViewHolder holder, int position) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                });
                if(holder instanceof CommenViewHolder){
                    String dateTime[]=mEsoItem.get(position).implementTime.split("\\s+");
                    if (position==0){
                        ((CommenViewHolder) holder).date.setText(dateTime[0]);
                    }else {
                        String tempTime[]=mEsoItem.get(position-1).implementTime.split("\\s+");
                        if (tempTime[0].equals(dateTime[0])){
                            ((CommenViewHolder) holder).date.setText(" ");
                        }else {
                            ((CommenViewHolder) holder).date.setText(dateTime[0]);
                        }
                    }

                    ((CommenViewHolder) holder).time.setText(dateTime[1]);
                    ((CommenViewHolder) holder).msg.setText(mEsoItem.get(position).orderContent);
                    ((CommenViewHolder) holder).addMsg.setText(mEsoItem.get(position).status);
                }
            }
        };
        mEsoListView.setLayoutManager(new LinearLayoutManager(this));
        mEsoListView.setAdapter(mEsoInfoListAdapter);
        getEsoOrEto(mTelemedicineInfoId, mTypeId);
    }

    private void getEsoOrEto(String tId, String type) {
        RequestParams params = new RequestParams(address + "/api/" + type + "?hisId=" + tId);
        //params.setSslSocketFactory(); // 设置ssl
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseJson(result);
                mEsoInfoListAdapter.notifyDataSetChanged();
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
                //mLoginLoadingLayout.setVisibility(View.GONE);
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
                        mEsoItem.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject k = array.getJSONObject(i);
                            DateInfo item = new DateInfo();
                            if (k.has("implementTime")) {
                                item.implementTime = k.getString("implementTime");
                            }
                            if (k.has("orderContent")) {
                                item.orderContent = k.getString("orderContent");
                            }
                            if (k.has("specification")) {
                                item.specification = k.getString("specification");
                            }
                            if (k.has("status")) {
                                item.status = k.getString("status");
                            }
                            mEsoItem.add(item);
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
