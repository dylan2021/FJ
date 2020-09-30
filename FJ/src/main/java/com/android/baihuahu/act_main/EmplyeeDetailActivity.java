package com.android.baihuahu.act_main;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.bean.EmplyeeAttendInfo;
import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.ScrollListView;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Dylan
 * 人员工资详情
 */

public class EmplyeeDetailActivity extends BaseFgActivity {
    private EmplyeeDetailActivity context;
    private int id;
    private EmplyeeInfo info = new EmplyeeInfo("");
    private String remark, hourNum, pieceNum, deduction;
    private double totalWage = 0;
    private ScrollListView attendLv;
    private EmplyeeDetailAttendAdapter adapter;
    List<EmplyeeAttendInfo> infos = new ArrayList<>();
    private RefreshLayout mRefreshLayout;
    private ListView processLv;
    private String name;
    private boolean isGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_emplyee_detail);

        context = this;
        id = getIntent().getIntExtra(KeyConst.id, 0);

        isGroupList = getIntent().getBooleanExtra(KeyConst.type, false);
        name = getIntent().getStringExtra(KeyConst.name);
        initTitleBackBt("人员详情");
        initView();

    }

    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        getInfoData();

        getAttendData();
    }

    private void getAttendData() {
        //公共人员==0   临聘人员==1
        String upl_p = isGroupList ? "/biz/bizWorker/attend/list?workerId=" :
                "/biz/tempWorker/attend/list?tempWorkerId=";
        String url = Constant.WEB_SITE + upl_p + id;
        Response.Listener<List<EmplyeeAttendInfo>> successListener = new Response.Listener
                <List<EmplyeeAttendInfo>>() {
            @Override
            public void onResponse(List<EmplyeeAttendInfo> result) {
                if (context == null || context.isFinishing()) {
                    return;
                }
                mRefreshLayout.finishRefresh(0);
                if (TextUtil.isEmptyList(result)) {
                    ToastUtil.show(context, "暂无考勤记录");
                    return;
                }
                adapter.setData(result);
                processLv.setSelection(0);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mRefreshLayout.finishRefresh(0);
                ToastUtil.show(context, R.string.server_exception);
            }
        };

        Request<List<EmplyeeAttendInfo>> request = new GsonRequest<List<EmplyeeAttendInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<EmplyeeAttendInfo>>() {
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

    private void getInfoData() {
        String url_p = isGroupList ? "/biz/bizWorker/" : "/biz/tempWorker/";
        String url = Constant.WEB_SITE + url_p + id;
        Response.Listener<EmplyeeInfo> successListener = new Response.Listener<EmplyeeInfo>() {
            @Override
            public void onResponse(EmplyeeInfo result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null) {
                    ToastUtil.show(context, "人员信息查询失败");
                    return;
                }
                info = result;
                setView();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(context, R.string.server_exception);
                mRefreshLayout.finishRefresh(0);
            }
        };

        Request<EmplyeeInfo> request = new GsonRequest<EmplyeeInfo>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<EmplyeeInfo>() {
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

    private void setView() {
        ((TextView) findViewById(R.id.emplyee_name_mobile_tv)).setText(TextUtil.initNamePhone(name, info.getPhone()));
        SimpleDraweeView iconIv = (SimpleDraweeView) findViewById(R.id.emplyee_icon_tv);
        iconIv.setImageURI( ImageUtil.getImageUrl(info.getPic()));

        String groupName = info.getBuildSiteName();
        ((TextView) findViewById(R.id.group_name_tv)).setText(groupName);
        ((TextView) findViewById(R.id.id_card_num_tv)).setText("身份证：" + info.getIdentityNo());
        String genderAge = "性别：" + Utils.getDictNameByValue(context, KeyConst.GENDER, info.getGender())
                + "　　年龄：" + TextUtil.remove_N(info.getAge());
        ((TextView) findViewById(R.id.age_tv)).setText(genderAge);
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        processLv = (ListView) findViewById(R.id.process_lv);
        adapter = new EmplyeeDetailAttendAdapter(context);
        processLv.setAdapter(adapter);

        Utils.setLoadHeaderFooter(context, mRefreshLayout);
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                ToastUtil.showBottom(context, getString(R.string.no_more_data));
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                //请求数据
                getData();
            }
        });

    }

}
