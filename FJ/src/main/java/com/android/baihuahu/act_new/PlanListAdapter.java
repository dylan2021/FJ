package com.android.baihuahu.act_new;

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
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.BorderLabelTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class PlanListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<CommonInfo> emplyeeInfoList = new ArrayList<>();
    BaseFgActivity context;

    public PlanListAdapter(BaseFgActivity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<CommonInfo> data) {
        this.emplyeeInfoList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return emplyeeInfoList == null ? 0 : emplyeeInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return emplyeeInfoList == null ? null : emplyeeInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final CommonInfo info = emplyeeInfoList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new PlanListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_process_lv, viewGroup, false);
            holder.deviceNameTv = convertView.findViewById(R.id.title_tv);
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
            holder.deviceNameTv.setText(info.getPname());
            holder.valueTv1.setText(info.getPstartDate());
            holder.valueTv2.setText(info.getPendDate());
            holder.valueTv3.setText(info.getPcycle() + "");
            holder.valueTv4.setText(info.getPvalue() + "");

            final int status = info.getStatus();
            holder.statusTv.setText(Utils.getPlanStatusText(status));
            int color = Utils.getPlanStatusColor(context, status);
            holder.statusTv.setTextColor(color);
            holder.statusTv.setStrokeColor(color);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, ProcessDetailActivity.class);
                    intent.putExtra(KeyConst.title, info.getPname());
                    intent.putExtra(KeyConst.stageProgress, info.getStageProgress());
                    intent.putExtra(KeyConst.id, info.id);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView deviceNameTv, valueTv1, valueTv2, valueTv3, valueTv4;
        private BorderLabelTextView statusTv;

    }

}
