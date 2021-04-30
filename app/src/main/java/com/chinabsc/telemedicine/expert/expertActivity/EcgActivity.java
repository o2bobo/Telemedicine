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
import com.chinabsc.telemedicine.expert.entity.EcgItem;
import com.chinabsc.telemedicine.expert.myAdapter.EcgAdapter;
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

@ContentView(R.layout.activity_ecg)
public class EcgActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";

    @ViewInject(R.id.EcgSwipeToLoadLayout)
    private SwipeToLoadLayout mEcgSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mEcgRecyclerView;
    private LinearLayoutManager mEcgLayoutManager;
    private boolean isLoading;
    private int mEcgTotal = 0;
    private int mEcgStart = 0;
    private EcgAdapter mEcgAdapter;
    private Handler mEcgHandler = new Handler();

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

    public ArrayList<EcgItem> mEcgItem = new ArrayList<EcgItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            Log.i("Ecg Bundle", "Telemedicine" + mTelemedicineInfoId);
        } else {
            Log.i("Ecg Bundle", "bundle == null");
            finish();
        }

        init();

        mEcgTotal = 0;
        mEcgStart = 0;
        mEcgItem.clear();
        getEcg(mTelemedicineInfoId);
    }

    private void init() {
        mEcgLayoutManager = new LinearLayoutManager(this);
        mEcgRecyclerView.setLayoutManager(mEcgLayoutManager);
        mEcgAdapter = new EcgAdapter(this, mEcgItem);
        mEcgRecyclerView.setAdapter(mEcgAdapter);

        mEcgRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Ecgtate) {
                super.onScrollStateChanged(recyclerView, Ecgtate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mEcgLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mEcgAdapter.getItemCount()) {
                    if (mEcgTotal > mEcgItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mEcgHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getEcg(mTelemedicineInfoId);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mEcgAdapter.setOnItemClickListener(new EcgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getEcg(String tId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + tId + "/ecg");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("begin", mEcgStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getEcg onSuccess", result);
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
            }
        });
    }

    private void parseJson(String result) {
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
                            mEcgTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                EcgItem item = new EcgItem();
                                item.total = total;
                                if (l.has("sndSiteName")) {
                                    item.sndSiteName = l.getString("sndSiteName");
                                }
                                if (l.has("name")) {
                                    item.name = l.getString("name");
                                }
                                if (l.has("age")) {
                                    item.age = l.getString("age");
                                }
                                if (l.has("gender")) {
                                    item.gender = l.getString("gender");
                                }
                                if (l.has("doctorName")) {
                                    item.doctorName = l.getString("doctorName");
                                }
                                if (l.has("createTime")) {
                                    item.createTime = l.getString("createTime");
                                }
                                mEcgItem.add(item);
                            }
                            mEcgStart = mEcgStart + newsArray.length();
                            mEcgAdapter.notifyDataSetChanged();
                            mEcgSwipeToLoadLayout.setRefreshing(false);
                            mEcgAdapter.notifyItemRemoved(mEcgAdapter.getItemCount());
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
