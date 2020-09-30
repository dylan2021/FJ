package com.android.baihuahu.act_new;

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

public class WageListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<CommonInfo> infoList = new ArrayList<>();
    BaseFgActivity context;

    public WageListAdapter(BaseFgActivity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<CommonInfo> data) {
        this.infoList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return infoList == null ? 0 : infoList.size();
    }

    @Override
    public Object getItem(int i) {
        return infoList == null ? null : infoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final CommonInfo info = infoList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new WageListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_wage_lv, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.title_tv);
            holder.valueTv1 = convertView.findViewById(R.id.item_value_1);
            holder.valueTv2 = convertView.findViewById(R.id.item_value_2);
            holder.valueTv3 = convertView.findViewById(R.id.item_value_3);

            holder.statusTv = (BorderLabelTextView) convertView.findViewById(R.id.status_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            final int status = info.getStatus();

            holder.titleTv.setText(info.getWagesDate());
            holder.valueTv1.setText("班组：" + info.getGroupName());


            holder.valueTv2.setText("参与人数："+info.getPeopleNum()+"人");
            holder.valueTv3.setText("总计时：" + info.getTimes() + "小时    总计件：" + info.getPiece());

            holder.statusTv.setText(Utils.getWageStatusText(status));//0-未确认 1-已确认
            int color = Utils.getWageStatusColor(context, status);
            holder.statusTv.setTextColor(color);
            holder.statusTv.setStrokeColor(color);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setClass(context, WageDetailActivity.class);
                    intent.putExtra(KeyConst.title, info.getGroupName() + "工资详情(" + info.getWagesDate() + ")");
                    intent.putExtra(KeyConst.id, info.id);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView titleTv, valueTv1, valueTv2, valueTv3,valueTv4,valueTv5;
        private BorderLabelTextView statusTv;

    }

}
