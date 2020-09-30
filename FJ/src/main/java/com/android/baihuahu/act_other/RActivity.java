package com.android.baihuahu.act_other;

import android.os.Bundle;

import com.android.baihuahu.R;
import com.android.baihuahu.core.utils.KeyConst;

/**
 * @author Dylan
 * @Date 监理旁站记录
 */
public class RActivity extends BaseFgActivity {

    public final String TAG = RActivity.class.getSimpleName();
    private String title;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_record);
        title = getIntent().getStringExtra(KeyConst.title);
        id = getIntent().getIntExtra(KeyConst.id, 0);
        initTitleBackBt(title);
    }
}
