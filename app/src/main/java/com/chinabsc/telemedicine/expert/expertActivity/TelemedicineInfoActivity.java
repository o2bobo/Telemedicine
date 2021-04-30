package com.chinabsc.telemedicine.expert.expertActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;

import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.application.MyApplication;
import com.chinabsc.telemedicine.expert.entity.StopFloatViewMessage;
import com.chinabsc.telemedicine.expert.expertFragment.VIdeoPlayFragment;
import com.chinabsc.telemedicine.expert.myView.ButtomDialogView;
import com.chinabsc.telemedicine.expert.myView.CenterDialogView;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.QRCodeUtil;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.source.adnroid.comm.ui.activity.UserSharedListActivity;

import com.source.adnroid.comm.ui.entity.CommenResponse;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class TelemedicineInfoActivity extends FragmentActivity implements TabHost.OnTabChangeListener, VIdeoPlayFragment.IVideoCallBack {
    public static String TAG = "TelemedicineInfoActivity";
    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public static String TELEMEDICINE_INFO_TYPE = "TELEMEDICINE_INFO_TYPE";

    public ImageView mBackImageView;
    public TextView mTitleTextView;
    public ImageView mShareImageView;//病例分享按钮
    private FragmentTabHost mTabHost;

    public String mTelemedicineInfoId = "";
    public String mTelemedicineType = "";
    private String patientUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemedicine_info);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            mTelemedicineType = bundle.getString(TELEMEDICINE_INFO_TYPE, "DefaultType");
            Log.i(TAG, "Telemedicine" + mTelemedicineInfoId);
            Log.i(TAG, "mTelemedicineType" + mTelemedicineType);
        } else {
            Log.i("bundle", "bundle == null");
            finish();
        }
        init();
        initShareView();
        setViewListener();


    }

    //初始化病例分享按钮
    private void initShareView() {
        mShareImageView = findViewById(R.id.ShareImageView);
        mShareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPatientUrl();
            }
        });
    }

    //获取需要分享的病历地址
    private void getSharedPatientUrl() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/emr/shareEmr");
        params.addHeader("Authorization", getTokenFromLocal());
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getSharedPatientUrl success " + result + "**" + mTelemedicineInfoId);
                JSONObject jsonObject=JSONObject.parseObject(result);
                if (jsonObject.getInteger("resultCode")==200){
                    patientUrl=jsonObject.getString("data") + mTelemedicineInfoId;
                    Log.i(TAG, "getSharedPatientUrl ==" + patientUrl);
                    startPatinetShared();
                }else {
                    Log.e(TAG, "getSharedPatientUrl faied "+result );
                    T.show(TelemedicineInfoActivity.this, "无效token", Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getSharedPatientUrl faied " + ex.getMessage());
                T.show(TelemedicineInfoActivity.this, "获取分享地址失败", Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //分享地址获取成功开始显示分享界面
    private void startPatinetShared() {

        View ve = LayoutInflater.from(TelemedicineInfoActivity.this).inflate(R.layout.dialog_buttom_layout, null);
        final ButtomDialogView buttomDialogView = new ButtomDialogView(TelemedicineInfoActivity.this, ve, true, true);
        buttomDialogView.setOnClickListener(R.id.chat_v1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click chat");
                Intent intent = new Intent();
                intent.setClass(TelemedicineInfoActivity.this, UserSharedListActivity.class);
                intent.setType("patientshared");
                intent.putExtra(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                intent.putExtra("userId", SPUtils.get(TelemedicineInfoActivity.this, PublicUrl.USER_ID_KEY, "").toString());
                intent.putExtra(PublicUrl.TOKEN_KEY, SPUtils.get(TelemedicineInfoActivity.this, PublicUrl.TOKEN_KEY, "").toString());
                startActivity(intent);
                buttomDialogView.dismiss();
            }
        });
        buttomDialogView.setOnClickListener(R.id.erwema_v2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click erwema");
                Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(patientUrl, 480, 480);
                showErWeiMa(bitmap);
                buttomDialogView.dismiss();
            }
        });
        buttomDialogView.setOnClickListener(R.id.weixin_v3, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click weixin");
                startShared();
                buttomDialogView.dismiss();
            }
        });
        buttomDialogView.show();
    }

    //Dialog显示图片
    private void showErWeiMa(Bitmap bitmap) {
        View imgView = LayoutInflater.from(TelemedicineInfoActivity.this).inflate(R.layout.dialog_imageview_layout, null);
        ImageView imageView = imgView.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        CenterDialogView centerDialogView = new CenterDialogView(TelemedicineInfoActivity.this, imgView, true, true);
        centerDialogView.show();
    }

    //开始和获取分享全新
    private void startShared() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        //SHARE_MEDIA.SINA, SHARE_MEDIA.QQ

        new ShareAction(TelemedicineInfoActivity.this).withText(patientUrl).setDisplayList(SHARE_MEDIA.WEIXIN)
                .setCallback(shareListener).open();

    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.i(TAG, "成功了");
            Toast.makeText(TelemedicineInfoActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(TelemedicineInfoActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "失败" + t.getMessage());
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(TelemedicineInfoActivity.this, "取消了", Toast.LENGTH_LONG).show();
            Log.i(TAG, "取消了");

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        stopFloat();

    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.BackImageView);
        mTitleTextView = (TextView) findViewById(R.id.TitleTextView);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentLayout);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(this);
        initTab();
    }

    private void setViewListener() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    private void initTab() {
        String tabs[] = TelemedicineInfoTabItem.getTabsTxt();

        for (int i = 0; i < tabs.length; i++) {

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabs[i]).setIndicator(getTabView(i));
            Bundle bundle = new Bundle();
            bundle.putString(TelemedicineInfoActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);

            if (i == 3) {
                switch (mTelemedicineType) {
                    case "TELEMEDICINE_INFO_TYPE_WAITING":
                        mTabHost.addTab(tabSpec, TelemedicineInfoTabItem.getFragments()[i], bundle);
                        mTabHost.setTag(i);
                        break;
                    case "DefaultType":
                        break;
                    default:
                        break;
                }
            } else {
                mTabHost.addTab(tabSpec, TelemedicineInfoTabItem.getFragments()[i], bundle);
                mTabHost.setTag(i);
            }
        }
    }

    private View getTabView(int idx) {
        View view = LayoutInflater.from(this).inflate(R.layout.telemedicine_info_tab_item, null);
        ((TextView) view.findViewById(R.id.TabTextTextView)).setText(TelemedicineInfoTabItem.getTabsTxt()[idx]);
        if (idx == 0) {
            ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#35a9f8"));
            ((ImageView) view.findViewById(R.id.TabPicImaveView)).setImageResource(TelemedicineInfoTabItem.getTabsImgLight()[idx]);
        } else {
            ((ImageView) view.findViewById(R.id.TabPicImaveView)).setImageResource(TelemedicineInfoTabItem.getTabsImg()[idx]);
        }
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.i("TelemedicineInfo", "onTabChanged==" + mTabHost.getCurrentTab());
        switch (mTabHost.getCurrentTab()) {
            case 0:
                mTitleTextView.setText("申请单");

                break;
            case 1:
                mTitleTextView.setText("病历信息");
                break;
            case 2:
                mTitleTextView.setText("病历附件");
                break;
            case 3:
                if (mTelemedicineType.equals("TELEMEDICINE_INFO_TYPE_WAITING")) {
                    mTitleTextView.setText("视频诊室");
                } else {
                    mTitleTextView.setText("会诊报告");
                }
                break;
            case 4:
                if (mTelemedicineType.equals("TELEMEDICINE_INFO_TYPE_WAITING")) {
                    mTitleTextView.setText("会诊报告");
                }
                break;
            default:
                break;
        }
        updateTab();
    }

    private void updateTab() {
        TabWidget TabItem = mTabHost.getTabWidget();
        for (int i = 0; i < TabItem.getChildCount(); i++) {
            View view = TabItem.getChildAt(i);
            ImageView TabPicImaveView = (ImageView) view.findViewById(R.id.TabPicImaveView);
            if (i == mTabHost.getCurrentTab()) {
                ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#35a9f8"));
                if (TabItem.getChildCount() == 3 && i > 1) {
                    TabPicImaveView.setImageResource(TelemedicineInfoTabItem.getTabsImgLight()[i + 1]);
                } else {
                    TabPicImaveView.setImageResource(TelemedicineInfoTabItem.getTabsImgLight()[i]);
                }
            } else {
                ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#d8d8d8"));
                if (TabItem.getChildCount() == 3 && i > 1) {
                    TabPicImaveView.setImageResource(TelemedicineInfoTabItem.getTabsImg()[i + 1]);
                } else {
                    TabPicImaveView.setImageResource(TelemedicineInfoTabItem.getTabsImg()[i]);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.i("zzw", "getUserStatus====" + MyApplication.isRtcServiceRuning);
        if (MyApplication.isRtcServiceRuning) {
            showDialog(TelemedicineInfoActivity.this);
        } else {
            super.onBackPressed();
        }
    }

    //显示基本的AlertDialog
    private void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.basic_logo_big);
        builder.setTitle("退出");
        builder.setMessage("您正在房间中，退出将断开连接...");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    public void backToVideo() {
        mTabHost.setCurrentTab(3);
    }

    private void stopFloat() {

    }
    public String getTokenFromLocal() {

        String token = SPUtils.get(this, PublicUrl.TOKEN_KEY, "").toString();
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return null;
        } else {
            Log.i("test", "token: " + token);
            return token;
        }
    }
}