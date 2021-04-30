package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.DataBean;
import com.chinabsc.telemedicine.expert.entity.NewsInfo;
import com.chinabsc.telemedicine.expert.entity.NodeJsServerEntity;
import com.chinabsc.telemedicine.expert.entity.RoomEntity;
import com.chinabsc.telemedicine.expert.myAdapter.NewsListAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.source.adnroid.comm.ui.activity.UserListActivity;
import com.source.adnroid.comm.ui.chatutils.ChatSerciceUtils;
import com.source.android.chatsocket.net.HttpReuqests;
import com.source.android.chatsocket.service.MainService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ContentView(R.layout.activity_records)
public class RecordsActivity extends BaseActivity {

    @ViewInject(R.id.NotScheduledTextView)
    private TextView mNotScheduledTextView;
    @ViewInject(R.id.WaitingTextView)
    private TextView mWaitingTextView;
    @ViewInject(R.id.AllTextView)
    private TextView mAllTextView;

    @ViewInject(R.id.NotScheduledSelectedView)
    private View mNotScheduledSelectedView;
    @ViewInject(R.id.WaitingSelectedView)
    private View mWaitingSelectedView;
    @ViewInject(R.id.AllSelectedView)
    private View mAllSelectedView;

    @ViewInject(R.id.NotScheduledListView)
    private ListView mNotScheduledListView;
    public NotScheduledListAdapter mNotScheduledListAdapter;

    @ViewInject(R.id.WaitinListView)
    private ListView mWaitingListView;
    public WaitingListAdapter mWaitingListAdapter;

    @ViewInject(R.id.AllListView)
    public ListView mAllListView;
    public AllListAdapter mAllListAdapter;

    @Event(value = {
            R.id.BackImageView,
            R.id.NotScheduledTextView,
            R.id.WaitingTextView,
            R.id.AllTextView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.NotScheduledTextView:
                initializeTextView();
                mNotScheduledSelectedView.setVisibility(View.VISIBLE);

                initializeListView();
                mNotScheduledListView.setVisibility(View.VISIBLE);
                break;
            case R.id.WaitingTextView:
                initializeTextView();
                mWaitingSelectedView.setVisibility(View.VISIBLE);

                initializeListView();
                mWaitingListView.setVisibility(View.VISIBLE);
                break;
            case R.id.AllTextView:
                initializeTextView();
                mAllSelectedView.setVisibility(View.VISIBLE);

                initializeListView();
                mAllListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class DateInfo {
        public String clinicId;
        public String datePosted;
        public String status;
        public String sndSiteName;
        public String doctorName;
        public String patientName;
        public String patientGender;
        public String age;
        public String purpose;
    }

    public ArrayList<DateInfo> mNotScheduledItem = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mWaitingItem = new ArrayList<DateInfo>();
    public ArrayList<DateInfo> mAllItem = new ArrayList<DateInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        String userId = SPUtils.get(RecordsActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
            init();
            startAllRequest(userId);
        }
    }

    private void init() {
        mNotScheduledListView = (ListView) findViewById(R.id.NotScheduledListView);
        mNotScheduledListAdapter = new NotScheduledListAdapter(this);
        mNotScheduledListView.setAdapter(mNotScheduledListAdapter);

        mWaitingListView = (ListView) findViewById(R.id.WaitinListView);
        mWaitingListAdapter = new WaitingListAdapter(this);
        mWaitingListView.setAdapter(mWaitingListAdapter);

        mAllListView = (ListView) findViewById(R.id.AllListView);
        mAllListAdapter = new AllListAdapter(this);
        mAllListView.setAdapter(mAllListAdapter);

        mNotScheduledListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecordsActivity.this, TelemedicineInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mNotScheduledItem.get(position).clinicId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mWaitingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecordsActivity.this, TelemedicineInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mWaitingItem.get(position).clinicId);
                bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_TYPE, "TELEMEDICINE_INFO_TYPE_WAITING");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mAllListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecordsActivity.this, TelemedicineInfoActivity.class);
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
        getNotScheduled(userId);
        getWaitingSchedule(userId);
        getAllSchedule(userId);
    }

    private void getNotScheduled(final String userId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/findListByDoctor");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("status", "01");
        params.addBodyParameter("doctorUserId", userId);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getNot onSuccess", result);
                mNotScheduledItem = parseJson(result);
                mNotScheduledListAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
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

    private void getWaitingSchedule(final String userId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/findListByDoctor");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("status", "03");
        params.addBodyParameter("doctorUserId", userId);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getWaiting onSuccess", result);
                mWaitingItem = parseJson(result);
                mWaitingListAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
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

    private void getAllSchedule(String userId) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/clinic/findListByDoctor");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("doctorUserId", userId);
        Callback.Cancelable cancelable;
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getAll onSuccess", result);
                mAllItem = parseJson(result);
                mAllListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
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
                            if (k.has("datePosted")) {
                                item.datePosted = k.getString("datePosted");
                            }
                            if (k.has("status")) {
                                item.status = k.getString("status");
                            }
                            /*if (k.has("sndSiteName")) {
                                item.sndSiteName = k.getString("sndSiteName");
                            }
                            if (k.has("doctorName")) {
                                item.doctorName = k.getString("doctorName");
                            }*/
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
                            if (k.has("expertList")) {
                                String expertList = k.getString("expertList");
                                JSONArray expertArray = new JSONArray(expertList);
                                if (expertArray.length() > 0) {
                                    JSONObject m = expertArray.getJSONObject(0);
                                    item.doctorName = m.getString("expertname");
                                    item.sndSiteName = m.getString("siteName");
                                }
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

    private void initializeTextView() {
        mNotScheduledSelectedView.setVisibility(View.INVISIBLE);
        mWaitingSelectedView.setVisibility(View.INVISIBLE);
        mAllSelectedView.setVisibility(View.INVISIBLE);
    }

    private void initializeListView() {
        mNotScheduledListView.setVisibility(View.GONE);
        mWaitingListView.setVisibility(View.GONE);
        mAllListView.setVisibility(View.GONE);
    }

    public class NotScheduledListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public NotScheduledListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mNotScheduledItem.size();
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
                convertView = mInflater.inflate(R.layout.records_schedule_item, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.DateTextView);
                viewHolder.sendSiteNameTitleView = (TextView) convertView.findViewById(R.id.SendSiteNameTitleView);
                viewHolder.sndSiteNameView = (TextView) convertView.findViewById(R.id.SendSiteNameView);
                viewHolder.statusView = (TextView) convertView.findViewById(R.id.StatusTextView);
                viewHolder.patientNameView = (TextView) convertView.findViewById(R.id.PatientNameView);
                viewHolder.purposeView = (TextView) convertView.findViewById(R.id.PurposeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(mNotScheduledItem.get(position).datePosted);

            String status = mNotScheduledItem.get(position).status;
            if (status.equals("01")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#00b3fe"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("未安排");
            } else if (status.equals("02")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#ff2600"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("未通过");
            } else if (status.equals("03")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#49d288"));
                viewHolder.sendSiteNameTitleView.setText("会诊专家");
                viewHolder.statusView.setText("待会诊");
            } else if (status.equals("04")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#000000"));
                viewHolder.sendSiteNameTitleView.setText("会诊专家");
                viewHolder.statusView.setText("已完成");
            } else if (status.equals("05")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#af7b78"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("已取消");
            } else {
                viewHolder.statusView.setText("");
            }
            viewHolder.sndSiteNameView.setText(mNotScheduledItem.get(position).sndSiteName + "    " + mNotScheduledItem.get(position).doctorName);

            String patientGender = mNotScheduledItem.get(position).patientGender;
            String sex = "";
            if (patientGender.equals("0") || patientGender.equals("男")) {
                sex = "男";
            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                sex = "女";
            } else {
                sex = "未知";
            }
            viewHolder.patientNameView.setText(mNotScheduledItem.get(position).patientName + "    " + sex + "    " + mNotScheduledItem.get(position).age + "岁");
            viewHolder.purposeView.setText(mNotScheduledItem.get(position).purpose);
            return convertView;
        }
    }

    public class WaitingListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public WaitingListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mWaitingItem.size();
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
                convertView = mInflater.inflate(R.layout.records_schedule_item, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.DateTextView);
                viewHolder.sendSiteNameTitleView = (TextView) convertView.findViewById(R.id.SendSiteNameTitleView);
                viewHolder.sndSiteNameView = (TextView) convertView.findViewById(R.id.SendSiteNameView);
                viewHolder.statusView = (TextView) convertView.findViewById(R.id.StatusTextView);
                viewHolder.patientNameView = (TextView) convertView.findViewById(R.id.PatientNameView);
                viewHolder.purposeView = (TextView) convertView.findViewById(R.id.PurposeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(mWaitingItem.get(position).datePosted);

            String status = mWaitingItem.get(position).status;
            if (status.equals("01")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#00b3fe"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("未安排");
            } else if (status.equals("02")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#ff2600"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("未通过");
            } else if (status.equals("03")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#49d288"));
                viewHolder.sendSiteNameTitleView.setText("会诊专家");
                viewHolder.statusView.setText("待会诊");
            } else if (status.equals("04")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#000000"));
                viewHolder.sendSiteNameTitleView.setText("会诊专家");
                viewHolder.statusView.setText("已完成");
            } else if (status.equals("05")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#af7b78"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("已取消");
            } else {
                viewHolder.statusView.setText("");
            }
            viewHolder.sndSiteNameView.setText(mWaitingItem.get(position).sndSiteName + "    " + mWaitingItem.get(position).doctorName);

            String patientGender = mWaitingItem.get(position).patientGender;
            String sex = "";
            if (patientGender.equals("0") || patientGender.equals("男")) {
                sex = "男";
            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                sex = "女";
            } else {
                sex = "未知";
            }
            viewHolder.patientNameView.setText(mWaitingItem.get(position).patientName + "    " + sex + "    " + mWaitingItem.get(position).age + "岁");
            viewHolder.purposeView.setText(mWaitingItem.get(position).purpose);
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
                convertView = mInflater.inflate(R.layout.records_schedule_item, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.DateTextView);
                viewHolder.sendSiteNameTitleView = (TextView) convertView.findViewById(R.id.SendSiteNameTitleView);
                viewHolder.sndSiteNameView = (TextView) convertView.findViewById(R.id.SendSiteNameView);
                viewHolder.statusView = (TextView) convertView.findViewById(R.id.StatusTextView);
                viewHolder.patientNameView = (TextView) convertView.findViewById(R.id.PatientNameView);
                viewHolder.purposeView = (TextView) convertView.findViewById(R.id.PurposeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(mAllItem.get(position).datePosted);

            String status = mAllItem.get(position).status;
            if (status.equals("01")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#00b3fe"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("未安排");
            } else if (status.equals("02")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#ff2600"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("未通过");
            } else if (status.equals("03")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#49d288"));
                viewHolder.sendSiteNameTitleView.setText("会诊专家");
                viewHolder.statusView.setText("待会诊");
            } else if (status.equals("04")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#000000"));
                viewHolder.sendSiteNameTitleView.setText("会诊专家");
                viewHolder.statusView.setText("已完成");
            } else if (status.equals("05")) {
                viewHolder.statusView.setTextColor(Color.parseColor("#af7b78"));
                viewHolder.sendSiteNameTitleView.setText("要求专家");
                viewHolder.statusView.setText("已取消");
            } else {
                viewHolder.statusView.setText("");
            }
            viewHolder.sndSiteNameView.setText(mAllItem.get(position).sndSiteName + "    " + mAllItem.get(position).doctorName);

            String patientGender = mAllItem.get(position).patientGender;
            String sex = "";
            if (patientGender.equals("0") || patientGender.equals("男")) {
                sex = "男";
            } else if (patientGender.equals("1") || patientGender.equals("女")) {
                sex = "女";
            } else {
                sex = "未知";
            }
            viewHolder.patientNameView.setText(mAllItem.get(position).patientName + "    " + sex + "    " + mAllItem.get(position).age + "岁");
            viewHolder.purposeView.setText(mAllItem.get(position).purpose);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView date;
        TextView sndSiteNameView;
        TextView sendSiteNameTitleView;
        TextView patientNameView;
        TextView purposeView;
        TextView statusView;
    }
}
