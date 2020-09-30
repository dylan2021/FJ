package com.android.baihuahu.act_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.BillInfo;
import com.android.baihuahu.bean.RecordInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.DialogUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 * 附件
 */

public class BillDetailActivity extends BaseFgActivity {

    private ListView processLv;
    private BillDetailActivity context;
    private BillDetailRecordAdapter adapter;
    private int id;
    private RefreshLayout mRefreshLayout;
    private String unit;
    private boolean writeData,signData,fileData;
    private boolean writeReport,signReport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_ledger_detail);
        context = this;
        id = getIntent().getIntExtra(KeyConst.id, 0);
        initTitleBackBt("进度详情");

        initView();
        getData();
    }

    private void initView() {
        processLv = findViewById(R.id.process_lv);
        Button dataBt1 = findViewById(R.id.audit_bt_1);
        Button fileBt2 = findViewById(R.id.audit_bt_2);
        Button reportBt3 = findViewById(R.id.audit_bt_3);

        //配置按钮权限,有就Visibilty 无权限就gone

        //资料确定
        dataBt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout = LayoutInflater.from(context).inflate(R.layout.layout_dialog_check_radio_3, null);
                ((CheckBox)layout.findViewById(R.id.check_profile_tv_1)).setChecked(writeData);
                ((CheckBox)layout.findViewById(R.id.check_profile_tv_2)).setChecked(signData);
                ((CheckBox)layout.findViewById(R.id.check_profile_tv_3)).setChecked(fileData);
                DialogUtils.getCustomDialog(context).title("资料完成")
                        .customView(layout, false).
                        onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View customView = dialog.getCustomView();
                                boolean writeData = ((CheckBox) customView.findViewById(R.id.check_profile_tv_1)).isChecked();
                                boolean signData = ((CheckBox) customView.findViewById(R.id.check_profile_tv_2)).isChecked();
                                boolean fileData = ((CheckBox) customView.findViewById(R.id.check_profile_tv_3)).isChecked();
                                //资料确定
                                Map<String, Object> map = new HashMap<>();

                                map.put(KeyConst.writeData, writeData);
                                map.put(KeyConst.signData, signData);
                                map.put(KeyConst.fileData, fileData);
                                postFileFinished(map, "data");
                            }
                        }).show();
            }
        });
        //实验资料
        fileBt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View layout = LayoutInflater.from(context).inflate(R.layout.layout_dialog_check_radio_2, null);
                ((CheckBox)layout.findViewById(R.id.check_profile_tv_1)).setChecked(writeReport);
                ((CheckBox)layout.findViewById(R.id.check_profile_tv_2)).setChecked(signReport);
                DialogUtils.getCustomDialog(context).title("资料完成")
                        .customView(layout, false).
                        onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View customView = dialog.getCustomView();
                                boolean writeReport = ((CheckBox) customView.findViewById(R.id.check_profile_tv_1)).isChecked();
                                boolean signReport = ((CheckBox) customView.findViewById(R.id.check_profile_tv_2)).isChecked();
                                //实验资料
                                Map<String, Object> map = new HashMap<>();

                                map.put(KeyConst.writeReport, writeReport);
                                map.put(KeyConst.signReport, signReport);
                                postFileFinished(map, "trail");
                            }
                        }).show();
            }
        });
        //进度汇报
        reportBt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BillProcessReportActivity.class);
                intent.putExtra(KeyConst.id, id);
                intent.putExtra(KeyConst.unit, unit);
                context.startActivity(intent);
            }
        });


        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        processLv = (ListView) findViewById(R.id.process_lv);
        adapter = new BillDetailRecordAdapter(context);
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
    private void setInfoView(BillInfo info) {
        ((TextView) findViewById(R.id.code_tv)).setText(TextUtil.remove_0(info.getCode()));
        ((TextView) findViewById(R.id.detail_name_tv)).setText(info.getName());
        ((TextView) findViewById(R.id.work_position_tv)).setText(info.getUsePosition());
        unit = info.getUnit();
        ((TextView) findViewById(R.id.project_num_tv)).setText(info.getTotalNum() + unit);
        String totalReportNum = info.getTotalReportNum() + unit;
        String todayReportNum = info.getTodayReportNum() + unit;
        ((TextView) findViewById(R.id.finished_num_tv)).setText(Html.fromHtml(totalReportNum +
                "<font color='#a5a5a5' >（今日完成：" + todayReportNum + "）</font>"));
        // ((TextView) findViewById(R.id.unit_tv)).setText(unit);

        writeData = info.isWriteData();
        signData = info.isSignData();
        fileData = info.isFileData();
        ((TextView) findViewById(R.id.write_data_tv)).setSelected(writeData);
        ((TextView) findViewById(R.id.sign_data_tv)).setSelected(signData);
        ((TextView) findViewById(R.id.file_data_tv)).setSelected(fileData);

        writeReport = info.isWriteReport();
        signReport = info.isSignReport();
        ((TextView) findViewById(R.id.write_report_tv)).setSelected(writeReport);
        ((TextView) findViewById(R.id.sign_report_tv)).setSelected(signReport);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }

    public void refreshData() {
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

    private void getRecordData() {
        String url = Constant.WEB_SITE + "/biz/bizBill/report/list?billDetailId=" + id;
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

    private void getInfoData() {
        String url = Constant.WEB_SITE + "/biz/bizBill/billDetail/" + id;
        Response.Listener<BillInfo> successListener = new Response.Listener<BillInfo>() {
            @Override
            public void onResponse(BillInfo info) {
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

        Request<BillInfo> request = new GsonRequest<BillInfo>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<BillInfo>() {
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

    private void postFileFinished(Map<String, Object> map, String url_param) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            return;
        }

        map.put(KeyConst.id, id);
        String url = Constant.WEB_SITE + "/biz/bizBill/billDetail/" + url_param;
        JSONObject jsonObject = new JSONObject(map);
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result != null) {
                    refreshData();
                    ToastUtil.show(context, "提交成功");
                } else {
                    ToastUtil.show(context, "提交失败");
                }
                DialogHelper.hideWaiting(fm);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(fm);
                ToastUtil.show(context, context.getString(R.string.server_exception));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Content_Type, Constant.application_json);
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                params.put(KeyConst.projectId, App.projecId);

                return params;
            }
        };
        App.requestQueue.add(jsonRequest);
    }

}
