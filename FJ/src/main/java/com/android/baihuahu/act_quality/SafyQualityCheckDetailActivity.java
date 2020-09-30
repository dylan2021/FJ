package com.android.baihuahu.act_quality;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_main.EmplyeeListAdapter;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.act_safy.DangerListActivity;
import com.android.baihuahu.adapter.FileListAdapter;
import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.bean.FileInfo;
import com.android.baihuahu.bean.FileListInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.ScrollListView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 */
public class SafyQualityCheckDetailActivity extends BaseFgActivity {
    private SafyQualityCheckDetailActivity context;
    private ListView mListView;
    private EmplyeeListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private int id = 0;
    private TextView timeTv;
    private TextView chooseGroupTv, chooseEmplyeeTv, chooseEmplyeeTitleTv;
    private TextView detailTv, cntentTv, remarkTv;
    private TextView checkThemeTv, checkResultTv;
    private CommonInfo info;
    private boolean isTypeSafy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_quaity_detail);
        context = this;
        Intent i = getIntent();
        id = i.getIntExtra(KeyConst.id, 0);
        isTypeSafy = getIntent().getBooleanExtra(KeyConst.type, false);

        initTitleBackBt(i.getStringExtra(KeyConst.title));
        initView();
        getData();
    }

    private List<EmplyeeInfo> employeeList = new ArrayList<>();

    private void initView() {
        checkThemeTv = findViewById(R.id.check_theme_tv);

        chooseEmplyeeTitleTv = findViewById(R.id.people_title_tv);
        chooseEmplyeeTv = findViewById(R.id.people_tv);

        timeTv = (TextView) findViewById(R.id.date_tv);
        chooseGroupTv = (TextView) findViewById(R.id.work_end_time_tv);

        detailTv = findViewById(R.id.hours_in_hours_sum_tv);
        cntentTv = findViewById(R.id.cntent_tv);
        checkResultTv = findViewById(R.id.check_result_tv);

        remarkTv = findViewById(R.id.remark_tv);
        findViewById(R.id.card_detail_file_title).setVisibility(View.GONE);
    }

    ArrayList<String> workerNameList;

    private void setView() {
        checkThemeTv.setText(info.getTheme());//主题

        workerNameList = new ArrayList<>();
        timeTv.setText(info.getInspectDate());
        chooseGroupTv.setText(info.getGroupName());
        chooseEmplyeeTv.setText(info.inspector);
        detailTv.setText(info.getDetailName());

        cntentTv.setText(info.getContent());
        int inspectResult = info.getInspectResult();
        if (inspectResult==2) {//有隐患
            findViewById(R.id.danger_list_bt).setVisibility(View.VISIBLE);
        }
        checkResultTv.setText(Utils.getCheckStatusText(inspectResult));
        remarkTv.setText(info.getRemark());

        setFileListData(info);//附件
    }

    private List<FileListInfo> fileData = new ArrayList<>();

    private void setFileListData(CommonInfo info) {
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        TextView fileTitleTv = (TextView) findViewById(R.id.card_detail_file_title);
        fileTitleTv.setText(R.string.file_link_list);
        fileTitleTv.setTextColor(ContextCompat.getColor(this, R.color.color999));
        ScrollListView listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        List<FileInfo> attList = info.inspectPic;
        if (attList != null) {
            for (FileInfo att : attList) {
                fileData.add(new FileListInfo(att.name, att.url, Constant.TYPE_SEE));
            }
        }
        if (fileData == null || fileData.size() == 0) {
            findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        } else {
            linkTv.setVisibility(View.GONE);
        }
        FileListAdapter fileListAdapter = new FileListAdapter(this, fileData);
        listView.setAdapter(fileListAdapter);
    }

    private void getData() {
        String url_p = isTypeSafy ? "/biz/bizSecurity/" : "/biz/bizQuality/";
        String url = Constant.WEB_SITE + url_p + id;
        Log.d(TAG, "请求" + url);
        Response.Listener<CommonInfo> successListener = new Response.Listener<CommonInfo>() {
            @Override
            public void onResponse(CommonInfo i) {
                Log.d(TAG, "请求" + i);
                info = i;
                if (info != null) {
                    setView();
                } else {
                    ToastUtil.show(context, R.string.no_data);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(context, R.string.server_exception);
                Log.d(TAG, "请求" + TextUtil.getErrorMsg(volleyError));
            }
        };

        Request<CommonInfo> request = new GsonRequest<CommonInfo>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<CommonInfo>() {
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
        if (null != mRefreshLayout) {
            mRefreshLayout.autoRefresh(0);
        }
    }

    public void onSeeDangerListClick(View view) {
        if (info != null) {
            Intent intent = new Intent(context, DangerListActivity.class);
            //返回选择的数据
            Bundle bundle = new Bundle();
            bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) info);//序列化,要注意转化(Serializable)
            intent.putExtras(bundle);

            intent.putExtra(KeyConst.type, isTypeSafy);
            context.startActivity(intent);
        } else {
            ToastUtil.show(context, R.string.loading);
        }
    }
}