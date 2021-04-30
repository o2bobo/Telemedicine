package com.chinabsc.telemedicine.expert.expertActivity;

import com.chinabsc.telemedicine.expert.expertFragment.ConsultationFragment;
import com.chinabsc.telemedicine.expert.expertFragment.ConsultationResultsFragment;
import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertFragment.ElectronicRecordsFragment;
import com.chinabsc.telemedicine.expert.expertFragment.MedicalRecordsFragment;
import com.chinabsc.telemedicine.expert.expertFragment.VIdeoPlayFragment;

public class TelemedicineInfoTabItem {
    public static String[] getTabsTxt() {
        String[] tabs = {"申请单", "病历信息", "病历附件", "视频诊室", "会诊报告"};
        return tabs;
    }

    public static int[] getTabsImg() {
        int[] ids = {R.mipmap.f1_hzd, R.mipmap.f2_dzbl, R.mipmap.f3_blzl, R.mipmap.f4_spzs, R.mipmap.f5_hzbg};
        //int[] ids = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        return ids;
    }

    public static int[] getTabsImgLight() {
        int[] ids = {R.mipmap.f1_hzd_light, R.mipmap.f2_dzbl_light, R.mipmap.f3_blzl_light, R.mipmap.f4_spzs_light, R.mipmap.f5_hzbg_light};
        //int[] ids = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        return ids;
    }

    public static Class[] getFragments() {
        Class[] clz = {ConsultationFragment.class, ElectronicRecordsFragment.class, MedicalRecordsFragment.class, VIdeoPlayFragment.class, ConsultationResultsFragment.class};
        return clz;
    }

    public static void delTab() {

    }
}
