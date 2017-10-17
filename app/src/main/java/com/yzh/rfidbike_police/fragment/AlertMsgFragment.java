package com.yzh.rfidbike_police.fragment;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yzh.rfid.app.request.GetAlarmMessageRequest;
import com.yzh.rfid.app.response.CommonResponse;
import com.yzh.rfid.app.response.GetAlarmMessageResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.adapter.AdapterAlertMsgFragment;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseV4Fragment;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.model.EventBusMsg;
import com.yzh.rfidbike_police.model.EventBusMsgMessage;
import com.yzh.rfidbike_police.model.EventBusMsgPush;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.logort.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by appadmin on 2017/1/13.
 */

public class AlertMsgFragment extends BaseV4Fragment {
    private PullToRefreshListView lv_alertmsg;
    private AdapterAlertMsgFragment adapter;
    private List<EventBusMsgPush> alartlist;
    private LinearLayout ll_alertmsg;
    private AlertDialog deleteOneDialog;
    private TextView tv_cancel, tv_sure;
    private ImageView iv_cancel;
    private int pageNum = 1;
    private int pageSize = 10;

    private int deleteType = 0;//0是删除某条数据   1001 表示删除整个fragment中的listView列表数据
    private int mPosition;
    private int dataFleshType = 0;//0初始加载 1表示下拉刷新 2表示上拉加载
    private static final int MSG_get_data_failed = 5125;

    private boolean isLastPage = false;

    private Handler handler = new Handler() {
        @SuppressWarnings({"unused", "unchecked"})
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_get_data_failed:

                        ToastUtils.showTextToast(getContext(), getResources().getString(R.string.get_data_error));
                        lv_alertmsg.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                lv_alertmsg.onRefreshComplete();
                            }
                        }, 1000);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //		super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.fragment_push_msg;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDialog();
        initView();
        initData();
    }


    private void initDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_layout_delete_one, null);
        deleteOneDialog = new AlertDialog.Builder(getContext()).setView(layout).create();
    }

    private void initView() {
        alartlist = new ArrayList<>();
        ll_alertmsg = (LinearLayout) getActivity().findViewById(R.id.ll_msg_alertfrag);
        ll_alertmsg.setVisibility(View.GONE);
        lv_alertmsg = (PullToRefreshListView) getActivity().findViewById(R.id.lv_alert_msg);
        lv_alertmsg.setMode(PullToRefreshBase.Mode.BOTH);// 同时支持上拉下拉
        adapter = new AdapterAlertMsgFragment(getContext());
        adapter.setDatas(alartlist);
        lv_alertmsg.setAdapter(adapter);


    }


    private void initData() {
        doSocket(); //获取报警消息

        adapter.setOnDeleteClickListener(new AdapterAlertMsgFragment.OnDeleteClickListener() {
            @Override
            public void onClickListen(View view, int position) {
                mPosition = position;
                //点击显示AlertDialog
                deleteOneDialog.show();

                //设置dialog的样式属性
                Window dialogView = deleteOneDialog.getWindow();
                int width = getActivity().getResources().getDisplayMetrics().widthPixels;
                WindowManager.LayoutParams lp = dialogView.getAttributes();
                dialogView.setBackgroundDrawable(new BitmapDrawable());
                lp.width = width - 160;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                //        lp.height = height / 3;
                lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
                //lp.x = 100;
                lp.y = -300;
                dialogView.setAttributes(lp);

                iv_cancel = (ImageView) dialogView.findViewById(R.id.delete);
                tv_cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
                tv_sure = (TextView) dialogView.findViewById(R.id.tv_sure);
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOneDialog.cancel();
                    }
                });
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOneDialog.cancel();
                    }
                });
                tv_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOneDialog.cancel();
                        deleteType = 0;
                        deleteAlarmMessage(); //调删除接口进行删除
                    }
                });
            }
        });

        //doSocket();

        lv_alertmsg.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                setUpdateTime(refreshView);
                isLastPage = false;
                dataFleshType = 1;
                pageNum = 1;

                doSocket();
//                doRefreshSocket();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isLastPage) {
                    ToastUtils.showTextToast(getContext(), getResources().getString(R.string.this_is_last_page));
                    lv_alertmsg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lv_alertmsg.onRefreshComplete();
                        }
                    }, 1000);
                    return;
                }
                setUpdateTime(refreshView);
                dataFleshType = 2;
                pageNum++;

                lv_alertmsg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lv_alertmsg.onRefreshComplete();
                    }
                }, 1000);

                //获取listView列表数据
                doSocket();
//                doRefreshSocket();

            }
        });
        initListViewTipText();
    }


    /**
     * 初始化列表刷新时的提示文本
     */
    private void initListViewTipText() {
        // 设置上拉刷新文本
        ILoadingLayout startLabels = lv_alertmsg.getLoadingLayoutProxy(true,
                false);
        startLabels.setPullLabel(getResources().getString(R.string.pull_down_refresh));
        startLabels.setReleaseLabel(getResources().getString(R.string.release_refresh));
        startLabels.setRefreshingLabel(getResources().getString(R.string.refreshing));

        // 设置下拉刷新文本
        ILoadingLayout endLabels = lv_alertmsg.getLoadingLayoutProxy(false, true);
        if (isLastPage) {
            endLabels.setPullLabel(getResources().getString(R.string.pull_up_load_more));
            endLabels.setReleaseLabel(getResources().getString(R.string.release_load_more));
            endLabels.setRefreshingLabel(getResources().getString(R.string.last_page));
        } else {
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
//        super.doSocket();
        handler.sendEmptyMessageDelayed(MSG_get_data_failed, App.WAITTING_SECOND);
        final GetAlarmMessageRequest.GetAlarmMessageRequestMessage.Builder alartMessage = GetAlarmMessageRequest.GetAlarmMessageRequestMessage.newBuilder();
        alartMessage.setPageNo(pageNum);
        alartMessage.setPageSize(pageSize);
        alartMessage.setSession(PreferenceData.loadSession(getActivity()));

        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_POLICE_ALART_MSG, alartMessage.build().toByteArray());
            }
        }.start();
    }

//    protected void doRefreshSocket() {
//        handler.sendEmptyMessageDelayed(MSG_get_data_failed, App.WAITTING_SECOND);
//        final GetAlarmMessageRequest.GetAlarmMessageRequestMessage.Builder alartMessage = GetAlarmMessageRequest.GetAlarmMessageRequestMessage.newBuilder();
//        alartMessage.setPageNo(pageNum);
//        alartMessage.setPageSize(pageSize);
//        alartMessage.setSession(PreferenceData.loadSession(getActivity()));
//
//        new Thread() {
//            public void run() {
//                SocketClient client;
//                client = SocketClient.getInstance();
//                client.send(SocketAppPacket.COMMAND_ID_POLICE_ALART_MSG, alartMessage.build().toByteArray());
//            }
//        }.start();
//    }


    @Override
    public void onEventMainThread(EventBusMsgMessage eventBusMsg) {
        super.onEventMainThread(eventBusMsg);
        if (eventBusMsg.getMsgType() == EventBusMsgMessage.MSG_Message_Alarm_Delete) {

            deleteType = EventBusMsgMessage.MSG_Message_Alarm_Delete;
            deleteAlarmMessage();
        }

    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {

        try {
            super.onEventMainThread(eventPackage);
            //删除数据
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_POLICE_DELETE_ALART_MSG) {
                CommonResponse.CommonResponseMessage commonResponseMessage = CommonResponse.CommonResponseMessage.parseFrom(eventPackage.getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);

                if (commonResponseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(getActivity(), commonResponseMessage.getErrorMsg().getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (commonResponseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    //doSocket();
                    if (deleteType == 0) {
                        alartlist.remove(mPosition);
                        adapter.notifyDataSetChanged();
                    } else {
                        alartlist.clear();
//                        alartlist = null;
                        adapter.notifyDataSetChanged();
                    }

                    ToastUtils.showTextToast(getContext(), getResources().getString(R.string.delete_success));
                }

            }

            //加载数据
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_POLICE_ALART_MSG) {
                GetAlarmMessageResponse.GetAlarmMessageResponseMessage getAlarmResponse = GetAlarmMessageResponse.GetAlarmMessageResponseMessage.parseFrom(eventPackage.getCommandData());

                handler.removeMessages(MSG_get_data_failed);
                if (dataFleshType == 1 || dataFleshType == 2) {
                    lv_alertmsg.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            lv_alertmsg.onRefreshComplete();
                        }
                    }, 1000);
//                    handler.removeMessages(MSG_get_data_failed);
//                    if (dlgWaiting.isShowing()) {
//                        dlgWaiting.hide();
//                    }
//                    mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                }


                if (getAlarmResponse.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(getActivity(), getAlarmResponse.getErrorMsg().getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (getAlarmResponse.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {

                    if (dataFleshType == 1 || dataFleshType == 0) {
                        if (alartlist != null) {
                            alartlist.clear();
                        }
                        int size = getAlarmResponse.getAlarmMessageList().size();
                        for (int i = 0; i < size; i++) {
                            EventBusMsgPush msg = new EventBusMsgPush();
                            msg.setMsgTitle(getAlarmResponse.getAlarmMessageList().get(i).getMessageSubject());
                            msg.setMsgContent(getAlarmResponse.getAlarmMessageList().get(i).getMessageDescription());
                            msg.setMsgId(getAlarmResponse.getAlarmMessageList().get(i).getId());
                            msg.setMsgType(getAlarmResponse.getAlarmMessageList().get(i).getMessageType());
                            msg.setSubMsgType(getAlarmResponse.getAlarmMessageList().get(i).getMessageSubType());
                            msg.setCreateDate(getAlarmResponse.getAlarmMessageList().get(i).getCreateDate());
                            msg.setReferenceId(String.valueOf(getAlarmResponse.getAlarmMessageList().get(i).getReferenceId()));
                            alartlist.add(msg);
                        }

                        if (getAlarmResponse.getIsLastPage().equals("1")) {
                            isLastPage = true;
                        }
                        //Log.d("print", "onEventMainThread: InsuranceMessageList " + alartlist.size());
                        adapter.notifyDataSetChanged();
                    } else if (dataFleshType == 2) {
                        if (getAlarmResponse.getIsLastPage().equals("1")) {
                            isLastPage = true;
                        }
                        for (int i = 0; i < getAlarmResponse.getAlarmMessageList().size(); i++) {
                            EventBusMsgPush msg = new EventBusMsgPush();
                            msg.setMsgTitle(getAlarmResponse.getAlarmMessageList().get(i).getMessageSubject());
                            msg.setMsgContent(getAlarmResponse.getAlarmMessageList().get(i).getMessageDescription());
                            msg.setMsgId(getAlarmResponse.getAlarmMessageList().get(i).getId());
                            msg.setMsgType(getAlarmResponse.getAlarmMessageList().get(i).getMessageType());
                            msg.setSubMsgType(getAlarmResponse.getAlarmMessageList().get(i).getMessageSubType());
                            msg.setCreateDate(getAlarmResponse.getAlarmMessageList().get(i).getCreateDate());
                            msg.setReferenceId(String.valueOf(getAlarmResponse.getAlarmMessageList().get(i).getReferenceId()));
                            alartlist.add(msg);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    if (alartlist.size() == 0) {
                        ll_alertmsg.setVisibility(View.VISIBLE);
                        lv_alertmsg.setVisibility(View.GONE);

                        EventBusMsg msg = new EventBusMsg();
                        msg.setListSize(alartlist.size());
                        msg.setEmptyType(EventBusMsg.MsgEmptyType.MSG_ALART_LIST_SIZE_CHANGE);
                        EventBus.getDefault().post(msg);
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
//        alartlist.clear();
    }

    public void onEventMainThread(final EventBusMsgPush eventPackage) {
        if (isActivityTop()) {

            if (eventPackage.getMsgType() == 2) {
                alartlist.add(0,eventPackage);
                adapter.notifyDataSetChanged();
                if (alartlist.size() == 0) {
                    ll_alertmsg.setVisibility(View.VISIBLE);

                    lv_alertmsg.setVisibility(View.GONE);
                } else {
                    ll_alertmsg.setVisibility(View.GONE);
                    lv_alertmsg.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected boolean isActivityTop() {
        ActivityManager manager = (ActivityManager) this.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(this.getActivity().getClass().getName());
    }


    private void deleteAlarmMessage() {

        dlgWaiting.show();
        mDlgWaitingHandler.sendEmptyMessageDelayed(MSG_cannt_get_data, App.WAITTING_SECOND);
        final GetAlarmMessageRequest.GetAlarmMessageRequestMessage.Builder deletemessage = GetAlarmMessageRequest.GetAlarmMessageRequestMessage.newBuilder();

        //删除某条选中的数据
        if (deleteType == 0) {
//            Log.d("print", "deleteInsuranceMessage: stringBuilder.toString(): " + alartlist.get(mPosition).getMsgId());
//            long msgId = alartlist.get(mPosition).getMsgId();
            deletemessage.setIds(String.valueOf(alartlist.get(mPosition).getMsgId()));
        }

        //删除整个fragment中的listView列表数据
        else if (deleteType == EventBusMsgMessage.MSG_Message_Alarm_Delete) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < alartlist.size(); i++) {
                if (i != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(alartlist.get(i).getMsgId());

            }
            deletemessage.setIds(stringBuilder.toString());
            Log.d("print", "deleteInsuranceMessage: stringBuilder.toString(): " + stringBuilder.toString());
        }

        deletemessage.setSession(PreferenceData.loadSession(getContext()));

        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_POLICE_DELETE_ALART_MSG, deletemessage.build().toByteArray());
            }

        }.start();

    }
}
