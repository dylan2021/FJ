package com.android.baihuahu.act_main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.BuildSiteGroupInfo;
import com.android.baihuahu.bean.RecordInfo;
import com.android.baihuahu.bean.SafyMaterailInfo;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 * 附件
 */

public class SafyMaterailDetailActivity extends BaseFgActivity {

    private ListView processLv;
    private SafyMaterailDetailActivity context;
    private List<SafyMaterailInfo> infos = new ArrayList<>();
    private SafyMaterailDetailRecordAdapter adapter;
    private int id;
    private String materailName;
    private TextView totalStorageNumTv, storageTimeTv, totalReceiveNumTv;
    private TextView supplierNameTv, custodianTv, residueNumTv;
    private int groupId;
    private RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_safy_materail_detail);
        context = this;
        id = getIntent().getIntExtra(KeyConst.id, 0);
        materailName = getIntent().getStringExtra(KeyConst.title);
        initTitleBackBt(materailName + "详情");

        initView();

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
        getGroupList();
    }


    private void initView() {
        totalStorageNumTv = findViewById(R.id.enter_num_tv);
        storageTimeTv = findViewById(R.id.enter_time_tv);

        totalReceiveNumTv = findViewById(R.id.get_used_tv);//领用
        residueNumTv = findViewById(R.id.remain_num_tv);//剩余

        supplierNameTv = findViewById(R.id.supplier_name_tv);//厂商
        custodianTv = findViewById(R.id.custodian_tv);//保管员


        final Button receiveBt = findViewById(R.id.take_away_bt);//去领用
        receiveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //领用
                showReceiveDialog();
            }
        });

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        processLv = (ListView) findViewById(R.id.process_lv);
        adapter = new SafyMaterailDetailRecordAdapter(context);
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


    private void getRecordData() {
        String url = Constant.WEB_SITE + "/biz/bizSafetyProduct/receive/list?safetyProductId=" + id;
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

    private List<String> groupNameList = new ArrayList();
    private List<Integer> groupIdList = new ArrayList();

    private int type = 1;//1  个人  2班组

    private void showReceiveDialog() {
        type = 1;
        View inflate = LayoutInflater.from(context).inflate(R.layout.
                layout_safy_materail_take, null);
        final RelativeLayout takeGroupLayout = inflate.findViewById(R.id.take_group_layout);
        final RadioButton takeRg = inflate.findViewById(R.id.material_dialog_rb_1);

        takeRg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean personal) {
                type = personal ? 1 : 2;
                takeGroupLayout.setVisibility(personal ? View.GONE : View.VISIBLE);
            }
        });
        final TextView takeGroupTv = inflate.findViewById(R.id.take_group_tv);
        final EditText takeNumEt = inflate.findViewById(R.id.take_num_et);
        takeNumEt.setSelection(1);
        final EditText remarkEt = inflate.findViewById(R.id.dialog_remark_tv);
        final Map<String, Object> map = new HashMap<>();
        new MaterialDialog.Builder(context)
                .customView(inflate, false)
                .positiveColorRes(R.color.mainColor)
                .positiveText(R.string.sure)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String remark = remarkEt.getText().toString();

                        String takeNumStr = takeNumEt.getText().toString();

                        if (type == 2 && groupId == 0) {
                            ToastUtil.show(context, "请选择班组");
                            return;
                        }
                        if (TextUtil.isEmpty(takeNumStr) || Integer.valueOf(takeNumStr) == 0) {
                            ToastUtil.show(context, "领用数量须大于0");
                            return;
                        }
                        map.put(KeyConst.safetyProductId, id);
                        map.put(KeyConst.type, type);
                        map.put(KeyConst.receiveNum, takeNumStr);
                        map.put(KeyConst.remark, remark);
                        map.put(KeyConst.workerId, 0);//个人领用的话,这里应该是当前用户自己

                        map.put(KeyConst.groupId, groupId);//班组领用

                        receivePost(dialog, new JSONObject(map));

                    }
                })
                .negativeColorRes(R.color.mainColor)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        DialogUtils.showKeyBorad(takeNumEt, context);

        //选择班组
        takeGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupNameList.size() == 0) {
                    ToastUtil.show(context, "班组数据为空");
                    getGroupList();
                    return;
                }
                new MaterialDialog.Builder(context)
                        .items(groupNameList)//列表数据
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView,
                                                    int position, CharSequence text) {
                                groupId = groupIdList.get(position);
                                takeGroupTv.setText(text);
                            }
                        }).show();

            }
        });

    }

    //获取班组列表
    private void getGroupList() {
        String url = Constant.WEB_SITE + "/biz/bizGroup/list";
        Response.Listener<List<BuildSiteGroupInfo>> successListener = new Response
                .Listener<List<BuildSiteGroupInfo>>() {
            @Override
            public void onResponse(List<BuildSiteGroupInfo> result) {
                if (result == null) {
                    return;
                }
                groupNameList.clear();
                groupIdList.clear();
                for (BuildSiteGroupInfo info : result) {
                    groupNameList.add(info.getName());
                    groupIdList.add(info.getId());
                }
            }
        };

        Request<List<BuildSiteGroupInfo>> versionRequest = new
                GsonRequest<List<BuildSiteGroupInfo>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, R.string.server_exception);
                    }
                }, new TypeToken<List<BuildSiteGroupInfo>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                        params.put(KeyConst.projectId, App.projecId);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void receivePost(final MaterialDialog dialog, JSONObject jsonObject) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizSafetyProduct/receive";
        DialogHelper.showWaiting(getSupportFragmentManager(), getString(R.string.loading));
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (null != context && !context.isFinishing()) {
                    DialogHelper.hideWaiting(getSupportFragmentManager());
                }
                if (result != null) {
                    getData();//刷新数据
                    dialog.dismiss();
                    ToastUtil.show(context, R.string.commit_success);
                } else {
                    ToastUtil.show(context, R.string.commit_faild);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != context && !context.isFinishing()) {
                    DialogHelper.hideWaiting(getSupportFragmentManager());
                }
                ToastUtil.show(context, getString(R.string.server_exception));
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

    private void getInfoData() {
        String url = Constant.WEB_SITE + "/biz/bizSafetyProduct/" + id;
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

    private void setInfoView(SafyMaterailInfo info) {
        totalStorageNumTv.setText(info.getTotalStorageNum() + "");
        storageTimeTv.setText(TextUtil.subTimeYMD(info.getStorageTime()));

        totalReceiveNumTv.setText(info.getTotalReceiveNum() + "");//领用
        residueNumTv.setText(info.getResidueNum() + "");//剩余

        supplierNameTv.setText(info.getSupplierName());//厂商
        custodianTv.setText(TextUtil.initNamePhone(info.getCustodian(), info.getPhone()));
    }

    private void post() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            return;
        }
        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.id, "");

        String url = Constant.WEB_SITE + "/biz/wage/wageDetail";
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result != null && result.toString().contains("200")) {
                    ToastUtil.show(context, "修改成功");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.show(context, context.getString(R.string.server_exception));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Content_Type, Constant.application_json);
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);

                return params;
            }
        };
        App.requestQueue.add(jsonRequest);
    }

}
