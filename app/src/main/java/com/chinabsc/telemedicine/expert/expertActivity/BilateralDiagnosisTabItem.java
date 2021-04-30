package com.chinabsc.telemedicine.expert.expertActivity;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.expertFragment.BilateralDiagnosisElectronicFragment;
import com.chinabsc.telemedicine.expert.expertFragment.BilateralDiagnosisFormFragment;
import com.chinabsc.telemedicine.expert.expertFragment.BilateralDiagnosisMedicalFragment;

public class BilateralDiagnosisTabItem {
    public static String[] getTabsTxt() {
        String[] tabs = {"申请单", "电子病历", "病历附件"};
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
        Class[] clz = {BilateralDiagnosisFormFragment.class, BilateralDiagnosisElectronicFragment.class, BilateralDiagnosisMedicalFragment.class};
        return clz;
    }

    public static void delTab() {

    }
}
