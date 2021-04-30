package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.umeng.analytics.MobclickAgent;

public class BilateralDiagnosisInfoActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
    public static String TAG = "BilateralDiagnosisInfoActivity";

    public static String CLINIC_ID = "CLINIC_ID";
    private String mClinicId = "";

    public ImageView mBackImageView;
    public TextView mTitleTextView;
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilateral_diagnosis_info);

        Intent i = getIntent();
        mClinicId = i.getStringExtra(CLINIC_ID);
        Log.i(TAG, "mClinicId==" + mClinicId);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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

    private void initTab() {
        String tabs[] = BilateralDiagnosisTabItem.getTabsTxt();
        for (int i = 0; i < tabs.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabs[i]).setIndicator(getTabView(i));
            Bundle bundle = new Bundle();
            bundle.putString(BilateralDiagnosisInfoActivity.CLINIC_ID, mClinicId);
            mTabHost.addTab(tabSpec, BilateralDiagnosisTabItem.getFragments()[i], bundle);
            mTabHost.setTag(i);
        }
    }

    private View getTabView(int idx) {
        View view = LayoutInflater.from(this).inflate(R.layout.telemedicine_info_tab_item, null);
        ((TextView) view.findViewById(R.id.TabTextTextView)).setText(BilateralDiagnosisTabItem.getTabsTxt()[idx]);
        if (idx == 0) {
            ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#35a9f8"));
            ((ImageView) view.findViewById(R.id.TabPicImaveView)).setImageResource(BilateralDiagnosisTabItem.getTabsImgLight()[idx]);
        } else {
            ((ImageView) view.findViewById(R.id.TabPicImaveView)).setImageResource(BilateralDiagnosisTabItem.getTabsImg()[idx]);
        }
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        switch (mTabHost.getCurrentTab()) {
            case 0:
                mTitleTextView.setText("申请单");
                break;
            case 1:
                mTitleTextView.setText("电子病历");
                break;
            case 2:
                mTitleTextView.setText("病历附件");
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
                TabPicImaveView.setImageResource(BilateralDiagnosisTabItem.getTabsImgLight()[i]);
            } else {
                ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#d8d8d8"));
                TabPicImaveView.setImageResource(BilateralDiagnosisTabItem.getTabsImg()[i]);
            }
        }
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
