package com.android.baihuahu.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.DictInfo;
import com.android.baihuahu.bean.GroupInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * 工具类
 */
public class Utils {
    public static void callPhone(FragmentActivity content, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        content.startActivity(intent);
    }

    /**
     * 调用第三方浏览器打开
     *
     * @param context
     * @param url     要浏览的资源地址
     */
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            ToastUtil.show(context, "请下载浏览器");
        }
    }

    public static void setLoadHeaderFooter(Activity context, RefreshLayout refreshLayout) {
        refreshLayout.setPrimaryColors(Color.WHITE);
        // Header
        final ClassicsHeader header = new ClassicsHeader(context);
        header.setTextSizeTitle(14);
        TextView headerLastUpdateTv = header.getLastUpdateText();
        TextView titleText = header.getTitleText();
        titleText.setVisibility(View.GONE);
        headerLastUpdateTv.setVisibility(View.GONE);
        header.setDrawableMarginRightPx(-26);
        header.setDrawableArrowSizePx(40);
        header.setDrawableProgressSizePx(60);
        header.setDrawableSizePx(40);
        header.setEnableLastTime(false);
        refreshLayout.setRefreshHeader(header, ImageUtil.getScreenWidth(context), 100);
        // Footer
        ClassicsFooter footer = new ClassicsFooter(context);
        footer.setTextSizeTitle(14);
        footer.setDrawableArrowSizePx(40);
        footer.setDrawableProgressSizePx(60);
        footer.setDrawableSizePx(40);
        refreshLayout.setRefreshFooter(footer, ImageUtil.getScreenWidth(context), 100);
        refreshLayout.setEnableFooterFollowWhenLoadFinished(true);
    }

    public static void setIndicator(final TabLayout tabLayout, final int leftRightMargin) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                    mTabStripField.setAccessible(true);

                    LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = leftRightMargin;
                        params.rightMargin = leftRightMargin;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static int getStatusColor(Context context, int status) {
        int[] statusColorArr = {0, R.color.status_waiting, R.color.status_auditing,
                R.color.status_passed,
                R.color.status_rejuct, R.color.status_back, R.color.status_deleted};
        return ContextCompat.getColor(context,
                (status > statusColorArr.length - 1 ? 0 : statusColorArr[status]));
    }


    public static String getStatusText(int status) {
        String[] statusArr = {"", "等待发起", "正在审核", "审核通过",
                "驳回申请", "撤销", "废除"};
        return status > statusArr.length - 1 ? "" : statusArr[status];
    }

    public static String getPlanStatusText(int status) {
        String[] statusArr = {"", "进行中", "已完成", "已超时",};
        return status > statusArr.length - 1 ? "" : statusArr[status];
    }

    public static String getWageStatusText(int status) {
        String[] statusArr = {"未确认", "已确认",};
        return status > statusArr.length - 1 ? "" : statusArr[status];
    }

    public static int getWageStatusColor(Context context, int status) {
        int[] statusColorArr = {R.color.status_waiting, R.color.status_passed,};
        return ContextCompat.getColor(context,
                (status > statusColorArr.length - 1 ? 0 : statusColorArr[status]));
    }

    public static String getCheckStatusText(int status) {
        String[] statusArr = {"其他","合格", "不合格"};
        return status > statusArr.length - 1 ? "" : statusArr[status];
    }

    public static int getSignerStatusColor(Context context, int status) {
        int[] statusColorArr = {R.color.status_waiting, R.color.status_passed,R.color.status_rejuct};
        return ContextCompat.getColor(context,
                (status > statusColorArr.length - 1 ? 0 : statusColorArr[status]));
    }
    public static int getCheckStatusColor(Context context, int status) {
        int[] statusColorArr = {R.color.status_waiting, R.color.status_passed,R.color.status_rejuct};
        return ContextCompat.getColor(context,
                (status > statusColorArr.length - 1 ? 0 : statusColorArr[status]));
    }

    public static int getPlanStatusColor(Context context, int status) {
        int[] statusColorArr = {0, R.color.status_waiting, R.color.status_passed,
                R.color.status_rejuct};
        return ContextCompat.getColor(context,
                (status > statusColorArr.length - 1 ? 0 : statusColorArr[status]));
    }

    public static String getObjStr(JsonObject object, String key) {
        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull()) {
            return "";
        }
        String asString = element.getAsString();
        if (asString.contains(".") && asString.length() > 16 && asString.contains("E+")) {
            asString = new BigDecimal(asString).toPlainString().substring(0, 16);
        }
        return asString;
    }

    public static List<DictInfo.DictValuesBean> getDictListByType(BaseFgActivity context, String DICT_TYPE) {
        List<DictInfo.DictValuesBean> dictValues = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        String dictArr = sp.getString(KeyConst.DICT_ARRAY, "");
        List<DictInfo> dictList = new Gson().fromJson(dictArr, new TypeToken<List<DictInfo>>() {
        }.getType());

        if (dictList != null) {
            for (DictInfo dictInfo : dictList) {
                String code = dictInfo.getCode();
                if (code.equals(DICT_TYPE)) {
                    dictValues = dictInfo.getDictValues();
                    break;
                }
            }
        }
        return dictValues;
    }

    public static String getDictNameByValue(BaseFgActivity context, String DICT_TYPE, String typeValue) {
        List<DictInfo.DictValuesBean> dictValues = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        String dictArr = sp.getString(KeyConst.DICT_ARRAY, "");
        List<DictInfo> dictList = new Gson().fromJson(dictArr, new TypeToken<List<DictInfo>>() {
        }.getType());

        if (dictList != null) {
            for (DictInfo dictInfo : dictList) {
                String code = dictInfo.getCode();
                if (code.equals(DICT_TYPE)) {
                    dictValues = dictInfo.getDictValues();
                    break;
                }
            }
        }
        if (dictValues != null) {
            for (DictInfo.DictValuesBean dictValue : dictValues) {
                String value = dictValue.getValue();
                if (value.equals(typeValue)) {
                    return dictValue.getName();
                }
            }

        }

        return "男";
    }

    public static void requestDictData(final BaseFgActivity context) {
        String url = Constant.WEB_SITE + "/dict/dicts/all?status=1";
        Response.Listener<JsonArray> successListener = new Response
                .Listener<JsonArray>() {
            @Override
            public void onResponse(JsonArray result) {
                if (result != null) {
                    SharedPreferences sp = context.getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
                    sp.edit().putString(KeyConst.DICT_ARRAY, result.toString()).commit();
                }
            }
        };

        Request<JsonArray> versionRequest = new
                GsonRequest<JsonArray>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("字典异常", "");
                    }
                }, new TypeToken<JsonArray>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    public static void sortList(List<GroupInfo> parentList) {
        Collections.sort(parentList, new Comparator<GroupInfo>() {
            @Override
            public int compare(GroupInfo bean1, GroupInfo bean2) {
                return Integer.valueOf(bean1.getOrderBy()).compareTo(Integer.valueOf(bean2.getOrderBy()));
            }
        });
    }
}
