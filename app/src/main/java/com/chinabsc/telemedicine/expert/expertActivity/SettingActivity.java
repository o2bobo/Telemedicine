package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chinabsc.telemedicine.expert.R;

import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.source.adnroid.comm.ui.chatutils.ChatSerciceUtils;
import com.source.android.chatsocket.service.MainService;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {

    @Event(value = {
            R.id.BackImageView,
            R.id.UserInfoButton,
            R.id.ChangePasswordButton,
            R.id.AboutButton,
            R.id.LogoutButton}, type = View.OnClickListener.class)
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
            case R.id.UserInfoButton:
                intent = new Intent(SettingActivity.this, UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.ChangePasswordButton:
                intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.AboutButton:
                intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.LogoutButton:
                Intent intent1=new Intent();
                intent1.setClass(this, MainService.class);
                stopService(intent1);
                Log.i("MainService","statues logout==>"+ ChatSerciceUtils.isServiceRunning(SettingActivity.this,"com.source.android.chatsocket.service.MainService"));
                delToken();
                intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }

}
