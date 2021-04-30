package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.EsoItem;
import com.chinabsc.telemedicine.expert.myAdapter.EsoAdapter;
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

@ContentView(R.layout.activity_eso)
public class EsoOrEtoActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public static String TYPE_ID = "TYPE_ID";
    public String mTelemedicineInfoId = "";
    public String mTypeId = "";

    @ViewInject(R.id.TitleTextView)
    private TextView mTitleTextView;

    @ViewInject(R.id.EsoSwipeToLoadLayout)
    private SwipeToLoadLayout mEsoSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mEsoRecyclerView;
    private LinearLayoutManager mEsoLayoutManager;
    private boolean isLoading;
    private int mEsoTotal = 0;
    private int mEsoStart = 0;
    private EsoAdapter mEsoAdapter;
    private Handler mEsoHandler = new Handler();

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

    public ArrayList<EsoItem> mEsoItem = new ArrayList<EsoItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            Log.i("EsoOrEtoActivity","mTelemedicineInfoId===>"+mTelemedicineInfoId);
            mTypeId = bundle.getString(TYPE_ID);
            if (mTypeId.equals("eso")) {
                mTitleTextView.setText("长期医嘱");
            } else if (mTypeId.equals("eto")) {
                mTitleTextView.setText("临时医嘱");
            }
            Log.i("Eso Bundle", "Telemedicine" + mTelemedicineInfoId);
        } else {
            Log.i("Eso Bundle", "bundle == null");
            finish();
        }

        init();

        mEsoTotal = 0;
        mEsoStart = 0;
        mEsoItem.clear();

        getEsoOrEto(mTelemedicineInfoId, mTypeId);
    }

    private void init() {
        mEsoLayoutManager = new LinearLayoutManager(this);
        mEsoRecyclerView.setLayoutManager(mEsoLayoutManager);
        mEsoAdapter = new EsoAdapter(this, mEsoItem);
        mEsoRecyclerView.setAdapter(mEsoAdapter);

        mEsoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Esotate) {
                super.onScrollStateChanged(recyclerView, Esotate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mEsoLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mEsoAdapter.getItemCount()) {
                    if (mEsoTotal > mEsoItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mEsoHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getEsoOrEto(mTelemedicineInfoId, mTypeId);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mEsoAdapter.setOnItemClickListener(new EsoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getEsoOrEto(String tId, String type) {
        Log.i("EsoOrEtoActivity","param=="+BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + tId + "/" + type+" token="+getTokenFromLocal());
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + tId + "/" + type);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("begin", mEsoStart+"");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getEso url", BaseConst.DEAULT_URL + "/mobile/clinic/emr/" + mTelemedicineInfoId + "/" + mTypeId);
                Log.i("getEso onSuccess", result);
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
        ArrayList<EsoItem> itemList = new ArrayList<EsoItem>();
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
                            mEsoTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                EsoItem item = new EsoItem();
                                item.total = total;
                                if (l.has("implementTime")) {
                                    item.implementTime = l.getString("implementTime");
                                }
                                if (l.has("orderContent")) {
                                    item.orderContent = l.getString("orderContent");
                                }
                                if (l.has("specification")) {
                                    item.specification = l.getString("specification");
                                }
                                if (l.has("status")) {
                                    item.status = l.getString("status");
                                }
                                mEsoItem.add(item);
                            }
                            mEsoStart = mEsoStart + newsArray.length();
                            mEsoAdapter.notifyDataSetChanged();
                            mEsoSwipeToLoadLayout.setRefreshing(false);
                            mEsoAdapter.notifyItemRemoved(mEsoAdapter.getItemCount());
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
