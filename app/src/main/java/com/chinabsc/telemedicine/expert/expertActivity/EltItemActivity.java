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
import com.chinabsc.telemedicine.expert.entity.EltItemInfo;
import com.chinabsc.telemedicine.expert.myAdapter.EltListAdapter;
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

@ContentView(R.layout.activity_elt_item)
public class EltItemActivity extends BaseActivity {

    public static String ELT_INFO_ID = "ELT_INFO_ID";
    public String mEltInfoId = "";


    @ViewInject(R.id.EltSwipeToLoadLayout)
    private SwipeToLoadLayout mEltSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mEltRecyclerView;
    private LinearLayoutManager mEltLayoutManager;
    private boolean isLoading;
    private int mEltTotal = 0;
    private int mEltStart = 0;
    private EltListAdapter mEltListAdapter;
    private Handler mEltHandler = new Handler();

    public ArrayList<EltItemInfo> mEltItemInfo = new ArrayList<EltItemInfo>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mEltInfoId = bundle.getString(ELT_INFO_ID);
            Log.i("Elt Bundle", "Telemedicine" + mEltInfoId);
        } else {
            Log.i("Elt Bundle", "bundle == null");
            finish();
        }

        init();

        mEltTotal = 0;
        mEltStart = 0;
        mEltItemInfo.clear();
        getEltInfo(mEltInfoId);
    }

    private void init() {
        mEltLayoutManager = new LinearLayoutManager(this);
        mEltRecyclerView.setLayoutManager(mEltLayoutManager);
        mEltListAdapter = new EltListAdapter(this, mEltItemInfo);
        mEltRecyclerView.setAdapter(mEltListAdapter);

        mEltRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Elttate) {
                super.onScrollStateChanged(recyclerView, Elttate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mEltLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mEltListAdapter.getItemCount()) {
                    if (mEltTotal > mEltItemInfo.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mEltHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getEltInfo(mEltInfoId);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });
    }

    private void getEltInfo(String tId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/emr/findLisTestItemsByTestId");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i("test", mEltStart + "");
        params.addQueryStringParameter("testId", tId);
        params.addQueryStringParameter("begin", mEltStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("test", "getEltInfo:" + result);
                parseJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.i("onError", "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
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
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONObject k = new JSONObject(data);
                        int total = 0;
                        if (k.has("total")) {
                            total = k.getInt("total");
                            mEltTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                EltItemInfo item = new EltItemInfo();
                                item.total = total;
                                if (l.has("itemName")) {
                                    item.itemName = l.getString("itemName");
                                }
                                if (l.has("itemValue")) {
                                    item.itemValue = l.getString("itemValue");
                                }
                                if (l.has("itemUnit")) {
                                    item.itemUnit = l.getString("itemUnit");
                                }
                                if (l.has("itemResult")) {
                                    item.itemResult = l.getString("itemResult");
                                }
                                if (l.has("lowerLimit")) {
                                    item.lowerLimit = l.getString("lowerLimit");
                                }
                                if (l.has("upperLimit")) {
                                    item.upperLimit = l.getString("upperLimit");
                                }
                                mEltItemInfo.add(item);
                            }
                            mEltStart = mEltStart + newsArray.length();
                            mEltListAdapter.notifyDataSetChanged();
                            mEltSwipeToLoadLayout.setRefreshing(false);
                            mEltListAdapter.notifyItemRemoved(mEltListAdapter.getItemCount());
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
