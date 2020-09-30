package com.android.baihuahu.act_new;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_main.EmplyeeListAdapter;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.bean.GroupItemInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 */
public class WageAddActivity extends BaseFgActivity {
    private WageAddActivity context;
    private ListView mListView;
    private EmplyeeListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private int groupId = 0;
    private LinearLayout itemLayout;
    private EditText remarkTv;
    private long date;
    private String groupName;
    private TextView startTimeValueTv;
    private TextView chooseGroupTv, chooseEmplyeeTv;
    private EditText durationHourTv;
    private boolean isGroupList;
    private EditText hoursSumEt, totalNumEt;
    private JSONArray bizWagesWorkerList = new JSONArray();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_wage_add);
        context = this;
        initTitleBackBt("工资录入");
        initView();
    }

    private void initView() {
        itemLayout = (LinearLayout) findViewById(R.id.item_layout);
        chooseEmplyeeTv = findViewById(R.id.people_tv);
        remarkTv = (EditText) findViewById(R.id.remark_tv);

        //计时,计件
        hoursSumEt = findViewById(R.id.hours_in_hours_sum_tv);
        totalNumEt = findViewById(R.id.hours_in_numbers_sum_tv);

        startTimeValueTv = (TextView) findViewById(R.id.date_tv);
        chooseGroupTv = (TextView) findViewById(R.id.work_end_time_tv);
        startTimeValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickeTimeDilog((TextView) v);
            }
        });
        getGroupList();
        chooseGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deptList.size() == 0) {
                    getGroupList();
                } else {
                    ArrayList<String> arr = new ArrayList<>();
                    for (GroupItemInfo deptInfo : deptList) {
                        arr.add(deptInfo.getName());
                    }
                    new MaterialDialog.Builder(context)
                            .items(arr)
                            .title("选择班组")
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView,
                                                        int position, CharSequence text) {
                                    groupId = deptList.get(position).getId();
                                    groupName = text + "";
                                    chooseGroupTv.setText(groupName);

                                    chooseEmplyeeTv.setText("");
                                    bizWagesWorkerList = new JSONArray();

                                    //选择班组后--> 请求改班组下的人员列表数据
                                    getEmployeeData();
                                }
                            })
                            .show();
                }

            }
        });
        chooseEmplyeeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtil.isEmptyList(employeeList)) {
                    ToastUtil.show(context, "该班组暂无人员");
                    getEmployeeData();
                    return;
                }
                showEmplyeeChoosedDialog(employeeList);
            }
        });
    }

    private void getGroupList() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizGroup/list";

        Response.Listener<List<GroupItemInfo>> successListener = new Response
                .Listener<List<GroupItemInfo>>() {
            @Override
            public void onResponse(List<GroupItemInfo> result) {
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, "班组数据为空");
                    return;
                }
                deptList = result;
            }
        };

        Request<List<GroupItemInfo>> versionRequest = new
                GsonRequest<List<GroupItemInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.show(context, "班组数据获取失败");
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

    private List<GroupItemInfo> deptList = new ArrayList<>();

    private void showPickeTimeDilog(final TextView timeBt) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        date = millseconds;
                        String timeStr = TimeUtils.getTimeYmd(millseconds);
                        timeBt.setText(timeStr);
                        //durationHourTv.setText(TimeUtils.betweenHours(startTime, endTime));
                    }
                })
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("")//标题
                .setCyclic(false)
                .setThemeColor(context.getResources().getColor(R.color.mainColor))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextSize(16)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
    }

    //获取班组下的人员列表
    private void getEmployeeData() {
        employeeList.clear();
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizWorker/allWorkerByGroup/" + groupId;
        Response.Listener<List<EmplyeeInfo>> successListener = new Response.Listener<List<EmplyeeInfo>>() {
            @Override
            public void onResponse(List<EmplyeeInfo> result) {
                employeeList = result;
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(context, R.string.server_exception);
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

    private List<EmplyeeInfo> employeeList = new ArrayList<>();

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


                bizWagesWorkerList = new JSONArray();
                String nameStr = "";
                for (int i = 0; i < employeeList.size(); i++) {
                    EmplyeeInfo info = employeeList.get(i);
                    if (info.getSeleted()) {
                        String name = info.getName();
                        nameStr = name + "、" + nameStr;
                    }
                }

                chooseEmplyeeTv.setText(TextUtil.isEmpty(nameStr) ? "" : nameStr.substring(0, nameStr.length() - 1));
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

    public void onCommitClick(View view) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        if (date == 0) {
            ToastUtil.show(context, "日期" + getString(R.string.cannot_empty));
            return;
        }
        int times = Integer.valueOf(hoursSumEt.getText().toString());
        int piece = Integer.valueOf(totalNumEt.getText().toString());

        try {
            int size = employeeList.size();
            Log.d(TAG, "数据size:" + size);
            for (int i = 0; i < size; i++) {
                EmplyeeInfo info = employeeList.get(i);
                if (info.getSeleted()) {

                    Log.d(TAG, info.getId() + "数据:" + info.getName());
                    JSONObject emplyeeInfo = new JSONObject();
                    emplyeeInfo.put(KeyConst.workerId, info.getId());
                    emplyeeInfo.put(KeyConst.workerName, info.getName());
                    emplyeeInfo.put(KeyConst.type, info.getType());

                    emplyeeInfo.put(KeyConst.times, times / size);
                    emplyeeInfo.put(KeyConst.piece, piece / size);
                    bizWagesWorkerList.put(emplyeeInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (bizWagesWorkerList.length() == 0) {
            ToastUtil.show(context, "至少选择一个人员");
            return;
        }

        if (0 == times && 0 == piece) {
            ToastUtil.show(context, "计时/计件数值不可都为空");
            return;
        }


        Map<String, Object> map = new HashMap<>();
        map.put(KeyConst.wagesDate, TimeUtils.getTimeYmd(date));
        map.put(KeyConst.groupId, groupId);
        map.put(KeyConst.groupName, groupName);
        map.put(KeyConst.times, times);//计时
        map.put(KeyConst.piece, piece);//计件

        map.put(KeyConst.bizWagesWorkerList, bizWagesWorkerList);
        map.put(KeyConst.remark, remarkTv.getText().toString());
        String url = Constant.WEB_SITE + "/biz/bizWages/save";
        Log.d(TAG, "工资录入数据:" + map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            return;
                        }
                        ToastUtil.show(context, "添加成功");
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (context == null || context.isFinishing()) {
                    return;
                }
                String errorMsg = TextUtil.getErrorMsg(error);
                Log.d(TAG, "工资录入数据失败:" + errorMsg);
                if (errorMsg != null) {
                    try {
                        JSONObject obj = new JSONObject(errorMsg);
                        if (obj != null) {
                            ToastUtil.showBottom(context, obj.getString(KeyConst.message));
                            return;
                        }
                    } catch (JSONException e) {
                    }
                }
                ToastUtil.show(context, getString(R.string.server_exception));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Content_Type, Constant.application_json);
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                params.put(KeyConst.projectId, App.projecId);

                return params;
            }
        };
        App.requestQueue.add(jsonRequest);
    }
}