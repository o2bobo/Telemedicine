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
import com.chinabsc.telemedicine.expert.entity.UnionInfo;
import com.chinabsc.telemedicine.expert.myAdapter.UnionListAdapter;
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

@ContentView(R.layout.activity_union)
public class UnionActivity extends BaseActivity {
    private String TAG = "UnionActivity";
    @ViewInject(R.id.UnionSwipeToLoadLayout)
    private SwipeToLoadLayout mUnionSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mUnionRecyclerView;
    private LinearLayoutManager mUnionLayoutManager;
    private boolean isLoading;
    private int mUnionTotal = 0;
    private int mUnionStart = 0;
    private UnionListAdapter mUnionListAdapter;
    private Handler mUnionHandler = new Handler();

    public ArrayList<UnionInfo> mUnionItem = new ArrayList<UnionInfo>();

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

        String userId = SPUtils.get(UnionActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            getUnionListData();
        }
    }

    private void init() {
        mUnionLayoutManager = new LinearLayoutManager(this);
        mUnionRecyclerView.setLayoutManager(mUnionLayoutManager);
        mUnionListAdapter = new UnionListAdapter(this, mUnionItem);
        mUnionRecyclerView.setAdapter(mUnionListAdapter);

        mUnionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Uniontate) {
                super.onScrollStateChanged(recyclerView, Uniontate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mUnionLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mUnionListAdapter.getItemCount()) {
                    if (mUnionTotal > mUnionItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mUnionHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getUnionListData();
                                    Log.i(TAG, "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mUnionListAdapter.setOnItemClickListener(new UnionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent it = new Intent(UnionActivity.this, UnionInfoActivity.class);
                it.putExtra(UnionInfoActivity.Union_ID, mUnionItem.get(position).id);
                it.putExtra(UnionInfoActivity.Union_NAME, mUnionItem.get(position).name);
                startActivity(it);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getUnionListData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/departUnion/getDepartUnionList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Log.i(TAG, mUnionStart + "");
        params.addQueryStringParameter("begin", mUnionStart + "");
        params.addQueryStringParameter("limit", "5");
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getUnionListData==" + result);
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
                            mUnionTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray UnionArray = new JSONArray(array);
                            for (int i = 0; i < UnionArray.length(); i++) {
                                JSONObject l = UnionArray.getJSONObject(i);
                                UnionInfo item = new UnionInfo();
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
                                mUnionItem.add(item);
                            }
                            mUnionStart = mUnionStart + UnionArray.length();
                            mUnionListAdapter.notifyDataSetChanged();
                            mUnionSwipeToLoadLayout.setRefreshing(false);
                            mUnionListAdapter.notifyItemRemoved(mUnionListAdapter.getItemCount());
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
