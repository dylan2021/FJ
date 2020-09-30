package com.android.baihuahu.act_safy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.adapter.FileListAdapter;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.bean.FileInfo;
import com.android.baihuahu.bean.FileListInfo;
import com.android.baihuahu.bean.RecordInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
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
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 * 附件
 */

public class DangerDetailActivity extends BaseFgActivity {

    private ListView processLv;
    private DangerDetailActivity context;
    private DangerDetailRecordAdapter adapter;
    private int id;
    private String title;
    private TextView contentTv,chooseSingerTv,dateValueTv;
    private RefreshLayout mRefreshLayout;
    private String unitName;
    private CommonInfo info;
    private List<FileListInfo> fileData=new ArrayList<>();
    private boolean isTypeSafy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_danger_detail);
        context = this;

        Intent i = getIntent();
        title = i.getStringExtra(KeyConst.title);
        info = (CommonInfo) i.getSerializableExtra(KeyConst.OBJ_INFO);
        isTypeSafy = i.getBooleanExtra(KeyConst.type, false);
        initTitleBackBt(title);

        initView();
        initFileView(info.dangerPic);
    }
    private void initFileView(List<FileInfo> attList) {
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        TextView fileTitleTv = (TextView) findViewById(R.id.card_detail_file_title);
        fileTitleTv.setVisibility(View.GONE);
        ScrollListView listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
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
    private void initView() {
        contentTv = findViewById(R.id.check_content_et);//描述
        dateValueTv = findViewById(R.id.date_tv);
        chooseSingerTv = (TextView) findViewById(R.id.singer_name_tv);

        contentTv.setText(info.dangerDesc);
        dateValueTv.setText(info.rectifyDate);

        chooseSingerTv.setText(info.signerName);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        processLv = (ListView) findViewById(R.id.process_lv);
        adapter = new DangerDetailRecordAdapter(context,id,isTypeSafy);
        processLv.setAdapter(adapter);
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
                getData();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    public void notifyData(){
        getData();
   }

    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            mRefreshLayout.finishRefresh(0);
            return;
        }
        getRecordData();
    }


    private void getRecordData() {
        String url_p=isTypeSafy?"/biz/bizSecurity/report/list?dangerId=":
                "/biz/bizRectify/list?dangerId=";
        String url = Constant.WEB_SITE + url_p+ info.id;
        Response.Listener<List<RecordInfo>> successListener = new Response.Listener
                <List<RecordInfo>>() {
            @Override
            public void onResponse(List<RecordInfo> result) {
                mRefreshLayout.finishRefresh(0);
                Log.d(TAG, "安全数据"+result);
                if (context == null || TextUtil.isEmptyList(result)) {
                    return;
                }
                adapter.setData(result);
                processLv.setSelection(0);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mRefreshLayout.finishRefresh(0);
                ToastUtil.show(context, R.string.server_exception);
            }
        };

        Request<List<RecordInfo>> request = new GsonRequest<List<RecordInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<RecordInfo>>() {
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


    public void onRectifyClick(View view) {
        Intent i = new Intent(context, DangerRectifyAddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) info);//序列化,要注意转化(Serializable)
        i.putExtras(bundle);
        i.putExtra(KeyConst.title, "新增隐患反馈");
        i.putExtra(KeyConst.type, isTypeSafy);
        startActivity(i);
    }
}
