package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.ElcItem;
import com.chinabsc.telemedicine.expert.myAdapter.ElcAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
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

@ContentView(R.layout.activity_elc)
public class ElcActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";

    @ViewInject(R.id.ElcSwipeToLoadLayout)
    private SwipeToLoadLayout mElcSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mElcRecyclerView;
    private LinearLayoutManager mElcLayoutManager;
    private boolean isLoading;
    private int mElcTotal = 0;
    private int mElcStart = 0;
    private ElcAdapter mElcAdapter;
    private Handler mElcHandler = new Handler();

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

    public ArrayList<ElcItem> mElcItem = new ArrayList<ElcItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            Log.i("Elc Bundle", "Telemedicine" + mTelemedicineInfoId);
        } else {
            Log.i("Elc Bundle", "bundle == null");
            finish();
        }
        
        init();

        mElcTotal = 0;
        mElcStart = 0;
        mElcItem.clear();
        getElc(mTelemedicineInfoId);
    }

    private void init() {
        mElcLayoutManager = new LinearLayoutManager(this);
        mElcRecyclerView.setLayoutManager(mElcLayoutManager);
        mElcAdapter = new ElcAdapter(this, mElcItem);
        mElcRecyclerView.setAdapter(mElcAdapter);

        mElcRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Elctate) {
                super.onScrollStateChanged(recyclerView, Elctate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mElcLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mElcAdapter.getItemCount()) {
                    if (mElcTotal > mElcItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mElcHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getElc(mTelemedicineInfoId);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mElcAdapter.setOnItemClickListener(new ElcAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    private void getElc(String tId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + tId + "/elc");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("begin", mElcStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getElc onSuccess", result);
                parseJson(result);
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
        ArrayList<ElcItem> itemList = new ArrayList<ElcItem>();
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONObject k = new JSONObject(data);
                        int total = 0;
                        if (k.has("total")) {
                            total = k.getInt("total");
                            mElcTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                ElcItem item = new ElcItem();
                                item.total = total;
                                if (l.has("diagnosis")) {
                                    item.diagnosis = l.getString("diagnosis");
                                }
                                if (l.has("purpose")) {
                                    item.purpose = l.getString("purpose");
                                }
                                if (l.has("snddepart")) {
                                    item.snddepart = l.getString("snddepart");
                                }
                                if (l.has("sndDoctor")) {
                                    item.snddoctor = l.getString("sndDoctor");
                                }
                                mElcItem.add(item);
                            }
                            mElcStart = mElcStart + newsArray.length();
                            mElcAdapter.notifyDataSetChanged();
                            mElcSwipeToLoadLayout.setRefreshing(false);
                            mElcAdapter.notifyItemRemoved(mElcAdapter.getItemCount());
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
