/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.baihuahu.act_new;

import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.adapter.FileListAdapter;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.bean.DeviceAttendInfo;
import com.android.baihuahu.bean.FileInfo;
import com.android.baihuahu.bean.FileListInfo;
import com.android.baihuahu.bean.RecordInfo;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.DialogUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.view.ScrollListView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dylan
 */
public class ProcessDetailRecordAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<RecordInfo> mList;
    private ProcessDetailActivity context;

    public ProcessDetailRecordAdapter(ProcessDetailActivity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<RecordInfo> infos) {
        mList = infos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_process_detail_lv, parent, false);
            holder.timeTagTv = (TextView) convertView.findViewById(R.id.time_tag_tv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.report_name_tv);
            holder.hourTv = (TextView) convertView.findViewById(R.id.attend_period_tv);
            holder.wageTv = (TextView) convertView.findViewById(R.id.attend_wage_tv);
            holder.remarkTv = (TextView) convertView.findViewById(R.id.remark_tv);
            holder.toCheckTv = (TextView) convertView.findViewById(R.id.to_checke_tv);
            holder.listView = (ScrollListView) convertView.findViewById(R.id.horizontal_gridview);


            holder.checkedTitleTv = (TextView) convertView.findViewById(R.id.checked_title_tv);
            holder.checkedReamrkTv = (TextView) convertView.findViewById(R.id.checked_remark_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final RecordInfo info = mList.get(position);

        holder.timeTagTv.setText(TextUtil.subTimeYMDHm(info.getWriteTime()));
        holder.nameTv.setText("汇报人：" + info.getWritorName());

        final double writeValue = info.getWriteValue();
        holder.hourTv.setText("本次已完成产值(万元)：" + writeValue);
        double writeCycle = info.getWriteCycle();
        holder.wageTv.setText("实际工期(天)：" +writeCycle);
        String remark = "";
        if (!TextUtil.isEmpty(remark)) {
            holder.remarkTv.setVisibility(View.VISIBLE);
            holder.remarkTv.setText("备注：" + remark);
        } else {
            holder.remarkTv.setVisibility(View.GONE);
        }


        List<FileListInfo> fileData = new ArrayList<>();
        List<FileInfo> attachment = info.getWriteAttachment();
        if (attachment != null) {
            for (FileInfo att : attachment) {
                fileData.add(new FileListInfo(att.name, att.url, Constant.TYPE_SEE));
            }
        }
        FileListAdapter fileListAdapter = new FileListAdapter(context, fileData);
        holder.listView.setAdapter(fileListAdapter);

        //---------------------------- 复核 ----------------------------------
        int status = info.getStatus();

        holder.checkedTitleTv.setVisibility(0==status ? View.GONE : View.VISIBLE);
        final String auditRemark = info.getAuditRemark();
        holder.checkedReamrkTv.setVisibility(0==status  || TextUtil.isEmpty(auditRemark) ? View.GONE : View.VISIBLE);

        holder.checkedTitleTv.setText("复核产值：" + info.getSignValue() +
                "　　复核人：" + info.getSignorName() +
                "\n复核时间：" + TextUtil.subTimeYMDHm(info.getSignTime()));
        holder.checkedReamrkTv.setText("备注：" + auditRemark);

        //---------------------------- 复核 --------------------------------
        holder.toCheckTv.setVisibility(0 == status ? View.VISIBLE : View.GONE);//确认按钮
        //复核
        holder.toCheckTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context, R.style.dialog_appcompat_theme);
                View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog_audit_process, null);
                final EditText auditNumEt = inflate.findViewById(R.id.audit_num_et);
                final EditText remarkEt = inflate.findViewById(R.id.audit_remark_et);
                auditNumEt.setText(writeValue+"");
                auditNumEt.setSelection(auditNumEt.getText().length());
                inflate.findViewById(R.id.commit_bt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String auditNum = auditNumEt.getText().toString();
                        String auditRemark2 = remarkEt.getText().toString();
                        if (ToastUtil.showCannotEmpty(context, auditNum, "确认产值")) {
                            return;
                        }
                        post(info, auditNum, auditRemark2, dialog);
                    }
                });
                inflate.findViewById(R.id.cancel_bt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.setContentView(inflate);

                DialogUtils.setDialogWindow200(context, dialog, Gravity.CENTER);
                DialogUtils.showKeyBorad(auditNumEt, context);

            }
        });

        return convertView;
    }

    private void post(RecordInfo info, String signValue, String auditRemark, final Dialog dialog) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            return;
        }
        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.id, info.getId());
        map.put(KeyConst.signValue, signValue);
        map.put(KeyConst.signAttachment, null);

        String url = Constant.WEB_SITE + "/biz/bizPlan/sign";
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result != null) {
                    dialog.cancel();
                    ToastUtil.show(context, "提交成功");
                    context.notifyData();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.show(context, context.getString(R.string.server_exception));
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

    public class ViewHolder {
        private TextView nameTv, hourTv, toCheckTv, remarkTv, wageTv, timeTagTv,checkedReamrkTv,checkedTitleTv;
        private ScrollListView listView;


    }

}














