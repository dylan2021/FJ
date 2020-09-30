package com.android.baihuahu.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载工具fi
 */
public class BillTreeUtil {
    /**
     * 台账章节
     */
    public static void initBillTreeData(final BaseFgActivity context) {
        String url = Constant.WEB_SITE + "/biz/bizBill/billTree/" + App.projecId;
        Response.Listener<List<BillTreeInfo>> successListener = new Response.Listener
                <List<BillTreeInfo>>() {
            @Override
            public void onResponse(List<BillTreeInfo> billList) {
                if (context == null || TextUtil.isEmptyList(billList) ||
                        TextUtil.isEmptyList(billList.get(0).children)) {
                    ToastUtil.show(context, "台账章节数据为空");
                    return;
                }
                App.list1.clear();
                App.list2.clear();
                App.list3.clear();

                App.billIdTreeList = billList.get(0).children;
                for (DeptInfo billInfo_1 : billList.get(0).children) {
                    //第一层
                    ArrayList list11 = new ArrayList();
                    ArrayList<List<String>> list22 = new ArrayList();
                    List<DeptInfo.ChildrenBeanX> children1 = billInfo_1.getChildren();
                    if (!TextUtil.isEmptyList(children1)) {
                        for (DeptInfo.ChildrenBeanX child : children1) {
                            //第二层
                            list11.add(child.getName());
                            List<String> list33 = new ArrayList();
                            List<DeptInfo.ChildrenBeanX.ChildrenBean> children2 = child.getChildren();
                            if (!TextUtil.isEmptyList(children2)) {
                                for (DeptInfo.ChildrenBeanX.ChildrenBean c2 : children2) {
                                    list33.add(c2.getName());
                                }
                            }
                            list22.add(list33);
                        }
                    } else {
                        list11.add("");
                        list22.add(new ArrayList<String>());
                    }
                    App.list1.add(billInfo_1.getName());
                    App.list2.add(list11);
                    App.list3.add(list22);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };

        Request<List<BillTreeInfo>> request = new GsonRequest<List<BillTreeInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<BillTreeInfo>>() {
        }.getType()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                params.put(KeyConst.projectId, App.projecId);
                return params;
            }
        };
        App.requestQueue.add(request);
    }
}