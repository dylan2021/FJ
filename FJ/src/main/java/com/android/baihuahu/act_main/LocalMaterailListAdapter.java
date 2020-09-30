package com.android.baihuahu.act_main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.SafyMaterailInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class LocalMaterailListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<SafyMaterailInfo> infoList = new ArrayList<>();
    BaseFgActivity context;

    public LocalMaterailListAdapter(BaseFgActivity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<SafyMaterailInfo> data) {
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
        final SafyMaterailInfo info = infoList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new LocalMaterailListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_local_materail_lv, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.title_tv);
            holder.valueTv1 = convertView.findViewById(R.id.item_value_1);
            holder.valueTv2 = convertView.findViewById(R.id.item_value_2);
            holder.valueTv3 = convertView.findViewById(R.id.item_value_3);

            holder.valueTv1_2 = convertView.findViewById(R.id.item_value_1_2);//单位
            holder.valueTv2_2 = convertView.findViewById(R.id.item_value_2_2);//计划数量
            holder.valueTv3_2 = convertView.findViewById(R.id.item_value_3_2);//复核数量
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            holder.titleTv.setText(info.getName());
            holder.valueTv1.setText( Utils.getDictNameByValue(context, KeyConst.Material_Category, info.getCategory()));//规格
            holder.valueTv1_2.setText( Utils.getDictNameByValue(context, KeyConst.unit, info.getUnit()));//单位

            holder.valueTv2.setText(TextUtil.remove_0(info.getDesignNum()+""));//设计数量
            holder.valueTv2_2.setText(TextUtil.remove_0(info.getPlanNum()+""));//计划数量

            holder.valueTv3.setText(TextUtil.remove_0(info.getUseNum()+""));//实际数量
            holder.valueTv3_2.setText(TextUtil.remove_0(info.getAuditNum()+""));//复核数量
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, LocalMaterailDetailActivity.class);
                    intent.putExtra(KeyConst.title, info.getName());
                    intent.putExtra(KeyConst.id, info.getId());
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView titleTv, valueTv1, valueTv2, valueTv3;
        private TextView valueTv1_2, valueTv2_2, valueTv3_2;

    }

}
