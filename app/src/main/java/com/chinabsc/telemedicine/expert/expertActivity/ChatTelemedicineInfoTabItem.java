package com.chinabsc.telemedicine.expert.expertActivity;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertFragment.ConsultationFragment;
import com.chinabsc.telemedicine.expert.expertFragment.ConsultationResultsFragment;
import com.chinabsc.telemedicine.expert.expertFragment.ElectronicRecordsFragment;
import com.chinabsc.telemedicine.expert.expertFragment.MedicalRecordsFragment;
import com.chinabsc.telemedicine.expert.expertFragment.VIdeoPlayFragment;

public class ChatTelemedicineInfoTabItem {
    public static String[] getTabsTxt() {
        String[] tabs = {"申请单", "电子病历", "病历附件"};
        return tabs;
    }

    public static int[] getTabsImg() {
        int[] ids = {R.mipmap.f1_hzd, R.mipmap.f2_dzbl, R.mipmap.f3_blzl};
        //int[] ids = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        return ids;
    }

    public static int[] getTabsImgLight() {
        int[] ids = {R.mipmap.f1_hzd_light, R.mipmap.f2_dzbl_light, R.mipmap.f3_blzl_light};
        //int[] ids = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        return ids;
    }

    public static Class[] getFragments() {
        Class[] clz = {ConsultationFragment.class, ElectronicRecordsFragment.class, MedicalRecordsFragment.class};
        return clz;
    }
}
