package com.android.baihuahu.act_main;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class DeviceListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<CommonInfo> emplyeeInfoList = new ArrayList<>();
    BaseFgActivity context;

    public DeviceListAdapter(BaseFgActivity context) {
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
            holder = new DeviceListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_device_lv, viewGroup, false);
            holder.deviceNameTv = convertView.findViewById(R.id.title_tv);
            holder.valueTv1 = convertView.findViewById(R.id.item_value_1);
            holder.valueTv2 = convertView.findViewById(R.id.item_value_2);
            holder.valueTv3 = convertView.findViewById(R.id.item_value_3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            holder.deviceNameTv.setText(info.getName());
            holder.valueTv1.setText(info.getContractNo());
            String category = Utils.getDictNameByValue(context, KeyConst.Device_Category, info.getCategory());
            holder.valueTv2.setText(category);
            holder.valueTv3.setText(info.getInDate());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setClass(context, DeviceDetailActivity.class);
                    intent.putExtra(KeyConst.title, info.getName());
                    intent.putExtra(KeyConst.OBJ_INFO, info);
                    intent.putExtra(KeyConst.id, info.id);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView deviceNameTv, valueTv1, valueTv2, valueTv3;

    }

}
