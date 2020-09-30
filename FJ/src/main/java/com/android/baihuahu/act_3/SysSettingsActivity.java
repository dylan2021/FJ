package com.android.baihuahu.act_3;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.act_main.MainActivity;
import com.android.baihuahu.act_other.ChangePhoneShowActivity;
import com.android.baihuahu.act_other.ChangePwdActivity;
import com.android.baihuahu.act_other.LoginActivity;
import com.android.baihuahu.bean.AccountInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.core.utils.UrlConstant;
import com.android.baihuahu.util.DialogUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Dylan
 */
public class SysSettingsActivity extends BaseFgActivity {

    public static SysSettingsActivity context;
    private SharedPreferences.Editor sp;
    private TextView deptTv, nameTv, iconTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        this.setContentView(R.layout.activity_me_settings);
        context = this;

        initTitleBackBt("个人中心");
        sp = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE).edit();

        initView();
        getUserData();
    }

    private void initView() {
        iconTv = findViewById(R.id.me_icon_tv);
        nameTv = findViewById(R.id.name_tv);
        deptTv = findViewById(R.id.dept_tv);
    }

    private void getUserData() {
        String url = Constant.WEB_SITE + "/upms" + UrlConstant.accounts_current;
        Response.Listener<AccountInfo> successListener = new Response
                .Listener<AccountInfo>() {
            @Override
            public void onResponse(AccountInfo info) {
                if (info != null) {
                    String username = info.employeeName;

                    nameTv.setText(TextUtil.initNamePhone(username, info.employeeMobile));
                    iconTv.setText(TextUtil.getLast2(username));
                    deptTv.setText(info.deptName);
                }
            }
        };

        Request<AccountInfo> versionRequest = new
                GsonRequest<AccountInfo>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<AccountInfo>() {
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

    public void onLogoutClick(View view) {
        showLogout();
    }

    public void onChangePwdClick(View view) {
        startActivity(new Intent(this, ChangePwdActivity.class));
    }


    private void showLogout() {
        final Dialog dialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_logout, null);

        inflate.findViewById(R.id.logout_yes_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Constant.WEB_SITE + "/authorization/token/logout";
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                        new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        sp.putString(Constant.sp_pwd, "").commit();
                        MainActivity.context.finish();
                        context.finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sp.putString(Constant.sp_pwd, "").commit();
                        startActivity(new Intent(context, LoginActivity.class));
                        MainActivity.context.finish();
                        context.finish();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                        return params;
                    }
                };
                App.requestQueue.add(jsonRequest);


                dialog.cancel();
            }
        });
        inflate.findViewById(R.id.logout_cancel_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(inflate);

        DialogUtils.setDialogWindow(context, dialog, Gravity.BOTTOM);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }
}
