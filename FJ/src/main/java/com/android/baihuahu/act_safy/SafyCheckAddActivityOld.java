package com.android.baihuahu.act_safy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_main.EmplyeeListAdapter;
import com.android.baihuahu.act_other.CommonBaseActivity;
import com.android.baihuahu.act_other.PictBean;
import com.android.baihuahu.adapter.FileListAdapter;
import com.android.baihuahu.bean.DictInfo;
import com.android.baihuahu.bean.FileListInfo;
import com.android.baihuahu.bean.GroupItemInfo;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.DialogHelper;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.android.baihuahu.core.utils.RetrofitUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.util.FileTypeUtil;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.baihuahu.view.ScrollListView;
import com.android.baihuahu.widget.mulpicture.MulPictureActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dylan
 */
public class SafyCheckAddActivityOld extends CommonBaseActivity {
    private SafyCheckAddActivityOld context;
    private ListView mListView;
    private EmplyeeListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private String areaId ;
    private LinearLayout itemLayout;
    private EditText remarkTv;
    private TextView dateValueTv;
    private TextView areaTv;
    private EditText inspector_et;
    private boolean isGroupList;
    private EditText checkContentEt;
    private TextView isDangerTv;
    private int isDanger = 2;//默认:不存在
    private List<DictInfo.DictValuesBean> dictList;
    private String inspectArea,inspectDate;

    //todo 新增安全检查; 主界面进去的质量和安全隐患列表:
    // 直接用"质量和安全检查(两者也是一个页面)"列表->
    // 直接用是否有隐患字段筛选(
    // inspectResult/isDanger:"1,存在隐患(隐患列表)", "2,合格(检查列表)")!!;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_safy_add);
        context = this;
        initTitleBackBt(R.string.check_add);
        initView();
        initFileView();
    }

    private void initView() {
        itemLayout = (LinearLayout) findViewById(R.id.item_layout);
        isDangerTv = findViewById(R.id.inspectResult_tv);
        remarkTv = (EditText) findViewById(R.id.remark_tv);

        checkContentEt = findViewById(R.id.check_content_et);
        inspector_et = findViewById(R.id.inspector_et);

        dateValueTv = (TextView) findViewById(R.id.date_tv);
        areaTv = (TextView) findViewById(R.id.area_tv);
        dateValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickeTimeDilog((TextView) v);
            }
        });
        areaTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDictListDialog(areaTv,KeyConst.CHECK_AREA_TYPE);

            }
        });
        isDangerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .items(R.array.items_safy_check_result)
                        .title("是否存在隐患")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView,
                                                    int position, CharSequence text) {
                                isDanger = position+1;//1 存在  2  不存在
                                isDangerTv.setText(text + "");
                            }
                        })
                        .show();
            }
        });
    }
    //读取本地字典数据
    private void showDictListDialog(final TextView tv, final String DICT_TYPE_KEY) {
        dictList = Utils.getDictListByType(context, DICT_TYPE_KEY);
        if (dictList == null || dictList.size() == 0) {
            ToastUtil.show(context, R.string.no_data);
            return;
        }
        ArrayList<String> nameList = new ArrayList<>();
        for (DictInfo.DictValuesBean info : dictList) {
            nameList.add(info.getName());
        }
        new MaterialDialog.Builder(context)
                .items(nameList)// 列表数据
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView,
                                            int position, CharSequence text) {
                        areaId =dictList.get(position).getValue();
                        inspectArea=text+"";
                        tv.setText(text);
                    }
                })
                .show();
    }
    private List<GroupItemInfo> deptList = new ArrayList<>();

    private void showPickeTimeDilog(final TextView timeBt) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        inspectDate = TimeUtils.getTimeYmd(millseconds);
                        timeBt.setText(inspectDate);
                    }
                })
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("")//标题
                .setCyclic(false)
                .setThemeColor(context.getResources().getColor(R.color.mainColor))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextSize(16)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != mRefreshLayout) {
            mRefreshLayout.autoRefresh(0);
        }
    }

    public void onCommitClick(View view) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        if (TextUtil.isEmpty(inspectArea)) {
            ToastUtil.show(context, "施工区域" + getString(R.string.cannot_empty));
            return;
        }
        if (TextUtil.isEmpty(inspectDate)) {
            ToastUtil.show(context, "检查日期" + getString(R.string.cannot_empty));
            return;
        }
        String inspector = inspector_et.getText().toString();
        if (TextUtil.isEmpty(inspector)) {
            ToastUtil.show(context, "检查人" + getString(R.string.cannot_empty));
            return;
        }
        String contentStr = checkContentEt.getText().toString();
        if (TextUtil.isEmpty(contentStr)) {
            ToastUtil.show(context, "报检内容" + getString(R.string.cannot_empty));
            return;
        }

        Map<String, Object> map = new HashMap<>();
        //附件
        JSONArray attachList = new JSONArray();
        try {
            if (fileData != null) {
                for (FileListInfo fileInfo : fileData) {
                    String fileUrl = fileInfo.fileUrl;
                    String fileName = fileInfo.fileName;
                    JSONObject fileObj = new JSONObject();
                    fileObj.put(KeyConst.name, fileName);
                    fileObj.put(KeyConst.url, fileUrl);
                    attachList.put(fileObj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put(KeyConst.pic, attachList);
        map.put(KeyConst.areaId, areaId);
        map.put(KeyConst.inspectArea, inspectArea);
        map.put(KeyConst.inspectDate, inspectDate);

        map.put(KeyConst.inspector, inspector);
        map.put(KeyConst.inspectInfo, contentStr);

        map.put(KeyConst.isDanger, isDanger);

        map.put(KeyConst.remark, remarkTv.getText().toString());

        String url = Constant.WEB_SITE + "/biz/bizQuality/save";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            return;
                        }
                        ToastUtil.show(context, "添加成功");
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (context == null || context.isFinishing()) {
                    return;
                }
                String errorMsg = TextUtil.getErrorMsg(error);
                Log.d(TAG, "工资录入数据失败:" + errorMsg);
                if (errorMsg != null) {
                    try {
                        JSONObject obj = new JSONObject(errorMsg);
                        if (obj != null) {
                            ToastUtil.showBottom(context, obj.getString(KeyConst.message));
                            return;
                        }
                    } catch (JSONException e) {
                    }
                }
                ToastUtil.show(context, getString(R.string.server_exception));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Content_Type, Constant.application_json);
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                params.put(KeyConst.projectId, App.projecId);

                return params;
            }
        };
        App.requestQueue.add(jsonRequest);
    }

    private List<FileListInfo> fileData = new ArrayList<>();

    private ScrollListView listView;
    private FileListAdapter fileListAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //上传附件
        String fileType = "";
        String path = "";
        if (data != null && data.getData() != null) {
            path = FileTypeUtil.getPath(context, data.getData());
            //不是合格的类型
            if (!FileTypeUtil.isFileType(path) && !ImageUtil.isImageSuffix(path)) {
                ToastUtil.show(context, "暂不支持该文件类型");
                return;
            }
            fileType = ImageUtil.isImageSuffix(path) ? Constant.FILE_TYPE_IMG : Constant.FILE_TYPE_DOC;
        }
        //上传图片
        if (requestCode == 101 && data != null) {
            setIntent(data);
            getBundleP();
            if (pictures != null && pictures.size() > 0) {
                fileType = Constant.FILE_TYPE_IMG;
                for (int i = 0; i < pictures.size(); i++) {
                    path = pictures.get(i).getLocalURL();
                    fileType = Constant.FILE_TYPE_IMG;
                }
            }
        }
        if (TextUtil.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        uploadPictureThread(file, fileType);
    }

    private void initFileView() {
        //附件
        listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
        fileListAdapter.setCallBack(new FileListAdapter.DataRemoveCallBack() {
            @Override
            public void finish(List<FileListInfo> data) {
                fileData = data;
            }
        });

        findViewById(R.id.card_detail_file_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseFileDialog();
            }
        });
    }

    //上传附件
    private void uploadPictureThread(final File file, final String fileType) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, getString(R.string.uploading));
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(KeyConst.fileType, fileType);
        final String url = Constant.WEB_FILE_UPLOAD;
        new Thread() {
            @Override
            public void run() {
                try {
                    RetrofitUtil.upLoadByCommonPost(url, file, map,
                            new RetrofitUtil.FileUploadListener() {
                                @Override
                                public void onProgress(long pro, double precent) {
                                }

                                @Override
                                public void onFinish(int code, final String responseUrl, Map<String, List<String>> headers) {
                                    if (200 == code && responseUrl != null) {
                                        final String finalResponseUrl = responseUrl;
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fileData.add(new FileListInfo(
                                                        file.getName(), finalResponseUrl, file.length(), finalResponseUrl));
                                                fileListAdapter.setDate(fileData);
                                                ImageUtil.reSetLVHeight(context, listView);
                                                DialogHelper.hideWaiting(fm);
                                            }
                                        });
                                    } else {
                                        ToastUtil.show(context, R.string.server_exception);
                                        DialogHelper.hideWaiting(fm);
                                    }
                                }
                            });
                } catch (IOException e) {
                    ToastUtil.show(context, R.string.server_exception);
                    DialogHelper.hideWaiting(fm);
                }

            }
        }.start();
    }


    public void getBundleP() {
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                pictures = (List<PictBean>) bundle.getSerializable("pictures") != null ?
                        (List<PictBean>) bundle.getSerializable("pictures") : new
                        ArrayList<PictBean>();
            }
        }
    }


    private void choisePicture() {
        int choose = 9 - pictures.size();
        Intent intent = new Intent(context, MulPictureActivity.class);
        bundle = setBundle();
        bundle.putInt("imageNum", choose);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivityForResult(intent, 101);
    }

    //选择文件
    private void choiseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    private void showChooseFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme);
        //    指定下拉列表的显示数据
        final String[] chooseFileArr = {"图片", "手机文件"};
        //    设置一个下拉的列表选择项
        builder.setItems(chooseFileArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    //文件
                    case 0:
                        choisePicture();
                        break;
                    //图片
                    case 1:
                        choiseFile();
                        break;
                }
            }
        });
        builder.show();
    }


}