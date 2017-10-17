package com.yzh.rfidbike_police.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yzh.rfid.app.request.GetBikeLostRequest;
import com.yzh.rfid.app.response.BikeLost;
import com.yzh.rfid.app.response.GetBikeLostResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.adapter.AdapterGetReportBike;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.view.widgets.Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by appadmin on 2017/2/23.
 */

public class ReportBikeListActivity extends BaseActivity implements Header.headerListener {

    @BindView(R.id.report_bike_header)
    Header mHeader;
    @BindView(R.id.report_bike_lv)
    PullToRefreshListView report_bike_lv;
    @BindView(R.id.ll_report_bike)
    LinearLayout ll_report_bike;

    private AdapterGetReportBike adapter;
    private List<BikeLost.BikeLostMessage> reportList = new ArrayList<>();

    private static final int Bike_Report_data_failed = 5012;
    private int getDataType = 0;//0初始加载 1表示下拉刷新
    private boolean isLastPage = false;
    private Handler handler = new Handler() {
        @SuppressWarnings({"unused", "unchecked"})
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case Bike_Report_data_failed:

                        ToastUtils.showTextToast(ReportBikeListActivity.this, getResources().getString(R.string.get_data_error));
                        report_bike_lv.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                report_bike_lv.onRefreshComplete();
                            }
                        }, 1200);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_report);
        ButterKnife.bind(this);
        mHeader.setListener(this);

        initView();

    }

    private void initView() {
        report_bike_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);// 同时支持上拉下拉
        adapter = new AdapterGetReportBike(this);

        report_bike_lv.setAdapter(adapter);
        adapter.setDatas(reportList);
        doSocket();
        report_bike_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.sendEmptyMessageDelayed(Bike_Report_data_failed, App.WAITTING_SECOND);
                setUpdateTime(refreshView);
                getDataType = 1;
                doSocket();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        initListViewTipText();
    }


    /**
     * 初始化列表刷新时的提示文本
     */
    private void initListViewTipText() {
        // 设置上拉刷新文本
        ILoadingLayout startLabels = report_bike_lv.getLoadingLayoutProxy(true,
                false);
        startLabels.setPullLabel(getResources().getString(R.string.pull_down_refresh));
        startLabels.setReleaseLabel(getResources().getString(R.string.release_refresh));
        startLabels.setRefreshingLabel(getResources().getString(R.string.refreshing));

        // 设置下拉刷新文本
        ILoadingLayout endLabels = report_bike_lv.getLoadingLayoutProxy(false, true);
        if(isLastPage)
        {
            endLabels.setPullLabel(getResources().getString(R.string.pull_up_load_more));
            endLabels.setReleaseLabel(getResources().getString(R.string.release_load_more));
            endLabels.setRefreshingLabel(getResources().getString(R.string.last_page));
        }else
        {
            endLabels.setPullLabel(getResources().getString(R.string.pull_up_load_more));
            endLabels.setReleaseLabel(getResources().getString(R.string.release_load_more));
            endLabels.setRefreshingLabel(getResources().getString(R.string.loading_more));
        }

    }

    /**
     * 设置更新时间
     *
     * @param refreshView
     */
    private void setUpdateTime(PullToRefreshBase refreshView) {
        String label = getStringDateNow();
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }


    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDateNow() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }



    @Override
    protected void doSocket() {
        if (getDataType == 0) {
            super.doSocket();
        }else {
            handler.sendEmptyMessageDelayed(Bike_Report_data_failed, App.WAITTING_SECOND);
        }

        final GetBikeLostRequest.GetBikeLostRequestMessage.Builder bikeLostRequest = GetBikeLostRequest.GetBikeLostRequestMessage.newBuilder();
        bikeLostRequest.setSession(PreferenceData.loadSession(this));
        bikeLostRequest.setUserId(PreferenceData.loadLoginAccount(this));
//        bikeLostRequest.setLastDownloadTime(PreferenceData.getLastDownloadTime(this) == 0L ?(System.currentTimeMillis() + 1000*3600*3000):PreferenceData.getLastDownloadTime(this));
        bikeLostRequest.setLastDownloadTime(0);
//        bikeLostRequest.setLastDownloadTime(System.currentTimeMillis() + 1000*3600*3000);

        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_GET_LOST_BIKELIST, bikeLostRequest.build().toByteArray());
            }
        }.start();
    }


    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            //丢失车辆列表
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_GET_LOST_BIKELIST) {
                GetBikeLostResponse.GetBikeLostResponseMessage getBikeLostResponse = GetBikeLostResponse.GetBikeLostResponseMessage.parseFrom(eventPackage.getCommandData());

                if (getDataType == 0) {
                    if (dlgWaiting.isShowing()) {
                        dlgWaiting.dismiss();
                    }
                    mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                }else {
                    report_bike_lv.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            report_bike_lv.onRefreshComplete();
                        }
                    }, 1200);
                    handler.removeMessages(Bike_Report_data_failed);
                }

                if (getBikeLostResponse.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(ReportBikeListActivity.this, getBikeLostResponse.getErrorMsg().getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (getBikeLostResponse.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {

                    reportList.clear();
                   // int size = getBikeLostResponse.getBikeLostList().size();
                    //获取丢失车辆列表
                    for (int i = 0; i < getBikeLostResponse.getBikeLostList().size(); i++) {
                        if (getBikeLostResponse.getBikeLostList().get(i).getStatus().equals("4") || getBikeLostResponse.getBikeLostList().get(i).getStatus().equals("1")) {
                            reportList.add(getBikeLostResponse.getBikeLostList().get(i));
                        }
                    }

//                    PreferenceData.setLastDownloadTime(ReportBikeListActivity.this,System.currentTimeMillis());

//                    adapter.setDatas(reportList);
                    adapter.notifyDataSetChanged();
                    if (reportList.size() == 0) {
                        report_bike_lv.setVisibility(View.GONE);
                        ll_report_bike.setVisibility(View.VISIBLE);
                    }else {
                        report_bike_lv.setVisibility(View.VISIBLE);
                        ll_report_bike.setVisibility(View.GONE);
                    }

                }
            }


        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }
    

    @Override
    public void onClickLeftIcon() {
        finish();
    }

    @Override
    public void onClickRightText() {

    }
}
