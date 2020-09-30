package com.android.baihuahu.util;

import android.util.Log;

import com.android.baihuahu.App;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.BillTreeInfo;
import com.android.baihuahu.bean.DeptInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 时间类型的转换
 */
public class TimeUtils {

    public static float getDiffAtendDay(long startTime, long endTime) {
        int hours = Integer.valueOf(getAttendTotalPeroid(startTime, endTime));//请假时长
        long attendHours = getAttendHours(startTime, endTime);//设置的上下班时长

        long remainderHours = hours % attendHours;
        float remainderDay = remainderHours == 0 ? 0 :
                remainderHours <= (attendHours / 2) ? 0.5f : 1;
        float day = hours / attendHours + remainderDay;
        return day;
    }

    public static String getTimeYm(long time) {
        if (time == 0) {
            return "";
        }
        sdf_Ym.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf_Ym.format(new Date(time));
    }

    public static String getTimeYmd(long time) {
        if (time == 0) {
            return "";
        }
        sdf_Ymd.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf_Ymd.format(new Date(time));
    }

    private static SimpleDateFormat sdf_YmdHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf_YmdHm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat sdf_Ymd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf_Ym = new SimpleDateFormat("yyyy-MM");

    public static String getTimeYmdHm(long time) {
        if (time == 0) {
            return "";
        }
        sdf_YmdHm.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf_YmdHm.format(new Date(time));
    }

    public static String getTimeYmdHms(long time) {
        if (time == 0) {
            return "";
        }
        sdf_YmdHms.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf_YmdHms.format(new Date(time));
    }

    public static String getSpecifiedDayBefore(String specifiedDay, int i) {

        try {
            Calendar c = Calendar.getInstance();
            Date date = null;
            try {
                date = sdf_YmdHm.parse(specifiedDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.setTime(date);
            int day = c.get(Calendar.DATE);
            c.set(Calendar.DATE, day - i);
            String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            return dayBefore;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 上班时长   8小时
     */
    public static long getAttendHours(long startTime, long endTime) {
        if (startTime == 0 || endTime == 0) {
            return 0;
        }
        Date startData = new Date(startTime);
        Date endDate = new Date(endTime);

        long startAmTime = getAmPmTime(startData, App.amTime);
        long startPmTime = getAmPmTime(startData, App.pmTime);

        long attendHours8 = getDiffHours8(startPmTime, startAmTime);
        return attendHours8;
    }

    /**
     * 考勤时长
     */
    public static String getAttendTotalPeroid(long startTime, long endTime) {
        if (startTime == 0 || endTime == 0) {
            return "";
        }
        Date startData = new Date(startTime);
        Date endDate = new Date(endTime);

        long startZeroTime = getTodayZeroTime(startData);
        long endZeroTime = getTodayZeroTime(endDate);

        int differentDays = getDifferentDays(startZeroTime, endZeroTime);

        long startAmTime = getAmPmTime(startData, App.amTime);
        long startPmTime = getAmPmTime(startData, App.pmTime);

        long attendHours8 = getDiffHours8(startPmTime, startAmTime);//上班时长

        long TOTAL_HOURS = differentDays * attendHours8;

        long endAmTime = getAmPmTime(endDate, App.amTime);
        long endPmTime = getAmPmTime(endDate, App.pmTime);

        //加上结束的时间
        if (endTime > endAmTime) {
            long diffHours = getDiffHours(endTime, endAmTime);
            TOTAL_HOURS = TOTAL_HOURS + (endTime < endPmTime ? diffHours : attendHours8);
        }

        //开始时间  大于当天的上班时间
        if (startTime > startAmTime) {
            //没有下班?减去当天上班时间:下班了,把多加的一天减去(因为从0点开始计算)
            long diffHours = getDiffHours(startTime, startAmTime);
            TOTAL_HOURS = TOTAL_HOURS - (startTime < startPmTime ? diffHours : attendHours8);
        }


        return TOTAL_HOURS + "";
    }

    public static long getDiffHours8(long endTime, long startTime) {
        long oneHour = 1000 * 60 * 60;
        // 获得两个时间的毫秒时间差异
        long totalMillis = endTime - startTime;
        long hour = totalMillis / oneHour;
        return hour;
    }

    public static long getDiffHours1(long endTime, long startTime) {
        long oneHour = 1000 * 60 * 60;
        // 获得两个时间的毫秒时间差异
        long totalMillis = endTime - startTime;
        Log.d("时间计算", "结束大于结束上班时间-毫秒值:" + totalMillis / oneHour);
        long hour = totalMillis / oneHour + (totalMillis % oneHour > 0 ? 1 : 0);
        return hour;
    }

    public static long getDiffHours(long endTime, long startTime) {
        long oneHour = 1000 * 60 * 60;
        // 获得两个时间的毫秒时间差异
        long totalMillis = endTime - startTime;
        Log.d("时间计算", "结束大于结束上班时间-毫秒值:" + totalMillis);
        long hour = totalMillis / oneHour;
        return hour;
    }

    private static long getAmPmTime(Date amPmDate, String amPm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(amPmDate);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(amPm.substring(0, 2)));
        calendar.set(Calendar.MINUTE, Integer.valueOf(amPm.substring(3, 5)));
        return calendar.getTime().getTime();
    }

    public static int getDifferentDays(long startTime, long endTime) {
        int days = (int) ((endTime - startTime) / (1000 * 3600 * 24));
        return days;
    }

    public static String betweenHours(long startTime, long endTime) {

        if (startTime == 0 || endTime == 0) {
            return "";
        }
        //小时差
        Date fromDate2 = new Date(startTime);
        Date toDate2 = new Date(endTime);
        long between = (toDate2.getTime() - fromDate2.getTime()) / 1000;//秒
        //long  miao=between/1000;//除以1000是为了转换成秒

        long hour = between / (60 * 60);  //多少小时
        long min = (between % (60 * 60)) / 60;  //还剩多少分

        return (hour == 0 ? "" : hour + "时") + (min == 0 ? "" : min + "分");
    }

    public static SimpleDateFormat getFormatYmdHm() {
        return sdf_YmdHm;
    }

    public static SimpleDateFormat getFormatYmdHms() {
        return sdf_YmdHms;
    }

    public static SimpleDateFormat getFormatYmd() {
        return sdf_Ymd;
    }

    public static long getTodayZeroTime() {
        Date date = new Date();
        long l = 24 * 60 * 60 * 1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000);
    }

    public static long getTodayZeroTime(Date curDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long date = calendar.getTime().getTime();
        return date;
    }

    //计算两个字符串日期之间的天数
    public static long getBetweenDays(String startDateStr, String endDateStr) {
        if (TextUtil.isEmpty(startDateStr) || TextUtil.isEmpty(endDateStr)) {
            return 0;
        }
        Calendar calendar_a = Calendar.getInstance();// 获取日历对象
        Calendar calendar_b = Calendar.getInstance();
        Date date_a = null;
        Date date_b = null;

        try {
            date_a = sdf_Ymd.parse(startDateStr);//字符串转Date
            date_b = sdf_Ymd.parse(endDateStr);
            calendar_a.setTime(date_a);// 设置日历
            calendar_b.setTime(date_b);
        } catch (ParseException e) {
            e.printStackTrace();//格式化异常
        }

        long time_a = calendar_a.getTimeInMillis();
        long time_b = calendar_b.getTimeInMillis();

        long between_days = (time_b - time_a) / (1000 * 3600 * 24);//计算相差天数

        return between_days + 1;
    }
}
