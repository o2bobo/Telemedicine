package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.BilateralDiagnosis;
import com.chinabsc.telemedicine.expert.myAdapter.BilateralDiagnosisListAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
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

@ContentView(R.layout.activity_bilateral_diagnosis)
public class BilateralDiagnosisActivity extends BaseActivity {
    private String TAG = "BilateralDiagnosisActivity";
    private String mUserId = "";
    private String mType = "";
    @ViewInject(R.id.DiagnosisSwipeToLoadLayout)
    private SwipeToLoadLayout mDiagnosisSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mDiagnosisRecyclerView;
    private LinearLayoutManager mDiagnosisLayoutManager;
    private boolean isLoading;
    private int mDiagnosisTotal = 0;
    private int mDiagnosisStart = 0;
    private BilateralDiagnosisListAdapter mDiagnosisListAdapter;
    private Handler mDiagnosisHandler = new Handler();

    public ArrayList<BilateralDiagnosis> mDiagnosisItem = new ArrayList<BilateralDiagnosis>();

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

        mUserId = SPUtils.get(BilateralDiagnosisActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        mType = getIntent().getStringExtra("type");
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(mUserId)) {
            init();
            getDiagnosisListData();
        }
    }

    private void init() {
        mDiagnosisLayoutManager = new LinearLayoutManager(this);
        mDiagnosisRecyclerView.setLayoutManager(mDiagnosisLayoutManager);
        mDiagnosisListAdapter = new BilateralDiagnosisListAdapter(this, mDiagnosisItem);
        mDiagnosisRecyclerView.setAdapter(mDiagnosisListAdapter);

        mDiagnosisRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Diagnosistate) {
                super.onScrollStateChanged(recyclerView, Diagnosistate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mDiagnosisLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mDiagnosisListAdapter.getItemCount()) {
                    if (mDiagnosisTotal > mDiagnosisItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mDiagnosisHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getDiagnosisListData();
                                    Log.i(TAG, "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mDiagnosisListAdapter.setOnItemClickListener(new BilateralDiagnosisListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onItemClick");
                Intent it = new Intent(BilateralDiagnosisActivity.this, BilateralDiagnosisInfoActivity.class);
                it.putExtra(BilateralDiagnosisInfoActivity.CLINIC_ID, mDiagnosisItem.get(position).clinicid);
                startActivity(it);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getDiagnosisListData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/refclinic/getListByType");
        Log.i(TAG, "getDiagnosisListData url==" + BaseConst.DEAULT_URL + "/mobile/refclinic/getListByType");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i(TAG, mDiagnosisStart + "");
        params.addQueryStringParameter("userId", mUserId);
        params.addQueryStringParameter("begin", mDiagnosisStart + "");
        params.addQueryStringParameter("limit", "5");
        params.addQueryStringParameter("refType",mType);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getDiagnosisListData:" + result);
                parseTabJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.i(TAG, "getDiagnosisListData onError:" + ex.getMessage());
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

    private void parseTabJson(String result) {
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
                            mDiagnosisTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray SchedulingArray = new JSONArray(array);
                            for (int i = 0; i < SchedulingArray.length(); i++) {
                                JSONObject l = SchedulingArray.getJSONObject(i);
                                BilateralDiagnosis item = new BilateralDiagnosis();
                                item.total = total;
                                if (l.has("clinicid")) {
                                    item.clinicid = l.getString("clinicid");
                                }
                                if (l.has("sndsitename")) {
                                    item.sndsitename = l.getString("sndsitename");
                                }
                                if (l.has("rcvsitename")) {
                                    item.rcvsitename = l.getString("rcvsitename");
                                }
                                if (l.has("statusname")) {
                                    item.statusname = l.getString("statusname");
                                }
                                if (l.has("name")) {
                                    item.name = l.getString("name");
                                }
                                if (l.has("gender")) {
                                    item.gender = l.getString("gender");
                                }
                                if (l.has("age")) {
                                    item.age = l.getString("age");
                                }
                                if (l.has("reftype")) {
                                    item.reftype = l.getString("reftype");
                                }
                                if (l.has("status")) {
                                    item.status = l.getString("status");
                                }
                                if (l.has("createtime")) {
                                    item.createtime = l.getLong("createtime");
                                }
                                if (l.has("updatetime")) {
                                    item.updatetime = l.getLong("updatetime");
                                }
                                mDiagnosisItem.add(item);
                            }
                            mDiagnosisStart = mDiagnosisStart + SchedulingArray.length();
                            mDiagnosisListAdapter.notifyDataSetChanged();
                            mDiagnosisSwipeToLoadLayout.setRefreshing(false);
                            mDiagnosisListAdapter.notifyItemRemoved(mDiagnosisListAdapter.getItemCount());
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
