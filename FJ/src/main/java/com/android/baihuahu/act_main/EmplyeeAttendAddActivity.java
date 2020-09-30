package com.android.baihuahu.act_main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.drawee.view.SimpleDraweeView;
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
public class EmplyeeAttendAddActivity extends BaseFgActivity {
    private EmplyeeAttendAddActivity context;
    private ListView mListView;
    private EmplyeeListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private int groupId = 0;
    private LinearLayout itemLayout;
    private EditText remarkTv;
    private long startTime, endTime;
    private TextView startTimeValueTv;
    private TextView endTimeValueTv;
    private EditText durationHourTv;
    private boolean isGroupList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_attend_set);
        context = this;
        initTitleBackBt("添加考勤");
        groupId = getIntent().getIntExtra(KeyConst.id, 0);
        isGroupList = getIntent().getBooleanExtra(KeyConst.type, false);

        employeeList = (List<EmplyeeInfo>) getIntent().getSerializableExtra(KeyConst.OBJ_INFO);
        initView();
    }

    private void initView() {
        itemLayout = (LinearLayout) findViewById(R.id.item_layout);
        remarkTv = (EditText) findViewById(R.id.remark_tv);

        startTimeValueTv = (TextView) findViewById(R.id.date_tv);
        endTimeValueTv = (TextView) findViewById(R.id.work_end_time_tv);
        durationHourTv = (EditText) findViewById(R.id.hours_tv);
        durationHourTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (TextUtil.setInput1Dot(durationHourTv, s)) {
                    return;
                }
             /*   if (!TextUtil.isEmpty(s.toString())) {
                    double num = TextUtil.convertToDouble(s.toString(), 0);
                    if (num > 24) {
                        ToastUtil.show(context, "时长不得超过24小时");
                        durationHourTv.setText("");
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        startTimeValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickeTimeDilog((TextView) v, 0);
            }
        });
        endTimeValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickeTimeDilog((TextView) v, 1);
            }
        });
    }

    private void showPickeTimeDilog(final TextView timeBt, final int type) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (type == 0) {
                            if (millseconds > System.currentTimeMillis()) {
                                ToastUtil.show(context, "开始时间不能大于现在");
                                return;
                            }
                            if (endTime != 0 && millseconds > endTime) {
                                ToastUtil.show(context, "开始时间不能大于结束时间");
                                return;
                            }
                            startTime = millseconds;
                        } else {
                            if (millseconds < startTime) {
                                ToastUtil.show(context, "结束时间不能小于开始时间");
                                return;
                            }
                            endTime = millseconds;
                        }
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


    private JSONArray workerIdList = new JSONArray();
    private JSONArray tempWorkerIdList = new JSONArray();

    public void onEmplyeeChooseClick(View view) {
        showEmplyeeChoosedDialog(employeeList);
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
               /* String groupName = info.getSectionName() + " - " + info.getBuildSiteName()
                        + " - " + info.getGroupName();*/
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

                notifyItemView();//刷新人员列表
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
            }
        });
    }

    private void notifyItemView() {
        itemLayout.removeAllViews();
        int size = employeeList.size();
        for (int i = 0; i < size; i++) {
            EmplyeeInfo info = employeeList.get(i);
            if (info.getSeleted()) {
                final View itemView = View.inflate(context, R.layout.item_emplyee, null);
                ImageView rightIv = itemView.findViewById(R.id.right_iv);
                SimpleDraweeView iconIv = (SimpleDraweeView) itemView.findViewById(R.id.emplyee_icon_iv);
                TextView nameTv = itemView.findViewById(R.id.emplyee_name_tv);
                TextView groupTv = itemView.findViewById(R.id.emplyee_group_tv);
                rightIv.setImageResource(R.drawable.ic_delete_gray_circle);

                nameTv.setText(info.getName());

                iconIv.setImageURI( ImageUtil.getImageUrl(info.getPic()));
                final String namePhone = info.getName() + (info.getPhone() == null ? "" : "(" + info.getPhone() + ")");
                nameTv.setText(namePhone);
                //String groupName = info.getSectionName() + " - " + info.getBuildSiteName() + " - " + info.getGroupName();
                String gender = Utils.getDictNameByValue(context, KeyConst.GENDER, info.getGender());
                groupTv.setText(gender + "  " + info.getAge() + "岁");

                final int finalI = i;
                rightIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemLayout.removeView(itemView);
                        employeeList.get(finalI).setSeleted(false);
                    }
                });

                itemLayout.addView(itemView);
            }
        }

    }

    //设置考勤
    private void postData(JSONArray idList, int type) {

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConst.startTime, TimeUtils.getTimeYmdHms(startTime));
        map.put(KeyConst.endTime, TimeUtils.getTimeYmdHms(endTime));
        map.put(KeyConst.remark, remarkTv.getText().toString());

        map.put(KeyConst.groupId, groupId);
        map.put(KeyConst.workerIdList, idList);
       /* if (type == 0) {
            map.put(KeyConst.workerIdList, idList);
        } else {
            map.put(KeyConst.tempWorkerIdList, idList);
        }*/

        String url = Constant.WEB_SITE + (isGroupList ? "/biz/bizWorker/attend" : "/biz/tempWorker/attend");
        Log.d(TAG, "考勤数据:" + map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            // ToastUtil.show(context, "添加考勤失败,稍后重试");
                            return;
                        }

                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (context == null || context.isFinishing()) {
                    return;
                }
                if (error != null && error.toString().contains("End of input at character 0 of")) {
                    ToastUtil.show(context, "考勤添加成功");
                    context.finish();
                    return;
                }
                String errorMsg = TextUtil.getErrorMsg(error);
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
        if (startTime == 0) {
            ToastUtil.show(context, "开始时间" + getString(R.string.cannot_empty));
            return;
        }
        if (endTime == 0) {
            ToastUtil.show(context, "结束时间" + getString(R.string.cannot_empty));
            return;
        }
        String hourStr = durationHourTv.getText().toString();
        if (ToastUtil.showCannotEmpty(context, hourStr, "考勤时长不能为空")) {
            return;
        }
        workerIdList = new JSONArray();
        tempWorkerIdList = new JSONArray();
        //正式
        for (int i = 0; i < employeeList.size(); i++) {
            EmplyeeInfo info = employeeList.get(i);
            if (info.getSeleted()) {
               /* if (info.getType() == 0) {
                    workerIdList.put(info.getId());
                } else {
                    tempWorkerIdList.put(info.getId());
                }*/
                workerIdList.put(info.getId());
            }
        }
        if (workerIdList.length() == 0) {
            ToastUtil.show(context, "请至少选择一个人员");
            return;
        }
        //公共人员
        postData(workerIdList, 0);
      /*  //临聘人员
        if (tempWorkerIdList.length() > 0) {
            postData(tempWorkerIdList, 1);
        }*/
    }
}