package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class MainHomePageAdapter extends FragmentPagerAdapter{
    private Context context;
    private List<Fragment> fragmentList;

    public MainHomePageAdapter(FragmentManager fm, Context context, List<Fragment> fragmentList) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


}
