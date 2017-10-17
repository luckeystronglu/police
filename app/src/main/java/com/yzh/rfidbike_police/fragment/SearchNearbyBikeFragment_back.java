package com.yzh.rfidbike_police.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.yzh.rfidbike_police.model.SearchBikeEntity;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.CRC16;
import com.yzh.rfidbike_police.util.FormatTransfer;
import com.yzh.rfidbike_police.util.logort.ToastUtils;

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

import android_serialport_api.SerialPort;
import butterknife.ButterKnife;


/**
 * Created by appadmin on 2017/1/10.
 * 附近车辆Fragment
 */

public class SearchNearbyBikeFragment_back extends BaseFragment {
    private ListView search_bike_lv;
    private LinearLayout ll_search_bike;
    private Button btn_click_searching;

    private AdapterGetLostBike adapter;
    private List<MyBikeLostMessage> disPlayLostList;
    private List<SearchBikeEntity> findBikeList;

    SerialPort mSerialPort;
    ReadThread mReadThread;
    SendThread mSendThread;

    OutputStream mOutputStream;
    InputStream mInputStream;
    int m_nRead = 0, m_nWrite = 0;
    FinalDb mDb;
    private boolean isSearching = false;
    private boolean isLastFinished = true;
    private View headView;
    private int rssiNum;
    private LinearLayout llAbnormalChecked;
    private TextView tvAbnormalChecked;
    private boolean isChecked=false;

    private int findListSize = 0;
    private static final int MSG_UPDATE_RSSI_VALUE = 7157;

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_UPDATE_RSSI_VALUE:
                        if (disPlayLostList.size() > 1) {
                            Collections.sort(disPlayLostList, new Comparator<MyBikeLostMessage>() {
                                @Override
                                public int compare(MyBikeLostMessage o1, MyBikeLostMessage o2) {
                                    int a = 255 - o1.getRssinum();
                                    int b = 255 - o2.getRssinum();
                                    logMessage((a - b) + "");
                                    return a - b;
                                }
                            });
                        }
                        adapter.notifyDataSetChanged();
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
        disPlayLostList = new ArrayList<>();
        findBikeList = new ArrayList<>();
        search_bike_lv = findViewByIds(R.id.search_bike_lv);
        ll_search_bike = findViewByIds(R.id.ll_search_bike);
        btn_click_searching = findViewByIds(R.id.btn_click_searing);
        llAbnormalChecked= findViewByIds(R.id.ll_abnormal_check);
        tvAbnormalChecked=findViewByIds(R.id.tv_abnormal_check);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        headView = layoutInflater.inflate(R.layout.item_lost_bike_headview, null);
        search_bike_lv.addHeaderView(headView);
        headView.setVisibility(View.GONE);
        adapter = new AdapterGetLostBike(getContext());
        adapter.setDatas(disPlayLostList);
        search_bike_lv.setAdapter(adapter);
        llAbnormalChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSearching)
                {
                    ToastUtils.showTextToast(SearchNearbyBikeFragment_back.this.getActivity(),"请先停止搜寻");
                    return ;
                }
                if(isChecked)
                {
                    isChecked=false;
                    tvAbnormalChecked.setBackgroundResource(R.drawable.checked_no);
                }else
                {
                    isChecked=true;
                    tvAbnormalChecked.setBackgroundResource(R.drawable.checked);
                }

            }
        });
        search_bike_lv.setVisibility(View.VISIBLE);
        ll_search_bike.setVisibility(View.GONE);

        search_bike_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent searchIntent = new Intent(getActivity(), ShowLostBikeInfoActivity.class);
                searchIntent.putExtra("lostPlateNum", disPlayLostList.get(position - 1).getPlateNumber());
                searchIntent.putExtra("rssiNum", disPlayLostList.get(position - 1).getRssinum());
                searchIntent.putExtra("cardNo", disPlayLostList.get(position - 1).getCardNo());
                if (isSearching) {
                    btn_click_searching.performClick(); //虚拟点击
                }

                startActivity(searchIntent);
            }
        });
//        }
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
                        mReadThread.interrupt();
                        closeSerialPort();
//                        mSerialPort = null;
                    }
                    if (mSendThread != null) {
                        mSendThread.interrupt();
                    }

                    if (disPlayLostList.size() > 0) {
                        search_bike_lv.setVisibility(View.VISIBLE);
                        headView.setVisibility(View.VISIBLE);
                        ll_search_bike.setVisibility(View.GONE);
                    } else {
                        search_bike_lv.setVisibility(View.GONE);
                        headView.setVisibility(View.GONE);
                        ll_search_bike.setVisibility(View.VISIBLE);
                    }
                } else {
                    isSearching = true;

                    if (disPlayLostList != null) {
                        disPlayLostList.clear();
                    }
                    if (findBikeList != null) {
                        findBikeList.clear();
                    }

                    adapter.notifyDataSetChanged();
                    search_bike_lv.setVisibility(View.VISIBLE);
                    headView.setVisibility(View.INVISIBLE);
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
                                if(!(bikesList.get(i).getStatus().equals("0")))
                                 {
                                    MyBikeLostMessage myBikeLost = new MyBikeLostMessage();
                                    myBikeLost.setStatus(bikesList.get(i).getStatus());
                                    myBikeLost.setPlateNumber(bikesList.get(i).getPlateNumber());
                                    myBikeLost.setCardNo(String.valueOf(bikesList.get(i).getCardNo()));
                                    for (int i1 = 0; i1 < findListSize; i1++) {
                                        if (findBikeList.get(i1).getFindCardNo().equals(String.valueOf(bikesList.get(i).getCardNo()))) {
                                            myBikeLost.setRssinum(findBikeList.get(i1).getFindRssinum());

                                        }
                                    }
                                    disPlayLostList.add(myBikeLost);
                                    if (disPlayLostList.size() > 1) {
                                        Collections.sort(disPlayLostList, new Comparator<MyBikeLostMessage>() {
                                            @Override
                                            public int compare(MyBikeLostMessage o1, MyBikeLostMessage o2) {
                                                int a = 255 - o1.getRssinum();
                                                int b = 255 - o2.getRssinum();
                                                return a - b;
                                            }
                                        });
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }else
                            {
                                MyBikeLostMessage myBikeLost = new MyBikeLostMessage();
                                myBikeLost.setStatus(bikesList.get(i).getStatus());
                                myBikeLost.setPlateNumber(bikesList.get(i).getPlateNumber());
                                myBikeLost.setCardNo(String.valueOf(bikesList.get(i).getCardNo()));

                                for (int i1 = 0; i1 < findListSize; i1++) {
                                    if (findBikeList.get(i1).getFindCardNo().equals(String.valueOf(bikesList.get(i).getCardNo()))) {
                                        myBikeLost.setRssinum(findBikeList.get(i1).getFindRssinum());
                                    }
                                }
                                disPlayLostList.add(myBikeLost);
                                if (disPlayLostList.size() > 1) {
                                    Collections.sort(disPlayLostList, new Comparator<MyBikeLostMessage>() {
                                        @Override
                                        public int compare(MyBikeLostMessage o1, MyBikeLostMessage o2) {
                                            int a = 255 - o1.getRssinum();
                                            int b = 255 - o2.getRssinum();
                                            return a - b;
                                        }
                                    });
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    if (disPlayLostList.size() > 0) {
                        search_bike_lv.setVisibility(View.VISIBLE);
                        headView.setVisibility(View.VISIBLE);
                        ll_search_bike.setVisibility(View.GONE);
                    }
//                    else {
//                        search_bike_lv.setVisibility(View.GONE);
//                        headView.setVisibility(View.GONE);
//                        ll_search_bike.setVisibility(View.VISIBLE);
//                    }

//                    List<String> strings = findBikeList.subList(0, findListSize);
                    for (int i = 0; i < findListSize; i++) {
                        findBikeList.get(i).setRequestStatus("A");

                    }
                    isLastFinished = true;
                }
            }


            //丢失车辆列表


        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }


    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (mInputStream == null)
                    return;
                try {
                    int count = 4;
                    byte[] buffer = new byte[count];//4位帧控制,第4位是帧长度，
//                    int readCount = -1; // 已经成功读取的字节的个数
                    if (mInputStream.available()>=4) {
                        mInputStream.read(buffer,0,4);
                        if (buffer[0] != (byte) 0xAA || buffer[1] != 10) // header is not correct, abandoned
                        {
                            mInputStream.read(new byte[mInputStream.available()]);
                            continue;
                        }

                        int nContnetCount = (int) (buffer[3]) + 3;
                        if (nContnetCount < 0) {
                            Log.i("test by tom-----------", nContnetCount + "");
                            mInputStream.read(new byte[mInputStream.available()]);//读取完所有的字节流，退出
                            continue;
                        }

                        byte[] bufferContent = new byte[nContnetCount];//4位帧控制,第4位是帧长度，3位帧校验，其他为帧数据长度
//                        int readCountContent = 0; // 已经成功读取的字节的个数
                        if (mInputStream.available()>= nContnetCount) {
                            mInputStream.read(bufferContent,0,nContnetCount);
//                            readCountContent += mInputStream.read(bufferContent, readCountContent, nContnetCount - readCountContent);
                        }else
                        {
                            continue;
                        }
                        onDataReceived(bufferContent, nContnetCount);
                    }
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        boolean isDisPlay=false;
        boolean isInFoundList=false;
        //判断cardNo和rssiNum在显示的集合中是否存在  并相应处理
        if (disPlayLostList.size() > 0) {
            for (int i = 0; i < disPlayLostList.size(); i++) {
                if (disPlayLostList.get(i).getCardNo().equals(String.valueOf(cardNo))) {
                    isDisPlay = true;
                    if (disPlayLostList.get(i).getRssinum() == rssiNum) {
                        break;
                    } else {
                        disPlayLostList.get(i).setRssinum(rssiNum);
                        updateHandler.sendEmptyMessage(MSG_UPDATE_RSSI_VALUE);
                        break;
                    }
                }
            }
            if (!isDisPlay) {
                if (findBikeList.size() > 0) {
                    for (int i = 0; i < findBikeList.size(); i++) {
                        if (findBikeList.get(i).getFindCardNo().equals(String.valueOf(cardNo))) {
                            isInFoundList = true;
                            if (findBikeList.get(i).getFindRssinum() == rssiNum) {
                                break;
                            }else {
                                findBikeList.get(i).setFindRssinum(rssiNum);
                                break;
                            }
                        }
                    }

                    if (!isInFoundList) {
                        SearchBikeEntity searchEntity = new SearchBikeEntity();
                        searchEntity.setFindCardNo(String.valueOf(cardNo));
                        searchEntity.setFindRssinum(rssiNum);
                        searchEntity.setRequestStatus("B");
                        findBikeList.add(searchEntity);
                    }
                }else {
                    SearchBikeEntity searchEntity = new SearchBikeEntity();
                    searchEntity.setFindCardNo(String.valueOf(cardNo));
                    searchEntity.setFindRssinum(rssiNum);
                    searchEntity.setRequestStatus("B");
                    findBikeList.add(searchEntity);
                }

            }

        }else {
            if (findBikeList.size() > 0) {
                for (int i = 0; i < findBikeList.size(); i++) {
                    if (findBikeList.get(i).getFindCardNo().equals(String.valueOf(cardNo))) {
                        isInFoundList = true;
                        if (findBikeList.get(i).getFindRssinum() == rssiNum) {
                            break;
                        }else {
                            findBikeList.get(i).setFindRssinum(rssiNum);
                            break;
                        }
                    }
                }

                if (!isInFoundList) {
                    SearchBikeEntity searchEntity = new SearchBikeEntity();
                    searchEntity.setFindCardNo(String.valueOf(cardNo));
                    searchEntity.setFindRssinum(rssiNum);
                    searchEntity.setRequestStatus("B");
                    findBikeList.add(searchEntity);
                }
            }else {
                SearchBikeEntity searchEntity = new SearchBikeEntity();
                searchEntity.setFindCardNo(String.valueOf(cardNo));
                searchEntity.setFindRssinum(rssiNum);
                searchEntity.setRequestStatus("B");
                findBikeList.add(searchEntity);
            }
        }


//        按卡号搜寻车辆列表

        if (isLastFinished) {
            findListSize = findBikeList.size(); //需查找集合的长度
            if (findListSize > 0) {
                doGitBikeByCardSocket();
            }
        }

    }

    private void doGitBikeByCardSocket() {
//        super.doSocket();

        final GetBikeByCardNoRequest.GetBikeByCardNoRequestMessage.Builder getBikeByCardbuilder = GetBikeByCardNoRequest.GetBikeByCardNoRequestMessage.newBuilder();
        getBikeByCardbuilder.setSession(PreferenceData.loadSession(getContext()));  //Session
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < findListSize; i++) {
//            if (i != 0) {
//                if (!findBikeList.get(i).getRequestStatus().equals("A")) {
//                    stringBuffer.append(",");
//                }
//
//            }
//            if (!findBikeList.get(i).getRequestStatus().equals("A")) {
//                stringBuffer.append(findBikeList.get(i).getFindCardNo());
//            }
            if (!findBikeList.get(i).getRequestStatus().equals("A")) {
                stringBuffer.append(",");
                 stringBuffer.append(findBikeList.get(i).getFindCardNo());
                }



        }
        Log.e("test",stringBuffer.toString());
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
        if (mReadThread != null) {
            mReadThread.interrupt();
            closeSerialPort();
//            mSerialPort = null;
        }
        if (mSendThread != null) {
            mSendThread.interrupt();
        }
        if (findBikeList != null) {
            findBikeList.clear();
        }

        if (disPlayLostList != null) {
            disPlayLostList.clear();
        }

    }

    //----------------------------------------------------
    private class SendThread extends Thread {
        public boolean suspendFlag = true;

        @Override
        public void run() {
            super.run();
            sendFilterData();
        }
    }

    public void sendFilterData() {
        try {
            byte[] bCommand = new byte[]{0x25, 0x01, 0x01, 0x00};
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


}
