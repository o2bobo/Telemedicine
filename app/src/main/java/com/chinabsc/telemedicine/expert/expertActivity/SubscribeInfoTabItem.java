package com.chinabsc.telemedicine.expert.expertActivity;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertFragment.SubscribeFormFragment;
import com.chinabsc.telemedicine.expert.expertFragment.SubscribeMedicalRecordsFragment;
import com.chinabsc.telemedicine.expert.expertFragment.SubscribeResultsFragment;

public class SubscribeInfoTabItem {
    public static String[] getTabsTxt() {
        String[] tabs = {"申请单", "病历附件", "门诊报告"};
        return tabs;
    }

    public static int[] getTabsImg() {
        int[] ids = {R.mipmap.f1_hzd, R.mipmap.f3_blzl, R.mipmap.f5_hzbg};
        //int[] ids = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        return ids;
    }

    public static int[] getTabsImgLight() {
        int[] ids = {R.mipmap.f1_hzd_light, R.mipmap.f3_blzl_light, R.mipmap.f5_hzbg_light};
        //int[] ids = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        return ids;
    }

    public static Class[] getFragments() {
        Class[] clz = {SubscribeFormFragment.class, SubscribeMedicalRecordsFragment.class, SubscribeResultsFragment.class};
        return clz;
    }

    public static void delTab() {

    }
}
