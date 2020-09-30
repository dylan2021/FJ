package com.android.baihuahu.act_main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.bean.GroupItemInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.Utils;

import java.util.List;

/**
 * Dylan
 * 安全生产
 */

public class EmplyeeGroupListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<GroupItemInfo> list;
    private EmplyeeGroupListActivity context;

    public EmplyeeGroupListAdapter(EmplyeeGroupListActivity context,
                                   List<GroupItemInfo> datats) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        list = datats;
    }

    public void setData(List<GroupItemInfo> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (list == null) {
            return null;
        } else {
            return list.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final GroupItemInfo info = list.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_emplyee_group, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.name_tv);
            holder.totalTv = convertView.findViewById(R.id.total_wage_tv);
            holder.realInNumTv = convertView.findViewById(R.id.real_in_num_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            holder.titleTv.setText(info.getName());
            holder.totalTv.setText( info.getBuildSiteName());
            int WorkerNum = info.getBizWorkerNum();
            holder.realInNumTv.setText(WorkerNum + "人");

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EmplyeeListActivity.class);
                    intent.putExtra(KeyConst.id, info.getId());//班组id
                    intent.putExtra(KeyConst.type, true);//是否是班组
                    intent.putExtra(KeyConst.title, "人员列表("+info.getBuildSiteName()+")");
                    context.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    public class ViewHolder {
        private TextView titleTv, totalTv, realInNumTv, emplyeeNumberTv, timeTv, everyNumberWageTv;
    }

}
