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
import android.view.View;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.ChooseDataInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.ToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan  工区列表
 */
public class SectionListActivity extends BaseFgActivity {

    private RecyclerView recyclerView;
    private List<ChooseDataInfo> dataBean = new ArrayList<>();
    private ChooseProjectAdapter adapter;
    private SharedPreferences.Editor spEd;
    private SectionListActivity context;
    private int id, TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        context = this;
        setContentView(R.layout.activity_choose_project);
        Intent intent = getIntent();
        id = intent.getIntExtra(KeyConst.id, 0);
        TYPE = intent.getIntExtra(KeyConst.type, 0);

        initTitleBackBt(intent.getStringExtra(KeyConst.title) + (TYPE == 1 ? "-工区" : "工地-")+"列表");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChooseProjectAdapter(dataBean, context, TYPE);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ChooseProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int id, String name, int type) {
                Intent intent = new Intent(context, DesignInfoActivity.class);
                intent.putExtra(KeyConst.id, id);
                intent.putExtra(KeyConst.title, name);
                intent.putExtra(KeyConst.type, TYPE);
                context.startActivity(intent);
            }
        });

        getData();
    }

    //获取权限数据列表
    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        int method = TYPE == 1 ? Request.Method.POST : Request.Method.GET;
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + (TYPE == 1 ? "/biz/section/all" : "/biz/buildSite/allBuildSite/" + id);
        Response.Listener<List<ChooseDataInfo>> successListener = new Response
                .Listener<List<ChooseDataInfo>>() {
            @Override
            public void onResponse(List<ChooseDataInfo> result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (!TextUtil.isEmptyList(result)) {
                    adapter.setData(result);

                } else {
                    ToastUtil.show(context, R.string.empty_data);
                }
                //设置数据
            }
        };
        Request<List<ChooseDataInfo>> versionRequest = new GsonRequest<List<ChooseDataInfo>>(
                method, url, successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }

            }
        }, new TypeToken<List<ChooseDataInfo>>() {
        }.getType()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                if (TYPE == 1) {
                    params.put(KeyConst.Content_Type, Constant.application_json);
                }
                params.put(KeyConst.projectId, App.projecId);
                return params;
            }
        };
        App.requestQueue.add(versionRequest);
    }
}
