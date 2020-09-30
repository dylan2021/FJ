package com.android.baihuahu.act_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.GroupItemInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
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
 * <p>
 */
public class EmplyeeGroupListActivity extends BaseFgActivity {
    private EmplyeeGroupListActivity context;
    private List<GroupItemInfo> mDataList = new ArrayList<>();
    private ListView mListView;
    private EmplyeeGroupListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();

        setContentView(R.layout.activity_emplyee_attend_manager);
        context = this;
        id = getIntent().getStringExtra(KeyConst.id);

        init();
    }

    private void init() {
        initTitleBackBt("考勤管理");
        getTitleRightBt("临聘人员").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EmplyeeListActivity.class);
                intent.putExtra(KeyConst.id, id);//班组id
                intent.putExtra(KeyConst.title, "人员列表(临聘)");//班组id
                context.startActivity(intent);
            }
        });
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);

        mListView = (ListView) findViewById(R.id.list_view);

        mAdapter = new EmplyeeGroupListAdapter(context, mDataList);
        mListView.setAdapter(mAdapter);


        Utils.setLoadHeaderFooter(context, mRefreshLayout);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mDataList.clear();
                getDatas("");
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                ToastUtil.show(context, getString(R.string.no_more_data));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRefreshLayout.autoRefresh();
    }

    private void getDatas(String searchData) {
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizGroup/list";

        Response.Listener<List<GroupItemInfo>> successListener = new Response
                .Listener<List<GroupItemInfo>>() {
            @Override
            public void onResponse(List<GroupItemInfo> result) {
                mDataList.clear();
                mAdapter.setData(result);
                if (result == null || result.size() == 0) {
                    mRefreshLayout.finishRefresh(0);
                    return;
                }
                mRefreshLayout.finishRefresh(0);
            }
        };

        Request<List<GroupItemInfo>> versionRequest = new
                GsonRequest<List<GroupItemInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mRefreshLayout.finishRefresh(0);
                    }
                }, new TypeToken<List<GroupItemInfo>>() {
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

}