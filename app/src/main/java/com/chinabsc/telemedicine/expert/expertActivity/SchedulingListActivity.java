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

@ContentView(R.layout.activity_scheduling_list)
public class SchedulingListActivity extends BaseActivity {
    private String TAG = "SchedulingListActivity";
    @ViewInject(R.id.SchedulingSwipeToLoadLayout)
    private SwipeToLoadLayout mSchedulingSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mSchedulingRecyclerView;
    private LinearLayoutManager mSchedulingLayoutManager;
    private boolean isLoading;
    private int mSchedulingTotal = 0;
    private int mSchedulingStart = 0;
    private SchedulingListAdapter mSchedulingListAdapter;
    private Handler mSchedulingHandler = new Handler();

    public ArrayList<SchedulingInfo> mSchedulingItem = new ArrayList<SchedulingInfo>();

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

        String userId = SPUtils.get(SchedulingListActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            getSchedulingListData();
        }
    }

    private void init() {
        mSchedulingLayoutManager = new LinearLayoutManager(this);
        mSchedulingRecyclerView.setLayoutManager(mSchedulingLayoutManager);
        mSchedulingListAdapter = new SchedulingListAdapter(this, mSchedulingItem);
        mSchedulingRecyclerView.setAdapter(mSchedulingListAdapter);

        mSchedulingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Schedulingtate) {
                super.onScrollStateChanged(recyclerView, Schedulingtate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mSchedulingLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mSchedulingListAdapter.getItemCount()) {
                    if (mSchedulingTotal > mSchedulingItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mSchedulingHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getSchedulingListData();
                                    Log.i(TAG, "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mSchedulingListAdapter.setOnItemClickListener(new SchedulingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent it = new Intent(SchedulingListActivity.this, AddSchedulingActivity.class);
                it.putExtra(AddSchedulingActivity.SCHEDULE_ID, mSchedulingItem.get(position).scheduleid);
                startActivity(it);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getSchedulingListData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/outPatient/schedule/list");
        Log.i(TAG, "getSchedulingListData url==" + BaseConst.DEAULT_URL + "/mobile/outPatient/schedule/list");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i(TAG, mSchedulingStart + "");
        params.addQueryStringParameter("begin", mSchedulingStart + "");
        params.addQueryStringParameter("limit", "5");
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getSchedulingListData:" + result);
                parseTabJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.i(TAG, "getSchedulingListData onError:" + ex.getMessage());
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
        // TODO: 2018-12-25 暂时先手动解析，应改为自动转为实体类 Alt+Insert GsonFormat
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
                            mSchedulingTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray SchedulingArray = new JSONArray(array);
                            for (int i = 0; i < SchedulingArray.length(); i++) {
                                JSONObject l = SchedulingArray.getJSONObject(i);
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
                                mSchedulingItem.add(item);
                            }
                            mSchedulingStart = mSchedulingStart + SchedulingArray.length();
                            mSchedulingListAdapter.notifyDataSetChanged();
                            mSchedulingSwipeToLoadLayout.setRefreshing(false);
                            mSchedulingListAdapter.notifyItemRemoved(mSchedulingListAdapter.getItemCount());
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
