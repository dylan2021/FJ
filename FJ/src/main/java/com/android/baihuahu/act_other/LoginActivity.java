package com.android.baihuahu.act_other;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.baihuahu.act_main.ChooseProjectActivity;
import com.android.baihuahu.act_main.MainActivity;
import com.android.baihuahu.core.utils.CommonUtil;
import com.android.baihuahu.util.DialogUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Dylan
 */
public class LoginActivity extends BaseFgActivity implements View.OnClickListener {
    private EditText et_pwd, et_user;
    private TextView bt_find_pwd, bt_register;
    private Button bt_login;
    private SharedPreferences sp;
    private LoginActivity context;
    private DialogHelper dialogHelper;
    private String accessToken;
    private String pwd, username;
    private ImageView welcomeIv;
    private int projectId;
    private LinearLayout loginBglayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);
        context = this;
        sp = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);

        loginBglayout = findViewById(R.id.login_bg_layout);
        et_user = findViewById(R.id.et_login_user);
        et_pwd = findViewById(R.id.et_login_pwd);
        username = sp.getString(KeyConst.username, "");
        pwd = sp.getString(Constant.sp_pwd, "");

        projectId = sp.getInt(KeyConst.SP_PROJECT_ID, 0);

        welcomeIv = (ImageView) findViewById(R.id.welcome_iv);
        bt_find_pwd = (TextView) findViewById(R.id.tv_find_pwd);
        bt_find_pwd.setOnClickListener(this);
        bt_register = (TextView) findViewById(R.id.tv_register);
        bt_register.setOnClickListener(this);
        bt_login = (Button) findViewById(R.id.but_login);
        bt_login.setOnClickListener(this);

        dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        findViewById(R.id.login_qq_bt).setOnClickListener(this);
        findViewById(R.id.login_wechat_bt).setOnClickListener(this);
        findViewById(R.id.login_sina_bt).setOnClickListener(this);

        et_user.setText(username);

        if (!TextUtil.isEmpty(username) && !TextUtil.isEmpty(pwd)) {
            welcomeIv.setVisibility(View.VISIBLE);

            et_pwd.setText(pwd);
            doLogin(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    CommonUtil.requestStoragePermissions(context);
                    welcomeIv.setVisibility(View.GONE);
                    loginBglayout.setBackgroundResource(R.drawable.ic_login_bg);
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
            }, 0);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_login:
                username = et_user.getText().toString();
                if (TextUtil.isEmpty(username)) {
                    ToastUtil.show(context, "请输入账号");
                    return;
                }
                pwd = et_pwd.getText().toString();
                if (TextUtil.isEmpty(pwd)) {
                    ToastUtil.show(context, "请输入密码");
                    return;
                }
                if (!NetUtil.isNetworkConnected(context)) {
                    ToastUtil.show(context, getString(R.string.no_network));
                    return;
                }
                dialogHelper.showAlert("登录中...", true);
                doLogin(false);
                break;
            case R.id.tv_find_pwd:
                startActivity(new Intent(this, FindPwdActivity.class));
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void doLogin(final boolean isAutoLogin) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context,R.string.no_network);
            return;
        }
        String url = Constant.WEB_SITE + Constant.URL_USER_LOGIN;
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (result == null) {
                            if (isAutoLogin) {
                                startActivity(new Intent(context, MainActivity.class));
                                context.finish();
                                return;
                            }
                            if (null != context && !context.isFinishing()) {
                                dialogHelper.hideAlert();
                            }
                            ToastUtil.show(context, getString(R.string.server_exception));
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            accessToken = jsonObject.getString(KeyConst.access_token);
                        } catch (JSONException e) {
                            accessToken = null;
                        }
                        if (TextUtil.isEmpty(accessToken)) {
                            if (isAutoLogin) {
                                startActivity(new Intent(context, MainActivity.class));
                                context.finish();
                                return;
                            }
                            if (null != context && !context.isFinishing()) {
                                dialogHelper.hideAlert();
                            }
                            ToastUtil.show(context, getString(R.string.server_exception));
                            return;
                        }
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Constant.SP_TOKEN, accessToken);
                        editor.putString(KeyConst.username, username);
                        editor.putString(Constant.sp_pwd, pwd);

                        editor.apply();
                        App.token = accessToken;
                        App.passWord = pwd;
                        App.username = username;
                        App.phone = username;

                        Intent intent = new Intent();
                        if (projectId > 0) {
                            intent.setClass(context, MainActivity.class);
                        } else {
                            intent.setClass(context, ChooseProjectActivity.class);
                        }
                        startActivity(intent);
                        context.finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (isAutoLogin) {
                    startActivity(new Intent(context, MainActivity.class));
                    context.finish();
                    return;
                }
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                String errorMsg = TextUtil.getErrorMsg(error);
                try {
                    if (errorMsg != null) {
                        JSONObject obj = new JSONObject(errorMsg);
                        if (obj != null) {
                            int errInt = obj.getInt(KeyConst.error);
                            //账号密码错误
                            if (errInt == 10001) {
                                DialogUtils.showTipDialog(context, getString(R.string.account_pwd_error));
                                return;
                                //账号冻结
                            } else if (errInt == 10003) {
                                DialogUtils.showTipDialog(context, getString(R.string.contact_admin));
                                return;
                            }
                        }

                    }
                } catch (JSONException e) {
                }
                ToastUtil.show(context, R.string.server_exception);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Content_Type, Constant.application_form);
                params.put(KeyConst.Authorization, Constant.authorization);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.username, username);
                params.put(KeyConst.password, pwd);
                params.put(KeyConst.grant_type, KeyConst.password);
                return params;
            }

        };

        App.requestQueue.add(jsonObjRequest);
    }
}
