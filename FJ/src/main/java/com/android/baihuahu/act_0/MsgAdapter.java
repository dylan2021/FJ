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

package com.android.baihuahu.act_0;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_main.DesignInfoActivity;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.MsgInfo;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.BorderLabelTextView;
import com.google.gson.JsonObject;

import java.util.List;


/**
 * @author Goll Lee
 */
public class MsgAdapter extends BaseAdapter {
    private List<MsgInfo> msgList;
    private BaseFgActivity context;
    private int TYPE = 0;

    public MsgAdapter(BaseFgActivity context) {
        super();
        this.context = context;

    }

    public void setDate(List<MsgInfo> fileInfoList, int TYPE) {
        this.TYPE = TYPE;
        this.msgList = fileInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (msgList != null) {
            return msgList.size();
        }
        return 0;
    }


    @Override
    public Object getItem(int position) {
        if (msgList != null) {
            return msgList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final MsgInfo info = msgList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_msg_lv_1, parent, false);
            holder.titleTv = (TextView) convertView.findViewById(R.id.msg_item_title_tv);
            holder.statusTv = (BorderLabelTextView) convertView.findViewById(R.id.status_tv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.msg_item_time_tv);

            holder.tv1 = (TextView) convertView.findViewById(R.id.msg_item_tv_1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.msg_item_tv_2);
            holder.tv3 = (TextView) convertView.findViewById(R.id.msg_item_tv_3);
            holder.redTag = convertView.findViewById(R.id.red_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            String isRead = info.isRead;
            holder.redTag.setVisibility("2".equals(isRead) ? View.VISIBLE : View.GONE);

            holder.titleTv.setText(info.headline);
            final int status = info.status;

            holder.statusTv.setText(Utils.getStatusText(status));
            int color = Utils.getStatusColor(context, status);
            holder.statusTv.setTextColor(color);
            holder.statusTv.setStrokeColor(color);
            holder.statusTv.setVisibility(TYPE == 0 ? View.VISIBLE : View.GONE);

            holder.timeTv.setText(info.createTime);

            holder.tv2.setVisibility(View.VISIBLE);
            holder.tv3.setVisibility(View.VISIBLE);


            String type = info.type;
            JsonObject object = info.object;
            if (null != object) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, DesignInfoActivity.class);
                        intent.putExtra(KeyConst.id, info.id);
                        intent.putExtra(KeyConst.title, info.headline);
                        if (status == 2 && (App.userId + "").equals(info.auditorId)) {//我是审核人才能看到这条消息 -所以只有:同意&驳回
                            intent.putExtra(KeyConst.agreeReject_recall, 1);
                        }
                        context.startActivity(intent);
                        holder.redTag.setVisibility(View.GONE);
                    }
                });

                if (Constant.LEAVE.equals(type)) {//请假
                    String typeValue = Utils.getObjStr(object, KeyConst.leaveType);
                    holder.tv1.setText("请假类型：" + Utils.getDictNameByValue(context, KeyConst.LEAVE_TYPE, typeValue));
                    holder.tv2.setText("开始时间：" + TextUtil.subTimeYMDHm(
                            Utils.getObjStr(object, KeyConst.startTime)));
                    holder.tv3.setText("结束时间：" + TextUtil.subTimeYMDHm(
                            Utils.getObjStr(object, KeyConst.endTime)));
                } else if (Constant.BUSINESS_TRIP.equals(type)) {//出差
                    String objStr = Utils.getObjStr(object, KeyConst.period);
                    holder.tv1.setText("出差天数：" + objStr.replace(".0", "") + "天");
                    String peers = Utils.getObjStr(object, KeyConst.peers);
                    holder.tv2.setText("同行人员：" + (TextUtil.isEmpty(peers) ? "无" : peers));
                    holder.tv3.setVisibility(View.GONE);
                } else if (Constant.OVERTIME.equals(type)) {//加班
                    holder.tv1.setText("开始时间：" + Utils.getObjStr(object, KeyConst.startTime));
                    holder.tv2.setText("结束时间：" + Utils.getObjStr(object, KeyConst.endTime));
                    holder.tv3.setText("时长：" + Utils.getObjStr(object, KeyConst.period) + "小时");
                } else if (Constant.REGULAR_WORKER.equals(type)) {//转正
                    holder.tv1.setText("入职日期：" + Utils.getObjStr(object, KeyConst.employmentDate));
                    holder.tv2.setText("试用期：" +
                            Utils.getObjStr(object, KeyConst.probationPeriod).replace(".00", "个月"));
                    holder.tv3.setText("转正日期：" + Utils.getObjStr(object, KeyConst.regularDate));
                } else if (Constant.RECRUIT.equals(type)) {//招聘
                    holder.tv1.setText("需求岗位：" + Utils.getObjStr(object, KeyConst.recruitPost));
                    holder.tv2.setText("需求人数：" + Utils.getObjStr(object, KeyConst.recruitNum) + "人");
                    holder.tv3.setVisibility(View.GONE);
                } else if (Constant.DIMISSION.equals(type)) {//离职
                    holder.tv1.setText("入职日期：" + Utils.getObjStr(object, KeyConst.employmentDate));
                    holder.tv2.setText("最后工作日：" + Utils.getObjStr(object, KeyConst.lastWorkDate));
                    holder.tv3.setVisibility(View.GONE);
                } else if (Constant.OFFICIAL_DOCUMENT.equals(type)) {//公文
                    holder.tv1.setText("审批部门：" + Utils.getObjStr(object, KeyConst.auditDept));
                    holder.tv2.setVisibility(View.GONE);
                    holder.tv3.setVisibility(View.GONE);
                } else if (Constant.REIMBURSE.equals(type)) {//报销
                    holder.tv1.setText("报销总金额：" +
                            Utils.getObjStr(object, KeyConst.totalAmount) + "元");
                    holder.tv2.setVisibility(View.GONE);
                    holder.tv3.setVisibility(View.GONE);
                } else if (Constant.PURCHASE.equals(type)) {//申购
                    holder.tv1.setText("采购类型：" + Utils.getObjStr(object, KeyConst.purchaseType));
                    holder.tv2.setText("总价格：" + Utils.getObjStr(object, KeyConst.totalAmount) + "元");
                    holder.tv3.setText("支付方式：" + Utils.getObjStr(object, KeyConst.payType));
                } else if (Constant.PAY.equals(type)) {//付款
                    holder.tv1.setText("付款金额：" + Utils.getObjStr(object, KeyConst.amount) + "元");
                    holder.tv2.setText("付款方式：" + Utils.getObjStr(object, KeyConst.payType));
                    holder.tv3.setVisibility(View.GONE);
                } else if (Constant.PETTY_CASH.equals(type)) {//备用金
                    holder.tv1.setText("申请金额：" + Utils.getObjStr(object, KeyConst.applyAmount) + "元");
                    holder.tv2.setText("使用日期：" + Utils.getObjStr(object, KeyConst.useDate));
                    holder.tv3.setText("归还日期：" + Utils.getObjStr(object, KeyConst.returnDate));
                } else if (Constant.WAGE_AUDIT.equals(type)) {//工资审核
                    holder.tv1.setText("合计工资：" + Utils.getObjStr(object, KeyConst.totalWage) + "元");
                    holder.tv2.setText("参与人数：" + Utils.getObjStr(object, KeyConst.personNum) + "人");
                    holder.tv3.setVisibility(View.GONE);
                } else if (type.contains(Constant.NOTICE)) {//公告
                    holder.tv1.setText("摘要：" + Utils.getObjStr(object, KeyConst.summary));
                    holder.tv2.setText("发布人：" + info.applicantName);
                    holder.tv3.setVisibility(View.GONE);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, NoticeDetailUrlActivity.class);
                            intent.putExtra(KeyConst.id, info.id);
                            intent.putExtra(KeyConst.title, info.headline);
                            if (status == 2 && (App.userId + "").equals(info.auditorId)) {
                                //我是审核人才能看到这条消息 -所以只有:同意&驳回
                                intent.putExtra(KeyConst.agreeReject_recall, 1);
                            }
                            context.startActivity(intent);
                            holder.redTag.setVisibility(View.GONE);
                        }
                    });
                }

            }
            holder.timeTv.setText(info.createTime);

        }
        return convertView;
    }

    private class ViewHolder {
        private TextView titleTv, timeTv, tv1, tv2, tv3, redTag;
        private BorderLabelTextView statusTv;

    }

}














