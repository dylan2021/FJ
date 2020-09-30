package com.android.baihuahu.act_quality;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.BorderLabelTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class SafyQualityCheckListAdapter extends BaseAdapter {
    private boolean isTypeSafy;
    LayoutInflater mInflater;
    List<CommonInfo> commonInfoList = new ArrayList<>();
    BaseFgActivity context;

    public SafyQualityCheckListAdapter(BaseFgActivity context, boolean isTypeSafy) {
        this.context = context;
        this.isTypeSafy = isTypeSafy;
        mInflater = LayoutInflater.from(context);
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
        final CommonInfo info = commonInfoList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new SafyQualityCheckListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_quality_check_lv, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.title_tv);
            holder.valueTv1 = convertView.findViewById(R.id.item_value_1);
            holder.valueTv2 = convertView.findViewById(R.id.item_value_2);
            holder.valueTv3 = convertView.findViewById(R.id.item_value_3);
            holder.valueTv4 = convertView.findViewById(R.id.item_value_4);

            holder.statusTv = (BorderLabelTextView) convertView.findViewById(R.id.status_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            int status;
            final String titleStr;

            status = info.inspectResult;
            titleStr = info.getTheme();
            holder.titleTv.setText(titleStr);
            holder.valueTv1.setText("被检查班组：" + info.getGroupName());
            holder.valueTv2.setText("检查日期：" + info.getInspectDate());
            holder.valueTv3.setText("检查人：" + info.inspector);
            holder.valueTv4.setText("隐患情况：" + info.getDangerStatue());
            holder.statusTv.setText(Utils.getCheckStatusText(status));

            int color = Utils.getCheckStatusColor(context, status);
            holder.statusTv.setTextColor(color);
            holder.statusTv.setStrokeColor(color);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, SafyQualityCheckDetailActivity.class);
                    intent.putExtra(KeyConst.title, titleStr + "检查详情");
                    intent.putExtra(KeyConst.id, info.id);
                    intent.putExtra(KeyConst.type, isTypeSafy);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView titleTv, valueTv1, valueTv2, valueTv3, valueTv4;
        private BorderLabelTextView statusTv;

    }

}
