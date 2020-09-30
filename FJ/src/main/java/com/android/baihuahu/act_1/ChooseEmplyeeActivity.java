package com.android.baihuahu.act_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.baihuahu.R;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.adapter.MyExpandableListAdapter;
import com.android.baihuahu.bean.GroupInfo;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.MyExpandableListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dylan
 * 质量检查类型
 */
public class ChooseEmplyeeActivity extends BaseFgActivity {
    private Button title_bar;
    private TextView tv_content;
    private int processorConfigId;
    private MyExpandableListView expandableListView;
    private ChooseEmplyeeActivity context;
    private MyExpandableListAdapter expAdapter;
    private Button rightBt;
    private List<GroupInfo> parentList = new ArrayList<>();
    private DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_check_type_setting);
        context = this;
        processorConfigId = getIntent().getIntExtra(KeyConst.id, 0);
        parentList = (List<GroupInfo>) getIntent().getSerializableExtra(KeyConst.OBJ_INFO);
        Utils.sortList(parentList);//排序
        initTitleBackBt("请选择人员");

        initRightBt();

        initExpandLv();

        expAdapter.setData(parentList, true);

    }
    private void initExpandLv() {
        expandableListView = (MyExpandableListView) findViewById(R.id.expand_list);
        expAdapter = new MyExpandableListAdapter(context);
        expandableListView.setAdapter(expAdapter);

        //设置分组项的点击监听事件
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView,
                                        View view, int i, long l) {
                AppCompatCheckBox groupCheckBox = (AppCompatCheckBox) view.findViewById(R.id.expand_check_box);

                return false; // 返回 false，否则分组不会展开
            }
        });

        expAdapter.setData(parentList, true);
    }

    private void initRightBt() {
        rightBt = (Button) findViewById(R.id.title_right_bt);
        rightBt.setVisibility(View.VISIBLE);
        rightBt.setText("确定");
        rightBt.setTextColor(ContextCompat.getColor(context, R.color.mainColor));
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == parentList || parentList.size() == 0) {
                    ToastUtil.show(context, "请选择至少一个人员");
                    return;
                }

                //返回选择的数据
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConst.OBJ_INFO, (Serializable) parentList);//序列化,要注意转化(Serializable)
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(2, intent);

                finish();

            }
        });

    }

}
