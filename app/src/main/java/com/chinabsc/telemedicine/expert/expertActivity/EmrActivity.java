package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.EmrItem;
import com.chinabsc.telemedicine.expert.myAdapter.EmrAdapter;
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
import java.util.List;

@ContentView(R.layout.activity_emr)
public class EmrActivity extends BaseActivity {
    private String TAG="EmrActivity";
    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";
    public String mType = "";

    @ViewInject(R.id.EmrSpinner)
    private Spinner mEmrSpinner;
    @ViewInject(R.id.LoadingLayout)
    private RelativeLayout mLoadingLayout;
    @ViewInject(R.id.EmrSwipeToLoadLayout)
    private SwipeToLoadLayout mEmrSwipeToLoadLayout;
    @ViewInject(R.id.swipe_target)
    private RecyclerView mEmrRecyclerView;
    private LinearLayoutManager mEmrLayoutManager;
    private boolean isLoading;
    private int mEmrTotal = 0;
    private int mEmrStart = 0;
    private EmrAdapter mEmrAdapter;
    private Handler mEmrHandler = new Handler();

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

    public ArrayList<EmrItem> mEmrItem = new ArrayList<EmrItem>();
    public ArrayList<String> mSnList = new ArrayList<String>();

    List<String> mTypeNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            Log.i("Emr Bundle", "Telemedicine" + mTelemedicineInfoId);
        } else {
            Log.i("Emr Bundle", "bundle == null");
            finish();
        }

        init();

        getEmrType(mTelemedicineInfoId);
    }

    private void init() {
        ArrayAdapter<String> ethnicAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, mTypeNameList);
        ethnicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEmrSpinner.setAdapter(ethnicAdapter);
        mEmrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mEmrTotal = 0;
                mEmrStart = 0;
                mEmrItem.clear();
                mType = mTypeNameList.get(pos);
                getEmr(mTelemedicineInfoId, mTypeNameList.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        mEmrLayoutManager = new LinearLayoutManager(this);
        mEmrRecyclerView.setLayoutManager(mEmrLayoutManager);
        mEmrAdapter = new EmrAdapter(this, mEmrItem);
        mEmrRecyclerView.setAdapter(mEmrAdapter);

        mEmrRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int Emrtate) {
                super.onScrollStateChanged(recyclerView, Emrtate);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = mEmrLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mEmrAdapter.getItemCount()) {
                    if (mEmrTotal > mEmrItem.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mEmrHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getEmr(mTelemedicineInfoId, mType);
                                    Log.d("test", "load more completed");
                                    isLoading = false;
                                }
                            }, 300);
                        }
                    }
                }
            }
        });

        mEmrAdapter.setOnItemClickListener(new EmrAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mSnList.clear();
                for (int i = 0; i < mEmrItem.size(); i++) {
                    mSnList.add(mEmrItem.get(i).sn);
                }
                Intent intent = new Intent(EmrActivity.this, EmrWebActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(EmrWebActivity.EO_EMR_SN_LIST, mSnList);
                bundle.putInt(EmrWebActivity.EO_EMR_ID, position);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void getEmrType(String tId) {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getEmrTypes?clinicId=" + tId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                mTypeNameList = parseTypeJson(result);
                ((ArrayAdapter) mEmrSpinner.getAdapter()).notifyDataSetChanged();
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
                mLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private List<String> parseTypeJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray array = new JSONArray(data);
                        mTypeNameList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject k = array.getJSONObject(i);
                            if (k.has("recordType")) {
                                String type = k.getString("recordType");
                                mTypeNameList.add(type);
                            }
                        }
                        return mTypeNameList;
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
        return null;
    }

    private void getEmr(String tId, String type) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/getEmrListByType");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addQueryStringParameter("clinicId", tId);
        params.addQueryStringParameter("recordType", type);
        params.addQueryStringParameter("begin", mEmrStart + "");
        params.addQueryStringParameter("limit", "10");
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getEmr onSuccess", result);
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
        ArrayList<EmrItem> itemList = new ArrayList<EmrItem>();
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
                            mEmrTotal = total;
                        }
                        if (k.has("data")) {
                            String array = k.getString("data");
                            JSONArray newsArray = new JSONArray(array);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject l = newsArray.getJSONObject(i);
                                EmrItem item = new EmrItem();
                                item.total = total;
                                if (l.has("sn")) {
                                    item.sn = l.getString("sn");
                                }
                                if (l.has("recordType")) {
                                    item.recordType = l.getString("recordType");
                                }
                                if (l.has("recordName")) {
                                    item.recordName = l.getString("recordName");
                                }
                                if (l.has("recordTime")) {
                                    item.recordTime = l.getString("recordTime");
                                }
                                if (TextUtils.isEmpty(item.recordName)||item.recordName.equals("null")){
                                    item.recordName=item.recordType;
                                }
                                Log.i(TAG,"item===>"+item.recordName+"--"+item.recordType+"-"+item.recordTime);
                                mEmrItem.add(item);
                            }
                            mEmrStart = mEmrStart + newsArray.length();
                            mEmrAdapter.notifyDataSetChanged();
                            mEmrSwipeToLoadLayout.setRefreshing(false);
                            mEmrAdapter.notifyItemRemoved(mEmrAdapter.getItemCount());
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
