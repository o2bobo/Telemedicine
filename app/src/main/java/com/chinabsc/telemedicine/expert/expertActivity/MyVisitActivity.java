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
import com.chinabsc.telemedicine.expert.entity.SchedulingInfo;
import com.chinabsc.telemedicine.expert.myAdapter.SchedulingListAdapter;
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

@ContentView(R.layout.activity_my_visit)
public class MyVisitActivity extends BaseActivity {
    private String TAG = "MyVisitActivity";
    @ViewInject(R.id.VisitSwipeToLoadLayout)
    private SwipeToLoadLayout mVisitSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mVisitRecyclerView;
    private LinearLayoutManager mVisitLayoutManager;
    private String mUserId = "";
    private boolean isLoading;
    private int mVisitTotal = 0;
    private int mVisitStart = 0;
    private SchedulingListAdapter mVisitListAdapter;
    private Handler mVisitHandler = new Handler();

    public ArrayList<SchedulingInfo> mVisitItem = new ArrayList<SchedulingInfo>();

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

        String userId = SPUtils.get(MyVisitActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        mUserId = userId;
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            getVisitListData();
        }
    }

    private void init() {
        mVisitLayoutManager = new LinearLayoutManager(this);
        mVisitRecyclerView.setLayoutManager(mVisitLayoutManager);
        mVisitListAdapter = new SchedulingListAdapter(this, mVisitItem);
        mVisitRecyclerView.setAdapter(mVisitListAdapter);

        mVisitRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Visittate) {
                super.onScrollStateChanged(recyclerView, Visittate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mVisitLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mVisitListAdapter.getItemCount()) {
                    if (mVisitTotal > mVisitItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mVisitHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getVisitListData();
                                    Log.i(TAG, "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mVisitListAdapter.setOnItemClickListener(new SchedulingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent it = new Intent(MyVisitActivity.this, SubscribeListActivity.class);
                it.putExtra(SubscribeListActivity.SCHEDULE_ID, mVisitItem.get(position).scheduleid);
                it.putExtra(SubscribeListActivity.SCHEDULE_TIME, mVisitItem.get(position).formattime + " " + mVisitItem.get(position).se);
                startActivity(it);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getVisitListData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/outPatient/expert/scheduleList");
        Log.i(TAG, "getVisitListData url==" + BaseConst.DEAULT_URL + "/mobile/outPatient/expert/scheduleList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i(TAG, mVisitStart + "");
        params.addQueryStringParameter("begin", mVisitStart + "");
        params.addQueryStringParameter("limit", "5");
        params.addQueryStringParameter("userId", mUserId);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getVisitListData:" + result);
                parseTabJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.i(TAG, "getVisitListData onError:" + ex.getMessage());
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
                            mVisitTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray VisitArray = new JSONArray(array);
                            for (int i = 0; i < VisitArray.length(); i++) {
                                JSONObject l = VisitArray.getJSONObject(i);
                                SchedulingInfo item = new SchedulingInfo();
                                item.total = total;
                                if (l.has("scheduleid")) {
                                    item.scheduleid = l.getString("scheduleid");
                                }
                                if (l.has("rcvsitename")) {
                                    item.rcvsitename = l.getString("rcvsitename");
                                }
                                if (l.has("department")) {
                                    item.department = l.getString("department");
                                }
                                if (l.has("expertid")) {
                                    item.expertid = l.getString("expertid");
                                }
                                if (l.has("expertname")) {
                                    item.expertname = l.getString("expertname");
                                }
                                if (l.has("starttime")) {
                                    item.starttime = l.getLong("starttime");
                                }
                                if (l.has("endtime")) {
                                    item.endtime = l.getLong("endtime");
                                }
                                if (l.has("regusable")) {
                                    item.regusable = l.getLong("regusable");
                                }
                                if (l.has("formattime")) {
                                    item.formattime = l.getString("formattime");
                                }
                                if (l.has("se")) {
                                    item.se = l.getString("se");
                                }
                                mVisitItem.add(item);
                            }
                            mVisitStart = mVisitStart + VisitArray.length();
                            mVisitListAdapter.notifyDataSetChanged();
                            mVisitSwipeToLoadLayout.setRefreshing(false);
                            mVisitListAdapter.notifyItemRemoved(mVisitListAdapter.getItemCount());
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

