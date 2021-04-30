package com.chinabsc.telemedicine.expert.expertActivity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertFragment.NewsFragment;
import com.chinabsc.telemedicine.expert.myAdapter.NewsTabAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_news)
public class NewsActivity extends AppCompatActivity {

    @Event(value = {
            R.id.BackImageView}, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.BackImageView:
                finish();
                break;
        }
    }

    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();

    private int mTabSize = 0;
    private int mTabAddNum = 0;
    private View mTabView[];
    private View mTabSelectedView[];

    @ViewInject(R.id.NewsScrollView)
    private HorizontalScrollView mNewsScrollView;

    @ViewInject(R.id.NewsViewPager)
    private ViewPager mNewsViewPager;

    @ViewInject(R.id.NewsTableRow)
    private TableRow mNewsTableRow;

    public class TabItem {
        public String colid;
        public String colname;
    }

    public ArrayList<TabItem> mTabItem = new ArrayList<TabItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        getTabData();
        mNewsViewPager.addOnPageChangeListener(new NewsViewPagerPageChangeListener());
    }

    private class NewsViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(final int arg0) {
            initializeNewsTab();
            selectedNewsViewPager(arg0);
            moveNewsTab(arg0);
        }
    }

    private void getTabData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL+ "/mobile/news/findNewsList");
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getTabData" + result);
                parseTabJson(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    private void parseTabJson(String result) {
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray tabArray = new JSONArray(data);
                        mTabItem.clear();
                        for (int i = 0; i < tabArray.length(); i++) {
                            JSONObject k = tabArray.getJSONObject(i);
                            TabItem item = new TabItem();
                            if (k.has("colid")) {
                                item.colid = k.getString("colid");
                                Log.i("test", "colid: " + k.getString("colid"));
                            }
                            if (k.has("colname")) {
                                item.colname = k.getString("colname");
                            }
                            mTabItem.add(item);
                        }
                        mTabSize = mTabItem.size();
                        if (mTabSize > 0) {
                            setTabData();
                            initNewsViewPager();
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
    }

    private void setTabData() {
        mNewsTableRow.removeAllViews();
        mTabView = new View[mTabSize];
        mTabSelectedView = new View[mTabSize];
        for (int i = 0; i < mTabSize; i++) {
            TabView item = new TabView();
            if (mTabSize == 1) {
                item.mTabSelectedView.setVisibility(View.VISIBLE);
            } else {
                if (i == 0) {
                    item.mTabSelectedView.setVisibility(View.VISIBLE);
                } else if (i == mTabSize - 1) {
                    item.mTabSelectedView.setVisibility(View.INVISIBLE);
                } else {
                    item.mTabSelectedView.setVisibility(View.INVISIBLE);
                }
            }
            item.mTabText.setText(mTabItem.get(i).colname);
            mTabView[i] = item.mTabView;
            mTabSelectedView[i] = item.mTabSelectedView;
            mNewsTableRow.addView(mTabView[i]);
        }

        mTabAddNum = 0;
        for (int i = 0; i < mTabSize; i++) {
            mTabView[i].setTag(mTabAddNum);
            mTabAddNum++;
            mTabView[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();
                    selectedNewsViewPager(n);
                }
            });

        }
    }

    private void selectedNewsViewPager(int n) {
        mNewsViewPager.setCurrentItem(n);
        View a = mTabSelectedView[n];
        if (mTabSize == 1) {
            a.setVisibility(View.VISIBLE);
        } else {
            if (n == 0) {
                initializeNewsTab();
                a.setVisibility(View.VISIBLE);
            } else if (n == mTabSize - 1) {
                initializeNewsTab();
                a.setVisibility(View.VISIBLE);
            } else {
                initializeNewsTab();
                a.setVisibility(View.VISIBLE);
            }
        }
    }

    private void moveNewsTab(int n) {
        mNewsScrollView.measure(0, 0);
        if (n == 0) {
            mNewsScrollView.scrollTo(0, 0);
        } else {
            int width = mNewsScrollView.getMeasuredWidth() / mTabSize;
            width = width * n;
            mNewsScrollView.scrollTo(width, 0);
        }
    }

    private void initNewsViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < mTabSize; i++) {
            NewsFragment f = new NewsFragment();
            Bundle b = new Bundle();
            b.putString(NewsFragment.TAB_ID_KEY, mTabItem.get(i).colid);
            f.setArguments(b);
            fragments.add(f);
        }
        NewsTabAdapter mFragmentAdapteradapter =
                new NewsTabAdapter(getSupportFragmentManager(), fragments);
        mNewsViewPager.setAdapter(mFragmentAdapteradapter);
    }

    private void initializeNewsTab() {
        if (mTabSize > 1) {
            for (int i = 0; i < mTabSize; i++) {
                if (i == 0) {
                    mTabSelectedView[i].setVisibility(View.INVISIBLE);
                } else if (i == mTabSize - 1) {
                    mTabSelectedView[i].setVisibility(View.INVISIBLE);
                } else {
                    mTabSelectedView[i].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private class TabView {
        public View mTabView;
        public TextView mTabText;
        public View mTabSelectedView;

        public TabView() {
            mTabView = LayoutInflater.from(NewsActivity.this).inflate(
                    R.layout.news_tab_item, null);
            viewfinder();
        }

        public void viewfinder() {
            mTabText = (TextView) mTabView.findViewById(R.id.TabTextView);
            mTabSelectedView = (View) mTabView.findViewById(R.id.TabSelectedView);
        }
    }

    public String getTokenFromLocal() {

        String token = SPUtils.get(this, PublicUrl.TOKEN_KEY, "").toString();
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return null;
        } else {
            Log.i("token", token);
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
}
