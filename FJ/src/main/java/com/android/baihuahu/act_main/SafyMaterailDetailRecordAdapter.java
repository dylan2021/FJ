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

import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.bean.RecordInfo;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.TextUtil;

import java.util.List;


/**
 * Dylan
 */
public class SafyMaterailDetailRecordAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<RecordInfo> mList;

    public SafyMaterailDetailRecordAdapter(BaseFgActivity context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<RecordInfo> infos) {
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
            convertView = inflater.inflate(R.layout.item_safy_materail_detail_lv, parent, false);
            holder.getUseNumTv = (TextView) convertView.findViewById(R.id.get_used_num_tv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.get_and_used_name_tv);
            holder.timeTagTv = (TextView) convertView.findViewById(R.id.time_tag_tv);
            holder.remarkTv = (TextView) convertView.findViewById(R.id.remark_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecordInfo info = mList.get(position);
        if (info != null) {
            holder.timeTagTv.setText(TextUtil.subTimeYMDHm(info.getCreateTime()));
            holder.getUseNumTv.setText("领用数量：" + TextUtil.remove_0(info.getReceiveNum()+""));

            String type = info.getType();//1.个人领用  2.班组领用
            String creatorName = info.getCreatorName();//发放人

            String reciveName = "1".equals(type)? creatorName :info.getGroupName();
            holder.nameTv.setText("领用人：" + reciveName + "　　发放人：" + creatorName);
            holder.remarkTv.setText("备注：" + info.getRemark());
        }

        return convertView;
    }

    public class ViewHolder {
        private TextView nameTv, remarkTv, getUseNumTv, timeTagTv;
    }

}














