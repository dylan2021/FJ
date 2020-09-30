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

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.android.baihuahu.R;
import com.android.baihuahu.bean.BillInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.view.BorderLabelTextView;

import java.util.List;

/**
 * @author Dylan
 * @since
 */
public class BillListAdapter extends BaseAdapter {

    private List<BillInfo> msgInfos;

    private BillListActivity context;

    public BillListAdapter(BillListActivity context, List<BillInfo> msgInfos) {
        super();
        this.context = context;
        this.msgInfos = msgInfos;
    }

    /**
     * 设置ListView中的数据
     */

    public void setData(List<BillInfo> msgInfoList) {
        msgInfos = msgInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (msgInfos != null) {
            return msgInfos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (msgInfos != null) {
            return msgInfos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ledger_lv, parent, false);
            holder.titleTv = (TextView) convertView.findViewById(R.id.apply_item_title_tv);
            holder.tv1 = (TextView) convertView.findViewById(R.id.apply_item_1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.project_num_tv);
            holder.tv3 = (TextView) convertView.findViewById(R.id.finished_num_tv);
            holder.write_data_tv = (TextView) convertView.findViewById(R.id.check_proj_tv_1);
            holder.sign_data_tv = (TextView) convertView.findViewById(R.id.check_proj_tv_2);
            holder.file_data_tv = (TextView) convertView.findViewById(R.id.check_proj_tv_3);

            holder.write_report_tv = (TextView) convertView.findViewById(R.id.check_test_tv_1);
            holder.sign_report_tv = (TextView) convertView.findViewById(R.id.check_test_tv_2);

            holder.status_tv =  convertView.findViewById(R.id.status_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BillInfo info = msgInfos.get(position);
        if (info != null) {
            String unit = info.getUnit();
            holder.titleTv.setText(info.getName());
            holder.tv1.setText(info.getUsePosition());
            holder.tv2.setText(info.getOriginNum() + unit);
            String totalReportNum = info.getTotalReportNum() + unit;
            String todayReportNum = info.getTodayReportNum() + unit;
            holder.tv3.setText(Html.fromHtml(totalReportNum +
                    "<font color='#a5a5a5' >（今日完成：" + todayReportNum + "）</font>"));


            holder.write_data_tv.setSelected(info.isWriteData());
            holder.sign_data_tv.setSelected(info.isSignData());
            holder.file_data_tv.setSelected(info.isFileData());

            holder.write_report_tv.setSelected(info.isWriteReport());
            holder.sign_report_tv.setSelected(info.isSignReport());

            String confirmResult = info.getConfirmResult();
            holder.status_tv.setText(confirmResult);
            int color = ContextCompat.getColor(context, "已复核".equals(confirmResult) ?
                    R.color.mainColor : R.color.a5a5a5);
            holder.status_tv.setTextColor(color);
            holder.status_tv.setStrokeColor(color);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BillDetailActivity.class);
                    intent.putExtra(KeyConst.id, info.getId());
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    private class ViewHolder {
        private BorderLabelTextView status_tv;
        private TextView tv1, tv2, tv3, titleTv,
                write_data_tv, sign_data_tv, file_data_tv,
                write_report_tv, sign_report_tv;
    }

}














