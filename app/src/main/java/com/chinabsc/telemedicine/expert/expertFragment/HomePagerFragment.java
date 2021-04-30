package com.chinabsc.telemedicine.expert.expertFragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.DataBean;
import com.chinabsc.telemedicine.expert.entity.RoomEntity;
import com.chinabsc.telemedicine.expert.expertActivity.AddConsultationActivity;
import com.chinabsc.telemedicine.expert.expertActivity.AddDiagnosisActivity;
import com.chinabsc.telemedicine.expert.expertActivity.BilateralDiagnosisActivity;
import com.chinabsc.telemedicine.expert.expertActivity.MyOutpatientActivity;
import com.chinabsc.telemedicine.expert.expertActivity.MyVisitActivity;
import com.chinabsc.telemedicine.expert.expertActivity.RecordsActivity;
import com.chinabsc.telemedicine.expert.expertActivity.SchedulingListActivity;
import com.chinabsc.telemedicine.expert.expertActivity.SettingActivity;
import com.chinabsc.telemedicine.expert.expertActivity.StatisticsInfoActivity;
import com.chinabsc.telemedicine.expert.expertActivity.TelemedicineActivity;
import com.chinabsc.telemedicine.expert.expertActivity.WebActivity;
import com.chinabsc.telemedicine.expert.utils.PublicUrl;
import com.chinabsc.telemedicine.expert.utils.SPUtils;
import com.chinabsc.telemedicine.expert.utils.T;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.source.android.chatsocket.service.MainService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePagerFragment extends BaseLazyFragment {
    //View view;
    private ImageView mSettingImageView;
    LinearLayout mTelemedicineLayout;
    LinearLayout mAddConsultationLayout;
    LinearLayout mConsultationRecordsLayout;

    LinearLayout mSchedulingListLayout;
    LinearLayout mMyOutpatientLayout;
    LinearLayout mMyVisitLayout;

    LinearLayout mMainHuizhenstatics;
    LinearLayout mMainMenZhenstatics;

    LinearLayout mAddDiagnosis;
    LinearLayout mBilateralDiagnosis;
    LinearLayout mBilateralDiagnosis2;
    LinearLayout mDiagnosisStatistics;

    //banner
    private ViewPager mBannerViewPager;
    private Timer timer = new Timer();
    public static final int UPTATE_VIEWPAGER = 0x123;
    private int mCurrentPicItem = 0;
    private ArrayList<Banner> mBannerList = new ArrayList<Banner>();
    private LinearLayout mPointLayout;

    public class Banner {
        public String artid = "";
        public String pic = "";
        public String title = "";
    }

    public HomePagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("HomePagerFragment","HomePagerFragment onCreateView");
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_main_material2, container, false);
        }
        mBannerViewPager = rootView.findViewById(R.id.BannerViewPager);
        mPointLayout = rootView.findViewById(R.id.PointLayout);

        mSettingImageView = rootView.findViewById(R.id.SettingImageView);
        mSettingImageView.setOnClickListener(new HomeOnClickListener());
        mTelemedicineLayout = rootView.findViewById(R.id.TelemedicineLayout);
        mTelemedicineLayout.setOnClickListener(new HomeOnClickListener());

        mAddConsultationLayout = rootView.findViewById(R.id.AddConsultationLayout);
        mAddConsultationLayout.setOnClickListener(new HomeOnClickListener());
        mConsultationRecordsLayout = rootView.findViewById(R.id.ConsultationRecordsLayout);
        mConsultationRecordsLayout.setOnClickListener(new HomeOnClickListener());

        mSchedulingListLayout = rootView.findViewById(R.id.SchedulingListLayout);
        mSchedulingListLayout.setOnClickListener(new HomeOnClickListener());
        mMyOutpatientLayout = rootView.findViewById(R.id.MyOutpatientLayout);
        mMyOutpatientLayout.setOnClickListener(new HomeOnClickListener());
        mMyVisitLayout = rootView.findViewById(R.id.MyVisitLayout);
        mMyVisitLayout.setOnClickListener(new HomeOnClickListener());

        mMainHuizhenstatics = rootView.findViewById(R.id.main_huizhenstatics);
        mMainHuizhenstatics.setOnClickListener(new HomeOnClickListener());
        mMainMenZhenstatics = rootView.findViewById(R.id.main_menzhenstatics);
        mMainMenZhenstatics.setOnClickListener(new HomeOnClickListener());

        mAddDiagnosis = rootView.findViewById(R.id.AddDiagnosisLayout);
        mAddDiagnosis.setOnClickListener(new HomeOnClickListener());

        mBilateralDiagnosis = rootView.findViewById(R.id.BilateralDiagnosisLayout);
        mBilateralDiagnosis.setOnClickListener(new HomeOnClickListener());

        mBilateralDiagnosis2 = rootView.findViewById(R.id.BilateralDiagnosis2Layout);
        mBilateralDiagnosis2.setOnClickListener(new HomeOnClickListener());

        mDiagnosisStatistics = rootView.findViewById(R.id.DiagnosisStatisticsLayout);
        mDiagnosisStatistics.setOnClickListener(new HomeOnClickListener());

        getBannerData();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("HomePagerFragment","HomePagerFragment onDestroyView");
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
    }

    class HomeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent;
            switch (v.getId()) {
                case R.id.SettingImageView:
                    intent = new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.TelemedicineLayout:
                    intent = new Intent(getActivity(), TelemedicineActivity.class);
                    startActivity(intent);
                    break;

                case R.id.AddConsultationLayout:
                    intent = new Intent(getActivity(), AddConsultationActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ConsultationRecordsLayout:
                    intent = new Intent(getActivity(), RecordsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.SchedulingListLayout:
                    intent = new Intent(getActivity(), SchedulingListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.MyOutpatientLayout:
                    intent = new Intent(getActivity(), MyOutpatientActivity.class);
                    startActivity(intent);
                    break;
                case R.id.MyVisitLayout:
                    intent = new Intent(getActivity(), MyVisitActivity.class);
                    startActivity(intent);
                    break;

                case R.id.main_huizhenstatics:
                    intent = new Intent(getActivity(), StatisticsInfoActivity.class);
                    intent.putExtra("type", "1");
                    startActivity(intent);
                    break;
                case R.id.main_menzhenstatics:
                    intent = new Intent(getActivity(), StatisticsInfoActivity.class);
                    intent.putExtra("type", "2");
                    startActivity(intent);
                    break;
                case R.id.AddDiagnosisLayout:
                    intent = new Intent(getActivity(), AddDiagnosisActivity.class);
                    startActivity(intent);
                    break;
                case R.id.BilateralDiagnosisLayout:
                    intent = new Intent(getActivity(), BilateralDiagnosisActivity.class);
                    intent.putExtra("type", "上转诊");
                    startActivity(intent);
                    break;
                case R.id.BilateralDiagnosis2Layout:
                    intent = new Intent(getActivity(), BilateralDiagnosisActivity.class);
                    intent.putExtra("type", "下转诊");
                    startActivity(intent);
                    break;
                case R.id.DiagnosisStatisticsLayout:
                    intent = new Intent(getActivity(), StatisticsInfoActivity.class);
                    intent.putExtra("type", "3");
                    startActivity(intent);
                    break;
            }
        }
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
                            T.showMessage(getActivity(), getString(R.string.login_timeout));
                            delToken();
                            doLogout();
                        } else {
                            T.showMessage(getActivity(), getString(R.string.api_error) + resultCode);
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
                Log.e("test", "getBannerData onError:" + ex.getMessage());
                T.showMessage(getActivity(), getString(R.string.server_error));
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
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
            ArrayList<ImageView> views = new ArrayList<ImageView>();
            views.clear();
            for (int i = 0; i < mBannerList.size(); i++) {
                //Log.i("test", "banner pic:" + mBannerList.get(i).pic);
                ImageView imageView = new ImageView(getActivity());
                imageLoader.displayImage(mBannerList.get(i).pic, imageView, options);
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
                        Intent it = new Intent(getActivity(), WebActivity.class);
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
            mPointon = LayoutInflater.from(getActivity()).inflate(
                    R.layout.point_on, null);
        }
    }

    private class pointoff {
        public View mPointoff;

        public pointoff() {
            mPointoff = LayoutInflater.from(getActivity()).inflate(
                    R.layout.point_off, null);
        }
    }

    private void initChatSocket() {
        // TODO:加入超时判断重连机制
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/snsgroup/getSnsGroupListByType");
        //params.addHeader("authorization", getTokenFromLocal());
        Log.i("MainActivity", "getTokenFromLocal==>" + getTokenFromLocal() + "userId==>" + SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString());
        params.addHeader("Authorization", getTokenFromLocal());
        params.addBodyParameter("userId", SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString());
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
                intent.putExtra(PublicUrl.TOKEN_KEY, SPUtils.get(getActivity(), PublicUrl.TOKEN_KEY, "").toString());
                intent.putExtra(PublicUrl.USER_ID_KEY, SPUtils.get(getActivity(), PublicUrl.USER_ID_KEY, "").toString());
                intent.setClass(getActivity(), MainService.class);
                getActivity().startService(intent);
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
}
