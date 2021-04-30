package com.chinabsc.telemedicine.expert.expertActivity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.source.android.chatsocket.entity.SocketConst;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@ContentView(R.layout.activity_statistics_info)
public class StatisticsInfoActivity extends BaseActivity {
    @ViewInject(R.id.BackImageView)
    private ImageView mBackImageView;
    @ViewInject(R.id.selected_date)
    private TextView mSelectedDate;
    @ViewInject(R.id.month_rg)
    private RadioGroup monthRg;
    @ViewInject(R.id.title)
    private TextView mTitle;
    @ViewInject(R.id.title_name_first)
    private TextView mTitleNameFirst;
    @ViewInject(R.id.title_num_first)
    private TextView mTitleNumFirst;
    @ViewInject(R.id.title_name_second)
    private TextView mTitleNameSecond;
    @ViewInject(R.id.title_num_second)
    private TextView mTitleNumSecond;
    String type = "";
    private int numFirst = 0;
    private int numSecond = 0;
    private String firstDayOfMonth;
    private String lastDayOfMonth;
    private int nowMonth;
    private static int LAST_MONTH = 1;
    private static int CURRENT_MONTH = 2;
    private static int NEXT_MONTH = 3;
    StaticsHandler staticsHandler = new StaticsHandler(this);

    private String mUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mUserId = SPUtils.get(StatisticsInfoActivity.this, PublicUrl.USER_ID_KEY, "").toString();
        getIntentMsg();
        if (type.equals("1")) {
            mTitle.setText("会诊统计");
            mTitleNameFirst.setText("申请会诊数量");
            mTitleNameSecond.setText("接收会诊数量");
        } else if (type.equals("2")) {
            mTitle.setText("门诊统计");
            mTitleNameFirst.setText("门诊预约数量");
            mTitleNameSecond.setText("门诊出诊数量");
        } else if (type.equals("3")) {
            mTitle.setText("转诊统计");
            mTitleNameFirst.setText("上转诊数量");
            mTitleNameSecond.setText("下转诊数量");
        }

        monthRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.current_month:
                        changeDate(CURRENT_MONTH);
                        if (type.equals("1")) {
                            getHuiZhenMsg();
                        } else if (type.equals("2")) {
                            getMenZhenMsg();
                        } else if (type.equals("3")) {
                            getShangZhuanZhen("0");
                            getXiaZhuanZhen("0");
                        }
                        break;

                }
            }
        });
        monthRg.check(R.id.current_month);
    }

    private void getIntentMsg() {
        type = getIntent().getStringExtra("type");
        Log.i(TAG, "type=" + type);
    }

    @Event(value = {R.id.BackImageView, R.id.current_month, R.id.last_month, R.id.next_month}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Log.i(TAG, "onClick=");
        switch (v.getId()) {

            case R.id.BackImageView:
                onBackPressed();
                break;
            case R.id.last_month:
                changeDate(LAST_MONTH);
                if (type.equals("1")) {
                    getHuiZhenMsg();
                } else if (type.equals("2")) {
                    getMenZhenMsg();
                } else if (type.equals("3")) {
                    getShangZhuanZhen("1");
                    getXiaZhuanZhen("1");
                }
                break;
            case R.id.next_month:
                changeDate(NEXT_MONTH);
                if (type.equals("1")) {
                    getHuiZhenMsg();
                } else if (type.equals("2")) {
                    getMenZhenMsg();
                } else if (type.equals("3")) {
                    getShangZhuanZhen("-1");
                    getXiaZhuanZhen("-1");
                }
                break;
            default:
                break;
        }
    }

    private void changeDate(int dateType) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        Calendar calendar = Calendar.getInstance();//获取当前日期
        Log.i(TAG, "nowMonth==" + nowMonth);
        if (dateType == CURRENT_MONTH) {
            nowMonth = Integer.parseInt(monthFormat.format(calendar.getTime())) - 1;
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            firstDayOfMonth = format.format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            lastDayOfMonth = format.format(calendar.getTime());
        }
        if (dateType == LAST_MONTH) {
            nowMonth = nowMonth - 1;
            calendar.set(Calendar.MONTH, nowMonth);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            firstDayOfMonth = format.format(calendar.getTime());
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            lastDayOfMonth = format.format(calendar.getTime());
        }
        if (dateType == NEXT_MONTH) {
            nowMonth = nowMonth + 1;
            calendar.set(Calendar.MONTH, nowMonth);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            firstDayOfMonth = format.format(calendar.getTime());
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            lastDayOfMonth = format.format(calendar.getTime());
        }
        Log.i(TAG, "changeMonth==" + nowMonth);
        mSelectedDate.setText(firstDayOfMonth + "  至  " + lastDayOfMonth);

    }
/*    private void setDateTv(final int dateType) {
        Calendar ca = Calendar.getInstance();
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.i(TAG, "setDate=" + year + "-" + (month + 1) + "-" + dayOfMonth);
                if (dateType == 1) {
                    mStartTime.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                } else if (dateType == 2) {
                    mStopTime.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                }

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }*/

    private void getHuiZhenMsg() {
        Log.w(TAG, "getHuiZhenMsg BaseConst.DEAULT_URL=" + BaseConst.DICOMCLOUD_URL);
        RequestParams params = new RequestParams(BaseConst.DICOMCLOUD_URL + "/api/cloud/exam/count1");
        params.addBodyParameter("auth", getTokenFromLocal());
        if (!TextUtils.isEmpty(firstDayOfMonth)) {
            params.addBodyParameter("startTime", firstDayOfMonth);
        }
        if (!TextUtils.isEmpty(lastDayOfMonth)) {
            params.addBodyParameter("endTime", lastDayOfMonth);
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getHuiZhenMsg==" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = Integer.parseInt(jsonObject.getString("applyCount"));
                msg.arg2 = Integer.parseInt(jsonObject.getString("receiveCount"));
                staticsHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getHuiZhenMsg" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void getMenZhenMsg() {
        Log.w(TAG, "getMenZhenMsg BaseConst.DEAULT_URL=" + BaseConst.DICOMCLOUD_URL);
        RequestParams params = new RequestParams(BaseConst.DICOMCLOUD_URL + "/api/cloud/exam/count2");
        params.addBodyParameter("auth", getTokenFromLocal());
        if (!TextUtils.isEmpty(firstDayOfMonth)) {
            params.addBodyParameter("startTime", firstDayOfMonth);
        }
        if (!TextUtils.isEmpty(lastDayOfMonth)) {
            params.addBodyParameter("endTime", lastDayOfMonth);
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getMenZhenMsg==" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = Integer.parseInt(jsonObject.getString("applyCount"));
                msg.arg2 = Integer.parseInt(jsonObject.getString("receiveCount"));
                staticsHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getMenZhenMsg" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getShangZhuanZhen(String month) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/refclinic/getCountByMonth");
        params.addHeader("authorization", getTokenFromLocal());
        Log.i(TAG, "getTokenFromLocal==" + getTokenFromLocal());
        Log.i(TAG, BaseConst.DEAULT_URL + "/mobile/refclinic/getCountByMonth");
        params.addBodyParameter("month", month);
        params.addBodyParameter("refType", "上转诊");
        params.addBodyParameter("userId", mUserId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getZhuanZhen==" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                String data = jsonObject.getString("data");
                mTitleNumFirst.setText(data);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getZhuanZhen" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getXiaZhuanZhen(String month) {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/refclinic/getCountByMonth");
        params.addHeader("authorization", getTokenFromLocal());
        Log.i(TAG, "getTokenFromLocal==" + getTokenFromLocal());
        Log.i(TAG, BaseConst.DEAULT_URL + "/mobile/refclinic/getCountByMonth");
        params.addBodyParameter("month", month);
        params.addBodyParameter("refType", "下转诊");
        params.addBodyParameter("userId", mUserId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getZhuanZhen==" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                String data = jsonObject.getString("data");
                mTitleNumSecond.setText(data);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getZhuanZhen" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setAnim(int first, int second) {
        ValueAnimator animator = ValueAnimator.ofInt(0, first);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                mTitleNumFirst.setText(String.valueOf(value));

            }
        });
        animator.start();
        ValueAnimator animator1 = ValueAnimator.ofInt(0, second);
        animator1.setDuration(1000);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                mTitleNumSecond.setText(String.valueOf(value));
            }
        });
        animator1.start();

    }

    static class StaticsHandler extends Handler {
        private final WeakReference<StatisticsInfoActivity> mActivity;

        public StaticsHandler(StatisticsInfoActivity mactivity) {
            mActivity = new WeakReference<StatisticsInfoActivity>(mactivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity == null) {
                return;
            }
            if (msg.what == 1) {
          /*      mActivity.get().mTitleNumFirst.setText(String.valueOf(msg.arg1));
                mActivity.get().mTitleNumSecond.setText(String.valueOf(msg.arg2));*/
                mActivity.get().setAnim(msg.arg1, msg.arg2);
            }
        }
    }

}
