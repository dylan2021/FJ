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
import com.android.baihuahu.bean.EmplyeeAttendInfo;
import com.android.baihuahu.core.utils.TextUtil;

import java.util.List;

/**
 * Dylan
 */
public class EmplyeeDetailAttendAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<EmplyeeAttendInfo> mList;
    private BaseFgActivity context;
    private String emptyStr = "&emsp;&emsp;";

    public EmplyeeDetailAttendAdapter(BaseFgActivity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<EmplyeeAttendInfo> infos) {
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
            convertView = inflater.inflate(R.layout.item_attend_lv, parent, false);
            holder.nameTv = (TextView) convertView.findViewById(R.id.attend_people_name_tv);
            holder.hourTimeTv = (TextView) convertView.findViewById(R.id.attend_hour_tv);
            holder.startEndTimeTv = (TextView) convertView.findViewById(R.id.attend_start_end_time_tv);
            holder.remarkTv = (TextView) convertView.findViewById(R.id.attend_remark_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EmplyeeAttendInfo info = mList.get(position);
        if (info != null) {
            int attendHour = info.getAttendHour();

            holder.hourTimeTv.setText(attendHour + "小时");
            holder.nameTv.setText("考勤人：" + info.getCreatorName());
            String startDate = TextUtil.subTimeYMD(info.getStartTime());
            String endDate = TextUtil.subTimeYMD(info.getEndTime());
            String attendDate = startDate.equals(endDate) ? startDate : startDate + "至" + endDate;
            holder.startEndTimeTv.setText("考勤时间：" + attendDate);
            if (!TextUtil.isEmpty(info.getRemark())) {
                holder.remarkTv.setText("备注：" + info.getRemark());
                holder.remarkTv.setVisibility(View.VISIBLE);
            } else {
                holder.remarkTv.setVisibility(View.GONE);
            }
        }
        return convertView;
    }


    public class ViewHolder {
        private TextView nameTv, remarkTv, hourTimeTv, startEndTimeTv;


    }

}














