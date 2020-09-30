package com.android.baihuahu.act_main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.SafyMaterailInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
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
 */
public class SafyMaterailListActivity extends BaseFgActivity {
    private SafyMaterailListActivity context;
    private ListView mListView;
    private SafyMaterailListAdapter adapter;
    private RefreshLayout mRefreshLayout;
    private int id;
    private EditText searchEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_local_mateail_list);
        context = this;
        initTitleBackBt(getString(R.string.safy_product));
        id = getIntent().getIntExtra(KeyConst.id, 0);

        initView();
    }


    private void initView() {

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();


        mListView = (ListView) findViewById(R.id.common_list_view);
        adapter = new SafyMaterailListAdapter(context);
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
                searchEt.setText("");
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
                            getSearchData("");
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
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        getSearchData("?search="+String.valueOf(editable).trim());
                    }
                }, 100);
            }
        });


       /* Button rightBt = getTitleRightBt("");
        Drawable filterDrawable = context.getResources().getDrawable(R.drawable.ic_add);
        rightBt.setCompoundDrawablesWithIntrinsicBounds(null, null, filterDrawable, null);
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //新增材料
                startActivity(new Intent(context, LocalMetarialAddActivity.class));
            }
        });*/

    }

    private List<SafyMaterailInfo> deviceList = new ArrayList<>();


    //获取数据
    private void getSearchData(String searchText) {
        adapter.setData(null);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizSafetyProduct/list"+searchText;
        Response.Listener<List<SafyMaterailInfo>> successListener = new Response.Listener<List<SafyMaterailInfo>>() {
            @Override
            public void onResponse(List<SafyMaterailInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (TextUtil.isEmptyList(result)) {
                    ToastUtil.show(context, R.string.no_data);
                    return;
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

        Request<List<SafyMaterailInfo>> request = new GsonRequest<List<SafyMaterailInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<SafyMaterailInfo>>() {
        }.getType()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                params.put(KeyConst.projectId,  App.projecId);
                return params;
            }
        };
        App.requestQueue.add(request);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        searchEt.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DialogUtils.hideKeyBorad(context);
    }

}