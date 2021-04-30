package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
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

@ContentView(R.layout.activity_union_info)
public class UnionInfoActivity extends BaseActivity {
    private String TAG = "UnionInfoActivity";
    public static String Union_ID = "Union_ID";
    public static String Union_NAME = "Union_NAME";

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
        public String sitename;
        public String departId;
        public String departname;
    }

    public ArrayList<HospitalInfo> mLeaderInfoList = new ArrayList<HospitalInfo>();
    public ArrayList<HospitalInfo> mCooperationInfoList = new ArrayList<HospitalInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        Intent i = getIntent();
        String unionId = i.getStringExtra(Union_ID);
        String title = i.getStringExtra(Union_NAME);

        mTitleTextView.setText(title);
        mDescriptionTextView.setMaxLineCount(3);

        String userId = SPUtils.get(UnionInfoActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        Log.i(TAG, "userId==" + userId);
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            getData(unionId);
        }
    }

    private void getData(String unionId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/departUnion/getDepartUnionMemberList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        Log.i(TAG, "unionId==" + unionId);
        params.addQueryStringParameter("unionId", unionId);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getData==" + result);
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
                Log.i(TAG, "onError:" + ex.getMessage());
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
                            if (!TextUtils.isEmpty(description)) {
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
                                if (l.has("sitename")) {
                                    item.sitename = l.getString("sitename");
                                }
                                if (l.has("departId")) {
                                    item.departId = l.getString("departId");
                                }
                                if (l.has("departname")) {
                                    item.departname = l.getString("departname");
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
                                if (l.has("sitename")) {
                                    item.sitename = l.getString("sitename");
                                }
                                if (l.has("departId")) {
                                    item.departId = l.getString("departId");
                                }
                                if (l.has("departname")) {
                                    item.departname = l.getString("departname");
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
            item.mTitle.setText(mLeaderInfoList.get(i).sitename + " " + mLeaderInfoList.get(i).departname);
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
                    Intent it = new Intent(UnionInfoActivity.this, DepartActivity.class);
                    it.putExtra(DepartActivity.Depart_ID, mLeaderInfoList.get(n).departId);
                    it.putExtra(DepartActivity.Depart_NAME, mLeaderInfoList.get(n).departname);
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
            item.mTitle.setText(mCooperationInfoList.get(i).sitename + " " + mCooperationInfoList.get(i).departname);
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
                    Intent it = new Intent(UnionInfoActivity.this, DepartActivity.class);
                    it.putExtra(DepartActivity.Depart_ID, mCooperationInfoList.get(n).departId);
                    it.putExtra(DepartActivity.Depart_NAME, mCooperationInfoList.get(n).departname);
                    startActivity(it);
                }

            });

        }
    }

    private class HospitalItemView {
        public View mHospitalItem;
        public TextView mTitle;

        public HospitalItemView() {
            mHospitalItem = LayoutInflater.from(UnionInfoActivity.this).inflate(
                    R.layout.add_text_view, null);
            viewfinder();
        }

        public void viewfinder() {
            mTitle = (TextView) mHospitalItem.findViewById(R.id.MedicalImageItemTitle);
        }
    }

}
