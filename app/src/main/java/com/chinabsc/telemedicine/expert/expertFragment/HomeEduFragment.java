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

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.myAdapter.MainNewsPageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeEduFragment extends BaseLazyFragment {
    TabLayout eduTab;
    ViewPager eduPager;
    List<Fragment> fragmentList;
    List<String> list_Title;

    public HomeEduFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home_edu, container, false);
        }
        eduTab = rootView.findViewById(R.id.main_edu_tab);
        eduPager = rootView.findViewById(R.id.main_edu_viewpager);

        return rootView;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        Log.i("HomeEduFragment","HomeEduFragment visi=="+isVisible);
        if (isVisible) {
            initView();
        } else {

        }
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        list_Title = new ArrayList<>();
        list_Title.add("直播中");
        EduItemFragment playingItem = new EduItemFragment();
        Bundle playingB = new Bundle();
        playingB.putString(EduItemFragment.EDU_TYPE, "playing");
        playingItem.setArguments(playingB);
        fragmentList.add(playingItem);
        list_Title.add("等待中");
        EduItemFragment waitingItem = new EduItemFragment();
        Bundle waitingB = new Bundle();
        waitingB.putString(EduItemFragment.EDU_TYPE, "preparing");
        waitingItem.setArguments(waitingB);
        fragmentList.add(waitingItem);
        list_Title.add("已结束");
        EduItemFragment stoppedItem = new EduItemFragment();
        Bundle stoppedB = new Bundle();
        stoppedB.putString(EduItemFragment.EDU_TYPE, "stopped");
        stoppedItem.setArguments(stoppedB);
        fragmentList.add(stoppedItem);
        list_Title.add("全部课程");
        EduItemFragment allItem = new EduItemFragment();
        Bundle allB = new Bundle();
        allB.putString(EduItemFragment.EDU_TYPE, "all");
        allItem.setArguments(allB);
        fragmentList.add(allItem);
        eduTab.setTabMode(TabLayout.MODE_FIXED);
        eduPager.setAdapter(new MainNewsPageAdapter(getChildFragmentManager(), getActivity(), fragmentList, list_Title));
        eduTab.setupWithViewPager(eduPager);
    }
}
