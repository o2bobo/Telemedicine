package com.chinabsc.telemedicine.expert.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.source.adnroid.comm.ui.chatutils.ChatCrashHanlder;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import org.xutils.x;

/**
 * Created by zzw on 2017/3/22.
 */

public class MyApplication extends Application {
   public static boolean isRtcServiceRuning=false;
    public static boolean hasRemoteStream=false;
    public static boolean hasLocalStream=false;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyApplication","MyApplication oncreate");
       // ChatCrashHanlder.getInstance().init(this);

        x.Ext.init(this);

        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=590017c6");
        UMConfigure.init(this, "5bd2c75eb465f5d82700028e", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "302d75a25b79f906ee142b205befffb8");
        UMConfigure.setLogEnabled(true);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.DEBUG=true;
        //mPushAgent.setDebugMode(false);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.i("MyApplication","register="+deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i("MyApplication","onFailure="+s+" - "+s1);
            }
        });
        //友盟推送注册

        PlatformConfig.setWeixin("wx0fa2601c19d5d2cf", "5b89896a57d8140006c027f591bdd6f0");
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
