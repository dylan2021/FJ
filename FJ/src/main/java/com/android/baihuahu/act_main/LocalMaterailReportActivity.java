package com.android.baihuahu.act_main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.CommonBaseActivity;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.util.ToastUtil;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Dylan
 * 现场资料
 */

public class LocalMaterailReportActivity extends CommonBaseActivity {
    private LocalMaterailReportActivity context;
    private int id;
    private FragmentManager fm;
    private double totalAmount = 0;
    private EditText remarkEt, numberPlateEt,useNumEt;
    private String unitName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_local_materail_report);
        context = this;
        fm = getSupportFragmentManager();
        id = getIntent().getIntExtra(KeyConst.id, 0);
        unitName = getIntent().getStringExtra(KeyConst.unit);

        initTitleBackBt("汇报材料");
        ((TextView) findViewById(R.id.report_title_tv)).setText("使用数量(" + unitName + ")");
        useNumEt = findViewById(R.id.report_num_tv);
        numberPlateEt = findViewById(R.id.number_plate_tv);
        remarkEt = findViewById(R.id.remark_tv);

        initFileView();
    }

    private void postReport() {
        String useNum = useNumEt.getText().toString();
        String numberPlate= numberPlateEt.getText().toString();
        String useRemark = remarkEt.getText().toString();
        if (ToastUtil.showCannotEmpty(context,useNum,"使用数量")) {
            return;
        }
        if (ToastUtil.showCannotEmpty(context,numberPlate,"车牌号")) {
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizMaterial/useRecord";
        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.materialId, id);
        map.put(KeyConst.numberPlate, numberPlate);
        map.put(KeyConst.useNum, useNum);
        map.put(KeyConst.useRemark, useRemark);

        map.put(KeyConst.auditNum, 0);
        map.put(KeyConst.auditRemark, "");
        DialogHelper.showWaiting(fm, "加载中...");
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result != null) {
                            DialogHelper.hideWaiting(fm);
                            ToastUtil.show(context, "提交成功");
                            context.finish();
                        } else {
                            ToastUtil.show(context, "提交失败");
                        }
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


    private void initFileView() {
        //附件
        findViewById(R.id.file_layout).setVisibility(View.GONE);

    }

    public void onReportCommitClick(View view) {
        //保存
        postReport();
    }

}
