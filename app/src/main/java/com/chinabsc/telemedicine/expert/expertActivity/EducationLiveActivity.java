package com.chinabsc.telemedicine.expert.expertActivity;

import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;

import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.educationvideo.EducationLiveMsgEntity;
import com.chinabsc.telemedicine.expert.liveUtlis.MediaUtils;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.source.adnroid.comm.ui.entity.Const;
import com.source.android.chatsocket.entity.SocketConst;

import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.net.Socket;

@ContentView(R.layout.activity_education_live)
public class EducationLiveActivity extends AppCompatActivity {
    private String TAG = "EducationLiveActivity";
    public static String LIVE_ID = "LIVE_ID";
    public static String VIDEO_URL = "VIDEO_URL";
    public static String LIVE_TITLE = "LIVE_TITLE";
    public static String LIVE_HOSPITAL = "LIVE_HOSPITAL";
    public static String LIVE_DOCTOR = "LIVE_DOCTOR";
    private String mLiveId = "";
    private String mVideoUrl = "";
    private String mName = "";
    private String mHospital = "";
    private String mDoctor = "";
    PlayerView player;
    RelativeLayout title;
    @ViewInject(R.id.sign_up)
    private Button signUpBt;
    @ViewInject(R.id.start_play)
    private Button startPlayBt;
    @ViewInject(R.id.course_name)
    private TextView courseName;
    /*    @ViewInject(R.id.hospital_name)
        private TextView hospitalName;
        @ViewInject(R.id.course_num)
        private TextView courseNum;*/
    @ViewInject(R.id.department_name)
    private TextView departmentName;
    @ViewInject(R.id.doctor_name)
    private TextView doctorName;
    @ViewInject(R.id.edu_type)
    private TextView eduType;
    @ViewInject(R.id.course_type)
    private TextView courseType;
    @ViewInject(R.id.course_time)
    private TextView courseTime;
    @ViewInject(R.id.course_hour)
    private TextView courseHour;
    @ViewInject(R.id.course_grade_type)
    private TextView courseGradeType;
    @ViewInject(R.id.course_grade)
    private TextView courseGrade;
    @ViewInject(R.id.course_describe)
    private TextView courseDescribe;

    EduHandler eduHandler = new EduHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title = findViewById(R.id.TitleLayout);
        init();
        getData();
        Log.i(TAG, "mLiveId==" + mLiveId);
        if (mLiveId != null) {
            getSignUpStatus();
            getVideoMsg();//获取视频详情并展示
        }
        if (!TextUtils.isEmpty(mVideoUrl)) {
            String path = mVideoUrl;
            Log.i(TAG, path);
            startPlay(path);
        }
    }

    private void getData() {
        Bundle bundle = this.getIntent().getExtras();
        mLiveId = bundle.getString(LIVE_ID);
        mVideoUrl = bundle.getString(VIDEO_URL);
        mName = bundle.getString(LIVE_TITLE);
        mHospital = bundle.getString(LIVE_HOSPITAL);
        mDoctor = bundle.getString(LIVE_DOCTOR);
    }

    //初始化播放器ijkplayer
    private void init() {
        RelativeLayout rootView = findViewById(R.id.app_video_box);
        player = new PlayerView(this, rootView)
                .setTitle(mName)
                .setScaleType(PlayStateParams.fitparent)
                .hideMenu(true)
                .forbidTouch(false)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                   /*     Glide.with(IjkTestActivity.this)
                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                                *//*        .placeholder(R.color.cl_default)
                                        .error(R.color.cl_error)*//*
                                .into(ivThumbnail);*/
                    }
                });

    }

    //播放方法
    private void startPlay(String url) {
        Log.i(TAG, "startPlay url=" + url);
        //rtmp://58.200.131.2:1935/livetv/hunantv
        player.setPlaySource(url)
                .startPlay();
    }

    @Event(value = {R.id.start_play, R.id.sign_up}, type = View.OnClickListener.class)
    private void OnClick(View v) {
        switch (v.getId()) {
            case R.id.start_play:
                startPlay(mVideoUrl);
                break;
            case R.id.sign_up:
                if (signUpBt.getText().toString().equals("报名观看")) {
                    signUp();
                } else if (signUpBt.getText().toString().equals("取消报名")) {
                    cancleSignUp();
                }
                break;
        }
    }

    //获取播放详情页
    private void getVideoMsg() {

        RequestParams params = new RequestParams(BaseConst.EDUCATION_URL + "mobile/edu/live/onInfo");
        //get  String liveid 详情
        //params.setSslSocketFactory(); // 设置ssl
        // params.addHeader("authorization", getTokenFromLocal());
        params.addBodyParameter("liveId", mLiveId);
        //params.addQueryStringParameter("liveid", mLiveId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getVideoMsg==" + result);
                EducationLiveMsgEntity educationLiveMsgEntity = JSONObject.parseObject(result, EducationLiveMsgEntity.class);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = educationLiveMsgEntity;
                eduHandler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //获取报名状态
    private void getSignUpStatus() {
        RequestParams params = new RequestParams(BaseConst.EDUCATION_URL + "mobile/edu/live/checkSignUp");
        params.addBodyParameter("liveId", mLiveId);
        params.addBodyParameter("doctorId", getUserID());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getSignUpStatus==" + result);
                //resultCode 200 已经报名
                JSONObject temp = JSONObject.parseObject(result);
                if (temp.getString("resultCode").equals("200")) {
                    eduHandler.sendEmptyMessage(2);
                } else {
                    eduHandler.sendEmptyMessage(3);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getSignUpStatus onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //取消报名
    private void cancleSignUp() {
        RequestParams params = new RequestParams(BaseConst.EDUCATION_URL + "mobile/edu/live/delSign");
        params.addBodyParameter("liveId", mLiveId);
        params.addBodyParameter("doctorId", getUserID());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "cancleSignUp result==" + result);
                JSONObject temp = JSONObject.parseObject(result);
                if (temp.getString("resultCode").equals("200")) {
                    eduHandler.sendEmptyMessage(3);
                } else {
                    T.showMessage(getApplicationContext(), "操作失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getSignUpStatus onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //报名
    private void signUp() {

        RequestParams params = new RequestParams(BaseConst.EDUCATION_URL + "mobile/edu/live/signUp");
        params.addBodyParameter("liveId", mLiveId);
        params.addBodyParameter("doctorId", getUserID());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "signUp result==" + result);
                JSONObject temp = JSONObject.parseObject(result);
                if (temp.getString("resultCode").equals("200")) {
                    eduHandler.sendEmptyMessage(2);
                } else {
                    T.showMessage(getApplicationContext(), "操作失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getSignUpStatus onError:" + ex.getMessage());
                T.showMessage(getApplicationContext(), getString(R.string.server_error));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    static class EduHandler extends Handler {
        WeakReference<EducationLiveActivity> reference;

        public EduHandler(EducationLiveActivity activity) {
            reference = new WeakReference<EducationLiveActivity>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference == null) {
                return;
            }
            switch (msg.what) {
                case 1:
                    EducationLiveMsgEntity entity = (EducationLiveMsgEntity) msg.obj;
                    if (!reference.get().isNull(entity.getData().getWarename())) {
                        reference.get().courseName.setText("课程名称: " + entity.getData().getWarename());
                    }
                    if (!reference.get().isNull(entity.getData().getDepartname())) {
                        reference.get().departmentName.setText("授课科室: " + entity.getData().getDepartname());
                    }
                    if (!reference.get().isNull(entity.getData().getExpertname())) {
                        reference.get().doctorName.setText("授课专家: " + entity.getData().getExpertname());
                    }
                    if (!reference.get().isNull(entity.getData().getCourseclass())) {
                        reference.get().eduType.setText("教育类别: " + entity.getData().getCourseclass());
                    }
                    if (!reference.get().isNull(entity.getData().getCoursemajor())) {
                        reference.get().courseType.setText("课程类别: " + entity.getData().getCoursemajor());
                    }
                    if (!reference.get().isNull(entity.getData().getStarttime())) {
                        reference.get().courseTime.setText("授课时间: " + entity.getData().getStarttime() + "到" + entity.getData().getEndtime());
                    }
                    if (entity.getData().getCourseduration() != null) {
                        reference.get().courseHour.setText("授课学时: "+entity.getData().getCourseduration());
                    } else {
                        reference.get().courseHour.setText("授课学时: ");
                    }
                    if (!reference.get().isNull(entity.getData().getScoretype())) {
                        reference.get().courseGradeType.setText("学分类别: " + entity.getData().getScoretype());
                    }
                    if (!reference.get().isNull(entity.getData().getScore())) {
                        reference.get().courseGrade.setText("课程学分: " + entity.getData().getScore());
                    }
                    if (!reference.get().isNull(entity.getData().getCoursedesc())) {
                        reference.get().courseDescribe.setText("课程描述: " + entity.getData().getCoursedesc());
                    }
                    // reference.get().courseNum.setText("频道号: " + entity.getData().getCoursemajor());
                    reference.get().mVideoUrl = entity.getData().getLiveurl();
                    break;
                case 2://已报名
                    reference.get().signUpBt.setText("取消报名");
                    reference.get().startPlayBt.setEnabled(true);
                    break;
                case 3://未报名
                    reference.get().signUpBt.setText("报名观看");
                    reference.get().startPlayBt.setEnabled(false);
                    break;

            }
        }
    }

    private boolean isNull(String temp) {
        if (temp.equals("null") || TextUtils.isEmpty(temp)) {
            return true;
        }
        return false;
    }

    public String getUserID() {
        String userId = SPUtils.get(this, PublicUrl.USER_ID_KEY, "").toString();
        return userId;
    }
/*    public String getTokenFromLocal() {

        String token = SPUtils.get(this, PublicUrl.TOKEN_KEY, "").toString();
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return null;
        } else {
            Log.i("test", "token: " + token);
            return token;
        }
    }*/

    public void delToken() {
        SPUtils.put(this, PublicUrl.TOKEN_KEY, "");
    }

    public void doLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        //MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        /**demo的内容，暂停系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(this, false);
        /**demo的内容，激活设备常亮状态*/
        //if (wakeLock != null) {
        //    wakeLock.acquire();
        //}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
        Log.i(TAG, "onConfigurationChanged===" + newConfig.orientation);
      /*  if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_education_live_honeral);
        } else {
            setContentView(R.layout.activity_education_live);
        }*/

    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        /**demo的内容，恢复设备亮度状态*/
        //if (wakeLock != null) {
        //    wakeLock.release();
        //}
    }
}
