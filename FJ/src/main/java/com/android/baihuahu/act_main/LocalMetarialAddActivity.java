package com.android.baihuahu.act_main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.DictInfo;
import com.android.baihuahu.bean.GroupInfo;
import com.android.baihuahu.bean.ProjDeptInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.util.DialogUtils;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  @author Dylan
 */
public class LocalMetarialAddActivity extends BaseFgActivity {
    private String chooseId;
    private LocalMetarialAddActivity context;
    private TextView timeTv, categoryTv, supplierPhoneTv;
    private EditText designNumEt, planNumEt;
    private TextView supplierTv, unitTv;
    private EditText usePositionEt, materailNameEt;
    private String planDate;
    private List<ProjDeptInfo> projInfoList = new ArrayList<>();
    private int pieceWageId;
    private String categoryId;
    private String materailNameId;
    private String supplier, unit;
    private List<DictInfo.DictValuesBean> dictList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_metarail_add);
        context = this;
        initTitleBackBt("添加现场材料");
        chooseId = getIntent().getStringExtra(KeyConst.id);

        initView();
    }


    //产品规格选择框
    private void showDictListDialog(final TextView tv, final String DICT_TYPE_KEY) {
        dictList = Utils.getDictListByType(context, DICT_TYPE_KEY);
        if (dictList == null || dictList.size() == 0) {
            ToastUtil.show(context, R.string.no_data);
            return;
        }
        ArrayList<String> nameList = new ArrayList<>();
        for (DictInfo.DictValuesBean info : dictList) {
            nameList.add(info.getName());
        }
        new MaterialDialog.Builder(context)
                .items(nameList)// 列表数据
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView,
                                            int position, CharSequence text) {
                        String dictValue = dictList.get(position).getValue();
                        tv.setText(text);

                        if (DICT_TYPE_KEY.equals(KeyConst.Material_Category)) {
                            categoryId = dictValue;
                        } else if (DICT_TYPE_KEY.equals(KeyConst.unit)) {
                            unit = dictValue;
                        } else if (DICT_TYPE_KEY.equals(KeyConst.Supplier_Material)) {
                            supplier = dictValue;
                        }
                    }
                })
                .show();
    }


    String[] projItems = {};
    private List<GroupInfo> parentList = new ArrayList<>();

    private void initView() {
        materailNameEt = findViewById(R.id.name_et);

        categoryTv = (TextView) findViewById(R.id.spec_tv);
        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDictListDialog(categoryTv, KeyConst.Material_Category);
            }
        });
        timeTv = (TextView) findViewById(R.id.hours_add_time_tv);

        designNumEt = findViewById(R.id.design_num_tv);
        planNumEt = findViewById(R.id.plan_num_tv);
        supplierTv = (TextView) findViewById(R.id.supplier_tv);
        supplierPhoneTv = (TextView) findViewById(R.id.supplier_phone_tv);
        long todayTime = TimeUtils.getTodayZeroTime();
        planDate = TimeUtils.getTimeYmd(todayTime);
        timeTv.setText(planDate);
        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.Builder timePickerDialog = DialogUtils.getTimePicker_Ymd(context);
                timePickerDialog.setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                    /*    if (millseconds < System.currentTimeMillis()) {
                            ToastUtil.show(context, "时间不可以小于今日");
                            return;
                        }*/
                        planDate = TimeUtils.getTimeYmd(millseconds);
                        timeTv.setText(planDate);
                    }
                });

                timePickerDialog.build().show(context.getSupportFragmentManager(), "");
            }
        });

        unitTv = (TextView) findViewById(R.id.unit_tv);
        usePositionEt = findViewById(R.id.used_position_et);

        // 单位
        unitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDictListDialog(unitTv, KeyConst.unit);
            }
        });

        //供应商
        supplierTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDictListDialog(supplierTv, KeyConst.Supplier_Material);
            }
        });
    }

    List<AlertDialog> dialogList = new ArrayList<>();


    //材料名称
    private void getMaterailNameList() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + "/biz/wage/projectName";
        Response.Listener<List<ProjDeptInfo>> successListener = new Response
                .Listener<List<ProjDeptInfo>>() {
            @Override
            public void onResponse(List<ProjDeptInfo> result) {
                projInfoList = result;
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, "数据为空");
                    return;
                }
                projItems = new String[result.size()];
                for (int i = 0; i < projItems.length; i++) {
                    ProjDeptInfo projDeptInfo = result.get(i);
                    projItems[i] = projDeptInfo.getProjectName();
                }

                new MaterialDialog.Builder(context)
                        .items(projItems)// 列表数据
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView,
                                                    int position, CharSequence text) {
                                ProjDeptInfo projDeptInfo = projInfoList.get(position);
                                materailNameEt.setText(projDeptInfo.getProjectName());
                                pieceWageId = projDeptInfo.getId();
                            }
                        })
                        .show();

            }
        };

        Request<List<ProjDeptInfo>> versionRequest = new
                GsonRequest<List<ProjDeptInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, getString(R.string.get_data_faild));
                        Log.d(TAG, "数据异常" + volleyError.toString());
                    }
                }, new TypeToken<List<ProjDeptInfo>>() {
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

    private void addPost() {
        String materailName = materailNameEt.getText().toString();
        if (ToastUtil.showCannotEmpty(context, materailName, "材料名称")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, categoryId, "规格型号")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, unit, "单位")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, planDate, "计划使用时间")) {
            return;
        }
        String designNum = designNumEt.getText().toString();
        if (ToastUtil.showCannotEmpty(context, designNum, "设计使用数量")) {
            return;
        }
        String planNum = planNumEt.getText().toString();
        if (ToastUtil.showCannotEmpty(context, planNum, "计划使用数量")) {
            return;
        }

        String usePosition = usePositionEt.getText().toString();
        if (ToastUtil.showCannotEmpty(context, usePosition, "使用部位")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context, supplier, "供应商")) {
            return;
        }
        String supplierPhone = supplierPhoneTv.getText().toString();
        if (ToastUtil.showCannotEmpty(context, supplierPhone,
                "供应商电话")) {
            return;
        }

        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.code, "");
        map.put(KeyConst.name, materailName);
        map.put(KeyConst.category, categoryId);
        map.put(KeyConst.planDate, planDate);
        map.put(KeyConst.designNum, designNum);
        map.put(KeyConst.planNum, planNum);
        map.put(KeyConst.unit, unit);
        map.put(KeyConst.usePosition, usePosition);
        map.put(KeyConst.supplier, supplier);
        map.put(KeyConst.phone, supplierPhone);

        //添加
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + "/biz/bizMaterial";
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        DialogHelper.hideWaiting(fm);
                        if (result != null) {
                            ToastUtil.show(context, "材料添加成功");
                            finish();
                            return;
                        }
                        ToastUtil.show(context, "材料添加失败");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(fm);
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

    public void onReportCommitClick(View view) {
        addPost();
    }
}
