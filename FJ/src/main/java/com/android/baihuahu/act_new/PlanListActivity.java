package com.android.baihuahu.act_new;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_main.BillListAdapter;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.BillInfo;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.bean.DeptInfo;
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
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
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
public class PlanListActivity extends BaseFgActivity {
    private PlanListActivity context;
    private ListView listView;
    private PlanListAdapter adapter;
    private RefreshLayout mRefreshLayout;
    private EditText searchEt;
    private TextView filterTv;
    private int BILL_ID_3;
    private View titleLayout;
    private TextView cancleBt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_process_list);
        context = this;
        initTitleBackBt("进度计划");

        initView();
    }


    private void initView() {
        filterTv = findViewById(R.id.filter_tv);
        filterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtil.isEmptyList(App.billIdTreeList)) {
                    ToastUtil.show(context, "台账章节为空");
                    return;
                }
                //条件选择器
                OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int index1, int index2, int index3, View v) {
                        String str1 = App.list1.get(index1).toString();
                        String str2 = "";
                        String str3 = "";
                        if (App.list2.get(index1) == null) {
                            adapter.setData(null);
                            return;
                        }
                        if (index2 < App.list2.get(index1).size()) {
                            str2 = "-" + App.list2.get(index1).get(index2);
                        }
                        if (index2 < App.list3.get(index1).size()) {
                            if (index3 < App.list3.get(index1).get(index2).size()) {
                                str3 = "-" + App.list3.get(index1).get(index2).get(index3);
                            }
                        }

                        filterTv.setText("章节-编号：" + str1 + str2 + str3);
                        //根据章节筛选
                        DeptInfo deptInfo = App.billIdTreeList.get(index1);
                        if (deptInfo == null || TextUtil.isEmptyList(deptInfo.getChildren())) {
                            ToastUtil.show(context, R.string.no_data);
                            adapter.setData(null);
                            return;
                        }
                        DeptInfo.ChildrenBeanX childrenBean2 = deptInfo.getChildren().get(index2);
                        if (childrenBean2 == null || TextUtil.isEmptyList(childrenBean2.getChildren())) {
                            ToastUtil.show(context, R.string.no_data);
                            adapter.setData(null);
                            return;
                        }
                        //章节第三层
                        BILL_ID_3 = childrenBean2.getChildren().get(index3).getId();
                        getSearchData("?thirdBillId=" + BILL_ID_3);
                    }
                }).setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterTv.setText("章节-编号：全部");
                        getSearchData("");
                    }
                }).setCancelText("全部").setSubmitColor(ContextCompat.getColor(context, R.color.mainColor))
                        .setCancelColor(ContextCompat.getColor(context, R.color.mainColor)).build();
                pvOptions.setPicker(App.list1, App.list2, App.list3);
                pvOptions.show();

            }
        });


        listView = (ListView) findViewById(R.id.listView);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh(0);
        mRefreshLayout.setPrimaryColors(Color.WHITE);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                searchEt.setText("");
                filterTv.setText("章节-编号：全部");
            }
        });
        Utils.setLoadHeaderFooter(context, mRefreshLayout);//设置头部,底部样式

        adapter = new PlanListAdapter(this);
        listView.setAdapter(adapter);


        searchEt = findViewById(R.id.search_et);
        titleLayout = findViewById(R.id.activity_title_layout);
        cancleBt = findViewById(R.id.search_cancle_bt);
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
                            getSearchData(BILL_ID_3 > 0 ? "?thirdBillId=" + BILL_ID_3 : "");
                        }
                    });
                    titleLayout.setVisibility(View.GONE);
                    filterTv.setVisibility(View.GONE);
                } else {
                    cancleBt.setVisibility(View.GONE);
                    titleLayout.setVisibility(View.VISIBLE);
                    filterTv.setVisibility(View.VISIBLE);
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
                        getSearchData("?pname=" + String.valueOf(editable).trim());
                    }
                }, 100);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSearchData(BILL_ID_3 > 0 ? "?thirdBillId=" + BILL_ID_3 : "");
    }


    //获取数据
    private void getSearchData(String searchText) {
        adapter.setData(null);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizPlan/list"+searchText;
        Response.Listener<List<CommonInfo>> successListener = new Response.Listener<List<CommonInfo>>() {
            @Override
            public void onResponse(List<CommonInfo> result) {
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

        Request<List<CommonInfo>> request = new GsonRequest<List<CommonInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<CommonInfo>>() {
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
    protected void onPause() {
        super.onPause();
        DialogUtils.hideKeyBorad(context);
    }

    public void onAddClick(View view) {
        startActivity(new Intent(context, PlanAddActivity.class));
    }
}