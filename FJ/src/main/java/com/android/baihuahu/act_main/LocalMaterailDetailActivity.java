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
import com.android.baihuahu.bean.RecordInfo;
import com.android.baihuahu.bean.SafyMaterailInfo;
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

public class LocalMaterailDetailActivity extends BaseFgActivity {

    private ListView processLv;
    private LocalMaterailDetailActivity context;
    private LocalMaterailDetailRecordAdapter adapter;
    private int id;
    private String materailName;
    private TextView valueTv1;
    private TextView valueTv2, valueTv3;
    private TextView valueTv1_2;
    private TextView valueTv2_2;
    private TextView valueTv3_2;
    private RefreshLayout mRefreshLayout;
    private String unitName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_local_materail_detail);
        context = this;
        id = getIntent().getIntExtra(KeyConst.id, 0);
        materailName = getIntent().getStringExtra(KeyConst.title);
        initTitleBackBt(materailName + "详情");

        initView();
    }

    private void initView() {
        //规格
        valueTv1 = findViewById(R.id.item_value_1);
        //设计数量
        valueTv2 = findViewById(R.id.item_value_2);
        valueTv3 = findViewById(R.id.item_value_3);//实际数量

        //单位
        valueTv1_2 = findViewById(R.id.item_value_1_2);
        //计划数量
        valueTv2_2 = findViewById(R.id.item_value_2_2);
        //复核数量
        valueTv3_2 = findViewById(R.id.item_value_3_2);


        Button addAttend = findViewById(R.id.audit_bt_3);
        addAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加考勤
                Intent intent = new Intent();
                intent.setClass(context, LocalMaterailReportActivity.class);
                intent.putExtra(KeyConst.id, id);
                intent.putExtra(KeyConst.unit, unitName);
                context.startActivity(intent);

            }
        });
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        processLv = (ListView) findViewById(R.id.process_lv);
        adapter = new LocalMaterailDetailRecordAdapter(context,id);
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

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    public void notifyData(){
        getData();
   }

    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            mRefreshLayout.finishRefresh(0);
            return;
        }
        getInfoData();

        getRecordData();
    }


    private void getInfoData() {
        String url = Constant.WEB_SITE + "/biz/bizMaterial/" + id;
        Response.Listener<SafyMaterailInfo> successListener = new Response.Listener<SafyMaterailInfo>() {
            @Override
            public void onResponse(SafyMaterailInfo info) {
                if (info != null) {
                    setInfoView(info);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(context, R.string.server_exception);
            }
        };

        Request<SafyMaterailInfo> request = new GsonRequest<SafyMaterailInfo>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<SafyMaterailInfo>() {
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

    private void getRecordData() {
        String url = Constant.WEB_SITE + "/biz/bizMaterial/useRecord/list?materialId=" + id;
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

    private void setInfoView(SafyMaterailInfo info) {
        valueTv1.setText( Utils.getDictNameByValue(context, KeyConst.Material_Category, info.getCategory()));//规格
        unitName = Utils.getDictNameByValue(context, KeyConst.unit, info.getUnit());//单位
        valueTv1_2.setText(unitName);//单位

        valueTv2.setText(TextUtil.remove_0(info.getDesignNum() + ""));//设计数量
        valueTv2_2.setText(TextUtil.remove_0(info.getPlanNum() + ""));//计划数量

        valueTv3.setText(TextUtil.remove_0(info.getUseNum() + ""));//实际数量
        valueTv3_2.setText(TextUtil.remove_0(info.getAuditNum() + ""));//复核数量

        ((TextView) findViewById(R.id.use_position_tv)).setText(info.getUsePosition());//使用部位

        String supplierName = Utils.getDictNameByValue(context, KeyConst.Supplier_Material, info.getSupplier());
        String supplierPhone = TextUtil.initNamePhone(supplierName, info.getPhone());
        ((TextView) findViewById(R.id.supplier_tv)).setText(supplierPhone);//供应商

    }


}
