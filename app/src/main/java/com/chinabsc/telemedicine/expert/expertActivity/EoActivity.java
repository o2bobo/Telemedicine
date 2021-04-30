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
import com.chinabsc.telemedicine.expert.entity.EoItem;
import com.chinabsc.telemedicine.expert.myAdapter.EoAdapter;
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

@ContentView(R.layout.activity_eo)
public class EoActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";

    @ViewInject(R.id.EoSwipeToLoadLayout)
    private SwipeToLoadLayout mEoSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mEoRecyclerView;
    private LinearLayoutManager mEoLayoutManager;
    private boolean isLoading;
    private int mEoTotal = 0;
    private int mEoStart = 0;
    private EoAdapter mEoAdapter;
    private Handler mEoHandler = new Handler();

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

    public ArrayList<EoItem> mEoItem = new ArrayList<EoItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
        } else {
            finish();
        }

        init();

        mEoTotal = 0;
        mEoStart = 0;
        mEoItem.clear();

        getEo(mTelemedicineInfoId);
    }

    private void init() {
        mEoLayoutManager = new LinearLayoutManager(this);
        mEoRecyclerView.setLayoutManager(mEoLayoutManager);
        mEoAdapter = new EoAdapter(this, mEoItem);
        mEoRecyclerView.setAdapter(mEoAdapter);

        mEoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Eotate) {
                super.onScrollStateChanged(recyclerView, Eotate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mEoLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mEoAdapter.getItemCount()) {
                    if (mEoTotal > mEoItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mEoHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getEo(mTelemedicineInfoId);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mEoAdapter.setOnItemClickListener(new EoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getEo(String tId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + tId + "/eo");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("begin", mEoStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getEo onSuccess", result);
                parseJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
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
        ArrayList<EoItem> itemList = new ArrayList<EoItem>();
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
                            mEoTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                EoItem item = new EoItem();
                                item.total = total;
                                if (l.has("sn")) {
                                    item.sn = l.getString("sn");
                                }
                                if (l.has("startTime")) {
                                    item.startTime = l.getString("startTime");
                                }
                                if (l.has("operationName")) {
                                    item.operationName = l.getString("operationName");
                                }
                                if (l.has("incisionPart")) {
                                    item.incisionPart = l.getString("incisionPart");
                                }
                                if (l.has("incisionLevel")) {
                                    item.incisionLevel = l.getString("incisionLevel");
                                }
                                mEoItem.add(item);
                            }
                            mEoStart = mEoStart + newsArray.length();
                            mEoAdapter.notifyDataSetChanged();
                            mEoSwipeToLoadLayout.setRefreshing(false);
                            mEoAdapter.notifyItemRemoved(mEoAdapter.getItemCount());
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
