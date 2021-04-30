package com.chinabsc.telemedicine.expert.expertActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;

import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;


import com.chinabsc.telemedicine.expert.entity.DataBean;

import com.chinabsc.telemedicine.expert.entity.NodeJsServerEntity;
import com.chinabsc.telemedicine.expert.entity.RoomEntity;


import com.chinabsc.telemedicine.expert.myView.CustomViewPager;
import com.chinabsc.telemedicine.expert.expertFragment.HomeCooperationFragment;
import com.chinabsc.telemedicine.expert.expertFragment.HomeEduFragment;
import com.chinabsc.telemedicine.expert.expertFragment.HomeMedicalRecordFragment;
import com.chinabsc.telemedicine.expert.expertFragment.HomeNewsFragment;
import com.chinabsc.telemedicine.expert.expertFragment.HomePagerFragment;
import com.chinabsc.telemedicine.expert.myAdapter.MainHomePageAdapter;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;


import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import com.source.android.chatsocket.net.HttpReuqests;
import com.source.android.chatsocket.service.MainService;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


@ContentView(R.layout.activity_main_tab)
public class MainActivity extends BaseActivity {
    private String TAG = "MainActivity";
    @ViewInject(R.id.main_rg)
    private RadioGroup mMainRg;
    @ViewInject(R.id.main_viewpager)
    private CustomViewPager mViewPager;
    public DisplayImageOptions mOptions;
    public ImageLoader mImageLoader = ImageLoader.getInstance();
    private List<Fragment> fragments;

    @ViewInject(R.id.BannerViewPager)
    private ViewPager mBannerViewPager;
    private Timer timer = new Timer();
    public static final int UPTATE_VIEWPAGER = 0x123;
    private int mCurrentPicItem = 0;
    private ArrayList<Banner> mBannerList = new ArrayList<Banner>();
    @ViewInject(R.id.PointLayout)
    private LinearLayout mPointLayout;

    public class Banner {
        public String artid = "";
        public String pic = "";
        public String title = "";
    }

    public class DateInfo {
        public String clinicId;
        public String dateDone;
        public String sndSiteName;
        public String doctorName;
        public String patientName;
        public String patientGender;
        public String age;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        Log.i("test", "onCreate");

/*        mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.basic_image_download) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.basic_image_error)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.basic_image_error)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                //message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        }, 5000, 5000);*/

        // TODO: 2018-12-26 在首页请求相册和拍照权限

        initView();
    }

    private void initView() {
        fragments = new ArrayList<>();
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        HomeMedicalRecordFragment homeMedicalRecordFragment = new HomeMedicalRecordFragment();
        HomeNewsFragment homeNewsFragment = new HomeNewsFragment();
        HomeEduFragment homeEduFragment = new HomeEduFragment();
        HomeCooperationFragment homeCooperationFragment = new HomeCooperationFragment();
        fragments.add(homePagerFragment);
        fragments.add(homeMedicalRecordFragment);
        fragments.add(homeNewsFragment);
        fragments.add(homeEduFragment);
        fragments.add(homeCooperationFragment);
        MainHomePageAdapter adapter = new MainHomePageAdapter(getSupportFragmentManager(), this, fragments);
        mViewPager.setAdapter(adapter);
        mMainRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home_page:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.home_medical:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.home_news:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.home_edu:
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.home_coop:
                        mViewPager.setCurrentItem(4);
                        break;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainActivity", "onStart");
        String token = getTokenFromLocal();
        if (!TextUtils.isEmpty(token)) {
            startAllRequest();
            //获取nodeServerjs服务器地址
            getNodeJsServerAddress();

        }
    }

    private void getNodeJsServerAddress() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/getInfo/getNodeJsServer");
        //params.addHeader("authorization", getTokenFromLocal());
        Log.i("MainActivity", "getNodeJsServerAddress getTokenFromLocal==>" + getTokenFromLocal());
        params.addHeader("Authorization", getTokenFromLocal());
        params.addBodyParameter("config", "sns.message.nodeserver");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("MainActivity", "getNodeJsServerAddress result==>" + result.toString());
                NodeJsServerEntity roomEntity = com.alibaba.fastjson.JSONObject.parseObject(result, NodeJsServerEntity.class);
                if (roomEntity.getResultCode() == 200) {
                    //获取讨论组列表并启动socket 聊天服务
                    String socketUrl = roomEntity.getData();
                    Log.i(TAG, "URL===" + socketUrl);
                    if (socketUrl.startsWith("http")) {
                        Log.i(TAG, "URL=http==" + true);
                        BaseConst.CHAT_SOCKET_URL = socketUrl;
                    } else if (socketUrl.startsWith("/")) {
                        Log.i(TAG, "URL=/==" + true);
                        BaseConst.CHAT_SOCKET_URL = BaseConst.CHAT_DEAULT_BASE + socketUrl;
                    }

                    initChatSocket();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "getNodeJsServerAddress err==>" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void initChatSocket() {
        // TODO:加入超时判断重连机制
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/snsgroup/getSnsGroupListByType");
        //params.addHeader("authorization", getTokenFromLocal());
        Log.i("MainActivity", "getTokenFromLocal==>" + getTokenFromLocal() + "userId==>" + SPUtils.get(MainActivity.this, PublicUrl.USER_ID_KEY, "").toString());
        params.addHeader("Authorization", getTokenFromLocal());
        params.addBodyParameter("userId", SPUtils.get(MainActivity.this, PublicUrl.USER_ID_KEY, "").toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("MainActivity", "result==>" + result.toString());
                List<String> roomIds = new ArrayList<String>();
                try {
                    RoomEntity roomEntity = com.alibaba.fastjson.JSONObject.parseObject(result, RoomEntity.class);
                    List<DataBean> list = roomEntity.getData();
                    for (DataBean data : list) {
                        roomIds.add(data.getId());
                    }
                } catch (Exception e) {
                    Log.e("MainActivity", "" + e.getMessage());
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("roomIds", (ArrayList<String>) roomIds);
                intent.putExtra(PublicUrl.TOKEN_KEY, SPUtils.get(MainActivity.this, PublicUrl.TOKEN_KEY, "").toString());
                intent.putExtra(PublicUrl.USER_ID_KEY, SPUtils.get(MainActivity.this, PublicUrl.USER_ID_KEY, "").toString());
                intent.setClass(MainActivity.this, MainService.class);
                startService(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "initChatSocket onError==>" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled");
            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");

//        getTokenFromLocal();
//        if (!TextUtils.isEmpty(token)) {
//            init();
//            setViewListener();
//            startAllRequest();
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int ringName = getIntent().getIntExtra(LoginActivity.FINISH_ALL, 0);
        if (ringName == 1) {
            finish();
        }
    }

    public void setPager() {
        if (mBannerList.size() > 0) {

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.basic_image_download_widescreen) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.basic_image_error_widescreen)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.basic_image_error_widescreen)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();

            ArrayList<ImageView> views = new ArrayList<ImageView>();
            views.clear();
            for (int i = 0; i < mBannerList.size(); i++) {
                //Log.i("test", "banner pic:" + mBannerList.get(i).pic);
                ImageView imageView = new ImageView(this);
                ImageLoader.getInstance().displayImage(mBannerList.get(i).pic, imageView, options);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                views.add(imageView);
            }
            //mBannerList.clear();
            BannerAdapter BannerAdapter = new BannerAdapter(views);
            mBannerViewPager.setAdapter(BannerAdapter);
            mBannerViewPager.setOnPageChangeListener(new MyPageChangeListener());


            mPointLayout.removeAllViews();
            for (int i = 0; i < mBannerList.size(); i++) {
                if (mBannerList.size() - 1 == 0) {
                    mPointLayout.removeAllViews();
                } else if (i == 0) {
                    pointon pointonitem = new pointon();
                    View pointonview = pointonitem.mPointon;
                    mPointLayout.addView(pointonview);

                } else {
                    pointoff pointoffitem = new pointoff();
                    View pointoffview = pointoffitem.mPointoff;
                    mPointLayout.addView(pointoffview);
                }
            }
        }
    }

    private class BannerAdapter extends PagerAdapter {

        private ArrayList<ImageView> mViews;

        public BannerAdapter(ArrayList<ImageView> views) {
            mViews = views;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {

            ((ViewPager) arg0).addView(mViews.get(arg1));
            mViews.get(arg1).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int pagernum = mBannerViewPager.getCurrentItem();
                    if (!TextUtils.isEmpty(mBannerList.get(pagernum).artid)) {
                        Intent it = new Intent(MainActivity.this, WebActivity.class);
                        it.putExtra(WebActivity.URL_ID, BaseConst.DEAULT_URL + "/mobile/news/findNewsByid?artid=" + mBannerList.get(pagernum).artid);
                        startActivity(it);
                    }
                }
            });

            return mViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    if (mBannerList.size() > 0) {
                        if (mBannerViewPager.getCurrentItem() < mBannerList.size() - 1) {
                            int n = mBannerViewPager.getCurrentItem();
                            mBannerViewPager.setCurrentItem(n + 1);
                        } else {
                            mBannerViewPager.setCurrentItem(0);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageSelected(int position) {
            mPointLayout.removeAllViews();
            for (int i = 0; i < mBannerList.size(); i++) {
                if (i == position) {
                    pointon pointonitem = new pointon();
                    View pointonview = pointonitem.mPointon;
                    mPointLayout.addView(pointonview);

                } else {
                    pointoff pointoffitem = new pointoff();
                    View pointoffview = pointoffitem.mPointoff;
                    mPointLayout.addView(pointoffview);
                }
            }
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

    }

    private class pointon {
        public View mPointon;

        public pointon() {
            mPointon = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.point_on, null);
        }
    }

    private class pointoff {
        public View mPointoff;

        public pointoff() {
            mPointoff = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.point_off, null);
        }
    }

    private void startAllRequest() {
        Log.i("test", "startAllRequest");

        //读取保存的服务器列表序号
        SharedPreferences sp = getSharedPreferences("spUrlList", Context.MODE_PRIVATE);
        int pos = sp.getInt("urlListPos", 0);

        //读取xml中服务器地址列表
        Resources res = getResources();
        String[] urlList = res.getStringArray(R.array.urls);

        //更改配置地址
        BaseConst.CHAT_DEAULT_BASE = urlList[pos];
        BaseConst.Socket_URL = urlList[pos] + "/BSCTelmed/";
        BaseConst.CHAT_PIC_URL = urlList[pos] + "/BSCTelmed";//图片地址
        BaseConst.DEAULT_URL = urlList[pos] + "/BSCTelmed";
        BaseConst.EDUCATION_URL = urlList[pos] + "/BSCEdu/";
        BaseConst.DICOMCLOUD_URL = urlList[pos] + "/DicomCloud";
        HttpReuqests.resetRequest();
        com.source.adnroid.comm.ui.net.HttpReuqests.resetRequest();
        getUserInfo();
        //getBannerData();
        getUpdate();
    }

    private void getUserInfo() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/user");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getUserInfo==>" + result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONObject k = new JSONObject(data);
                                if (k.has("userId")) {
                                    String userId = k.getString("userId");
                                    SPUtils.put(MainActivity.this, PublicUrl.USER_ID_KEY, userId);
                                    Log.i("test", "userId: " + userId);
                                    addUmengPushAlias(userId);
                                }
                                if (k.has("userName")) {
                                    String userName = k.getString("userName");
                                    Log.i("userName", userName);
                                    //mUserNameTextView.setText(userName);
                                }
                                if (k.has("siteName")) {
                                    String siteName = k.getString("siteName");
                                    //mUserSiteName.setText(siteName);
                                    Log.i("siteName", siteName);
                                }
                                if (k.has("siteId")) {
                                    String siteId = k.getString("siteId");
                                    SPUtils.put(MainActivity.this, PublicUrl.USER_SITE_ID_KEY, siteId);
                                }
                                if (k.has("departId")) {
                                    String departId = k.getString("departId");
                                    SPUtils.put(MainActivity.this, PublicUrl.USER_DEPART_ID_KEY, departId);
                                    //mUserTitleTextView.setText(departName);
                                }
                                if (k.has("jobTitle")) {

                                    //String jobTitle = mUserTitleTextView.getText().toString();
                                    //jobTitle = jobTitle + "   " + k.getString("jobTitle");

                                    //mUserTitleTextView.setText(jobTitle);
                                    //Log.i("jobTitle", jobTitle);
                                }
                                if (k.has("photo")) {
                                    String photo = BaseConst.DEAULT_URL + k.getString("photo");
                                    //ImageLoader.getInstance().displayImage(photo, mUserHeadImageView, mOptions);
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
                    Log.e(TAG, "getUserInfo JSONException:" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.e(TAG, "getUserInfo onError:" + ex.getMessage());
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

    private void getBannerData() {
        mBannerList.clear();
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/news/searchNewsList");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        params.addQueryStringParameter("pagesize", "5");
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getBanner:" + result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String array = j.getString("data");
                                JSONArray newsArray = new JSONArray(array);
                                for (int i = 0; i < newsArray.length(); i++) {
                                    JSONObject l = newsArray.getJSONObject(i);
                                    Banner item = new Banner();
                                    if (l.has("artid")) {
                                        item.artid = l.getString("artid");
                                    }
                                    if (l.has("coverimage")) {
                                        item.pic = BaseConst.DEAULT_URL + l.getString("coverimage");
                                        //Log.i("test", "json banner pic:" + item.pic);
                                    }
                                    if (l.has("title")) {
                                        item.title = l.getString("title");
                                    }
                                    mBannerList.add(item);
                                }
                                setPager();
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

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.e(TAG, "getBannerData onError:" + ex.getMessage());
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

    private void getUpdate() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/version/findupdat");
        //params.setSslSocketFactory(); // 设置ssl
        params.addHeader("authorization", getTokenFromLocal());
        //params.setConnectTimeout(2 * 1000);
        int vc = PublicUrl.getVersionCode(MainActivity.this);
        params.addQueryStringParameter("version", vc + "");
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("test", "getNewVersion" + result);
                try {
                    JSONObject j = new JSONObject(result);
                    if (j.has("resultCode")) {
                        String resultCode = j.getString("resultCode");
                        if (resultCode.equals("200")) {
                            if (j.has("data")) {
                                String data = j.getString("data");
                                JSONObject k = new JSONObject(data);
                                int nvc = 0;
                                int vc = PublicUrl.getVersionCode(MainActivity.this);
                                String url = "";
                                String text = "";
                                boolean forced = false;
                                if (k.has("version")) {
                                    nvc = k.getInt("version");
                                }
                                if (k.has("url")) {
                                    //url = "https://www.bsc1.cn" + k.getString("url");
                                    url = BaseConst.DEAULT_URL + k.getString("url");
                                }
                                if (k.has("comments")) {
                                    text = k.getString("comments").replaceAll("/n", "\n");
                                    if (TextUtils.isEmpty(text)) {
                                        text = "点击确定，开始更新";
                                    }
                                }
                                if (k.has("updateinfo")) {
                                    String updateinfo = k.getString("updateinfo");
                                    if (updateinfo.equals("1")) {
                                        forced = true;
                                    } else {
                                        forced = false;
                                    }
                                }
                                if (nvc > vc && !TextUtils.isEmpty(url)) {
                                    showUpdateDialog(url, text, forced);
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

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ex.printStackTrace();
                Log.i("onError", "getUpdate onError:" + ex.getMessage());
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


    private void showUpdateDialog(final String url, String text, boolean forced) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("发现新版本");
        builder.setMessage(text);
        if (forced) {
            builder.setCancelable(false);
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startUpdateDownload(url);
                showProgressDialog();
            }
        });
        if (!forced) {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private ProgressDialog mUpdateDialog;

    private void showProgressDialog() {
        mUpdateDialog = new ProgressDialog(MainActivity.this);
        mUpdateDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        mUpdateDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        mUpdateDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        mUpdateDialog.setIcon(R.mipmap.basic_logo);// 设置提示的title的图标，默认是没有的
        mUpdateDialog.setTitle("下载中");
        mUpdateDialog.setMax(100);
    }

    private void startUpdateDownload(String url) {
        String path = MainActivity.this.getExternalFilesDir(null).toString();
        Log.i("test", path);
        RequestParams params = new RequestParams(url);
        params.setAutoResume(true);//设置是否在下载是自动断点续传
        params.setAutoRename(false);//设置是否根据头信息自动命名文件
        params.setSaveFilePath(path + "/new.apk");
        params.setExecutor(new PriorityExecutor(2, true));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
        params.setCancelFast(true);//是否可以被立即停止.
        //下面的回调都是在主线程中运行的,这里设置的带进度的回调
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onCancelled(CancelledException arg0) {
                Log.i("test", "取消下载");
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Log.e(TAG, "startUpdateDownload onError:" + arg0.getMessage());
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "下载过程结束");
            }

            @Override
            public void onSuccess(File arg0) {
                Log.i("test", "下载成功");
                String path = MainActivity.this.getExternalFilesDir(null).toString();
                File file = new File(path + "/new.apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.chinabsc.telemedicine.expert.fileprovider", file);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.i(TAG, "文件量" + (int) total);
                Log.i(TAG, "下载量" + (int) current);
                mUpdateDialog.setMax((int) total);
                mUpdateDialog.setProgress((int) current);
            }

            @Override
            public void onStarted() {
                Log.i(TAG, "开始下载");
                mUpdateDialog.show();
            }

            @Override
            public void onWaiting() {
                Log.i(TAG, "下载等待");
            }

        });
    }

    private void addUmengPushAlias(String uid) {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.addAlias(uid, "UserId",
                new UTrack.ICallBack() {
                    @Override
                    public void onMessage(boolean isSuccess, String message) {

                    }
                });
       /* mPushAgent.addExclusiveAlias(uid, "UserId",
                new UTrack.ICallBack() {
                    @Override
                    public void onMessage(boolean isSuccess, String message) {

                    }
                });*/
    }

    private ArrayList<DateInfo> parseJson(String result) {
        ArrayList<DateInfo> itemList = new ArrayList<DateInfo>();
        try {
            JSONObject j = new JSONObject(result);
            if (j.has("resultCode")) {
                String resultCode = j.getString("resultCode");
                if (resultCode.equals("200")) {
                    if (j.has("data")) {
                        String data = j.getString("data");
                        JSONArray firstArray = new JSONArray(data);
                        itemList.clear();
                        for (int i = 0; i < firstArray.length(); i++) {
                            JSONObject k = firstArray.getJSONObject(i);
                            DateInfo item = new DateInfo();
                            if (k.has("clinicId")) {
                                item.clinicId = k.getString("clinicId");
                            }
                            if (k.has("dateDone")) {
                                item.dateDone = k.getString("dateDone");
                            }
                            if (k.has("sndSiteName")) {
                                item.sndSiteName = k.getString("sndSiteName");
                            }
                            if (k.has("doctorName")) {
                                item.doctorName = k.getString("doctorName");
                            }
                            if (k.has("patientName")) {
                                item.patientName = k.getString("patientName");
                            }
                            if (k.has("patientGender")) {
                                item.patientGender = k.getString("patientGender");
                            }
                            if (k.has("age")) {
                                item.age = k.getString("age");
                            }
                            itemList.add(item);
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
        return itemList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "MainActivity onDestroy==");
    }

}
