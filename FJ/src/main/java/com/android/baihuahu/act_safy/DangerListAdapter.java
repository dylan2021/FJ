package com.android.baihuahu.act_safy;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.BorderLabelTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class DangerListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<CommonInfo> commonInfoList = new ArrayList<>();
    BaseFgActivity context;
    private boolean isTypeSafy;

    public DangerListAdapter(BaseFgActivity context, boolean isTypeSafy) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.isTypeSafy = isTypeSafy;
    }

    public void setData(List<CommonInfo> data) {
        this.commonInfoList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return commonInfoList == null ? 0 : commonInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return commonInfoList == null ? null : commonInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new DangerListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_quality_danger_lv, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.title_tv);
            holder.valueTv1 = convertView.findViewById(R.id.item_value_1);
            holder.valueTv2 = convertView.findViewById(R.id.item_value_2);
            holder.valueTv3 = convertView.findViewById(R.id.item_value_3);
            holder.valueTv4 = convertView.findViewById(R.id.item_value_4);
            holder.valueTv5 = convertView.findViewById(R.id.item_value_5);

            holder.titleTv4 = convertView.findViewById(R.id.item_title_4);
            holder.titleTv4.setText("是否反馈: ");
            holder.titleTv5 = convertView.findViewById(R.id.item_title_5);
            holder.titleTv5.setText("复核结果: ");

            holder.statusTv = (BorderLabelTextView) convertView.findViewById(R.id.status_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CommonInfo info = commonInfoList.get(position);

        if (info != null) {
            holder.titleTv.setText("限期整改时间: " + info.rectifyDate);
            final String dangerDesc = info.dangerDesc;
            holder.valueTv2.setText("隐患描述: " + dangerDesc);
            holder.valueTv1.setText("签收人: " + info.signerName);
            holder.valueTv3.setText("检查人/检查日期: "+TextUtil.initNamePhone(info.inspector,info.getInspectDate()));

            //反馈
            String rectifyStatus = TextUtil.isEmpty(info.rectifyStatus)?"未反馈":info.rectifyStatus;
            holder.valueTv4.setText(rectifyStatus);
            int statusRec = rectifyStatus.startsWith("已") ? 1 : 2;
            int color = Utils.getSignerStatusColor(context,
                    statusRec);
            holder.valueTv4.setTextColor(color);

            //复核
            String checkStatus = info.checkStatus;
            int color2 = Utils.getSignerStatusColor(context, checkStatus.startsWith("已") ? 1 : 2);
            holder.valueTv5.setText(checkStatus);
            holder.valueTv5.setTextColor(color2);

        /*    holder.statusTv.setText(Utils.getCheckStatusText(status));//0-否 不合格  1-合格
            int color = Utils.getCheckStatusColor(context, status);
            holder.statusTv.setTextColor(color);
            holder.statusTv.setStrokeColor(color);*/

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, DangerDetailActivity.class);
                    intent.putExtra(KeyConst.title, dangerDesc + "隐患详情");
                    intent.putExtra(KeyConst.OBJ_INFO, info);
                    intent.putExtra(KeyConst.type, isTypeSafy);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView titleTv, valueTv1, valueTv2, valueTv3, valueTv4,
                titleTv4, titleTv5, valueTv5;
        private BorderLabelTextView statusTv;

    }

}
