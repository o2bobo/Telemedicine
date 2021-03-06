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
import com.chinabsc.telemedicine.expert.entity.SubscribeInfo;
import com.chinabsc.telemedicine.expert.myAdapter.SubscribeListAdapter;
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

@ContentView(R.layout.activity_subscribe_list)
public class SubscribeListActivity extends BaseActivity {
    private String TAG = "SubscribeListActivity";
    //门诊id TAG
    public static String SCHEDULE_ID = "SCHEDULE_ID";
    //门诊时间 TAG
    public static String SCHEDULE_TIME = "SCHEDULE_TIME";
    @ViewInject(R.id.SubscribeSwipeToLoadLayout)
    private SwipeToLoadLayout mSubscribeSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mSubscribeRecyclerView;
    private LinearLayoutManager mSubscribeLayoutManager;
    private boolean isLoading;
    private int mSubscribeTotal = 0;
    private int mSubscribeStart = 0;
    private SubscribeListAdapter mSubscribeListAdapter;
    private Handler mSubscribeHandler = new Handler();

    //门诊id
    private String mSchedulingId = "";
    //门诊时间
    private String mSchedulingTime = "";

    public ArrayList<SubscribeInfo> mSubscribeItem = new ArrayList<SubscribeInfo>();

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

        Intent i = getIntent();
        mSchedulingId = i.getStringExtra(SCHEDULE_ID);
        mSchedulingTime= i.getStringExtra(SCHEDULE_TIME);

        String userId = SPUtils.get(SubscribeListActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            getSubscribeListData();
        }
    }

    private void init() {
        mSubscribeLayoutManager = new LinearLayoutManager(this);
        mSubscribeRecyclerView.setLayoutManager(mSubscribeLayoutManager);
        mSubscribeListAdapter = new SubscribeListAdapter(this, mSubscribeItem);
        mSubscribeRecyclerView.setAdapter(mSubscribeListAdapter);

        mSubscribeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Subscribetate) {
                super.onScrollStateChanged(recyclerView, Subscribetate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mSubscribeLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mSubscribeListAdapter.getItemCount()) {
                    if (mSubscribeTotal > mSubscribeItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mSubscribeHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getSubscribeListData();
                                    Log.i(TAG, "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mSubscribeListAdapter.setOnItemClickListener(new SubscribeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent it = new Intent(SubscribeListActivity.this, SubscribeInfoActivity.class);
                it.putExtra(SubscribeInfoActivity.CLINIC_ID, mSubscribeItem.get(position).clinicid);
                startActivity(it);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getSubscribeListData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/outPatient/expert/myList");
        Log.i(TAG, "getSubscribeListData url==" + BaseConst.DEAULT_URL + "/mobile/outPatient/expert/myList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i(TAG, mSubscribeStart + "");
        params.addQueryStringParameter("begin", mSubscribeStart + "");
        params.addQueryStringParameter("limit", "5");
        params.addQueryStringParameter("scheduleId", mSchedulingId);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getSubscribeListData:" + result);
                parseTabJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.i(TAG, "getSubscribeListData onError:" + ex.getMessage());
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
        // TODO: 2018-12-27 暂时先手动解析，应改为自动转为实体类 Alt+Insert GsonFormat
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
                            mSubscribeTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray SubscribeArray = new JSONArray(array);
                            for (int i = 0; i < SubscribeArray.length(); i++) {
                                JSONObject l = SubscribeArray.getJSONObject(i);
                                SubscribeInfo item = new SubscribeInfo();
                                item.total = total;
                                if (l.has("clinicid")) {
                                    item.clinicid = l.getString("clinicid");
                                }
                                if (l.has("sndsitename")) {
                                    item.sndsitename = l.getString("sndsitename");
                                }
                                if (l.has("doctorid")) {
                                    item.doctorid = l.getString("doctorid");
                                }
                                if (l.has("doctorname")) {
                                    item.doctorname = l.getString("doctorname");
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
                                if (l.has("statusname")) {
                                    item.statusname = l.getString("statusname");
                                }
                                item.schedulingtime = mSchedulingTime;
                                mSubscribeItem.add(item);
                            }
                            mSubscribeStart = mSubscribeStart + SubscribeArray.length();
                            mSubscribeListAdapter.notifyDataSetChanged();
                            mSubscribeSwipeToLoadLayout.setRefreshing(false);
                            mSubscribeListAdapter.notifyItemRemoved(mSubscribeListAdapter.getItemCount());
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
