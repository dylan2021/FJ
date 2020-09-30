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
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.bean.EmplyeeInfo;
import com.android.baihuahu.bean.FileListInfo;
import com.android.baihuahu.bean.GroupItemInfo;
import com.android.baihuahu.core.net.GsonRequest;
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
import com.android.baihuahu.view.ScrollListView;
import com.android.baihuahu.widget.mulpicture.MulPictureActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;
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
public class DangerAddActivity extends CommonBaseActivity {
    private DangerAddActivity context;
    private ListView mListView;
    private EmplyeeListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private int signerId = 0;
    private LinearLayout itemLayout;
    private long date;
    private String signerName;
    private TextView dateValueTv;
    private TextView chooseSingerTv;
    private EditText checkContentEt;
    private CommonInfo info;
    private boolean isTypeSafy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_quality_danger_add);
        context = this;
        Intent i = getIntent();
        info = (CommonInfo) i.getSerializableExtra(KeyConst.OBJ_INFO);
        isTypeSafy = i.getBooleanExtra(KeyConst.type, false);
        initTitleBackBt(R.string.add_new_danger);
        initView();
        initFileView();
    }

    private void initView() {
        itemLayout = (LinearLayout) findViewById(R.id.item_layout);

        checkContentEt = findViewById(R.id.check_content_et);//描述

        dateValueTv = (TextView) findViewById(R.id.date_tv);
        dateValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickeTimeDilog((TextView) v);
            }
        });
        chooseSingerTv = (TextView) findViewById(R.id.singer_name_tv);
        getEmployeeData();
        chooseSingerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtil.isEmptyList(employeeList)) {
                    getEmployeeData();
                    return;
                }
                new MaterialDialog.Builder(context)
                        .items(employeeNameList)
                        .title("选择人员")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView,
                                                    int position, CharSequence text) {
                                signerId = employeeList.get(position).getId();
                                signerName = text + "";
                                chooseSingerTv.setText(signerName);
                            }
                        })
                        .show();
            }
        });
    }

    private List<GroupItemInfo> deptList = new ArrayList<>();

    private void showPickeTimeDilog(final TextView timeBt) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        date = millseconds;
                        String timeStr = TimeUtils.getTimeYmd(millseconds);
                        timeBt.setText(timeStr);
                        //durationHourTv.setText(TimeUtils.betweenHours(startTime, endTime));
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

    //获取班组下的人员列表
    private void getEmployeeData() {
        employeeList.clear();
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/upms/employees/all";
        Response.Listener<List<EmplyeeInfo>> successListener =
                new Response.Listener<List<EmplyeeInfo>>() {
                    @Override
                    public void onResponse(List<EmplyeeInfo> result) {
                        employeeList = result;
                        if (TextUtil.isEmptyList(employeeList)) {
                            return;
                        }
                        for (EmplyeeInfo info : employeeList) {
                            employeeNameList.add(info.employeeName);
                        }
                    }
                };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "返回失败+" + TextUtil.getErrorMsg(volleyError));
                ToastUtil.show(context, R.string.server_exception);
            }
        };

        Request<List<EmplyeeInfo>> request = new GsonRequest<List<EmplyeeInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<EmplyeeInfo>>() {
        }.getType()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                //params.put(KeyConst.Accept, "*/*");
                return params;
            }
        };
        App.requestQueue.add(request);
    }

    private List<EmplyeeInfo> employeeList = new ArrayList<>();
    private List<String> employeeNameList = new ArrayList<>();


    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != mRefreshLayout) {
            mRefreshLayout.autoRefresh(0);
        }
    }

    public void onCommitClick(View view) {
        String dangerDesc = checkContentEt.getText().toString();
        if (TextUtil.isEmpty(dangerDesc)) {
            ToastUtil.show(context, "隐患描述" + getString(R.string.cannot_empty));
            return;
        }

        if (date == 0) {
            ToastUtil.show(context, "限期整改日期" + getString(R.string.cannot_empty));
            return;
        }
        if (TextUtil.isEmpty(signerName)) {
            ToastUtil.show(context, "签收人" + getString(R.string.cannot_empty));
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

        if (isTypeSafy) {
            map.put(KeyConst.securityId, info.id);
            map.put(KeyConst.inspectPic, attachList);
        } else {
            map.put(KeyConst.qualityId, info.id);

            map.put(KeyConst.dangerPic, attachList);

            map.put(KeyConst.inspectDate, info.getInspectDate());

            map.put(KeyConst.inspector, info.inspector);
            map.put(KeyConst.inspectResult, info.getInspectResult());
        }
        map.put(KeyConst.dangerDesc, dangerDesc);

        map.put(KeyConst.rectifyDate, TimeUtils.getTimeYmd(date));

        map.put(KeyConst.signer, signerId);//签收人编号
        map.put(KeyConst.signerName, signerName);//签收人名称


        String url_p = isTypeSafy ? "/biz/bizSecurity/danger/save" : "/biz/bizDanger/save";
        String url = Constant.WEB_SITE + url_p;
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