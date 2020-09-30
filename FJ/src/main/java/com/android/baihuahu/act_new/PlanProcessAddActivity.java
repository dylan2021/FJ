package com.android.baihuahu.act_new;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_other.CommonBaseActivity;
import com.android.baihuahu.act_other.PictBean;
import com.android.baihuahu.adapter.FileListAdapter;
import com.android.baihuahu.bean.FileListInfo;
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
import com.android.volley.toolbox.StringRequest;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

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

public class PlanProcessAddActivity extends CommonBaseActivity {
    private PlanProcessAddActivity context;
    private int planId;
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;
    private FragmentManager fm;
    private EditText report_price_et, quantities_et;
    private long startTime, endTime;
    private TextView startTimeValueTv, endTimeValueTv, durationHourTv;
    private int BILL_ID_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_process_add);
        context = this;
        fm = getSupportFragmentManager();
        planId = getIntent().getIntExtra(KeyConst.id, 0);
        BILL_ID_3 = getIntent().getIntExtra(KeyConst.thirdBillId, 0);


        initTitleBackBt("进度汇报");

        initView();

        initFileView();
    }

    public void getPrice(String quantities) {
        String url = Constant.WEB_SITE +
                "/biz/bizPlan/getTotal?thirdBillId=" + BILL_ID_3 + "&quantities=" + quantities;
        Log.d(TAG, "数据"+url);
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        report_price_et.setText(result);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                report_price_et.setText("");
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                params.put(KeyConst.projectId, App.projecId);
                return params;
            }

        };

        App.requestQueue.add(jsonObjRequest);

    }

    private void initView() {
        quantities_et = findViewById(R.id.report_quantities_et);
        quantities_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {
                getPrice(c + "");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        report_price_et = findViewById(R.id.report_price_et);
        startTimeValueTv = findViewById(R.id.start_time_tv);
        endTimeValueTv = findViewById(R.id.end_time_tv);
        durationHourTv = findViewById(R.id.hour_tv);
        startTimeValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickeTimeDilog((TextView) v, 0);
            }
        });
        endTimeValueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickeTimeDilog((TextView) v, 1);
            }
        });
    }

    private void showPickeTimeDilog(final TextView timeBt, final int type) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (type == 0) {
                            if (millseconds > System.currentTimeMillis()) {
                                ToastUtil.show(context, "开工时间不能大于现在");
                                return;
                            }
                            if (endTime != 0 && millseconds > endTime) {
                                ToastUtil.show(context, "开工时间不能大于完工时间");
                                return;
                            }
                            startTime = millseconds;
                        } else {
                            if (millseconds < startTime) {
                                ToastUtil.show(context, "完工时间不能小于开工时间");
                                return;
                            }
                            endTime = millseconds;
                        }
                        String timeStr = TimeUtils.getTimeYmd(millseconds);
                        timeBt.setText(timeStr);
                        durationHourTv.setText(TimeUtils.getBetweenDays(
                                TimeUtils.getTimeYmd(startTime), TimeUtils.getTimeYmd(endTime)) + "");
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


    private void postReport() {
        String reportQuantities = quantities_et.getText().toString();
        String writeValue = report_price_et.getText().toString();
        if (startTime == 0) {
            ToastUtil.show(context, "实际开工日期" + getString(R.string.cannot_empty));
            return;
        }
        if (endTime == 0) {
            ToastUtil.show(context, "实际完工日期" + getString(R.string.cannot_empty));
            return;
        }
        if (ToastUtil.showCannotEmpty(context, reportQuantities, "汇报实物工程量")) {
            return;
        }

        String url = Constant.WEB_SITE + "/biz/bizPlan/report";
        Map<String, Object> map = new HashMap<>();

        map.put(KeyConst.planId, planId);
    /*    map.put(KeyConst.startTime, TimeUtils.getTimeYmdHms(startTime));
        map.put(KeyConst.endTime, TimeUtils.getTimeYmdHms(endTime));*/

        map.put(KeyConst.writeCycle, durationHourTv.getText().toString());//总工期
        map.put(KeyConst.writeValue, writeValue);//产值

        map.put(KeyConst.writeAttachment, null);
        //附件
        if (fileData != null) {
            try {
                JSONArray attachList = new JSONArray();
                for (FileListInfo fileInfo : fileData) {
                    String fileUrl = fileInfo.fileUrl;
                    String fileName = fileInfo.fileName;
                    JSONObject fileObj = new JSONObject();
                    fileObj.put(KeyConst.name, fileName);
                    fileObj.put(KeyConst.url, fileUrl);
                    attachList.put(fileObj);
                }
                map.put(KeyConst.writeAttachment, attachList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        DialogHelper.showWaiting(fm, "加载中...");
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        DialogHelper.hideWaiting(fm);
                        if (result != null) {
                            ToastUtil.show(context, "提交成功");
                            context.finish();
                        } else {
                            ToastUtil.show(context, "提交失败");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(fm);
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

    private List<FileListInfo> fileData = new ArrayList<>();

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

    public void onReportCommitClick(View view) {
        //保存
        postReport();
    }

}
