package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.zw.libslibrary.mload.MLoadingDialog;

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

@ContentView(R.layout.activity_telemedicine)
public class TelemedicineActivity extends BaseActivity {
    public String TAG = "TelemedicineActivity";
    @ViewInject(R.id.WaitingTextView)
    private TextView mWaitingTextView;
    @ViewInject(R.id.CompletedTextView)
    private TextView mCompletedTextView;
    @ViewInject(R.id.AllTextView)
    private TextView mAllTextView;

    @ViewInject(R.id.WaitingSelectedView)
    private View mWaitingSelectedView;
    @ViewInject(R.id.CompletedSelectedView)
    private View mCompletedSelectedView;
    @ViewInject(R.id.AllSelectedView)
    private View mAllSelectedView;

    @ViewInject(R.id.WaitinListView)
    private ListView mWaitingListView;
    public WaitinListAdapter mWaitingListAdapter;

    @ViewInject(R.id.CompletedListView)
    private ListView mCompletedListView;
    public CompletedListAdapter mCompletedListAdapter;

    @ViewInject(R.id.AllListView)
    public ListView mAllListView;
    public AllListAdapter mAllListAdapter;

    private Callback.Cancelable mWaitingCancelable;
    private Callback.Cancelable mCompletedCancelable;
    private Callback.Cancelable mAllCancelable;
    Dialog loadDialog;
    private boolean mWaitingTag = false;
    private boolean mCompletedTag = false;
    private boolean mAllTag = false;

    @Event(value = {
            R.id.BackImageView,
            R.id.WaitingTextView,
            R.id.CompletedTextView,
            R.id.AllTextView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.WaitingTextView:
                initializeScheduleTextView();
                mWaitingSelectedView.setVisibility(View.VISIBLE);

                initializeScheduleListView();
                mWaitingListView.setVisibility(View.VISIBLE);
                break;
            case R.id.CompletedTextView:
                initializeScheduleTextView();
                mCompletedSelectedView.setVisibility(View.VISIBLE);

                initializeScheduleListView();
                mCompletedListView.setVisibility(View.VISIBLE);
                break;
            case R.id.AllTextView:
                initializeScheduleTextView();
                mAllSelectedView.setVisibility(View.VISIBLE);

                initializeScheduleListView();
                mAllListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class DateInfo {
        public String clinicId;
        public String dateDone;
        public String sndSiteName;
        public String sndSiteId;
        public String doctorName;
        public String patientName;
        public String patientGender;
        public String age;
        public String purpose;
        public String status;
    }

    public ArrayList<DateInfo> mWattingItem = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mCompletedItem = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mAllItem = new ArrayList<DateInfo>();

    private String mSiteId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        loadDialog = MLoadingDialog.createLoadingDialog(TelemedicineActivity.this, "loading...");
        String userId = SPUtils.get(TelemedicineActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        mSiteId = SPUtils.get(TelemedicineActivity.this, PublicUrl.USER_SITE_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            loadDialog.show();
            startAllRequest(userId);
        }
    }

    private void init() {

        mWaitingListView = (ListView) findViewById(R.id.WaitinListView);
        mWaitingListAdapter = new WaitinListAdapter(this);
        mWaitingListView.setAdapter(mWaitingListAdapter);

        mCompletedListView = (ListView) findViewById(R.id.CompletedListView);
        mCompletedListAdapter = new CompletedListAdapter(this);
        mCompletedListView.setAdapter(mCompletedListAdapter);

        mAllListView = (ListView) findViewById(R.id.AllListView);
        mAllListAdapter = new AllListAdapter(this);
        mAllListView.setAdapter(mAllListAdapter);

        mWaitingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TelemedicineActivity.this, TelemedicineInfoActivity.class);
                //Intent intent = new Intent(TelemedicineActivity.this, TelemedicineInfoVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mWattingItem.get(position).clinicId);
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_TYPE, "TELEMEDICINE_INFO_TYPE_WAITING");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mCompletedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TelemedicineActivity.this, TelemedicineInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mCompletedItem.get(position).clinicId);
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_TYPE, "TELEMEDICINE_INFO_TYPE_WAITING");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mAllListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TelemedicineActivity.this, TelemedicineInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mAllItem.get(position).clinicId);
                if (mAllItem.get(position).status.equals("03") || mAllItem.get(position).status.equals("04")) {
                    bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_TYPE, "TELEMEDICINE_INFO_TYPE_WAITING");
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void startAllRequest(String userId) {
        getWaitingSchedule(userId);
        getCompletedSchedule(userId);
        getAllSchedule(userId);
    }

    private void getWaitingSchedule(String userId) {
        Log.i(TAG, "getWaitingSchedule= " + BaseConst.DEAULT_URL);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("status", "03");
        params.addBodyParameter("expertUserId", userId);
        mWaitingCancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                mWattingItem = parseJson(result);
                mWaitingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i(TAG, "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                mWaitingTag = true;
                if (mWaitingTag && mCompletedTag && mAllTag) {
                    loadDialog.cancel();
                }
                //mLoginLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getCompletedSchedule(String userId) {
        Log.i(TAG, "getWaitingSchedule= " + BaseConst.DEAULT_URL);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("status", "04");
        params.addBodyParameter("expertUserId", userId);
        mCompletedCancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                mCompletedItem = parseJson(result);
                mCompletedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i(TAG, "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                mCompletedTag = true;
                if (mWaitingTag && mCompletedTag && mAllTag) {
                    loadDialog.cancel();
                }
                //mLoginLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getAllSchedule(String userId) {
        Log.i(TAG, "getWaitingSchedule= " + BaseConst.DEAULT_URL + "token " + getTokenFromLocal() + " expertUserId " + userId);
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("expertUserId", userId);
        mAllCancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                mAllItem = parseJson(result);
                mAllListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                Log.i(TAG, "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                mAllTag = true;
                if (mWaitingTag && mCompletedTag && mAllTag) {
                    loadDialog.cancel();
                }
                //mLoginLoadingLayout.setVisibility(View.GONE);

            }
        });
    }

    private ArrayList<DateInfo> parseJson(String result) {
        ArrayList<DateInfo> itemList = new ArrayList<DateInfo>();
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray array = new JSONArray(data);
                        itemList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject k = array.getJSONObject(i);
                            DateInfo item = new DateInfo();
                            if (k.has("clinicId")) {
                                item.clinicId = k.getString("clinicId");
                            }
                            if (k.has("dateDone")) {
                                item.dateDone = k.getString("dateDone");
                            }
                            if (k.has("sndSiteName")) {
                                item.sndSiteName = k.getString("sndSiteName");
                            }
                            if (k.has("sndSiteId")) {
                                item.sndSiteId = k.getString("sndSiteId");
                            }
                            if (k.has("doctorName")) {
                                item.doctorName = k.getString("doctorName");
                            }
                            if (k.has("patientName")) {
                                item.patientName = k.getString("patientName");
                            }
                            if (k.has("patientGender")) {
                                item.patientGender = k.getString("patientGender");
                            }
                            if (k.has("age")) {
                                item.age = k.getString("age");
                            }
                            if (k.has("purpose")) {
                                item.purpose = k.getString("purpose");
                            }
                            if (k.has("status")) {
                                item.status = k.getString("status");
                            }
                            itemList.add(item);
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
        return itemList;
    }

    private void initializeScheduleTextView() {
        mWaitingSelectedView.setVisibility(View.INVISIBLE);
        mCompletedSelectedView.setVisibility(View.INVISIBLE);
        mAllSelectedView.setVisibility(View.INVISIBLE);
    }

    private void initializeScheduleListView() {
        mWaitingListView.setVisibility(View.GONE);
        mCompletedListView.setVisibility(View.GONE);
        mAllListView.setVisibility(View.GONE);
    }

    public class WaitinListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public WaitinListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mWattingItem.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.telemedicine_schedule_item, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.DateTextView);
                viewHolder.StatusTextView = (TextView) convertView.findViewById(R.id.StatusTextView);
                viewHolder.sndSiteNameView = (TextView) convertView.findViewById(R.id.SendSiteNameView);
//                viewHolder.doctorNameView = (TextView) convertView.findViewById(R.id.DoctorNameView);
                viewHolder.patientNameView = (TextView) convertView.findViewById(R.id.PatientNameView);
//                viewHolder.patientGenderView = (TextView) convertView.findViewById(R.id.PatientGenderView);
//                viewHolder.ageView = (TextView) convertView.findViewById(R.id.AgeView);
                viewHolder.purposeView = (TextView) convertView.findViewById(R.id.PurposeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(mWattingItem.get(position).dateDone);
            if (mSiteId.equals(mWattingItem.get(position).sndSiteId)) {
                viewHolder.StatusTextView.setText("院内会诊");
            } else {
                viewHolder.StatusTextView.setText("");
            }
            viewHolder.sndSiteNameView.setText(mWattingItem.get(position).sndSiteName + "    " + mWattingItem.get(position).doctorName);
//            viewHolder.doctorNameView.setText(mAllItem.get(position).doctorName);

            String patientGender = mWattingItem.get(position).patientGender;
            String sex = "";
            if (patientGender.equals("0") || patientGender.equals("男")) {
                sex = "男";
            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                sex = "女";
            } else {
                sex = "未知";
            }
//            viewHolder.ageView.setText(mAllItem.get(position).age);
            viewHolder.patientNameView.setText(mWattingItem.get(position).patientName + "    " + sex + "    " + mWattingItem.get(position).age + "岁");
            viewHolder.purposeView.setText(mWattingItem.get(position).purpose);
            return convertView;
        }
    }

    public class CompletedListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public CompletedListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mCompletedItem.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.telemedicine_schedule_item, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.DateTextView);
                viewHolder.StatusTextView = (TextView) convertView.findViewById(R.id.StatusTextView);
                viewHolder.sndSiteNameView = (TextView) convertView.findViewById(R.id.SendSiteNameView);
//                viewHolder.doctorNameView = (TextView) convertView.findViewById(R.id.DoctorNameView);
                viewHolder.patientNameView = (TextView) convertView.findViewById(R.id.PatientNameView);
//                viewHolder.patientGenderView = (TextView) convertView.findViewById(R.id.PatientGenderView);
//                viewHolder.ageView = (TextView) convertView.findViewById(R.id.AgeView);
                viewHolder.purposeView = (TextView) convertView.findViewById(R.id.PurposeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(mCompletedItem.get(position).dateDone);
            if (mSiteId.equals(mCompletedItem.get(position).sndSiteId)) {
                viewHolder.StatusTextView.setText("院内会诊");
            } else {
                viewHolder.StatusTextView.setText("");
            }
            viewHolder.sndSiteNameView.setText(mCompletedItem.get(position).sndSiteName + "    " + mCompletedItem.get(position).doctorName);
//            viewHolder.doctorNameView.setText(mAllItem.get(position).doctorName);

            String patientGender = mCompletedItem.get(position).patientGender;
            String sex = "";
            if (patientGender.equals("0") || patientGender.equals("男")) {
                sex = "男";
            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                sex = "女";
            } else {
                sex = "未知";
            }
//            viewHolder.ageView.setText(mAllItem.get(position).age);
            viewHolder.patientNameView.setText(mCompletedItem.get(position).patientName + "    " + sex + "    " + mCompletedItem.get(position).age + "岁");
            viewHolder.purposeView.setText(mCompletedItem.get(position).purpose);
            return convertView;
        }
    }

    public class AllListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public AllListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //return mArticleListItem.size();
            return mAllItem.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.telemedicine_schedule_item, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.DateTextView);
                viewHolder.StatusTextView = (TextView) convertView.findViewById(R.id.StatusTextView);
                viewHolder.sndSiteNameView = (TextView) convertView.findViewById(R.id.SendSiteNameView);
//                viewHolder.doctorNameView = (TextView) convertView.findViewById(R.id.DoctorNameView);
                viewHolder.patientNameView = (TextView) convertView.findViewById(R.id.PatientNameView);
//                viewHolder.patientGenderView = (TextView) convertView.findViewById(R.id.PatientGenderView);
//                viewHolder.ageView = (TextView) convertView.findViewById(R.id.AgeView);
                viewHolder.purposeView = (TextView) convertView.findViewById(R.id.PurposeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(mAllItem.get(position).dateDone);
            if (mSiteId.equals(mAllItem.get(position).sndSiteId)) {
                viewHolder.StatusTextView.setText("院内会诊");
            } else {
                viewHolder.StatusTextView.setText("");
            }
            viewHolder.sndSiteNameView.setText(mAllItem.get(position).sndSiteName + "    " + mAllItem.get(position).doctorName);
//            viewHolder.doctorNameView.setText(mAllItem.get(position).doctorName);

            String patientGender = mAllItem.get(position).patientGender;
            String sex = "";
            if (patientGender.equals("0") || patientGender.equals("男")) {
                sex = "男";
            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                sex = "女";
            } else {
                sex = "未知";
            }
//            viewHolder.ageView.setText(mAllItem.get(position).age);
            viewHolder.patientNameView.setText(mAllItem.get(position).patientName + "    " + sex + "    " + mAllItem.get(position).age + "岁");
            viewHolder.purposeView.setText(mAllItem.get(position).purpose);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView date;
        TextView StatusTextView;
        TextView sndSiteNameView;
        TextView patientNameView;
        TextView purposeView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWaitingCancelable.cancel();
        mCompletedCancelable.cancel();
        mAllCancelable.cancel();
    }
}
