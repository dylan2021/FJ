package com.android.baihuahu.act_main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_new.ProcessDetailActivity;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class MainListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<CommonInfo> billInfos = new ArrayList<>();
    private MainActivity context;

    public MainListAdapter(MainActivity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return billInfos == null ? 0 : billInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return billInfos == null ? null : billInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final CommonInfo info = billInfos.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new MainListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.main_item, viewGroup, false);
            holder.iconIv = convertView.findViewById(R.id.main_item_iv);
            holder.nameTv = convertView.findViewById(R.id.main_item_name_tv);
            holder.timeTv = convertView.findViewById(R.id.main_item_time_tv);
            holder.descTv = convertView.findViewById(R.id.main_item_desc_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (null != info) {
            holder.nameTv.setText(info.getPname());
            holder.descTv.setText("累计汇报产值："+info.getWriteValue()
                    +"万元　完成进度："+info.getStageProgress()+"%");
            holder.timeTv.setText(TextUtil.subTimeYMD(info.getWagesDate()));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

    public void setData(List<CommonInfo> infoList) {
        billInfos = infoList;
        notifyDataSetChanged();
    }


    public class ViewHolder {
        private TextView nameTv, timeTv, descTv;
        private ImageView iconIv;
    }

}
