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

@ContentView(R.layout.activity_depart)
public class DepartActivity extends BaseActivity {
    private String TAG = "DepartActivity";
    public static String Depart_ID = "Depart_ID";
    public static String Depart_NAME = "Depart_NAME";

    @ViewInject(R.id.TitleTextView)
    public TextView mTitleTextView;

    @ViewInject(R.id.DescriptionTextView)
    public StretchyTextView mDescriptionTextView;

    @ViewInject(R.id.DepartLayout)
    public LinearLayout mDepartLayout;

    @ViewInject(R.id.DepartNo)
    public TextView mDepartNo;

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

    public class UseridInfo {
        public String userid;
        public String realname;
        public String photo;
        public String jobtitle;
        public String skill;
    }

    public ArrayList<UseridInfo> mUseridInfoList = new ArrayList<UseridInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        Intent i = getIntent();
        String hid = i.getStringExtra(Depart_ID);
        String name = i.getStringExtra(Depart_NAME);
        Log.i(TAG, "hid + name==" + hid + "+" + name);
        mTitleTextView.setText(name);
        getData(hid);
    }

    private void getData(String departId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/union/getDepartById");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("id", departId);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getData==" + result);
                parseTabJson(result);
                if (mUseridInfoList.size() == 0) {
                    mDepartNo.setVisibility(View.VISIBLE);
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
                        Log.i("test", "data:" + data);
                        JSONObject k = new JSONObject(data);
                        if (k.has("depart")) {
                            String depart = k.getString("depart");
                            Log.i("test", "depart:" + depart);
                            JSONObject l = new JSONObject(depart);
                            if (l.has("description")) {
                                String description = l.getString("description");
                                if (!TextUtils.isEmpty(description)) {
                                    mDescriptionTextView.setContent(description);
                                } else {
                                    mDescriptionTextView.setContent("无");
                                }
                            }
                        }
                        if (k.has("userList")) {
                            String array = k.getString("userList");
                            JSONArray AllianceArray = new JSONArray(array);
                            for (int i = 0; i < AllianceArray.length(); i++) {
                                JSONObject l = AllianceArray.getJSONObject(i);
                                UseridInfo item = new UseridInfo();
                                if (l.has("userid")) {
                                    item.userid = l.getString("userid");
                                }
                                if (l.has("realname")) {
                                    item.realname = l.getString("realname");
                                }
                                mUseridInfoList.add(item);
                            }
                            setUserInfoData();
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

    private void setUserInfoData() {
        View ViewArray[] = new View[mUseridInfoList.size()];
        for (int i = 0; i < mUseridInfoList.size(); i++) {
            HospitalItemView item = new HospitalItemView();
            ViewArray[i] = item.mHospitalItem;
            item.mNameTextView.setText(mUseridInfoList.get(i).realname);
            item.mJobTitleTextView.setText(mUseridInfoList.get(i).jobtitle);
            item.mSkillTextView.setText(mUseridInfoList.get(i).skill);
            mDepartLayout.addView(ViewArray[i]);
        }

        int mUserItemNum = 0;
        for (int i = 0; i < mUseridInfoList.size(); i++) {
            ViewArray[i].setTag(mUserItemNum);
            mUserItemNum++;
            ViewArray[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    Intent it = new Intent(DepartActivity.this, ExpertActivity.class);
                    it.putExtra(ExpertActivity.Expert_ID, mUseridInfoList.get(n).userid);
                    it.putExtra(ExpertActivity.Expert_NAME, mUseridInfoList.get(n).realname);
                    startActivity(it);
                }

            });

        }
    }

    private class HospitalItemView {
        public View mHospitalItem;
        public TextView mNameTextView;
        public TextView mJobTitleTextView;
        public TextView mSkillTextView;

        public HospitalItemView() {
            mHospitalItem = LayoutInflater.from(DepartActivity.this).inflate(
                    R.layout.hospital_expert_item, null);
            viewfinder();
        }

        public void viewfinder() {
            mNameTextView = (TextView) mHospitalItem.findViewById(R.id.NameTextView);
            mJobTitleTextView = (TextView) mHospitalItem.findViewById(R.id.JobTitleTextView);
            mSkillTextView = (TextView) mHospitalItem.findViewById(R.id.SkillTextView);
        }
    }
}
