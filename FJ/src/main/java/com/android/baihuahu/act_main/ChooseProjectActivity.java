/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.baihuahu.act_main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.ChooseDataInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.core.utils.UrlConstant;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.util.DialogUtils;
import com.android.baihuahu.util.ToastUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 */
public class ChooseProjectActivity extends BaseFgActivity {

    private RecyclerView recyclerView;
    private List<ChooseDataInfo> dataBean = new ArrayList<>();
    private ChooseProjectAdapter adapter;
    private SharedPreferences.Editor spEd;
    private ChooseProjectActivity context;
    private int projectId = 0;
    private View finishBt;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        context = this;
        setContentView(R.layout.activity_choose_project);

        finishBt = findViewById(R.id.left_bt);
        initTitleBackBt(getString(R.string.choose_project));

        sp = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        spEd = sp.edit();
        projectId = sp.getInt(KeyConst.SP_PROJECT_ID, 0);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChooseProjectAdapter(dataBean, context,0);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ChooseProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int id, String name, int type) {
                spEd.putInt(KeyConst.SP_PROJECT_ID, id);
                spEd.commit();

                context.finish();
                if (projectId > 0) {
                    //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        getData();

        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtfinish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        backBtfinish();
    }

    private void backBtfinish() {
        finish();
        if (projectId > 0) {
            //不是第一次启动
            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            spEd.putString(Constant.sp_pwd, "").commit();
        }
    }

    //获取权限数据列表
    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        //final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        //dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + UrlConstant.url_biz_projects;

        Response.Listener<List<ChooseDataInfo>> successListener = new Response
                .Listener<List<ChooseDataInfo>>() {
            @Override
            public void onResponse(List<ChooseDataInfo> result) {
                if (null != context && !context.isFinishing()) {
                   // dialogHelper.hideAlert();
                }
                if (result == null) {
                    ToastUtil.show(context, R.string.no_data);
                    return;
                }
                //设置数据
                adapter.setData(result);
            }
        };

        Request<List<ChooseDataInfo>> versionRequest = new
                GsonRequest<List<ChooseDataInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null == context || context.isFinishing()) {
                            return;
                        }
                        //dialogHelper.hideAlert();
                        if (!TextUtil.isEmpty(TextUtil.getErrorMsg(error))) {
                            try {
                                JSONObject obj = new JSONObject(TextUtil.getErrorMsg(error));
                                if (obj != null) {
                                    DialogUtils.showTipDialog(context, obj.getString(KeyConst.message));
                                    return;
                                }
                            } catch (JSONException e) {
                            }
                        }
                        Log.d(TAG, "请求异常" + error);
                        ToastUtil.show(context, getString(R.string.server_exception));
                    }
                }, new TypeToken<List<ChooseDataInfo>>() {
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
}
