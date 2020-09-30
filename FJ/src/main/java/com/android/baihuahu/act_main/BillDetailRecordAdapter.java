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

import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.adapter.FileListAdapter;
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
public class BillDetailRecordAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<RecordInfo> mList;
    private BillDetailActivity context;

    public BillDetailRecordAdapter(BillDetailActivity context) {
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
            convertView = inflater.inflate(R.layout.item_ledger_detail_lv, parent, false);
            holder.timeTv = (TextView) convertView.findViewById(R.id.ledger_item_time_tv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.ledger_item_name_tv);
            holder.remarkTv = (TextView) convertView.findViewById(R.id.ledger_item_remark_tv);
            holder.toCheckTv = (TextView) convertView.findViewById(R.id.to_checke_tv);
            holder.checkedTitleTv = (TextView) convertView.findViewById(R.id.checked_title_tv);
            holder.checkedReamrkTv = (TextView) convertView.findViewById(R.id.checked_remark_tv);
            holder.listView = (ScrollListView) convertView.findViewById(R.id.horizontal_gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecordInfo info = mList.get(position);
        final int itemId = info.getId();
        if (info != null) {
            holder.timeTv.setText(TextUtil.subTimeYMDHm(info.getCreateTime()));

            holder.nameTv.setText("汇报人：" + info.getCreatorName() +
                    "　　汇报数量：" + TextUtil.remove_0(info.getReportNum()));

            String reportRemark = info.getReportRemark();
            holder.remarkTv.setText("备注：" + reportRemark);
            holder.remarkTv.setVisibility(TextUtil.isEmpty(reportRemark) ? View.GONE : View.VISIBLE);

            List<FileListInfo> fileData = new ArrayList<>();
            List<FileInfo> attachment = info.getAttachment();
            if (attachment != null) {
                for (FileInfo att : attachment) {
                    fileData.add(new FileListInfo(att.name, att.url, Constant.TYPE_SEE));
                }
            }
            FileListAdapter fileListAdapter = new FileListAdapter(context, fileData);
            holder.listView.setAdapter(fileListAdapter);

            String updatorName = info.getUpdatorName();
            if (TextUtil.isEmpty(updatorName)) {
                //复核
                holder.toCheckTv.setVisibility(View.VISIBLE);
                holder.checkedTitleTv.setVisibility(View.GONE);
                holder.checkedReamrkTv.setVisibility(View.GONE);
                holder.toCheckTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialDialog.Builder dialog = DialogUtils.getInputDialog(context,
                                "备注");
                        dialog.title("是否复核").onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String remark = ((EditText) dialog.getCustomView()).getText().toString();
                                //复核
                                post(itemId, remark, "1", dialog);
                            }
                        }).negativeText("驳回").negativeColorRes(R.color.status_rejuct).onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String remark = ((EditText) dialog.getCustomView()).getText().toString();
                                //驳回
                                post(itemId, remark, "2", dialog);

                            }
                        }).show();
                    }
                });
            } else {
                //已复核
                holder.toCheckTv.setVisibility(View.GONE);
                holder.checkedTitleTv.setVisibility(View.VISIBLE);
                holder.checkedReamrkTv.setVisibility(View.VISIBLE);

                String confirmResult = info.getConfirmResult();
                String updateTime = TextUtil.subTimeYMDHm(info.getUpdateTime());
                String statusText = "1".equals(confirmResult) ? "<font color='#8bd3c0' >已复核</font>" :
                        "<font color='#f98444' >已驳回</font>";

                holder.checkedTitleTv.setText(Html.fromHtml("复核人：" + updatorName
                        + "　　复核状态：" + statusText + "<br>复核时间：" + updateTime));


                String comfirmRemark = info.getComfirmRemark();
                holder.checkedReamrkTv.setVisibility(TextUtil.isEmpty(comfirmRemark) ? View.GONE : View.VISIBLE);
                holder.checkedReamrkTv.setText("备注：" + comfirmRemark);
            }
        }
        return convertView;
    }

    private void post(int itemId, String comfirmRemark, String confirmResult, final MaterialDialog dialog) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            return;
        }
        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.id, itemId);
        map.put(KeyConst.confirmResult, confirmResult);
        map.put(KeyConst.comfirmRemark, comfirmRemark);
        JSONObject jsonObject = new JSONObject(map);

        String url = Constant.WEB_SITE + "/biz/bizBill/report";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result != null) {
                    ToastUtil.show(context, R.string.commit_success);
                    context.refreshData();
                    dialog.dismiss();
                } else {
                    ToastUtil.show(context, R.string.commit_faild);
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
        private TextView nameTv, remarkTv, checkedTitleTv, checkedReamrkTv, toCheckTv, timeTv;
        private ScrollListView listView;


    }

}














