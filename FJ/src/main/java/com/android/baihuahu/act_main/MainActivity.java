package com.android.baihuahu.act_main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.baihuahu.App;
import com.android.baihuahu.R;
import com.android.baihuahu.act_3.SysSettingsActivity;
import com.android.baihuahu.act_new.PlanListActivity;
import com.android.baihuahu.act_new.WageListActivity;
import com.android.baihuahu.act_other.LoginActivity;
import com.android.baihuahu.act_quality.SafyQualityCheckListActivity;
import com.android.baihuahu.act_safy.DangerListActivity;
import com.android.baihuahu.act_safy.SafyDeliveryListActivity;
import com.android.baihuahu.bean.ChooseDataInfo;
import com.android.baihuahu.bean.CommonInfo;
import com.android.baihuahu.core.net.GsonRequest;
import com.android.baihuahu.core.utils.ImageUtil;
import com.android.baihuahu.core.utils.TextUtil;
import com.android.baihuahu.dialogfragment.SimpleDialogFragment;
import com.android.baihuahu.util.BillTreeUtil;
import com.android.baihuahu.util.DialogUtils;
import com.android.baihuahu.util.TimeUtils;
import com.android.baihuahu.util.ToastUtil;
import com.android.baihuahu.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.baihuahu.act_other.BaseFgActivity;
import com.android.baihuahu.core.utils.Constant;
import com.android.baihuahu.core.utils.KeyConst;
import com.android.baihuahu.core.utils.NetUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Dylan
 */
public class MainActivity extends BaseFgActivity {
    public static MainActivity context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SimpleDialogFragment reLoginDialog;
    private TextView constructionUnitNameTv, buildsiteNumberTv, projNameTv,
            startDateTv, investTv, periodTv, lengthTv;
    private int projId;
    private String projName;
    private RefreshLayout mRefreshLayout;
    private ListView mListView;
    private MainListAdapter mAdapter;
    private View headerView;
    private SimpleDraweeView picSdv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_main);
        context = this;

        sp = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        editor = sp.edit();

        initView();
        //showTipGuild();
    }

    private void showTipGuild() {
        boolean isFirstLuncher = sp.getBoolean(KeyConst.IS_FIRST_SHOW_GUILD, true);
        if (isFirstLuncher) {
            final int picId = R.drawable.ic_project_management;
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                    .dialog_appcompat_theme_fullscreen);
            final ImageView guildView = new ImageView(context);
            guildView.setImageResource(picId);
            guildView.setScaleType(ImageView.ScaleType.FIT_START);
            final Dialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setContentView(guildView);
            guildView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.AUTH_TYPE == Constant.BUILDSITE) {
                        dialog.dismiss();
                    } else {
                        guildView.setImageResource(picId);
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (App.AUTH_TYPE == Constant.BUILDSITE) {
                            return false;
                        } else {
                            guildView.setImageResource(picId);
                            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    return false;
                                }
                            });
                            return true;
                        }
                    }
                    return false;
                }
            });

            editor.putBoolean(KeyConst.IS_FIRST_SHOW_GUILD, false).commit();

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            });
        }
    }

    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        getProjInfo();
        getBottomList();

        BillTreeUtil.initBillTreeData(context);
    }

    //底部列表数据
    private void getBottomList() {
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz/bizPlan/main/list?projectId="+App.projecId;
        Response.Listener<List<CommonInfo>> successListener = new Response.Listener<List<CommonInfo>>() {
            @Override
            public void onResponse(List<CommonInfo> result) {
                mRefreshLayout.finishRefresh(0);
                mAdapter.setData(result);
                if (TextUtil.isEmptyList(result)) {
                    return;
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(context, R.string.server_exception);
                mRefreshLayout.finishRefresh(0);
            }
        };

        Request<List<CommonInfo>> request = new GsonRequest<List<CommonInfo>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<CommonInfo>>() {
        }.getType()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                params.put(KeyConst.projectId,  App.projecId);
                return params;
            }
        };
        App.requestQueue.add(request);
    }

    //获取项目信息
    private void getProjInfo() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + "/biz/project/" + projId;

        Response.Listener<ChooseDataInfo> successListener = new Response
                .Listener<ChooseDataInfo>() {
            @Override
            public void onResponse(ChooseDataInfo result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null) {
                    ToastUtil.show(context, "空数据");
                    return;
                }
                setInfoView(result);
            }
        };

        Request<ChooseDataInfo> versionRequest = new
                GsonRequest<ChooseDataInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null == context || context.isFinishing()) {
                            return;
                        }
                        mRefreshLayout.finishRefresh(0);
                        if (!TextUtil.isEmpty(TextUtil.getErrorMsg(error))) {
                            try {
                                JSONObject obj = new JSONObject(TextUtil.getErrorMsg(error));
                                if (obj != null) {
                                    if (10002 == obj.getInt(KeyConst.error)) {
                                        showDialog(context.getString(R.string.token_invalid));
                                        return;
                                    }
                                    DialogUtils.showTipDialog(context, obj.getString(KeyConst.message));
                                    return;
                                }
                            } catch (JSONException e) {
                            }
                        }
                        ToastUtil.show(context, R.string.server_exception);
                    }
                }, new TypeToken<ChooseDataInfo>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConst.Authorization, KeyConst.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);

    }

    private int[] mainBtsId = {R.id.ledger_tv,R.id.process_tv, R.id.emplyee_tv,R.id.wage_tv,
            R.id.device_tv, R.id.local_tv, R.id.quality_check_tv, R.id.quality_danger_tv,
            R.id.safety_check_tv,R.id.safy_danger_tv,R.id.safety_delivery_tv,R.id.safety_tv,
    };
    private Class classArr[] = {BillListActivity.class,PlanListActivity.class,
            EmplyeeGroupListActivity.class,WageListActivity.class,
            DeviceListActivity.class, LocalMaterailListActivity.class,
            SafyQualityCheckListActivity.class, SafyQualityCheckListActivity.class,
            SafyQualityCheckListActivity.class,SafyQualityCheckListActivity.class,
            SafyDeliveryListActivity.class,SafyMaterailListActivity.class,
    };

    private void initView() {
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        Utils.setLoadHeaderFooter(context, mRefreshLayout);
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                ToastUtil.show(context, getString(R.string.no_more_data));
            }
        });

        mListView = (ListView) findViewById(R.id.circle_lv);
        headerView = getLayoutInflater().inflate(R.layout.main_header_view, null);
        initHeadView();
        mListView.addHeaderView(headerView);//要放在setAdapter前面,

        mAdapter = new MainListAdapter(context);
        mListView.setAdapter(mAdapter);


        for (int i = 0; i < mainBtsId.length; i++) {
            final int finalI = i;
            final TextView v = headerView.findViewById(mainBtsId[i]);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, classArr[finalI]);
                    intent.putExtra(KeyConst.id, projId);
                    intent.putExtra(KeyConst.title, v.getText().toString());
                    startActivity(intent);
                }
            });
        }

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getData();//请求所有数据
            }
        });

    }

    private void initHeadView() {
        picSdv = headerView.findViewById(R.id.pic_sdv);
        projNameTv = headerView.findViewById(R.id.main_name_tv);
        constructionUnitNameTv = headerView.findViewById(R.id.construction_unit_name);
        buildsiteNumberTv = headerView.findViewById(R.id.buildsite_number_tv);

        startDateTv = headerView.findViewById(R.id.main_start_date_tv);
        periodTv = headerView.findViewById(R.id.main_period_tv);//总工期
        investTv = headerView.findViewById(R.id.main_invest_tv);//总投资
        lengthTv = headerView.findViewById(R.id.main_length_tv);

    }

    @Override
    protected void onStart() {
        super.onStart();
        projId = sp.getInt(KeyConst.SP_PROJECT_ID, 0);
        App.projecId = projId + "";
        getData();
        Utils.requestDictData(context);
    }

    private void setInfoView(ChooseDataInfo info) {
        projName = info.getName();
        projNameTv.setText(projName);
        picSdv.setImageURI(ImageUtil.getImageUrl(info.getPic()));
        List<Object> list = info.getBizSectionVOList();
        buildsiteNumberTv.setText((list == null ? 0 : list.size()) + "个");

        constructionUnitNameTv.setText(Utils.getDictNameByValue(context,
                KeyConst.Construction_Unit, info.getConstructionUnitId()));

        String beginDate = info.getPlanBeginDate();
        String endDate = info.getPlanEndDate();
        startDateTv.setText("开工日期：" + beginDate);

        periodTv.setText("总工期：" + TimeUtils.getBetweenDays(beginDate, endDate) + "天");

        investTv.setText("总投资：" + info.getInvest() + "万元");
        lengthTv.setText("地理位置：" + info.getLength() );

    }

    private void showDialog(String msg) {
        if (reLoginDialog != null) {
            return;
        }
        reLoginDialog = new SimpleDialogFragment();
        reLoginDialog.setDialogWidth(220);
        reLoginDialog.setCancelable(false);

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(msg);
        tv.setTextColor(getResources().getColor(R.color.color666));
        reLoginDialog.setContentView(tv);

        reLoginDialog.setNegativeButton(R.string.reLogin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reLoginDialog.dismiss();
                editor.putString(Constant.sp_pwd, "").commit();

                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                context.finish();

            }
        });
        reLoginDialog.show(getSupportFragmentManager().beginTransaction(), "successDialog");
    }


    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                ToastUtil.show(context, "再点一次退出");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            } else {
                context.finish();
            }
        }
        return false;
    }

    //选择项目
    public void onProjectChooseClick(View view) {
        startActivity(new Intent(context, ChooseProjectActivity.class));
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    //个人资料
    public void onMeProfileClick(View view) {
        startActivity(new Intent(context, SysSettingsActivity.class));
    }

    //规划概括
    public void onDesignInfoClick(View view) {
        Intent intent = new Intent(context, DesignInfoActivity.class);
        intent.putExtra(KeyConst.id, projId);
        intent.putExtra(KeyConst.type, 0);
        intent.putExtra(KeyConst.title, projName);
        context.startActivity(intent);
    }

    public void onSectionListBtClick(View view) {
        Intent intent = new Intent(context, SectionListActivity.class);
        intent.putExtra(KeyConst.id, projId);
        intent.putExtra(KeyConst.type, 1);
        intent.putExtra(KeyConst.title, projName);
        context.startActivity(intent);
    }
}