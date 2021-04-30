package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.AllianceInfo;
import com.chinabsc.telemedicine.expert.myAdapter.AllianceListAdapter;
import com.chinabsc.telemedicine.expert.myView.StretchyTextView;
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

@ContentView(R.layout.activity_alliance_info)
public class AllianceInfoActivity extends BaseActivity {

    public static String ALLIANCE_ID = "ALLIANCE_ID";
    public static String ALLIANCE_NAME = "ALLIANCE_NAME";

    @ViewInject(R.id.TitleTextView)
    public TextView mTitleTextView;

    @ViewInject(R.id.DescriptionTextView)
    public StretchyTextView mDescriptionTextView;

    @ViewInject(R.id.LeaderLayout)
    public LinearLayout mLeaderLayout;

    @ViewInject(R.id.LeaderNo)
    public TextView mLeaderNo;

    @ViewInject(R.id.CooperationLayout)
    public LinearLayout mCooperationLayout;

    @ViewInject(R.id.CooperationNo)
    public TextView mCooperationNo;


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

    public class HospitalInfo {
        public String siteid;
        public String sitename;
    }

    public ArrayList<HospitalInfo> mLeaderInfoList = new ArrayList<HospitalInfo>();
    public ArrayList<HospitalInfo> mCooperationInfoList = new ArrayList<HospitalInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        Intent i = getIntent();
        String allianceId = i.getStringExtra(ALLIANCE_ID);
        String title = i.getStringExtra(ALLIANCE_NAME);

        mTitleTextView.setText(title);
        mDescriptionTextView.setMaxLineCount(3);

        String userId = SPUtils.get(AllianceInfoActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        Log.i("test", "userId:" + userId);
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            getData(allianceId);
        }
    }

    private void getData(String allianceId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/union/getSiteMemberList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("unionId", allianceId);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getData:" + result);
                parseTabJson(result);
                if (mLeaderInfoList.size() == 0) {
                    mLeaderNo.setVisibility(View.VISIBLE);
                }
                if (mCooperationInfoList.size() == 0) {
                    mCooperationNo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("onError", "onError:" + ex.getMessage());
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
                        if (k.has("description")) {
                            String description = k.getString("description");
                            if(!TextUtils.isEmpty(description)) {
                                mDescriptionTextView.setContent(description);
                            } else {
                                mDescriptionTextView.setContent("无");
                            }
                        }
                        if (k.has("cooperation")) {
                            String array = k.getString("cooperation");
                            JSONArray AllianceArray = new JSONArray(array);
                            for (int i = 0; i < AllianceArray.length(); i++) {
                                JSONObject l = AllianceArray.getJSONObject(i);
                                HospitalInfo item = new HospitalInfo();
                                if (l.has("siteid")) {
                                    item.siteid = l.getString("siteid");
                                }
                                if (l.has("sitename")) {
                                    item.sitename = l.getString("sitename");
                                }
                                mCooperationInfoList.add(item);
                            }
                            setCooperationInfoData();
                        }
                        if (k.has("leader")) {
                            String array = k.getString("leader");
                            JSONArray AllianceArray = new JSONArray(array);
                            for (int i = 0; i < AllianceArray.length(); i++) {
                                JSONObject l = AllianceArray.getJSONObject(i);
                                HospitalInfo item = new HospitalInfo();
                                if (l.has("siteid")) {
                                    item.siteid = l.getString("siteid");
                                }
                                if (l.has("sitename")) {
                                    item.sitename = l.getString("sitename");
                                }
                                mLeaderInfoList.add(item);
                            }
                        }
                        setLeaderInfoData();
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

    private void setLeaderInfoData() {
        View ViewArray[] = new View[mLeaderInfoList.size()];
        for (int i = 0; i < mLeaderInfoList.size(); i++) {
            HospitalItemView item = new HospitalItemView();
            ViewArray[i] = item.mHospitalItem;
            item.mTitle.setText(mLeaderInfoList.get(i).sitename);
            mLeaderLayout.addView(ViewArray[i]);
        }

        int mLeaderItemNum = 0;
        for (int i = 0; i < mLeaderInfoList.size(); i++) {
            ViewArray[i].setTag(mLeaderItemNum);
            mLeaderItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    Intent it = new Intent(AllianceInfoActivity.this, HospitalActivity.class);
                    it.putExtra(HospitalActivity.HOSPITAL_ID, mLeaderInfoList.get(n).siteid);
                    it.putExtra(HospitalActivity.HOSPITAL_NAME, mLeaderInfoList.get(n).sitename);
                    startActivity(it);
                }

            });

        }
    }

    private void setCooperationInfoData() {
        View ViewArray[] = new View[mCooperationInfoList.size()];
        for (int i = 0; i < mCooperationInfoList.size(); i++) {
            HospitalItemView item = new HospitalItemView();
            ViewArray[i] = item.mHospitalItem;
            item.mTitle.setText(mCooperationInfoList.get(i).sitename);
            mCooperationLayout.addView(ViewArray[i]);
        }

        int mCooperationItemNum = 0;
        for (int i = 0; i < mCooperationInfoList.size(); i++) {
            ViewArray[i].setTag(mCooperationItemNum);
            mCooperationItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    Intent it = new Intent(AllianceInfoActivity.this, HospitalActivity.class);
                    it.putExtra(HospitalActivity.HOSPITAL_ID, mCooperationInfoList.get(n).siteid);
                    it.putExtra(HospitalActivity.HOSPITAL_NAME, mCooperationInfoList.get(n).sitename);
                    startActivity(it);
                }

            });

        }
    }

    private class HospitalItemView {
        public View mHospitalItem;
        public TextView mTitle;

        public HospitalItemView() {
            mHospitalItem = LayoutInflater.from(AllianceInfoActivity.this).inflate(
                    R.layout.add_text_view, null);
            viewfinder();
        }

        public void viewfinder() {
            mTitle = (TextView) mHospitalItem.findViewById(R.id.MedicalImageItemTitle);
        }
    }

}
