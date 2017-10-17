package com.yzh.rfidbike_police.fragment;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yzh.rfid.app.request.GetSystemMessageRequest;
import com.yzh.rfid.app.response.GetSystemMessageResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.adapter.AdapterSystemMsg;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseV4Fragment;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.model.EventBusMsgPush;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.logort.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yzh.rfidbike_police.socket.SocketAppPacket.COMMAND_ID_POLICE_SYSTEM_MSG;


/**
 * Created by appadmin on 2017/1/13.
 */

public class SystemMsgFragment extends BaseV4Fragment {
    private PullToRefreshListView lv_sysmsg;
    private AdapterSystemMsg adapter;
    private List<EventBusMsgPush> systemMessageList = new ArrayList<>();
    private LinearLayout ll_sysmsg;

    private int pageNum = 1;
    private int pageSize = 10;
    private static final int MSG_get_data_failed = 5112;
    private int getDataType = 0;//0初始加载 1表示下拉刷新 2表示上拉加载
    private boolean isLastPage = false;

    private Handler handler = new Handler() {
        @SuppressWarnings({"unused", "unchecked"})
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_get_data_failed:

                        ToastUtils.showTextToast(getContext(), getResources().getString(R.string.get_data_error));
                        lv_sysmsg.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                lv_sysmsg.onRefreshComplete();
                            }
                        }, 1000);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //		super.handleMessage(msg);
        }
    };


    @Override
    protected int getContentView() {
        return R.layout.fragment_sys_msg;
    }


//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_sys_msg,container,false);
//        return view;
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();

    }


    private void initView() {
        ll_sysmsg = (LinearLayout) getActivity().findViewById(R.id.ll_msg_sysfrag);
        ll_sysmsg.setVisibility(View.GONE);
        lv_sysmsg = (PullToRefreshListView)getActivity().findViewById(R.id.lv_sys_msg);
        lv_sysmsg.setMode(PullToRefreshBase.Mode.BOTH);// 同时支持上拉下拉
        adapter = new AdapterSystemMsg(getContext());
        adapter.setDatas(systemMessageList);
        lv_sysmsg.setAdapter(adapter);

        lv_sysmsg.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                setUpdateTime(refreshView);
                isLastPage = false;
                getDataType = 1;
                pageNum = 1;

                doSocketRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isLastPage) {
                    ToastUtils.showTextToast(getContext(), getResources().getString(R.string.this_is_last_page));
                    lv_sysmsg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lv_sysmsg.onRefreshComplete();
                        }
                    }, 1000);
                    return;
                }
                setUpdateTime(refreshView);
                getDataType = 2;

                pageNum++;

                lv_sysmsg.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        lv_sysmsg.onRefreshComplete();
                    }
                }, 1000);

                doSocketRefresh();

            }
        });
        initListViewTipText();

    }

    /**
     * 初始化列表刷新时的提示文本
     */
    private void initListViewTipText() {
        // 设置上拉刷新文本
        ILoadingLayout startLabels = lv_sysmsg.getLoadingLayoutProxy(true,
                false);
        startLabels.setPullLabel(getResources().getString(R.string.pull_down_refresh));
        startLabels.setReleaseLabel(getResources().getString(R.string.release_refresh));
        startLabels.setRefreshingLabel(getResources().getString(R.string.refreshing));

        // 设置下拉刷新文本
        ILoadingLayout endLabels = lv_sysmsg.getLoadingLayoutProxy(false, true);
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


    private void initData() {
        //预留listView item中删除标志的点击事件
        doSocket();
    }

    @Override
    protected void doSocket() {
        handler.sendEmptyMessageDelayed(MSG_get_data_failed, App.WAITTING_SECOND);
//        super.doSocket();
        final GetSystemMessageRequest.GetSystemMessageRequestMessage.Builder message = GetSystemMessageRequest.GetSystemMessageRequestMessage.newBuilder();
        message.setPageNo(pageNum);
        message.setPageSize(pageSize);
        message.setSession(PreferenceData.loadSession(getActivity()));

        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(COMMAND_ID_POLICE_SYSTEM_MSG, message.build().toByteArray());
            }
        }.start();

    }

    protected void doSocketRefresh() {
        handler.sendEmptyMessageDelayed(MSG_get_data_failed, App.WAITTING_SECOND);
        final GetSystemMessageRequest.GetSystemMessageRequestMessage.Builder message = GetSystemMessageRequest.GetSystemMessageRequestMessage.newBuilder();
        message.setPageNo(pageNum);
        message.setPageSize(pageSize);
        message.setSession(PreferenceData.loadSession(getContext()));

        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(COMMAND_ID_POLICE_SYSTEM_MSG, message.build().toByteArray());
            }
        }.start();


    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {

        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_POLICE_SYSTEM_MSG) {
                GetSystemMessageResponse.GetSystemMessageResponseMessage getSystemMessage = GetSystemMessageResponse.GetSystemMessageResponseMessage.parseFrom(eventPackage.getCommandData());
                if (getDataType == 0) {
                    handler.removeMessages(MSG_get_data_failed);
//                    mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
//                    if (dlgWaiting.isShowing()) {
//                        dlgWaiting.hide();
//                    }
                }else {
                    lv_sysmsg.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            lv_sysmsg.onRefreshComplete();
                        }
                    }, 1000);
                    handler.removeMessages(MSG_get_data_failed);
                }


                if (getSystemMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(getActivity(), getSystemMessage.getErrorMsg().getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (getSystemMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {

                    if (getDataType == 1 || getDataType == 0) {
                        if (systemMessageList.size() > 0) {
                            systemMessageList.clear();
                        }
                        for(int i=0;i<getSystemMessage.getSystemMessageList().size();i++)
                        {
                            EventBusMsgPush msg=new EventBusMsgPush();
                            msg.setMsgTitle(getSystemMessage.getSystemMessageList().get(i).getMessageSubject());
                            msg.setMsgContent(getSystemMessage.getSystemMessageList().get(i).getMessageDescription());

                            msg.setMsgType(getSystemMessage.getSystemMessageList().get(i).getMessageType());
                            msg.setSubMsgType(getSystemMessage.getSystemMessageList().get(i).getMessageSubType());
                            msg.setCreateDate(getSystemMessage.getSystemMessageList().get(i).getCreateDate());
                            msg.setReferenceId(String.valueOf(getSystemMessage.getSystemMessageList().get(i).getReferenceId()));
                            systemMessageList.add(msg);
                        }
//                        systemMessageList.addAll(getSystemMessage.getSystemMessageList());
                        if (getSystemMessage.getIsLastPage().equals("1")) {
                            isLastPage = true;
                        }
                        Log.d("print", "onEventMainThread: InsuranceMessageList " + systemMessageList.size());

                        adapter.notifyDataSetChanged();
                    }
                    else if (getDataType == 2) {
                        if (getSystemMessage.getIsLastPage().equals("1")) {
                            isLastPage = true;
                        }
                        for(int i=0;i<getSystemMessage.getSystemMessageList().size();i++)
                        {
                            EventBusMsgPush msg=new EventBusMsgPush();
                            msg.setMsgTitle(getSystemMessage.getSystemMessageList().get(i).getMessageSubject());
                            msg.setMsgContent(getSystemMessage.getSystemMessageList().get(i).getMessageDescription());

                            msg.setMsgType(getSystemMessage.getSystemMessageList().get(i).getMessageType());
                            msg.setSubMsgType(getSystemMessage.getSystemMessageList().get(i).getMessageSubType());
                            msg.setCreateDate(getSystemMessage.getSystemMessageList().get(i).getCreateDate());
                            msg.setReferenceId(String.valueOf(getSystemMessage.getSystemMessageList().get(i).getReferenceId()));
                            systemMessageList.add(msg);
                        }

//                        systemMessageList.addAll(getSystemMessage.getSystemMessageList());

                        adapter.notifyDataSetChanged();
                    }

                    if (systemMessageList.size() == 0) {
                        ll_sysmsg.setVisibility(View.VISIBLE);
                        lv_sysmsg.setVisibility(View.GONE);
                    }else
                    {
                        ll_sysmsg.setVisibility(View.GONE);
                        lv_sysmsg.setVisibility(View.VISIBLE);
                    }


                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        systemMessageList.clear();
    }


    public void onEventMainThread(final EventBusMsgPush eventPackage)
    {
        if(isActivityTop()) {

            if(eventPackage.getMsgType()==1)
            {
                systemMessageList.add(0,eventPackage);
                adapter.notifyDataSetChanged();
                if (systemMessageList.size() == 0) {
                    ll_sysmsg.setVisibility(View.VISIBLE);

                    lv_sysmsg.setVisibility(View.GONE);
                }else
                {
                    ll_sysmsg.setVisibility(View.GONE);
                    lv_sysmsg.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    protected boolean isActivityTop(){
        ActivityManager manager = (ActivityManager) this.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(this.getActivity().getClass().getName());
    }
}
