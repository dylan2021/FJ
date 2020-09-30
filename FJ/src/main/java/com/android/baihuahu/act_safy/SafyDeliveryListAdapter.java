package com.android.baihuahu.act_safy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.view.BorderLabelTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class SafyDeliveryListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<CommonInfo> commonInfoList = new ArrayList<>();
    BaseFgActivity context;

    public SafyDeliveryListAdapter(BaseFgActivity context) {
        this.context = context;
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new SafyDeliveryListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_quality_danger_lv, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.title_tv);
            holder.valueTv1 = convertView.findViewById(R.id.item_value_1);
            holder.valueTv2 = convertView.findViewById(R.id.item_value_2);
            holder.valueTv3 = convertView.findViewById(R.id.item_value_3);
            holder.valueTv4 = convertView.findViewById(R.id.item_value_4);
            holder.valueTv5 = convertView.findViewById(R.id.item_value_5);

            holder.titleTv4 = convertView.findViewById(R.id.item_title_4);
            holder.titleTv5 = convertView.findViewById(R.id.item_title_5);

            holder.statusTv = (BorderLabelTextView) convertView.findViewById(R.id.status_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CommonInfo info = commonInfoList.get(position);

        if (info != null) {
            holder.titleTv.setText(info.buildLocation);

            holder.valueTv1.setText("施工单位: " + info.buildOrg);

            holder.valueTv2.setText("交底日期: "+info.deliveryDate);

            holder.valueTv3.setText("交底人: " + info.deliverer);
            holder.valueTv4.setText("接收交底人: " + info.receiver);
            holder.valueTv5.setText("施工内容: " + info.buildContent);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SafyDeliveryAddUpdateDeatilActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) info);//序列化,要注意转化(Serializable)
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView titleTv, valueTv1, valueTv2, valueTv3,valueTv4,
                titleTv4,titleTv5,valueTv5;
        private BorderLabelTextView statusTv;

    }

}
