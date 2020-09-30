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

package com.android.baihuahu.act_main;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dylan
 */
public class LocalMaterailDetailRecordAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int materialId;
    private List<RecordInfo> mList;
    private LocalMaterailDetailActivity context;

    public LocalMaterailDetailRecordAdapter(LocalMaterailDetailActivity context, int materialId) {
        this.context = context;
        this.materialId = materialId;
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
            convertView = inflater.inflate(R.layout.item_local_materail_detail_lv, parent, false);
            holder.timeTagTv = (TextView) convertView.findViewById(R.id.time_tag_tv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.report_name_tv);
            holder.toCheckTv = (TextView) convertView.findViewById(R.id.to_checke_tv);
            holder.remarkTv = (TextView) convertView.findViewById(R.id.remark_tv);

            holder.checkedTitleTv = (TextView) convertView.findViewById(R.id.checked_title_tv);
            holder.checkedReamrkTv = (TextView) convertView.findViewById(R.id.checked_remark_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final RecordInfo info = mList.get(position);
        if (info != null) {
            holder.timeTagTv.setText(TextUtil.subTimeYMDHm(info.getCreateTime()));

            int status = info.getStatus();

            String remark = info.getUseRemark();
            if (!TextUtil.isEmpty(remark)) {
                holder.remarkTv.setVisibility(View.VISIBLE);
                holder.remarkTv.setText("备注：" + remark);
            } else {
                holder.remarkTv.setVisibility(View.GONE);
            }


            final String useNum = TextUtil.remove_0(info.getUseNum() + "");
            holder.nameTv.setText("汇报人：" + info.getCreatorName() +
                    "　　使用数量：" + useNum +
                    "\n车牌号：" + TextUtil.remove_N(info.getNumberPlate()));

            //---------------------------- 复核 ----------------------------------
            final String auditRemark = info.getAuditRemark();
            holder.checkedTitleTv.setVisibility(1==status ? View.GONE : View.VISIBLE);
            holder.checkedReamrkTv.setVisibility(1==status  || TextUtil.isEmpty(auditRemark) ? View.GONE : View.VISIBLE);

            holder.checkedTitleTv.setText("复核人：" + info.getUpdatorName() +
                    "　　复核数量：" + TextUtil.remove_0(info.getAuditNum()) +
                    "\n复核时间：" + TextUtil.subTimeYMDHm(info.getUpdateTime()));
            holder.checkedReamrkTv.setText("备注：" + auditRemark);

            //---------------------------- 复核 --------------------------------
            holder.toCheckTv.setVisibility(1==status  ? View.VISIBLE : View.GONE);//复核按钮
            //复核
            holder.toCheckTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context, R.style.dialog_appcompat_theme);
                    View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog_audit_local_materail_num, null);
                    final EditText auditNumEt = inflate.findViewById(R.id.audit_num_et);
                    final EditText remarkEt = inflate.findViewById(R.id.audit_remark_et);
                    auditNumEt.setText(useNum);
                    auditNumEt.setSelection(auditNumEt.getText().length());
                    remarkEt.setText(auditRemark);
                    inflate.findViewById(R.id.commit_bt).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String auditNum = auditNumEt.getText().toString();
                            String auditRemark2 = remarkEt.getText().toString();
                            if (ToastUtil.showCannotEmpty(context, auditNum, "复核数量")) {
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

        }
        return convertView;
    }

    private void post(RecordInfo info, String auditNum, String auditRemark, final Dialog dialog) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            return;
        }
        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.auditNum, auditNum);
        map.put(KeyConst.auditRemark, auditRemark);

        map.put(KeyConst.id, info.getId());
        map.put(KeyConst.materialId, materialId);
        map.put(KeyConst.numberPlate, info.getNumberPlate());
        map.put(KeyConst.useNum, info.getUseNum());
        map.put(KeyConst.useRemark, info.getUseRemark());

        String url = Constant.WEB_SITE + "/biz/bizMaterial/useRecord";
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result != null) {
                    dialog.cancel();
                    ToastUtil.show(context, "修改成功");
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
        private TextView nameTv, remarkTv, checkedTitleTv, checkedReamrkTv, toCheckTv, timeTagTv;
        private ScrollListView listView;


    }

}














