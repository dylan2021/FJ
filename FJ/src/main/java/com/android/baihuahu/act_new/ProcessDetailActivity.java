package com.android.baihuahu.act_new;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.bean.RecordInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 * 附件
 */

public class ProcessDetailActivity extends BaseFgActivity {

    private ListView processLv;
    private ProcessDetailActivity context;
    private ProcessDetailRecordAdapter adapter;
    private int planId;
    private String pName;
    private RefreshLayout mRefreshLayout;
    private double stageProgress;
    private int thirdBillId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_process_detail);
        context = this;
        planId = getIntent().getIntExtra(KeyConst.id, 0);
        pName = getIntent().getStringExtra(KeyConst.title);
        stageProgress = getIntent().getDoubleExtra(KeyConst.stageProgress, 0);
        initTitleBackBt(pName);

        initAttendView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mRefreshLayout.autoRefresh();
    }

    private void initAttendView() {
        Button addAttend = findViewById(R.id.audit_bt_3);
        addAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进度汇报
                Intent intent = new Intent();
                intent.setClass(context, PlanProcessAddActivity.class);
                intent.putExtra(KeyConst.title, pName);
                intent.putExtra(KeyConst.id, planId);
                intent.putExtra(KeyConst.thirdBillId, thirdBillId);
                context.startActivity(intent);

            }
        });

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        adapter = new ProcessDetailRecordAdapter(context);

        processLv = (ListView) findViewById(R.id.process_lv);
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
                notifyData();
            }
        });
    }

    private void initTopView(CommonInfo info) {
        thirdBillId = info.getThirdBillId();
        ((TextView) findViewById(R.id.category_tv)).setText(info.getBillNo());
        ((TextView) findViewById(R.id.start_date_tv)).setText(info.getPstartDate());
        ((TextView) findViewById(R.id.end_date_tv)).setText(info.getPendDate());
        ((TextView) findViewById(R.id.operator_phone_tv)).setText(info.getPcycle() + "");
        ((TextView) findViewById(R.id.operator_certificate_no_tv)).setText(info.getPvalue() + "");
        ((TextView) findViewById(R.id.operator_identity_no_tv)).setText(stageProgress + "%");
    }

    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizPlan/" + planId;
        Response.Listener<CommonInfo> successListener = new Response.Listener
                <CommonInfo>() {
            @Override
            public void onResponse(CommonInfo info) {
                if (context == null || context.isFinishing()) {
                    return;
                }
                mRefreshLayout.finishRefresh(0);
                if (info != null) {
                    initTopView(info);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mRefreshLayout.finishRefresh(0);
                Log.d(TAG, "数据"+TextUtil.getErrorMsg(volleyError));
                ToastUtil.show(context, R.string.server_exception);
            }
        };

        Request<CommonInfo> request = new GsonRequest<CommonInfo>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<CommonInfo>() {
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

    public void notifyData() {
        getData();
        getRecord();
    }

    private void getRecord() {
        String url = Constant.WEB_SITE + "/biz/bizPlan/report/list?planId=" + planId;
        Response.Listener<List<RecordInfo>> successListener = new Response.Listener
                <List<RecordInfo>>() {
            @Override
            public void onResponse(List<RecordInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (context == null || TextUtil.isEmptyList(result)) {
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

        Request<List<RecordInfo>> request = new GsonRequest<List<RecordInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<RecordInfo>>() {
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
