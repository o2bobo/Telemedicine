package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.AllianceInfo;
import com.chinabsc.telemedicine.expert.entity.AllianceInfo;
import com.chinabsc.telemedicine.expert.myAdapter.AllianceListAdapter;
import com.chinabsc.telemedicine.expert.myAdapter.AllianceListAdapter;
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

@ContentView(R.layout.activity_alliance)
public class AllianceActivity extends BaseActivity {
    private String TAG = "AllianceActivity";
    @ViewInject(R.id.AllianceSwipeToLoadLayout)
    private SwipeToLoadLayout mAllianceSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mAllianceRecyclerView;
    private LinearLayoutManager mAllianceLayoutManager;
    private boolean isLoading;
    private int mAllianceTotal = 0;
    private int mAllianceStart = 0;
    private AllianceListAdapter mAllianceListAdapter;
    private Handler mAllianceHandler = new Handler();

    public ArrayList<AllianceInfo> mAllianceItem = new ArrayList<AllianceInfo>();

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

        String userId = SPUtils.get(AllianceActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            getAllianceListData();
        }
    }

    private void init() {
        mAllianceLayoutManager = new LinearLayoutManager(this);
        mAllianceRecyclerView.setLayoutManager(mAllianceLayoutManager);
        mAllianceListAdapter = new AllianceListAdapter(this, mAllianceItem);
        mAllianceRecyclerView.setAdapter(mAllianceListAdapter);

        mAllianceRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Alliancetate) {
                super.onScrollStateChanged(recyclerView, Alliancetate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mAllianceLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAllianceListAdapter.getItemCount()) {
                    if (mAllianceTotal > mAllianceItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mAllianceHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getAllianceListData();
                                    Log.i (TAG, "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mAllianceListAdapter.setOnItemClickListener(new AllianceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //T.showMessage(MainActivity.this, mNewsItem.get(position).artid + "");
                Intent it = new Intent(AllianceActivity.this, AllianceInfoActivity.class);
                it.putExtra(AllianceInfoActivity.ALLIANCE_ID, mAllianceItem.get(position).id);
                it.putExtra(AllianceInfoActivity.ALLIANCE_NAME, mAllianceItem.get(position).name);
                startActivity(it);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getAllianceListData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/union/getSiteUnionList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i(TAG, mAllianceStart + "");
        params.addQueryStringParameter("begin", mAllianceStart + "");
        params.addQueryStringParameter("limit", "5");
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getAllianceListData:" + result);
                parseTabJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.e(TAG, "onError:" + ex.getMessage());
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
                            mAllianceTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray AllianceArray = new JSONArray(array);
                            for (int i = 0; i < AllianceArray.length(); i++) {
                                JSONObject l = AllianceArray.getJSONObject(i);
                                AllianceInfo item = new AllianceInfo();
                                item.total = total;
                                if (l.has("id")) {
                                    item.id = l.getString("id");
                                }
                                if (l.has("number")) {
                                    item.num = l.getString("number");
                                }
                                if (l.has("name")) {
                                    item.name = l.getString("name");
                                }
                                if (l.has("excerpt")) {
                                    item.excerpt = l.getString("excerpt");
                                }
                                mAllianceItem.add(item);
                            }
                            mAllianceStart = mAllianceStart + AllianceArray.length();
                            mAllianceListAdapter.notifyDataSetChanged();
                            mAllianceSwipeToLoadLayout.setRefreshing(false);
                            mAllianceListAdapter.notifyItemRemoved(mAllianceListAdapter.getItemCount());
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
