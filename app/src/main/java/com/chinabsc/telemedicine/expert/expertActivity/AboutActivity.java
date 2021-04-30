package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView titleTextView = findViewById(R.id.TitleTextView);
        TextView verTextView = findViewById(R.id.VerTextView);
        ImageView logoImage = findViewById(R.id.LogoImage);

        //读取当前使用的配置序号
        SharedPreferences sp = getSharedPreferences("spUrlList", Context.MODE_PRIVATE);
        int pos = sp.getInt("urlListPos", 0);

        //读取xml中服务器地址列表与名称列表
        Resources res = getResources();
        String[] hospitalList = res.getStringArray(R.array.hospitals);
        titleTextView.setText(hospitalList[pos]);
        switch (pos) {
            case 0:
                logoImage.setImageResource(R.mipmap.basic_logo_big);
                break;
            case 1:
                logoImage.setImageResource(R.mipmap.basic_logo_big);
                break;
            default:
                break;
        }

        //获取版本名
        String versionname = "";
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionname = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        verTextView.setText("版本：" + versionname);
    }
}
