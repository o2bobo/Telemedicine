package com.source.adnroid.comm.ui.chatutils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by zzw on 2018/4/18.
 */

public class ChatSerciceUtils {
    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
