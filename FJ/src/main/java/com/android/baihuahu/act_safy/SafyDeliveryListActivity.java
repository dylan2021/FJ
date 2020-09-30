package com.android.baihuahu.act_safy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.DialogUtils;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 */
public class SafyDeliveryListActivity extends BaseFgActivity {
    private SafyDeliveryListActivity context;
    private ListView mListView;
    private SafyDeliveryListAdapter adapter;
    private RefreshLayout mRefreshLayout;
    private EditText searchEt;
    private CommonInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_quality_check_list);
        context = this;
        initTitleBackBt("安全交底");
        info = (CommonInfo) getIntent().getSerializableExtra(KeyConst.OBJ_INFO);

        initView();
    }


    private void initView() {
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        mListView = (ListView) findViewById(R.id.common_list_view);
        adapter = new SafyDeliveryListAdapter(context);
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
        searchEt.setHint("搜索交底人");
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
                        getSearchData("?deliverer=" + String.valueOf(editable).trim());
                    }
                }, 100);
            }
        });

        Button rightBt = getTitleRightBt("交底日期");
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();

            }
        });

    }

    private long startTimeL, endTimeL;
    private String startTimeStr = "19800101", endTimeStr = "20500101";

    private void filter() {
        final Dialog dialog = new Dialog(context, R.style.dialog_top_to_bottom);
        dialog.setCanceledOnTouchOutside(true);

        View inflate = LayoutInflater.from(context).inflate(R.layout.
                layout_top_filter_msg, null);

        final TextView startTimeTv = inflate.findViewById(R.id.filter_time_bt_0);
        if (startTimeL > 0) {
            startTimeTv.setText(TimeUtils.getTimeYmd(startTimeL));
            startTimeTv.setTextColor(context.getResources().getColor(R.color.mainColor));
        }
        final TextView endTimeTv = inflate.findViewById(R.id.filter_time_bt_1);
        if (endTimeL > 0) {
            endTimeTv.setText(TimeUtils.getTimeYmd(endTimeL));
            endTimeTv.setTextColor(context.getResources().getColor(R.color.mainColor));
        }
        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                switch (v.getId()) {

                    //筛选 - 确定
                    case R.id.filter_ok_bt:

                        if (startTimeL > 0) {
                            startTimeStr = TimeUtils.getTimeYmd(startTimeL);
                        }
                        if (endTimeL > 0) {
                            endTimeStr = TimeUtils.getTimeYmd(endTimeL);
                        }
                        String url_p = "?startDate=" + startTimeStr
                                + "&endDate=" + endTimeStr;

                        getSearchData(url_p);
                        dialog.dismiss();
                        break;
                    case R.id.filter_cancel_bt:
                        dialog.dismiss();
                        break;
                    case R.id.filter_time_bt_0:
                        setTime(0, startTimeTv);
                        break;
                    case R.id.filter_time_bt_1:
                        setTime(1, endTimeTv);
                        break;
                }
            }
        };
        inflate.findViewById(R.id.filter_ok_bt).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.filter_cancel_bt).setOnClickListener(mDialogClickLstener);

        startTimeTv.setOnClickListener(mDialogClickLstener);
        endTimeTv.setOnClickListener(mDialogClickLstener);
        dialog.setContentView(inflate);

        DialogUtils.setDialogWindow(context, dialog, Gravity.TOP);
    }

    private void setTime(final int type, final TextView timeTv) {
        TimePickerDialog.Builder timePickerDialog = DialogUtils.getTimePicker_Ymd(context);
        timePickerDialog.setCallBack(new OnDateSetListener() {
            @Override
            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                if (0 == type) {
                    if (millseconds > System.currentTimeMillis()) {
                        ToastUtil.show(context, "开始时间不能大于现在");
                        return;
                    }
                    if (endTimeL != 0 && millseconds >= endTimeL) {
                        ToastUtil.show(context, "开始时间要小于结束时间");
                        return;
                    }
                    startTimeL = millseconds;
                } else {
                    if (millseconds < startTimeL) {
                        ToastUtil.show(context, "结束时间不能小于开始时间");
                        return;
                    }
                    endTimeL = millseconds;
                }

                timeTv.setText(TimeUtils.getTimeYmd(millseconds));
                timeTv.setTextColor(ContextCompat.getColor(context, R.color.mainColor));

            }
        });

        timePickerDialog.build().show(context.getSupportFragmentManager(), "");
    }

    //获取数据
    private void getSearchData(String searchText) {
        adapter.setData(null);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizDelivery/list" + searchText;
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
                params.put(KeyConst.projectId, App.projecId);
                return params;
            }
        };
        App.requestQueue.add(request);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSearchData("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DialogUtils.hideKeyBorad(context);
    }

    public void onAddClick(View view) {
        Intent intent = new Intent(context, SafyDeliveryAddUpdateDeatilActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) info);//序列化,要注意转化(Serializable)
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}