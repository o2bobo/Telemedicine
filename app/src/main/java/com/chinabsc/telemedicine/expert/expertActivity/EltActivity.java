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
import com.chinabsc.telemedicine.expert.entity.EltItem;
import com.chinabsc.telemedicine.expert.myAdapter.EltAdapter;
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

@ContentView(R.layout.activity_elt)
public class EltActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";

    @ViewInject(R.id.EltSwipeToLoadLayout)
    private SwipeToLoadLayout mEltSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mEltRecyclerView;
    private LinearLayoutManager mEltLayoutManager;
    private boolean isLoading;
    private int mEltTotal = 0;
    private int mEltStart = 0;
    private EltAdapter mEltAdapter;
    private Handler mEltHandler = new Handler();

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

    public ArrayList<EltItem> mEltItem = new ArrayList<EltItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            Log.i("Elt Bundle", "Telemedicine" + mTelemedicineInfoId);
        } else {
            Log.i("Elt Bundle", "bundle == null");
            finish();
        }

        init();

        mEltTotal = 0;
        mEltStart = 0;
        mEltItem.clear();
        getElt(mTelemedicineInfoId);
    }

    private void init() {
        mEltLayoutManager = new LinearLayoutManager(this);
        mEltRecyclerView.setLayoutManager(mEltLayoutManager);
        mEltAdapter = new EltAdapter(this, mEltItem);
        mEltRecyclerView.setAdapter(mEltAdapter);

        mEltRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Elttate) {
                super.onScrollStateChanged(recyclerView, Elttate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mEltLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mEltAdapter.getItemCount()) {
                    if (mEltTotal > mEltItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mEltHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getElt(mTelemedicineInfoId);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mEltAdapter.setOnItemClickListener(new EltAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(EltActivity.this, EltItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EltItemActivity.ELT_INFO_ID, mEltItem.get(position).testId);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getElt(String tId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + tId + "/elt");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("begin", mEltStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getElt onSuccess", result);
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
                            mEltTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                EltItem item = new EltItem();
                                item.total = total;
                                if (l.has("testId")) {
                                    item.testId = l.getString("testId");
                                }
                                if (l.has("sampleTime")) {
                                    item.sampleTime = l.getString("sampleTime");
                                }
                                if (l.has("purpose")) {
                                    item.purpose = l.getString("purpose");
                                }
                                if (l.has("sample")) {
                                    item.sample = l.getString("sample");
                                }
                                if (l.has("sampleMemo")) {
                                    item.sampleMemo = l.getString("sampleMemo");
                                }
                                if (l.has("diagnosis")) {
                                    item.diagnosis = l.getString("diagnosis");
                                }
                                mEltItem.add(item);
                            }
                            mEltStart = mEltStart + newsArray.length();
                            mEltAdapter.notifyDataSetChanged();
                            mEltSwipeToLoadLayout.setRefreshing(false);
                            mEltAdapter.notifyItemRemoved(mEltAdapter.getItemCount());
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
