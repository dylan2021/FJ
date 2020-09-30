package com.android.baihuahu.act_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.DialogUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 */
public class EmplyeeListActivity extends BaseFgActivity {
    private EmplyeeListActivity context;
    private ListView mListView;
    private EmplyeeListAdapter adapter;
    private RefreshLayout mRefreshLayout;
    private int id = 0;
    private String attendRewardNum;
    private EditText searchEt;
    private FragmentManager fm;
    private boolean isGroupList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_emplyee_list);
        context = this;
        initTitleBackBt(getIntent().getStringExtra(KeyConst.title));
        id = getIntent().getIntExtra(KeyConst.id, 0);
        isGroupList = getIntent().getBooleanExtra(KeyConst.type, false);

        initView();
    }

    private void initView() {
        //请求
        Button filterBt = getTitleRightBt("添加考勤");
        filterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtil.isEmptyList(employeeList)) {
                    ToastUtil.show(context, "人员为空");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) employeeList);//序列化,要注意转化(Serializable)
                Intent intent = new Intent(context, EmplyeeAttendAddActivity.class);
                intent.putExtra(KeyConst.id, id);
                intent.putExtra(KeyConst.type, isGroupList);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        mListView = (ListView) findViewById(R.id.common_list_view);
        adapter = new EmplyeeListAdapter(context, isGroupList);
        mListView.setAdapter(adapter);

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
                getSearchData();
            }
        });

        searchEt = findViewById(R.id.search_et);
        final View titleLayout = findViewById(R.id.activity_title_layout);
        final TextView cancleBt = findViewById(R.id.search_cancle_bt);
        searchEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    cancleBt.setVisibility(View.VISIBLE);
                    cancleBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchEt.setText("");
                            searchEt.clearFocus();
                            DialogUtils.hideKeyBorad(context);
                            getSearchData();
                        }
                    });
                    titleLayout.setVisibility(View.GONE);
                } else {
                    cancleBt.setVisibility(View.GONE);
                    titleLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                List<EmplyeeInfo> list = new ArrayList<>();
                for (EmplyeeInfo productInfo : employeeList) {
                    if (productInfo.getProjectName().contains(editable)) {
                        list.add(productInfo);
                    }
                }
                if (list.size() == 0) {
                    ToastUtil.show(context, R.string.search_no_data);
                }
                adapter.setData(list);
            }
        });
    }

    private List<EmplyeeInfo> employeeList = new ArrayList<>();

    //获取数据
    private void getSearchData() {
        employeeList.clear();
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url_group = "/biz/bizWorker/allWorkerByGroup/" + id;
        String url_p = "/biz/bizWorker/allWorkerByProject?projectId=" +
                id + "&status=1" ;
        String url = Constant.WEB_SITE + (isGroupList ? url_group : url_p);////1. 未退场  2.已退场
        Response.Listener<List<EmplyeeInfo>> successListener = new Response.Listener<List<EmplyeeInfo>>() {
            @Override
            public void onResponse(List<EmplyeeInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, R.string.no_data);
                    return;
                }
                if (!isGroupList) {//请求临聘人员
                    for (EmplyeeInfo info : result) {
                        if ("1".equals(info.getType())) {//公共人员==0   临聘人员==1
                            employeeList.add(info);
                        }
                    }
                } else {//请求班组人员
                    employeeList = result;
                }
                adapter.setData(result);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(context, R.string.server_exception);
                mRefreshLayout.finishRefresh(0);
            }
        };

        Request<List<EmplyeeInfo>> request = new GsonRequest<List<EmplyeeInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<EmplyeeInfo>>() {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        getSearchData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DialogUtils.hideKeyBorad(context);
    }

}