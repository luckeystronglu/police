package com.yzh.rfidbike_police.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.GetBikeByCardNoRequest;
import com.yzh.rfid.app.response.Bike;
import com.yzh.rfid.app.response.GetSimpleBikesResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.activity.ShowLostBikeInfoActivity;
import com.yzh.rfidbike_police.adapter.AdapterGetLostBike;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseApplication;
import com.yzh.rfidbike_police.base.BaseFragment;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.model.MyBikeLostMessage;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.CRC16;
import com.yzh.rfidbike_police.util.FormatTransfer;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.util.string.DateUtils;
import com.yzh.rfidbike_police.view.widgets.Header;

import net.tsz.afinal.FinalDb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;
import butterknife.ButterKnife;


/**
 * Created by appadmin on 2017/1/10.
 * 附近车辆Fragment
 */

public class SearchNearbyBikeFragment extends BaseFragment implements Header.headerListener {
    private ListView search_bike_lv;
    private LinearLayout ll_search_bike;
    private Button btn_click_searching;

    private AdapterGetLostBike adapter;
    private List<MyBikeLostMessage> foundBikeList;
    private List<MyBikeLostMessage> displayList;

    SerialPort mSerialPort;
    ReadThread mReadThread;
    SendThread mSendThread;

    SendSettingThread mSendSettingThread;
    ReadSettingThread mReadSettingThread;

    OutputStream mOutputStream;
    InputStream mInputStream;
    int m_nRead = 0, m_nWrite = 0;
    FinalDb mDb;
    private boolean isSearching = false;
    private boolean isLastFinished = true;
    private View listHeadView;
    private Header header;
    private int rssiNum;
    private int regionNum=45;
    private LinearLayout llAbnormalChecked;
    private TextView tvAbnormalChecked;
    private boolean isChecked = false;
    private boolean needRead = false;
    private boolean needReadSetting = false;
    private static final int MSG_UPDATE_RSSI_VALUE = 7157;
    private static final int MSG_STOP_SEARCHING = 7158;
    private static final int MSG_SETTING_SUCCESS = 7159;
    private static final int MSG_SETTING_FAIL = 7160;
    private long lastShowTime = 0;
    private Timer timer;

    private SoundPool soundPool;  //播放音效
    private int hitOkSfx;
    private int playStream = 0;


    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    //更新显示列表
                    case MSG_UPDATE_RSSI_VALUE:
                        if (foundBikeList.size() > 0) {
                            displayList.clear();
                            for (int i = 0; i < foundBikeList.size(); i++) {
                                if (foundBikeList.get(i).getRequestStatus() != MyBikeLostMessage.RequestStatus.NoExistInDB)
                                    displayList.add(foundBikeList.get(i));
                            }
                            //显示列表数量大于等于2,则按顺序排列
                            if (displayList.size() > 1) {
                                Collections.sort(displayList, new Comparator<MyBikeLostMessage>() {
                                    @Override
                                    public int compare(MyBikeLostMessage o1, MyBikeLostMessage o2) {
                                        if (o1.getRequestStatus() == o2.getRequestStatus()) {
                                            int a = 120 - o1.getRssinum();
                                            int b = 120 - o2.getRssinum();
//                                        logMessage((b - a) + "");
                                            return b - a;
                                        } else {
                                            return o2.getRequestStatus().compareTo(o1.getRequestStatus());
                                        }
                                    }
                                });
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            displayList.clear();
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case MSG_STOP_SEARCHING:
                        if (foundBikeList.size() > 0) {

                            displayList.clear();
                            for (int i = 0; i < foundBikeList.size(); i++) {
                                if (foundBikeList.get(i).getRequestStatus() == MyBikeLostMessage.RequestStatus.ExistInDB)
                                    displayList.add(foundBikeList.get(i));
                            }
                            if (displayList.size() > 0) {
                                //显示列表数量大于等于2,则按顺序排列
                                if (displayList.size() > 1) {
                                    Collections.sort(displayList, new Comparator<MyBikeLostMessage>() {
                                        @Override
                                        public int compare(MyBikeLostMessage o1, MyBikeLostMessage o2) {
                                            if (o1.getRequestStatus() == o2.getRequestStatus()) {
                                                int a = 120 - o1.getRssinum();
                                                int b = 120 - o2.getRssinum();
                                                logMessage((b - a) + "");
                                                return b - a;
                                            } else {
                                                return o2.getRequestStatus().compareTo(o1.getRequestStatus());
                                            }

                                        }
                                    });
                                }

                                adapter.notifyDataSetChanged();
                                search_bike_lv.setVisibility(View.VISIBLE);
                                listHeadView.setVisibility(View.VISIBLE);
                                ll_search_bike.setVisibility(View.GONE);
                            } else {
                                search_bike_lv.setVisibility(View.GONE);
                                listHeadView.setVisibility(View.GONE);
                                ll_search_bike.setVisibility(View.VISIBLE);
                            }
                        } else {

                        }
                        break;
                    case MSG_SETTING_SUCCESS:
                        if (mReadSettingThread != null) {
                            closeSerialPort();
                        }
                        if (mSendSettingThread != null) {
                            mSendSettingThread.interrupt();
                        }
                        Toast.makeText(SearchNearbyBikeFragment.this.getActivity(), "过滤设置成功", Toast.LENGTH_SHORT).show();
                        PreferenceData.saveSignalStrength(SearchNearbyBikeFragment.this.getActivity(),regionNum);
                        break;
                    case MSG_SETTING_FAIL:
                        if (mReadSettingThread != null) {
                            closeSerialPort();
                        }
                        if (mSendSettingThread != null) {
                            mSendSettingThread.interrupt();
                        }
                        Toast.makeText(SearchNearbyBikeFragment.this.getActivity(), "过滤设置失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    protected int getContentView() {
        ButterKnife.bind(getActivity());
        return R.layout.activity_bike_searching;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        mDb = FinalDb.create(getActivity().getApplicationContext(), App.DB_NAME, true, App
                .DB_VERSION, BaseApplication.getInstance());
        initView();
        initEvent();

    }

    private void initView() {
        foundBikeList = new ArrayList<>();
        displayList = new ArrayList<>();
        header = findViewByIds(R.id.search_bike_header);
        header.setListener(this);
        search_bike_lv = findViewByIds(R.id.search_bike_lv);
        ll_search_bike = findViewByIds(R.id.ll_search_bike);
        btn_click_searching = findViewByIds(R.id.btn_click_searing);
        llAbnormalChecked = findViewByIds(R.id.ll_abnormal_check);
        tvAbnormalChecked = findViewByIds(R.id.tv_abnormal_check);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        listHeadView = layoutInflater.inflate(R.layout.item_lost_bike_headview, null);
        search_bike_lv.addHeaderView(listHeadView);
        listHeadView.setVisibility(View.GONE);

        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        hitOkSfx = soundPool.load(getContext(), R.raw.a9, 0);

        adapter = new AdapterGetLostBike(getContext());
        adapter.setDatas(displayList);
        search_bike_lv.setAdapter(adapter);
        llAbnormalChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearching) {
                    ToastUtils.showTextToast(SearchNearbyBikeFragment.this.getActivity(), "请先停止搜寻");
                    return;
                }
                if (isChecked) {
                    isChecked = false;
                    tvAbnormalChecked.setBackgroundResource(R.drawable.checked_no);
                } else {
                    isChecked = true;
                    tvAbnormalChecked.setBackgroundResource(R.drawable.checked);
                }

            }
        });
        search_bike_lv.setVisibility(View.VISIBLE);
        ll_search_bike.setVisibility(View.GONE);

        search_bike_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                if (displayList.get(position - 1).getRequestStatus() != MyBikeLostMessage.RequestStatus.ExistInDB) {
                    ToastUtils.showTextToast(SearchNearbyBikeFragment.this.getActivity(), "车辆状态查询中");
                    return;
                }
                Intent searchIntent = new Intent(getActivity(), ShowLostBikeInfoActivity.class);
                searchIntent.putExtra("lostPlateNum", displayList.get(position - 1).getPlateNumber());
                searchIntent.putExtra("rssiNum", displayList.get(position - 1).getRssinum());
                searchIntent.putExtra("cardNo", displayList.get(position - 1).getCardNo());
                if (isSearching) {
                    btn_click_searching.performClick(); //虚拟点击
                }
                startActivity(searchIntent);
            }
        });
    }

    private void initEvent() {
//        search_bike.setListener(this);
        btn_click_searching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearching) {
                    isSearching = false;
                    btn_click_searching.setText("开始搜寻");
                    if (mReadThread != null) {
                        needRead = false;
                        closeSerialPort();
                    }
                    if (mSendThread != null) {
                        mSendThread.interrupt();
                    }
                    updateHandler.sendEmptyMessage(MSG_STOP_SEARCHING);
                    timer.cancel();
                    timer = null;
                } else {
                    isSearching = true;
                    //当3秒内没有扫描卡，就从FoundBikeList删除相应的卡号
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (foundBikeList.size() > 0) {
                                for (int i = foundBikeList.size() - 1; i >= 0; i--) {
                                    if (foundBikeList.get(i).getScanTime() + 3000 < DateUtils.getLongCurrentDateTime()) {
                                        foundBikeList.remove(i);
                                    }
                                }
                                updateHandler.sendEmptyMessage(MSG_UPDATE_RSSI_VALUE);
                            }
                        }
                    }, 3000, 3000);
                    if (foundBikeList != null) {
                        foundBikeList.clear();
                    }
                    if (displayList != null) {
                        displayList.clear();
                    }

                    adapter.notifyDataSetChanged();
                    search_bike_lv.setVisibility(View.VISIBLE);
                    listHeadView.setVisibility(View.VISIBLE);
                    ll_search_bike.setVisibility(View.GONE);
                    btn_click_searching.setText("正在搜寻中...");

                    if (mSerialPort != null) {
                        if (mReadThread != null) {
                            mReadThread.interrupt();
                        }
                        if (mSendThread != null) {
                            mSendThread.interrupt();
                        }
                        closeSerialPort();
                    }
                    try {

                        mSerialPort = getSerialPort();
                        mOutputStream = mSerialPort.getOutputStream();
                        mInputStream = mSerialPort.getInputStream();
                        mSendThread = new SendThread();
                        mSendThread.start();
                        needRead = true;
                        mReadThread = new ReadThread();
                        mReadThread.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_GET_LOST_BIKELIST_BYCARDNO) {
                final GetSimpleBikesResponse.GetSimpleBikesResponseMessage getSimpleBikesResponseMessage = GetSimpleBikesResponse.GetSimpleBikesResponseMessage.parseFrom(eventPackage.getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (getSimpleBikesResponseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(getActivity(), getSimpleBikesResponseMessage.getErrorMsg().getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (getSimpleBikesResponseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    //按卡号查询车辆列表  请求成功后执行

                    List<Bike.SimpleBikeMessage> bikesList = getSimpleBikesResponseMessage.getBikesList();
                    if (bikesList.size() > 0) {
                        for (int i = 0; i < bikesList.size(); i++) {
                            if (isChecked) {
                                if (!(bikesList.get(i).getStatus().equals("0"))) {
                                    for (int i1 = 0; i1 < foundBikeList.size(); i1++) {
                                        if (foundBikeList.get(i1).getCardNo().equals(String.valueOf(bikesList.get(i).getCardNo()))) {

                                            if(foundBikeList.get(i1).getRequestStatus()==MyBikeLostMessage.RequestStatus.WaitingForRequest)
                                            {
                                                //找到新的异常车辆，报警
                                                soundPool.stop(playStream);
                                                playStream = soundPool.play(hitOkSfx, (float) 0.3, (float) 0.3, 1, 0, 1);

                                            }
                                            foundBikeList.get(i1).setStatus(bikesList.get(i).getStatus());
                                            foundBikeList.get(i1).setRequestStatus(MyBikeLostMessage.RequestStatus.ExistInDB);
                                            foundBikeList.get(i1).setPlateNumber(bikesList.get(i).getPlateNumber());
                                        }
                                    }
                                    updateHandler.sendEmptyMessage(MSG_UPDATE_RSSI_VALUE);
                                }
                            } else {
                                for (int i1 = 0; i1 < foundBikeList.size(); i1++) {
                                    if (foundBikeList.get(i1).getCardNo().equals(String.valueOf(bikesList.get(i).getCardNo()))) {
//                                        if(foundBikeList.get(i1).getRequestStatus()==MyBikeLostMessage.RequestStatus.WaitingForRequest&& (!(bikesList.get(i).getStatus().equals("0"))))
                                        if(foundBikeList.get(i1).getRequestStatus()==MyBikeLostMessage.RequestStatus.WaitingForRequest)
                                        {
                                            //找到新的异常车辆，报警
                                            soundPool.stop(playStream);
                                            playStream = soundPool.play(hitOkSfx, (float) 0.3, (float) 0.3, 1, 0, 1);

                                        }

                                        foundBikeList.get(i1).setStatus(bikesList.get(i).getStatus());
                                        foundBikeList.get(i1).setRequestStatus(MyBikeLostMessage.RequestStatus.ExistInDB);
                                        foundBikeList.get(i1).setPlateNumber(bikesList.get(i).getPlateNumber());
                                    }
                                }
                                updateHandler.sendEmptyMessage(MSG_UPDATE_RSSI_VALUE);
                            }
                        }
                    }

                    if (foundBikeList.size() > 0) {
                        search_bike_lv.setVisibility(View.VISIBLE);
                        listHeadView.setVisibility(View.VISIBLE);
                        ll_search_bike.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < foundBikeList.size(); i++) {
                        if (foundBikeList.get(i).getRequestStatus() == MyBikeLostMessage.RequestStatus.WaitingForRequest) {
                            foundBikeList.get(i).setRequestStatus(MyBikeLostMessage.RequestStatus.NoExistInDB);
                        }
                    }
                    isLastFinished = true;
                }
            }


            //丢失车辆列表


        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClickLeftIcon() {

    }

    @Override
    public void onClickRightText() {
        if (needRead) {
            ToastUtils.showTextToast(SearchNearbyBikeFragment.this.getActivity(), "车辆搜寻已经开始");
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(SearchNearbyBikeFragment.this.getActivity());
        View layout = inflater.inflate(R.layout.dlg_filter_setting, null);
        final Dialog dlgSetting = new AlertDialog.Builder(SearchNearbyBikeFragment.this.getActivity()).setView
                (layout).create();
        dlgSetting.show();


//        final InputMethodManager imm = (InputMethodManager) SearchNearbyBikeFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);


        TextView tv_cancel = (TextView) dlgSetting.findViewById(R.id
                .tv_cancel);
        TextView tv_sure = (TextView) dlgSetting.findViewById(R.id.tv_sure);
        final EditText et_content = (EditText) dlgSetting.findViewById(R.id
                .et_content);

//        et_content.setText(String.valueOf(regionNum));
        SeekBar seekBar= (SeekBar) layout.findViewById(R.id.seekbar01);
        seekBar.setMax(90-45);
        regionNum=PreferenceData.loadSignalStrength(SearchNearbyBikeFragment.this.getActivity());
        et_content.setText("当前信号强度:"+regionNum);
        seekBar.setProgress(PreferenceData.loadSignalStrength(SearchNearbyBikeFragment.this.getActivity())-45);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                regionNum=progress+45;
                et_content.setText("当前信号强度:"+regionNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        final int numSmall = 45;
//        final int numBig = 90;
//        et_content.addTextChangedListener(new TextWatcher() {
//            String nums = null;
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                nums = s.toString();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (start > 1) {
//                    if (numSmall != -1 && numBig != -1) {
//                        int num = Integer.parseInt(s.toString());
//                        if (num > 90) {
//                            s = "90";
//                        }
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s != null && !s.equals("")) {
//                    if (numSmall != -1 && numBig != -1) {//最大值和最小值自设
//                        int a = 0;
//                        try {
//                            a = Integer.parseInt(s.toString());
//                        } catch (NumberFormatException e) {
//                            // TODO Auto-generated catch block
//                            a = 0;
//                        }
//                        if (a > 90) {
//                            et_content.setText("90");
//                            return;
//                        }
//
//                    }
//                }
//            }
//        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                dlgSetting.dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regionNum < 45) {
                    ToastUtils.showTextToast(SearchNearbyBikeFragment.this.getActivity(), "信号强度不能少于45");
                    return;
                }
                if (needRead) {
                    ToastUtils.showTextToast(SearchNearbyBikeFragment.this.getActivity(), "请先停止搜寻");
                    return;
                } else {
                    if (mSerialPort != null) {
                        if (mReadSettingThread != null) {
                            mReadSettingThread.interrupt();
                        }
                        if (mSendSettingThread != null) {
                            mSendSettingThread.interrupt();
                        }
                        closeSerialPort();
                    }
                    try {
                        mSerialPort = getSerialPort();
                        mOutputStream = mSerialPort.getOutputStream();
                        mInputStream = mSerialPort.getInputStream();

                        needReadSetting = true;
                        mSendSettingThread = new SendSettingThread();
                        mSendSettingThread.start();

                        mReadSettingThread = new ReadSettingThread();
                        mReadSettingThread.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                dlgSetting.dismiss();

            }
        });
    }

    private class ReadSettingThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                int recycleCount = 0;
                while (needReadSetting) {
                    recycleCount++;
                    if (recycleCount > 30) {
                        needReadSetting = false;
                        updateHandler.sendEmptyMessage(MSG_SETTING_SUCCESS);
                        return;
                    }
                    if (mInputStream == null)
                        return;
                    int count = 4;
                    byte[] buffer = new byte[count];//4位帧控制,第4位是帧长度，
                    int availableCount = 0;
                    int offset = 0;
                    if (mInputStream.available() >= 4) {
                        int nContnetCount = 0;
                        mInputStream.read(buffer, 0, 1);
                        if (buffer[0] != (byte) 0xAA) // header is not correct, abandoned
                        {
                            continue;
                        } else {
                            mInputStream.read(buffer, 0, 2);
                            if (buffer[0] != (byte) 0x29) {
                                if (buffer[0] == (byte) 0xAA && buffer[1] == (byte) 0x29) {
                                    mInputStream.read(buffer, 0, 2);
                                    nContnetCount = (int) (buffer[1]) + 3;
                                    offset = 5;
                                } else {
                                    continue;
                                }
                            } else {
                                mInputStream.read(buffer, 0, 1);
                                nContnetCount = (int) (buffer[0]) + 3;
                                offset = 4;
                            }

                        }
                        if (nContnetCount <= 0) {
                            continue;
                        }

                        byte[] bufferContent = new byte[nContnetCount];//4位帧控制,第4位是帧长度，3位帧校验，其他为帧数据长度
                        while (mInputStream.available() < nContnetCount) {
                           Thread.sleep(100);
                        }
                        mInputStream.read(bufferContent, 0, nContnetCount);
                        if (bufferContent[0] == 0) {
                            needReadSetting = false;
                            updateHandler.sendEmptyMessage(MSG_SETTING_SUCCESS);
                            break;
                        }else
                        {
                            needReadSetting = false;
                            updateHandler.sendEmptyMessage(MSG_SETTING_FAIL);
                            break;
                        }

                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (needRead) {
                    if (mInputStream == null)
                        return;
                    int count = 4;
                    byte[] buffer = new byte[count];//4位帧控制,第4位是帧长度，
                    int availableCount = 0;
                    int offset = 0;
                    if (mInputStream.available() >= 4) {
                        int nContnetCount = 0;
                        mInputStream.read(buffer, 0, 1);
                        if (buffer[0] != (byte) 0xAA) // header is not correct, abandoned
                        {
                            continue;
                        } else {
                            mInputStream.read(buffer, 0, 2);
                            if (buffer[0] != (byte) 0x0A) {
                                if (buffer[0] == (byte) 0xAA && buffer[1] == (byte) 0x0A) {
                                    mInputStream.read(buffer, 0, 2);
                                    nContnetCount = (int) (buffer[1]) + 3;
                                    offset = 5;
                                } else {
                                    continue;
                                }
                            } else {
                                mInputStream.read(buffer, 0, 1);
                                nContnetCount = (int) (buffer[0]) + 3;
                                offset = 4;
                            }

                        }
                        if (nContnetCount <= 0) {
                            continue;
                        }

                        byte[] bufferContent = new byte[nContnetCount];//4位帧控制,第4位是帧长度，3位帧校验，其他为帧数据长度
                        while (availableCount < nContnetCount + offset) {
                            availableCount = mInputStream.available();
                        }
                        mInputStream.read(bufferContent, 0, nContnetCount);

                        onDataReceived(bufferContent, nContnetCount);
                    }
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * �������ݣ�
     *
     * @param buffer
     * @param size
     */
    void onDataReceived(final byte[] buffer, final int size) {

        String str = FormatTransfer.BytesToString(buffer, size);
        byte[] byrssi = new byte[]{buffer[2]};
        rssiNum = Integer.parseInt(FormatTransfer.BytesToString(byrssi).trim(), 16);

        byte[] bytes = new byte[]{buffer[4], buffer[5], buffer[6], buffer[7]};
        final long cardNo = FormatTransfer.bytesToLong(bytes);

        boolean isDisPlay = false;
        boolean isInFoundList = false;
        //判断cardNo和rssiNum在显示的集合中是否存在  并相应处理
        //对找到的卡号进行处理，如果已经存在于找到卡的集合，只更新RSSI，否则加入到找到卡的集合
        if (foundBikeList.size() > 0) {
            for (int i = 0; i < foundBikeList.size(); i++) {
                if (foundBikeList.get(i).getCardNo().equals(String.valueOf(cardNo))) {
                    isDisPlay = true;
                    foundBikeList.get(i).setScanTime(DateUtils.getLongCurrentDateTime());
                    if (foundBikeList.get(i).getRequestStatus() == MyBikeLostMessage.RequestStatus.NoExistInDB) {
                        break;
                    } else {
                        foundBikeList.get(i).setRssinum(rssiNum);
                        break;
                    }
                }
            }
            if (!isDisPlay) {
                MyBikeLostMessage message = new MyBikeLostMessage();
                message.setCardNo(String.valueOf(cardNo));
                message.setRssinum(rssiNum);
                message.setStatus("10");
                message.setScanTime(DateUtils.getLongCurrentDateTime());
                message.setRequestStatus(MyBikeLostMessage.RequestStatus.Found);
                foundBikeList.add(message);
            }

        } else {
            MyBikeLostMessage message = new MyBikeLostMessage();
            message.setCardNo(String.valueOf(cardNo));
            message.setRssinum(rssiNum);
            message.setStatus("10");//10表示一种车辆的初始状态值
            message.setScanTime(DateUtils.getLongCurrentDateTime());
            message.setRequestStatus(MyBikeLostMessage.RequestStatus.Found);
            foundBikeList.add(message);
        }


//        按卡号搜寻车辆列表


        //隔1s刷新列表
        if (DateUtils.getLongCurrentDateTime() - lastShowTime > 1000) {
            lastShowTime = DateUtils.getLongCurrentDateTime();
            updateHandler.sendEmptyMessage(MSG_UPDATE_RSSI_VALUE);
            if (isLastFinished) {
                doGetBikeByCardSocket();
            }
        }
    }
//将找到的卡去数据库查询
    private void doGetBikeByCardSocket() {
        final GetBikeByCardNoRequest.GetBikeByCardNoRequestMessage.Builder getBikeByCardbuilder = GetBikeByCardNoRequest.GetBikeByCardNoRequestMessage.newBuilder();
        getBikeByCardbuilder.setSession(PreferenceData.loadSession(getContext()));  //Session
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < foundBikeList.size(); i++) {
            if (foundBikeList.get(i).getRequestStatus() == MyBikeLostMessage.RequestStatus.Found) {
                stringBuffer.append(",");
                stringBuffer.append(foundBikeList.get(i).getCardNo());
                foundBikeList.get(i).setRequestStatus(MyBikeLostMessage.RequestStatus.WaitingForRequest);
            }
        }
        getBikeByCardbuilder.setCardNoes(stringBuffer.toString());  //CardNo
        if (stringBuffer.indexOf(",") == 0) {
            stringBuffer.deleteCharAt(0); //删除第1个字符","
        }
        getBikeByCardbuilder.setCardNoes(stringBuffer.toString());  //CardNo

        if (stringBuffer.length() != 0) {
            new Thread() {
                public void run() {
                    SocketClient client;
                    client = SocketClient.getInstance();
                    client.send(SocketAppPacket.COMMAND_ID_GET_LOST_BIKELIST_BYCARDNO, getBikeByCardbuilder.build().toByteArray());
                }
            }.start();
            //设置isLastFinished标识,让每次请求依次进行
            isLastFinished = false;
        }

    }

    public String m_strBaud = "";
    public String m_strPort = "";

    public void closeSerialPort() {
        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
    }

    public SerialPort getSerialPort() {
        if (this.mSerialPort == null) {

            String str = "/dev/ttyMT1";
//            int i = 115200;
            int i = 57600;
            this.m_strPort = str;
            this.m_strBaud = i + "";
            if ((str.length() == 0) || (i == -1))
                throw new InvalidParameterException();
            try {
                this.mSerialPort = new SerialPort(new File(str), i, 0);
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return this.mSerialPort;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        needRead = false;
        if (mReadThread != null) {
            mReadThread.interrupt();
            closeSerialPort();
//            mSerialPort = null;
        }
        if (mSendThread != null) {
            mSendThread.interrupt();
        }
        if (displayList != null) {
            displayList.clear();
        }

        if (foundBikeList != null) {
            foundBikeList.clear();
        }

        if (soundPool != null) {
            soundPool.release();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        needRead = false;
        isSearching = false;
        btn_click_searching.setText("开始搜寻");
        if (mReadThread != null) {
            needRead = false;
            closeSerialPort();
        }
        if (mSendThread != null) {
            mSendThread.interrupt();
        }
        updateHandler.sendEmptyMessage(MSG_STOP_SEARCHING);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }



    }

    //----------------------------------------------------
    private class SendThread extends Thread {
        @Override
        public void run() {
            super.run();
            byte[] bCommand = new byte[]{0x25, 0x01, 0x01, 0x00};
            sendFilterData(bCommand);
        }
    }

    private class SendSettingThread extends Thread {
        @Override
        public void run() {
            super.run();
            byte b = FormatTransfer.toLH(regionNum)[0];
            byte[] bCommand = new byte[]{0x29, 0x00, 0x01, b};
            sendFilterData(bCommand);
        }
    }

    public void sendFilterData(byte[] bCommand) {
        try {

            byte[] bOutArray = encodeBytes(bCommand);
            mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * add by TOM for new chip
     * 编码要发送的字节数组
     *
     * @param bytes
     * @return
     */
    public static byte[] encodeBytes(byte[] bytes) {
        //计算校验码
        byte[] resCRC = CRC16.calcCRC(bytes);
        ArrayList<Byte> convertedBytes = new ArrayList<Byte>();
        //添加数据头
        convertedBytes.add((byte) 0xFF);
        convertedBytes.add((byte) 0xFF);
        convertedBytes.add((byte) 0x00);
        convertedBytes.add((byte) 0x00);
        convertedBytes.add((byte) 0xAA);
        //转义
        for (int index = 0; index < bytes.length; index++) {
            byte b = bytes[index];
            if (b == 0xAA) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x9A);
            } else if (b == 0xA9) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x99);
            } else {
                convertedBytes.add(bytes[index]);
            }
        }

        for (int index = resCRC.length - 1; index >= 0; index--) {
            byte b = resCRC[index];
            if (b == 0xAA) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x9A);
            } else if (b == 0xA9) {
                convertedBytes.add((byte) 0xA9);
                convertedBytes.add((byte) 0x99);
            } else {
                convertedBytes.add(resCRC[index]);
            }
        }

        convertedBytes.add((byte) 0xAA);
        byte[] packetBytes = new byte[convertedBytes.size()];
        for (int i = 0; i < convertedBytes.size(); i++) {
            packetBytes[i] = convertedBytes.get(i);
        }
        return packetBytes;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


}
