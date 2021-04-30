package com.chinabsc.telemedicine.expert.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Base64;

import com.source.android.chatsocket.entity.SocketConst;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;


public class PublicUrl {
    public static final String TOKEN_KEY = "TOKEN_KEY";
    public static final String USER_NAME_KEY = "USER_NAME_KEY";
    public static final String USER_ID_KEY = "USER_ID_KEY";
    public static final String USER_SITE_ID_KEY = "USER_SITE_ID_KEY";
    public static final String USER_DEPART_ID_KEY = "USER_DEPART_ID_KEY";

    //获取当前时间时间戳
    public static Long getNowTime() {
        return new Date().getTime();
    }

    //根据当前时间转换为星期
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String stampToDate(int go) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, go);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();   //这个时间就是日期往后推一天的结果
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String res = sdf.format(date);
        return res;
    }

    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String getFinalPassword(String password) {
        password = getMD5(password);
        password = getBase64(password);
        return password;
    }

    public static String getMD5(String password) {
        String result = password;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //md5_32=result;
            //md5_16= buf.toString().substring(8, 24);
            result = buf.toString().substring(8, 24);
            return result;
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String getBase64(String password) {
        password = Base64.encodeToString(password.getBytes(), Base64.NO_WRAP);
        return password;
    }

    public static String getFromBase64(String base) {
        String str = new String(Base64.decode(base.getBytes(), Base64.DEFAULT));
        return str;
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);

        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else
                return false;
        }
        return true;
    }

    public static void setSpeakerphoneOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
    }

    //验证身份证
    public static boolean isIDCard(String idCard) {
        final String REGEX_ID_CARD = "((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65)[0-9]{4})" +
                "(([1|2][0-9]{3}[0|1][0-9][0-3][0-9][0-9]{3}" +
                "[Xx0-9])|([0-9]{2}[0|1][0-9][0-3][0-9][0-9]{3}))";
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

}
