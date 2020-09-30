package com.android.baihuahu.act_main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.bean.SafyMaterailInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class SafyMaterailListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<SafyMaterailInfo> infoList = new ArrayList<>();
    FragmentActivity context;

    public SafyMaterailListAdapter(FragmentActivity context) {
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
            holder = new SafyMaterailListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_safy_materail_lv, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.title_tv);
            holder.valueTv2 = convertView.findViewById(R.id.item_value_2);
            holder.valueTv3 = convertView.findViewById(R.id.item_value_3);

            holder.valueTv2_2 = convertView.findViewById(R.id.item_value_2_2);//计划数量
            holder.valueTv3_2 = convertView.findViewById(R.id.item_value_3_2);//复核数量
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            holder.titleTv.setText(info.getName());

            holder.valueTv2.setText(info.getTotalStorageNum()+"");
            holder.valueTv2_2.setText(TextUtil.subTimeYMD(info.getStorageTime()));

            holder.valueTv3.setText(info.getResidueNum()+"");
            holder.valueTv3_2.setText(info.getTotalReceiveNum()+"");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, SafyMaterailDetailActivity.class);
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
