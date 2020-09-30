package com.android.baihuahu.act_main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.util.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */

public class EmplyeeListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<EmplyeeInfo> emplyeeInfoList = new ArrayList<>();
    private EmplyeeListActivity context;
    private boolean isGroupList;

    public EmplyeeListAdapter(EmplyeeListActivity context, boolean isGroupList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.isGroupList = isGroupList;
    }

    public void setData(List<EmplyeeInfo> data) {
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
        final EmplyeeInfo info = emplyeeInfoList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new EmplyeeListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_emplyee, viewGroup, false);
            holder.iconIv = (SimpleDraweeView) convertView.findViewById(R.id.emplyee_icon_iv);
            holder.nameTv = convertView.findViewById(R.id.emplyee_name_tv);
            holder.groupTv = convertView.findViewById(R.id.emplyee_group_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (info != null) {
            holder.iconIv.setImageURI(ImageUtil.getImageUrl(info.getPic()));
            final String namePhone = info.getName();
            holder.nameTv.setText(namePhone);

           /* String groupName = info.getSectionName() + " - " + info.getBuildSiteName()
                    + " - " + info.getGroupName();*/
            String gender = Utils.getDictNameByValue(context, KeyConst.GENDER, info.getGender());
            holder.groupTv.setText(gender + "  " + info.getAge() + "岁");

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, EmplyeeDetailActivity.class);
                    intent.putExtra(KeyConst.OBJ_INFO, info);
                    intent.putExtra(KeyConst.id, info.getId());
                    intent.putExtra(KeyConst.type, isGroupList);
                    intent.putExtra(KeyConst.name, namePhone);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView nameTv, timeTv, beginEndDateTv, tag1Tv, groupTv;
        private SimpleDraweeView iconIv;

    }

}
