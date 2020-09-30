package com.android.baihuahu.act_main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.baihuahu.act_other.CommonBaseActivity;
import com.android.baihuahu.act_other.SeePicActivity;
import com.android.baihuahu.bean.FileInfo;
import com.android.baihuahu.bean.ChooseDataInfo;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.adapter.FileListAdapter;
import com.android.baihuahu.bean.FileListInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.view.ScrollListView;
import com.google.gson.reflect.TypeToken;

import org.xml.sax.XMLReader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Dylan
 * @Date 申请详情
 */
public class DesignInfoActivity extends CommonBaseActivity {

    private int id = 1;
    private DesignInfoActivity context;
    private List<FileListInfo> fileData = new ArrayList<>();
    private ChooseDataInfo info;
    private View itemView;
    private LinearLayout itemsLayout;
    private TextView contentTv;
    private FragmentManager fm;
    private int maxWidth;
    private int TYPE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_design_info);
        Intent intent = getIntent();
        id = intent.getIntExtra(KeyConst.id, 0);
        TYPE = intent.getIntExtra(KeyConst.type, 0);

        initTitleBackBt(intent.getStringExtra(KeyConst.title));//项目/工区/工地 名称
        context = this;
        maxWidth = ImageUtil.getScreenWidth(context) - getResources().getDimensionPixelSize(R.dimen.dm060);
        strings.clear();

        initView();
        getData();
    }

    private void initView() {
        itemsLayout = findViewById(R.id.items_layout_1);
        contentTv = (TextView) findViewById(R.id.rich_text_tv);
    }

    String[] urlTypeArr = new String[]{"/biz/project/", "/biz/section/", "/biz/buildSite/"};

    //查询流程数据
    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        DialogHelper.showWaiting(getSupportFragmentManager(), "加载中...");
        String url_p = urlTypeArr[TYPE];
        String url = Constant.WEB_SITE + url_p + id;
        Response.Listener<ChooseDataInfo> successListener = new Response
                .Listener<ChooseDataInfo>() {
            @Override
            public void onResponse(ChooseDataInfo result) {
                DialogHelper.hideWaiting(getSupportFragmentManager());
                if (result == null) {
                    ToastUtil.show(context, R.string.no_data);
                    return;
                }
                info = result;

                if (TYPE == 0) {
                    addProjectView();
                } else if (TYPE == 1) {
                    addSectionView();
                } else {
                    addBuildSiteView();
                }
                setRichTv(info.getDescription());
                setFileListData();//附件
            }
        };

        Request<ChooseDataInfo> versionRequest = new
                GsonRequest<ChooseDataInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, R.string.server_exception);
                        DialogHelper.hideWaiting(getSupportFragmentManager());
                    }
                }, new TypeToken<ChooseDataInfo>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                        if (TYPE > 0) {
                            params.put(KeyConst.projectId, App.projecId);
                        }
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void setRichTv(String remark) {
        fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, getString(R.string.loading));

        if (remark != null && !remark.contains("img src=")) {//不包含图片
            DialogHelper.hideWaiting(fm);
        }

        try {
            MyTagHandler tagHandler = new MyTagHandler();
            contentTv.setText(Html.fromHtml(remark, new HtmlImageGetter(), tagHandler));
            contentTv.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception e) {
            DialogHelper.hideWaiting(fm);
        } catch (OutOfMemoryError e) {
            DialogHelper.hideWaiting(fm);
            ToastUtil.show(context, "数据加载异常");
        }

    }

    private ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        strings.clear();
    }

    public class MyTagHandler implements Html.TagHandler {

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            // 处理标签<img>
            if ("img".equals(tag.toLowerCase(Locale.getDefault()))) {
                // 获取长度
                int len = output.length();
                // 获取图片地址
                ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                String imgURL = images[0].getSource();
                // 记录所有图片地址
                strings.add(imgURL);
                // 记录是第几张图片
                final int position = strings.size() - 1;
                // 使图片可点击并监听点击事件
                output.setSpan(new MyTagHandler.ClickableImage(position), len - 1, len,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private class ClickableImage extends ClickableSpan {
            private int selectPosition;

            public ClickableImage(int selectPosition) {
                this.selectPosition = selectPosition;
            }

            @Override
            public void onClick(View widget) {
                Log.d(TAG, "点击图片:" + selectPosition);
                Intent i = new Intent(context, SeePicActivity.class);
                i.putExtra(KeyConst.type, 1);
                i.putStringArrayListExtra(KeyConst.LIST_STR, strings);
                i.putExtra(KeyConst.selectPosition, selectPosition);
                startActivity(i);
            }
        }

    }

    class HtmlImageGetter implements Html.ImageGetter {
        @Override
        public Drawable getDrawable(String source) {
            LevelListDrawable d = new LevelListDrawable();
            Drawable empty = getResources().getDrawable(
                    R.color.gray_1);
            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
            new HtmlImageGetter.LoadImage().execute(source, d);
            return d;
        }

        /**
         * 异步下载图片类
         */
        class LoadImage extends AsyncTask<Object, Void, Bitmap> {

            private LevelListDrawable mDrawable;

            @Override
            protected Bitmap doInBackground(Object... params) {
                //base64 解析方式
                String url = (String) params[0];
                mDrawable = (LevelListDrawable) params[1];
                return ImageUtil.getHttpBitmap(url);
            }


            /**
             * 图片下载完成后执行
             */
            @Override
            protected void onPostExecute(final Bitmap bitmap) {
                if (context == null || context.isFinishing()) {
                    return;
                }
                if (bitmap != null) {
                    mDrawable.addLevel(1, 1, new BitmapDrawable(bitmap));

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    if (width > maxWidth) {
                        double f1 = new BigDecimal((float) width / maxWidth).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        height = (int) (height / f1);
                        width = maxWidth;
                    }
                    mDrawable.setBounds(0, 0, width, height);
                    mDrawable.setLevel(1);
                    contentTv.invalidate();
                    CharSequence t = contentTv.getText();
                    contentTv.setText(t);
                }
                DialogHelper.hideWaiting(fm);

            }
        }

    }

    private void addItems(String title, String value) {
        itemView = View.inflate(context, R.layout.item_key_value, null);
        TextView keyTv = (TextView) itemView.findViewById(R.id.item_key_tv);
        TextView valueTv = (TextView) itemView.findViewById(R.id.item_value_tv);//工期

        keyTv.setText(title);
        valueTv.setText(value);
        //产值
        itemsLayout.addView(itemView);
    }


    private void setFileListData() {
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        TextView fileTitleTv = (TextView) findViewById(R.id.card_detail_file_title);
        fileTitleTv.setText(R.string.file_link_list);
        fileTitleTv.setTextColor(ContextCompat.getColor(this, R.color.color999));
        ScrollListView listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        List<FileInfo> attList = info.getAttachment();
        if (attList != null) {
            for (FileInfo att : attList) {
                fileData.add(new FileListInfo(att.name, att.url, Constant.TYPE_SEE));
            }
        }
        if (fileData == null || fileData.size() == 0) {
            findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        } else {
            linkTv.setVisibility(View.GONE);
        }
        FileListAdapter fileListAdapter = new FileListAdapter(this, fileData);
        listView.setAdapter(fileListAdapter);
    }

    String areaUnit = "平方米";

    //不同的申请类型  不同字段
    private void addProjectView() {
        //addItems("项目编号", info.getCode());
        addItems("所在省份", info.getProvinceName());
        addItems("总投资", info.getInvest() + "万元");
        addItems("项目地理位置", "纬度:" + info.getLatitude() + ",经度:" + info.getLongitude());
        String beginDate = info.getPlanBeginDate();
        String endDate = info.getPlanEndDate();
        addItems("计划施工日期", beginDate + "至" + endDate);
        addItems("总工期", TimeUtils.getBetweenDays(beginDate, endDate) + "天");
        addItems("占地面积", info.landArea+areaUnit);
        addItems("建筑面积", info.buildArea+areaUnit);
        String constructionUnitName = Utils.getDictNameByValue(context,
                KeyConst.Construction_Unit, info.getConstructionUnitId());
        addItems("建设单位", constructionUnitName);
    }

    private void addSectionView() {//工区
        addItems("所属项目", info.getProjectName());
        //addItems("项目编号", info.getCode());
        addItems("工区省份", info.getProvinceName());
        addItems("总投资", info.getInvest() + "万元");
        String beginDate = info.getPlanBeginDate();
        String endDate = info.getPlanEndDate();
        addItems("计划施工日期", beginDate + "至" + endDate);
        addItems("总工期", TimeUtils.getBetweenDays(beginDate, endDate) + "天");
        addItems("起点桩号", info.getStartChainage());
        addItems("终点桩号", info.getEndChainage());
        addItems("建筑面积", info.buildArea+areaUnit);

        addItems("建设单位", Utils.getDictNameByValue(context,
                KeyConst.Construction_Unit, info.getContractUnitId()));

        addItems("监理单位", Utils.getDictNameByValue(context,
                KeyConst.Supervision_Unit, info.getSupervisionUnitId()));
        String headerName = TextUtil.remove_N(info.getHeaderName());
        String headerPhone = info.getHeaderPhone();
        addItems("工区长", headerName + (headerPhone == null ? "" : "(" + headerPhone + ")"));
    }

    private void addBuildSiteView() {//工地
        //addItems("项目编号", info.getCode());
        addItems("所属工区", info.getSectionName());
        addItems("总投资", info.getInvest() + "万元");
        String beginDate = info.getPlanBeginDate();
        String endDate = info.getPlanEndDate();
        addItems("计划施工日期", beginDate + "至" + endDate);
        addItems("总工期", TimeUtils.getBetweenDays(beginDate, endDate) + "天");
        addItems("建筑面积", info.buildArea+areaUnit);
        addItems("建筑高度", info.buildHeight + "米");
        addItems("建设单位", Utils.getDictNameByValue(context,
                KeyConst.Construction_Unit, info.getConstructionUnitId()));

        addItems("设计单位", Utils.getDictNameByValue(context,
                KeyConst.Design_Unit, info.getDesignUnitId()));
    }

}
