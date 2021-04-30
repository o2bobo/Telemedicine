package com.chinabsc.telemedicine.expert.expertFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.bsc.chat.commenbase.BaseConst;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.home.MainNewsEntity;
import com.chinabsc.telemedicine.expert.myAdapter.MainNewsPageAdapter;
import com.chinabsc.telemedicine.expert.utils.T;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNewsFragment extends BaseLazyFragment {
   // View view;
    TabLayout newwTab;
    ViewPager viewPager;
    List<Fragment> fragmentList;
    List<String> list_Title;

    public HomeNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home_news, container, false);

        }
        newwTab = rootView.findViewById(R.id.main_news_tab);
        viewPager = rootView.findViewById(R.id.main_new_viewpager);
        return rootView;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            getTabData();
        } else {

        }
    }

    private void initView(MainNewsEntity entity) {
        fragmentList = new ArrayList<>();
        list_Title = new ArrayList<>();
        if (entity.getData().size() > 3) {
            newwTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            newwTab.setTabMode(TabLayout.MODE_FIXED);
        }
        for (int i = 0; i < entity.getData().size(); i++) {
            list_Title.add(entity.getData().get(i).getColname());
            NewsFragment f = new NewsFragment();
            Bundle b = new Bundle();
            b.putString(NewsFragment.TAB_ID_KEY, entity.getData().get(i).getColid());
            f.setArguments(b);
            fragmentList.add(f);
        }
        viewPager.setAdapter(new MainNewsPageAdapter(getChildFragmentManager(), getActivity(), fragmentList, list_Title));
        newwTab.setupWithViewPager(viewPager);
    }

    private void getTabData() {
        RequestParams params = new RequestParams(BaseConst.DEAULT_URL + "/mobile/news/findNewsList");
        params.addHeader("authorization", getTokenFromLocal());
        Callback.Cancelable cancelable;
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "getTabData" + result);
                MainNewsEntity entity = JSONObject.parseObject(result, MainNewsEntity.class);
                if (entity != null) {
                    initView(entity);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError:" + ex.getMessage());
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
}
