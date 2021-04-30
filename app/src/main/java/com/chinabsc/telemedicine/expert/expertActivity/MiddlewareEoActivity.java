
package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.myAdapter.CommenViewHolder;
import com.chinabsc.telemedicine.expert.myAdapter.MiddleCommenAdapter;
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

@ContentView(R.layout.activity_middleware_eo)
public class MiddlewareEoActivity extends BaseActivity {

    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public String mTelemedicineInfoId = "";
    private String address;
    @ViewInject(R.id.EoListView)
    private RecyclerView mEoListView;
    @ViewInject(R.id.LoadingLayout)
    private RelativeLayout mLoadingLayout;
    public MiddleCommenAdapter mEoInfoListAdapter;

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

    public class DateInfo {
        public String sn;
        public String startTime;
        public String operationName;
        public String incisionPart;
        public String incisionLevel;
    }

    public ArrayList<DateInfo> mEoItem = new ArrayList<DateInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            address = getIntent().getStringExtra("address");
        } else {
            finish();
        }
        mEoInfoListAdapter = new MiddleCommenAdapter(this, mEoItem) {
            @Override
            public void bindData(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof CommenViewHolder) {
                    String dateTime[] = mEoItem.get(position).startTime.split("\\s+");
                    if (position == 0) {
                        ((CommenViewHolder) holder).date.setText(dateTime[0]);
                    } else {
                        String tempTime[] = mEoItem.get(position - 1).startTime.split("\\s+");
                        if (tempTime[0].equals(dateTime[0])) {
                            ((CommenViewHolder) holder).date.setText(" ");
                        } else {
                            ((CommenViewHolder) holder).date.setText(dateTime[0]);
                        }
                    }
                    ((CommenViewHolder) holder).time.setText(dateTime[1]);
                    ((CommenViewHolder) holder).msg.setText(mEoItem.get(position).operationName + "\n" +
                            mEoItem.get(position).incisionPart
                    );
                }
            }
        };
        mEoListView.setLayoutManager(new LinearLayoutManager(this));
        mEoListView.setAdapter(mEoInfoListAdapter);
        getEo(mTelemedicineInfoId);
    }

    private void getEo(String tId) {
        mLoadingLayout.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams(address + "/api/operation?hisId=" + tId);
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
               parseJson(result);
                mEoInfoListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
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

    private void parseJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("code")) {
                String resultCode = j.getString("code");
                if (resultCode.equals("001")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray array = new JSONArray(data);
                        mEoItem.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject k = array.getJSONObject(i);
                            DateInfo item = new DateInfo();
                            if (k.has("sn")) {
                                item.sn = k.getString("sn");
                            }
                            if (k.has("startTime")) {
                                item.startTime = k.getString("startTime");
                            }
                            if (k.has("operationName")) {
                                item.operationName = k.getString("operationName");
                            }
                            if (k.has("incisionPart")) {
                                item.incisionPart = k.getString("incisionPart");
                            }
                            if (k.has("incisionLevel")) {
                                item.incisionLevel = k.getString("incisionLevel");
                            }
                            mEoItem.add(item);
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

