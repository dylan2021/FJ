/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.baihuahu.act_main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.adapter.FileListAdapter;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.bean.FileInfo;
import com.android.baihuahu.bean.FileListInfo;
import com.android.baihuahu.bean.DeviceAttendInfo;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.ScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 */
public class DeviceDetailRecordAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<CommonInfo>   mList;
    private BaseFgActivity context;

    public DeviceDetailRecordAdapter(BaseFgActivity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<CommonInfo> infos) {
        mList = infos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_device_detail_lv, parent, false);
            holder.timeTagTv = (TextView) convertView.findViewById(R.id.time_tag_tv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.report_name_tv);
            holder.hourTv = (TextView) convertView.findViewById(R.id.attend_period_tv);
            holder.wageTv = (TextView) convertView.findViewById(R.id.attend_wage_tv);
            holder.remarkTv = (TextView) convertView.findViewById(R.id.remark_tv);
            holder.listView = (ScrollListView) convertView.findViewById(R.id.horizontal_gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CommonInfo info = mList.get(position);

        holder.timeTagTv.setText(TextUtil.subTimeYMDHm(info.getCreateTime()));
        holder.nameTv.setText("上传人：" + info.getCreatorName());

        holder.hourTv.setText("设备考勤时长(小时)：" + TextUtil.remove_0(info.getPeriod() + ""));
        holder.wageTv.setText("设备考勤工资(元)：" + TextUtil.remove_0(info.getWage() + ""));
        String remark = info.getRemark();
        if (!TextUtil.isEmpty(remark)) {
            holder.remarkTv.setVisibility(View.VISIBLE);
            holder.remarkTv.setText("备注：" + remark);
        } else {
            holder.remarkTv.setVisibility(View.GONE);
        }


        List<FileListInfo> fileData = new ArrayList<>();
        List<FileInfo> attachment = info.attachment;
        if (attachment != null) {
            for (FileInfo att : attachment) {
                fileData.add(new FileListInfo(att.name, att.url, Constant.TYPE_SEE));
            }
        }
        FileListAdapter fileListAdapter = new FileListAdapter(context, fileData);
        holder.listView.setAdapter(fileListAdapter);

        return convertView;
    }

    public class ViewHolder {
        private TextView nameTv, hourTv, remarkTv, wageTv, timeTagTv;
        private ScrollListView listView;


    }

}














