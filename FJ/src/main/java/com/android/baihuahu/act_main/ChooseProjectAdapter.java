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

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.bean.ChooseDataInfo;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.TimeUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Dylan
 */

public class ChooseProjectAdapter extends RecyclerView.Adapter {
    private Activity context;
    private int type = 0;//1=工区  2=工地
    private List<ChooseDataInfo> contacts;
    private String [] typeStrArr=new String[]{"项目","工区","工地"};

    public ChooseProjectAdapter(List<ChooseDataInfo> contacts, Activity c, int type) {
        this.contacts = contacts;
        this.context = c;
        this.type = type;
    }

    public void setData(List<ChooseDataInfo> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_choose_project, parent, false);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ChooseDataInfo info = contacts.get(position);
        MyHolder holder = (MyHolder) viewHolder;
        //显示index
        if (info != null) {

            holder.picSdv.setImageURI(ImageUtil.getImageUrl(info.getPic()));

            final String infoName = info.getName();
            holder.projectName.setText(infoName+"("+typeStrArr[type]+")");
            if (type == 1) {
                //出现(工地)点击按钮
                holder.buildsiteNumTv.setVisibility(View.VISIBLE);
                List<Object> list = info.getBizBuildSiteVOList();
                holder.buildsiteNumTv.setText((list == null ? 0 : list.size()) + "个工地");
                holder.buildsiteNumTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SectionListActivity.class);
                        intent.putExtra(KeyConst.id, info.getId());
                        intent.putExtra(KeyConst.type, 2);
                        intent.putExtra(KeyConst.title, infoName);
                        context.startActivity(intent);
                    }
                });
            }


            holder.investTv.setText("总投资：" + info.getInvest() + "万元");
            holder.lengthTv.setText("项目地理位置：" + info.getLength() + "");
            String beginDate = info.getPlanBeginDate();
            String endDate = info.getPlanEndDate();
            holder.startDateTv.setText("开工日期：" + TextUtil.remove_N(beginDate));
            holder.periodTv.setText("(" + TimeUtils.getBetweenDays(beginDate, endDate) + "天)");

            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(view, info.getId(), infoName, type);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        TextView investTv, startDateTv, typeTv, buildsiteNumTv;
        TextView projectName, periodTv, lengthTv;
        View itemView;
        SimpleDraweeView picSdv;

        public MyHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            picSdv = (SimpleDraweeView) itemView.findViewById(R.id.pic_sdv);//项目名
            projectName = (TextView) itemView.findViewById(R.id.project_name_tv);//项目名
            investTv = (TextView) itemView.findViewById(R.id.invest_tv);//总投资
            lengthTv = (TextView) itemView.findViewById(R.id.length_tv);//项目地理位置度

            startDateTv = (TextView) itemView.findViewById(R.id.date_tv);//开工日期
            periodTv = (TextView) itemView.findViewById(R.id.period_tv);//总工期
            typeTv = (TextView) itemView.findViewById(R.id.type_tv);//总工期

            buildsiteNumTv = (TextView) itemView.findViewById(R.id.buildsite_num_tv);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position, String name, int type);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}










