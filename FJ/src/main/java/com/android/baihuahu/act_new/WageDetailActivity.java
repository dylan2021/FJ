package com.android.baihuahu.act_new;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_main.EmplyeeListAdapter;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.bean.WageInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 */
public class WageDetailActivity extends BaseFgActivity {
    private WageDetailActivity context;
    private ListView mListView;
    private EmplyeeListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private int id = 0;
    private LinearLayout itemLayout;
    private long date;
    private String groupName;
    private TextView timeTv;
    private TextView chooseGroupTv, chooseEmplyeeTv, chooseEmplyeeTitleTv;
    private TextView hoursEt, pieceEt, remarkTv;
    private List<WageInfo.BizWagesWorkerListBean> workerInfosList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_wage_detail);
        context = this;
        id = getIntent().getIntExtra(KeyConst.id, 0);
        initTitleBackBt(getIntent().getStringExtra(KeyConst.title));
        initView();
        getData();
    }

    private List<EmplyeeInfo> employeeList = new ArrayList<>();

    private void initView() {
        itemLayout = (LinearLayout) findViewById(R.id.item_layout);
        chooseEmplyeeTitleTv = findViewById(R.id.people_title_tv);
        chooseEmplyeeTv = findViewById(R.id.people_tv);

        timeTv = (TextView) findViewById(R.id.date_tv);
        chooseGroupTv = (TextView) findViewById(R.id.work_end_time_tv);

        //计时,计件
        hoursEt = findViewById(R.id.hours_in_hours_sum_tv);
        pieceEt = findViewById(R.id.hours_in_numbers_sum_tv);

        remarkTv = findViewById(R.id.remark_tv);
    }

    ArrayList<String> workerNameList;

    private void setView(WageInfo info) {
        String workerNameTotalStr = "";
        workerNameList = new ArrayList<>();
        timeTv.setText(info.getWagesDate());
        chooseGroupTv.setText(info.getGroupName());
        hoursEt.setText(info.getTimes() + "");
        pieceEt.setText(info.getPiece() + "");
        remarkTv.setText(info.getRemark());

        workerInfosList = info.getBizWagesWorkerList();
        final int size = workerInfosList.size();
        for (int i = 0; i < size; i++) {
            WageInfo.BizWagesWorkerListBean bean = workerInfosList.get(i);
            String workerName = bean.getWorkerName();
            String times = TextUtil.remove_0(bean.getTimes()+"");
            String timeWages = TextUtil.remove_0(bean.getTimeWages()+"");
            String piece = TextUtil.remove_0(bean.getPiece()+"");
            String pieceWages = TextUtil.remove_0(bean.getPieceWages()+"");
            String itemContent = workerName + "(计时:" + times + "小时，计时工资:"+timeWages
                    +"元，计件:" + piece +"，计件工资:"+pieceWages+ "元)";
            workerNameList.add(itemContent);
            if (i == 0) {
                workerNameTotalStr = workerName;
            } else {
                workerNameTotalStr = workerNameTotalStr + "," + workerName;
            }
        }
        chooseEmplyeeTv.setText(workerNameTotalStr);
        chooseEmplyeeTitleTv.setText("人员(" + size + "人)");

        chooseEmplyeeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context).title("工资详情(" + size + "人)")
                        .items(workerNameList)
                        .positiveText(R.string.close)
                        .positiveColorRes(R.color.mainColor).show();

            }
        });

    }


    private JSONArray choosedEmployeeIds = new JSONArray();

    private void getData() {
        String url = Constant.WEB_SITE + "/biz/bizWages/view?id=" + id;
        Response.Listener<WageInfo> successListener = new Response.Listener<WageInfo>() {
            @Override
            public void onResponse(WageInfo info) {
                if (info != null) {
                    setView(info);
                } else {
                    ToastUtil.show(context, R.string.server_exception);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(context, R.string.server_exception);
            }
        };

        Request<WageInfo> request = new GsonRequest<WageInfo>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<WageInfo>() {
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

    private void showEmplyeeChoosedDialog(final List<EmplyeeInfo> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme_fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_choose_emplyee_layout, null);

        LinearLayout itemsLayout = (LinearLayout) v.findViewById(R.id.emplyee_seleted_items_layout);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final EmplyeeInfo info = list.get(i);
                if (info == null) {
                    return;
                }
                View itemView = View.inflate(context, R.layout.dialog_choose_emplyee_item, null);
                SimpleDraweeView iconIv = (SimpleDraweeView) itemView.findViewById(R.id.emplyee_icon_iv);
                final TextView seleteTv = (TextView) itemView.findViewById(R.id.emplyee_select_tv);
                final TextView nameTv = (TextView) itemView.findViewById(R.id.emplyee_name_tv);
                final TextView groupTv = (TextView) itemView.findViewById(R.id.emplyee_group_tv);

                iconIv.setImageURI(Constant.WEB_FILE_SEE);
                nameTv.setText(info.getName());
                String gender = Utils.getDictNameByValue(context, KeyConst.GENDER, info.getGender());
                groupTv.setText(gender + "  " + info.getAge() + "岁");

                seleteTv.setSelected(info.getSeleted());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean selected = seleteTv.isSelected();
                        seleteTv.setSelected(!selected);

                        info.setSeleted(!selected);
                    }
                });
                itemsLayout.addView(itemView);
            }

        } else {
            ToastUtil.show(context, "无人员");
        }
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);

        v.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        v.findViewById(R.id.emplyee_seleted_save_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    EmplyeeInfo info = list.get(i);
                    boolean seleted = info.getSeleted();
                    employeeList.get(i).setSeleted(seleted);
                }
                dialog.dismiss();


                choosedEmployeeIds = new JSONArray();
                String nameStr = "";
                //正式
                for (int i = 0; i < employeeList.size(); i++) {
                    EmplyeeInfo info = employeeList.get(i);
                    if (info.getSeleted()) {
                        choosedEmployeeIds.put(info.getId());
                    }

                    String name = info.getName();
                    if (choosedEmployeeIds.length() == 1) {
                        nameStr = name;
                    } else if (choosedEmployeeIds.length() < Constant.EMPLYEE_SHOW_NUMBER) {
                        nameStr = name + "、" + nameStr;
                    }

                }
                if (choosedEmployeeIds.length() >= Constant.EMPLYEE_SHOW_NUMBER) {
                    nameStr = nameStr + getString(R.string.ellipsis_more) +
                            choosedEmployeeIds.length() + "人";
                }

                chooseEmplyeeTv.setText(nameStr);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != mRefreshLayout) {
            mRefreshLayout.autoRefresh(0);
        }
    }

}