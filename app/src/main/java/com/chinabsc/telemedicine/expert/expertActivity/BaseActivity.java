package com.chinabsc.telemedicine.expert.expertActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public class BaseActivity extends AppCompatActivity {
    public String TAG=this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        PushAgent.getInstance(getApplication()).onAppStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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


    public void delToken() {
        SPUtils.put(this, PublicUrl.TOKEN_KEY, "");
    }

    public void doLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public String getUserID() {
        String userId = SPUtils.get(this, PublicUrl.USER_ID_KEY, "").toString();
        if (TextUtils.isEmpty(userId)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return null;
        } else {
            Log.i(TAG, "userID==: " + userId);
            return userId;
        }
    }
    @Override
    protected void onDestroy() {
        Log.i("BaseActivity","BaseActivity==>onDestroy");
        super.onDestroy();
    }
    //设置字体不受系统影响
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


}
