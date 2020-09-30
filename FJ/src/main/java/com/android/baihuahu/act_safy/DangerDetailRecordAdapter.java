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

package com.android.baihuahu.act_safy;

import android.app.Dialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
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
public class DangerDetailRecordAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int materialId;
    private List<RecordInfo> mList;
    private DangerDetailActivity context;
    private boolean isTypeSafy;

    public DangerDetailRecordAdapter(DangerDetailActivity context, int materialId, boolean isTypeSafy) {
        this.context = context;
        this.materialId = materialId;
        this.isTypeSafy = isTypeSafy;
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
            convertView = inflater.inflate(R.layout.item_danger_detail_lv, parent, false);
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
            holder.timeTagTv.setText("反馈记录" + (position + 1));

            int status = info.checkStatus;

            String remark = info.getUseRemark();
            if (!TextUtil.isEmpty(remark)) {
                holder.remarkTv.setVisibility(View.VISIBLE);
                holder.remarkTv.setText("备注：" + remark);
            } else {
                holder.remarkTv.setVisibility(View.GONE);
            }

            String opinionStr = isTypeSafy?info.feedback:info.rectifyOpinion;
            holder.nameTv.setText("反馈时间：" + info.getCreateTime() +
                    "\n反馈意见：" + opinionStr);

            //---------------------------- 复核 ----------------------------------
            final String checkOpinion = info.checkOpinion;
            holder.checkedTitleTv.setVisibility(2 == status ? View.VISIBLE:View.GONE);
            holder.checkedReamrkTv.setVisibility(2 == status ? View.VISIBLE:View.GONE );

            String checkStatusStr = status == 2 ? "已整改" : "未整改";
            holder.checkedTitleTv.setText("复核时间：" + info.getUpdateTime() +
                    "\n复核结果：" + checkStatusStr);
            holder.checkedReamrkTv.setText("复核意见：" + checkOpinion);

            //---------------------------- 复核 --------------------------------
            holder.toCheckTv.setVisibility(2 == status ?  View.GONE:View.VISIBLE);//复核按钮
            //复核
            holder.toCheckTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context, R.style.dialog_appcompat_theme);
                    View inflate = LayoutInflater.from(context).inflate(R.layout.layout_dialog_danger_sign, null);
                    final RadioButton rb1 = inflate.findViewById(R.id.danger_rb_1);
                    final EditText remarkEt = inflate.findViewById(R.id.remark_tv);
                    remarkEt.setText(checkOpinion);
                    inflate.findViewById(R.id.commit_bt).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int checkStatus = rb1.isChecked() ? 2 : 1;//2,已整改
                            post(info, checkStatus, remarkEt.getText()+"", dialog);
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
                }
            });

        }
        return convertView;
    }

    private void post(RecordInfo info, int checkStatus, String checkOpinion, final Dialog dialog) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, R.string.no_network);
            return;
        }
        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.id, info.getId());
        map.put(KeyConst.checkStatus, checkStatus);
        map.put(KeyConst.checkOpinion, checkOpinion);


        String url = Constant.WEB_SITE +(isTypeSafy?"/biz/bizSecurity/report":
                "/biz/bizRectify/save");
        JSONObject jsonObject = new JSONObject(map);
        Log.d("", "提交我的:"+jsonObject.toString());
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
        private TextView nameTv, remarkTv, checkedTitleTv, checkedReamrkTv, toCheckTv, timeTagTv;
        private ScrollListView listView;


    }

}














