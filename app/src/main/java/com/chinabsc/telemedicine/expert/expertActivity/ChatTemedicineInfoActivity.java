package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import android.os.Bundle;
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
import com.source.adnroid.comm.ui.activity.UserSharedListActivity;
import com.umeng.analytics.MobclickAgent;

public class ChatTemedicineInfoActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
    public static String TELEMEDICINE_INFO_ID = "TELEMEDICINE_INFO_ID";
    public static String TELEMEDICINE_INFO_TYPE = "TELEMEDICINE_INFO_TYPE";

    public ImageView mBackImageView;
    public TextView mTitleTextView;
    public ImageView mShareImageView;//病例分享按钮
    private FragmentTabHost mTabHost;

    public String mTelemedicineInfoId = "";
    public String mTelemedicineType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemedicine_info);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTelemedicineInfoId = bundle.getString(TELEMEDICINE_INFO_ID);
            mTelemedicineType = bundle.getString(TELEMEDICINE_INFO_TYPE, "DefaultType");
            Log.i("zzw", "Telemedicine" + mTelemedicineInfoId);
            Log.i("zzw", "mTelemedicineType" + mTelemedicineType);
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
                Intent intent = new Intent();
                intent.setClass(ChatTemedicineInfoActivity.this, UserSharedListActivity.class);
                intent.putExtra(ChatTemedicineInfoActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);
                intent.putExtra("userId", SPUtils.get(ChatTemedicineInfoActivity.this, PublicUrl.USER_ID_KEY, "").toString());
                intent.putExtra(PublicUrl.TOKEN_KEY, SPUtils.get(ChatTemedicineInfoActivity.this, PublicUrl.TOKEN_KEY, "").toString());
                startActivity(intent);
            }
        });
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
        mBackImageView = findViewById(R.id.BackImageView);
        mTitleTextView = findViewById(R.id.TitleTextView);
        mTabHost = findViewById(android.R.id.tabhost);
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
        String tabs[] = ChatTelemedicineInfoTabItem.getTabsTxt();
        for (int i = 0; i < tabs.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabs[i]).setIndicator(getTabView(i));
            Bundle bundle = new Bundle();
            bundle.putString(ChatTemedicineInfoActivity.TELEMEDICINE_INFO_ID, mTelemedicineInfoId);

            if (i == 3) {
                switch (mTelemedicineType) {
                    case "TELEMEDICINE_INFO_TYPE_WAITING":
                        mTabHost.addTab(tabSpec, ChatTelemedicineInfoTabItem.getFragments()[i], bundle);
                        mTabHost.setTag(i);
                        break;
                    case "DefaultType":
                        break;
                    default:
                        break;
                }
            } else {
                mTabHost.addTab(tabSpec, ChatTelemedicineInfoTabItem.getFragments()[i], bundle);
                mTabHost.setTag(i);
            }
        }
    }

    private View getTabView(int idx) {
        View view = LayoutInflater.from(this).inflate(R.layout.telemedicine_info_tab_item, null);
        ((TextView) view.findViewById(R.id.TabTextTextView)).setText(ChatTelemedicineInfoTabItem.getTabsTxt()[idx]);
        if (idx == 0) {
            ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#35a9f8"));
            ((ImageView) view.findViewById(R.id.TabPicImaveView)).setImageResource(ChatTelemedicineInfoTabItem.getTabsImgLight()[idx]);
        } else {
            ((ImageView) view.findViewById(R.id.TabPicImaveView)).setImageResource(ChatTelemedicineInfoTabItem.getTabsImg()[idx]);
        }
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.i("onTabChanged", "" + mTabHost.getCurrentTab());
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
        }
        updateTab();
    }

    private void updateTab() {
        TabWidget TabItem = mTabHost.getTabWidget();
        for (int i = 0; i < TabItem.getChildCount(); i++) {
            View view = TabItem.getChildAt(i);
            ImageView TabPicImaveView = view.findViewById(R.id.TabPicImaveView);
            if (i == mTabHost.getCurrentTab()) {
                ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#35a9f8"));
                TabPicImaveView.setImageResource(ChatTelemedicineInfoTabItem.getTabsImgLight()[i]);
                TabPicImaveView.setImageResource(ChatTelemedicineInfoTabItem.getTabsImgLight()[i]);
            } else {
                ((TextView) view.findViewById(R.id.TabTextTextView)).setTextColor(Color.parseColor("#d8d8d8"));
                TabPicImaveView.setImageResource(ChatTelemedicineInfoTabItem.getTabsImg()[i]);
                TabPicImaveView.setImageResource(ChatTelemedicineInfoTabItem.getTabsImg()[i]);
            }
        }
    }

}
