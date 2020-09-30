package com.android.baihuahu.act_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.CommonInfo;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 * 附件
 */

public class DeviceDetailActivity extends BaseFgActivity {

    private ListView processLv;
    private DeviceDetailActivity context;
    private List<CommonInfo> list = new ArrayList<>();
    private DeviceDetailRecordAdapter adapter;
    private int deviceId;
    private CommonInfo info;
    private RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_device_detail);
        context = this;
        info = (CommonInfo) getIntent().getSerializableExtra(KeyConst.OBJ_INFO);
        deviceId = info.id;

        initTitleBackBt(info.getName());

        initTopView();
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
                //添加考勤
                Intent intent = new Intent();
                intent.setClass(context, DeviceAttendAddActivity.class);
                intent.putExtra(KeyConst.title, info.getName());
                intent.putExtra(KeyConst.id, deviceId);
                context.startActivity(intent);

            }
        });

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        adapter = new DeviceDetailRecordAdapter(context);

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
                getData();
            }
        });
    }

    private void initTopView() {
        String category = Utils.getDictNameByValue(context, KeyConst.Device_Category, info.getCategory());
        ((TextView) findViewById(R.id.category_tv)).setText(category);
        ((TextView) findViewById(R.id.contract_no_tv)).setText(info.getContractNo());
        ((TextView) findViewById(R.id.in_date_tv)).setText(info.getInDate());
        ((TextView) findViewById(R.id.operator_tv)).setText(info.getOperator());
        ((TextView) findViewById(R.id.operator_phone_tv)).setText(info.getOperatorPhone());
        ((TextView) findViewById(R.id.operator_certificate_no_tv)).setText(info.getOperatorCertificateNo());
        ((TextView) findViewById(R.id.operator_identity_no_tv)).setText(info.getOperatorIdentityNo());
    }

    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizDevice/attend/list?deviceId=" + deviceId;
        Response.Listener<List<CommonInfo>> successListener = new Response.Listener
                <List<CommonInfo>>() {
            @Override
            public void onResponse( List<CommonInfo> result) {
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

        Request< List<CommonInfo>> request = new GsonRequest< List<CommonInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken< List<CommonInfo>>() {
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
