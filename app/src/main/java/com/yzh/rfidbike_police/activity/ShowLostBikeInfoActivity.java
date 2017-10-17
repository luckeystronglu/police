package com.yzh.rfidbike_police.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.GetBikeByPlateNumberRequest;
import com.yzh.rfid.app.response.GetBikeAndBikeUserResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.CRC16;
import com.yzh.rfidbike_police.util.FormatTransfer;
import com.yzh.rfidbike_police.util.string.DateUtils;
import com.yzh.rfidbike_police.view.widgets.Header;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by appadmin on 2017/3/17.
 */

public class ShowLostBikeInfoActivity extends BaseActivity implements Header.headerListener {
    @BindView(R.id.showlost_bike_header)
    Header showlostinfo_header;
    @BindView(R.id.tv_lostbike_no)
    TextView tv_lostbike_no;
    @BindView(R.id.tv_lost_card_no)
    TextView tv_lost_card_no;
    @BindView(R.id.lost_distance_value)
    TextView tv_distance_value;
    @BindView(R.id.lostinfo_progressBar)
    ProgressBar lostinfo_progressBar;
    @BindView(R.id.bike_info_iv1)
    ImageView bike_info_iv1;
    @BindView(R.id.bike_info_iv2)
    ImageView bike_info_iv2;
    @BindView(R.id.bike_info_iv3)
    ImageView bike_info_iv3;
    @BindView(R.id.bike_info_iv4)
    ImageView bike_info_iv4;
    @BindView(R.id.bike_user_iv1)
    ImageView bike_user_iv1;
    @BindView(R.id.bike_user_iv2)
    ImageView bike_user_iv2;
    @BindView(R.id.btn_click_trace)
    Button btn_click_trace;

    @BindView(R.id.ll_trace_failed)
    LinearLayout ll_trace_failed;
    @BindView(R.id.ll_trace_progress)
    LinearLayout ll_trace_progress;

    private String lostPlateNum;
    private String intentCardNo;
    private int rssiNum;
    private int searchRssiNum;
    private ArrayList<String> picUrlList = new ArrayList<>();
    private Intent picsIntent;
    private static final int MSG_UPDATE_RSSI_VALUE = 7173;
    private static final int MSG_TRACE_FAILED = 7174;
    private long frequency = 0;
    private Timer timer, timer1;
    private long lastScanTime = 0;
    private int playResource = 0;

    private SoundPool soundPool;  //播放音效
    private int soundId;

    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_UPDATE_RSSI_VALUE:
                        ll_trace_failed.setVisibility(View.GONE);
                        ll_trace_progress.setVisibility(View.VISIBLE);
//                        tv_distance_value.setText(msg.arg1 + "");
                        int progressValue = msg.arg1;
                        if (progressValue <= 45) {
                            progressValue = 0;
                            frequency = 70;
                        } else if (progressValue >= 90) {
                            progressValue = 100;
                            frequency = 2070;
                        } else {
                            if (progressValue < 55){
                                progressValue = (progressValue - 45) * 100 / (90 - 45);
                                frequency = progressValue * 2 + 70;
                            }else if (progressValue < 65){
                                progressValue = (progressValue - 45) * 100 / (90 - 45);
                                frequency = progressValue * 5 + 70;
                            }else if (progressValue < 75){
                                progressValue = (progressValue - 45) * 100 / (90 - 45);
                                frequency = progressValue * 10 + 70;
                            }else {
                                progressValue = (progressValue - 45) * 100 / (90 - 45);
                                frequency = progressValue * 20 + 70;
                            }

//                            frequency = (progressValue - 45) * 66 + 70;
                        }

                        lostinfo_progressBar.setProgress(progressValue);
//                        frequency = progressValue * 10 + 70;


                        soundPool.stop(playResource);
                        if (timer1 != null) {
                            timer1.cancel();
                        }

                        timer1 = new Timer();
                        TimerTask task1 = new TimerTask() {
                            @Override
                            public void run() {
                                playResource = soundPool.play(soundId, (float) 0.3, (float) 0.3, 1, 0, 1);
                            }
                        };
                        timer1.schedule(task1, 0, frequency);
                        break;


                    case MSG_TRACE_FAILED:
                        ll_trace_failed.setVisibility(View.VISIBLE);
                        ll_trace_progress.setVisibility(View.GONE);

                        //若不在有效追踪范围内,则取消追踪声音
                        if (timer1 != null) {
                            timer1.cancel();
                        }
                        soundPool.stop(playResource);
//                        tv_distance_value.setText(msg.arg1 + "");
//                        lostinfo_progressBar.setProgress(msg.arg1);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Search bike
    SerialPort mSerialPort;
    ReadThread mReadThread;
    SendThread mSendThread;
    private boolean needRead = true;
    OutputStream mOutputStream;
    InputStream mInputStream;
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_lostbikeinfo_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        lostPlateNum = intent.getStringExtra("lostPlateNum");
        rssiNum = intent.getIntExtra("rssiNum", 80);
        intentCardNo = intent.getStringExtra("cardNo");
        tv_lostbike_no.setText(lostPlateNum);
        tv_lost_card_no.setText(intentCardNo + "");
//        tv_distance_value.setText(rssiNum + "");
        lostinfo_progressBar.setMax(100);
        lostinfo_progressBar.setProgress(rssiNum);

        soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);
        soundId = soundPool.load(this, R.raw.a9, 0);

        showlostinfo_header.setListener(this);
        //接口请求
        doSocket();
        initEvent();


    }


    private void initEvent() {
        picsIntent = new Intent(ShowLostBikeInfoActivity.this, LookPicsActivity.class);
        picsIntent.putStringArrayListExtra("urls", picUrlList);

        bike_info_iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picUrlList.size() > 0) {
                    picsIntent.putExtra("position", 0);
                    startActivity(picsIntent);
                }
            }
        });

        bike_info_iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picUrlList.size() > 0) {
                    picsIntent.putExtra("position", 1);
                    startActivity(picsIntent);
                }
            }
        });

        bike_info_iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picUrlList.size() > 0) {
                    picsIntent.putExtra("position", 2);
                    startActivity(picsIntent);
                }
            }
        });

        bike_info_iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picUrlList.size() > 0) {
                    picsIntent.putExtra("position", 3);
                    startActivity(picsIntent);
                }
            }
        });


        bike_user_iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picUrlList.size() > 4 && bike_user_iv1 != null) {
                    picsIntent.putExtra("position", 4);
                    startActivity(picsIntent);
                }
            }
        });

        bike_user_iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (picUrlList.size() == 5 && bike_user_iv2 != null) {
                    picsIntent.putExtra("position", 4);
                } else if (picUrlList.size() == 6 && bike_user_iv1 != null) {
                    picsIntent.putExtra("position", 5);
                }
                startActivity(picsIntent);
            }
        });
    }

    private void doTrace() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (DateUtils.getLongCurrentDateTime() - lastScanTime > 3000) {
                    Message message = Message.obtain();
                    message.what = MSG_TRACE_FAILED;
//                    message.arg1 = 120;
                    refreshHandler.sendMessage(message);

                }
            }
        }, 3000, 3000);
        if (mSerialPort != null) {
            if (mReadThread != null) {
                mReadThread.interrupt();
            }
            if (mSendThread != null) {
                mSendThread.interrupt();
            }
            closeSerialPort();
            // mSerialPort.close();
//                        mSerialPort = null;
        }
        try {

            mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            needRead = true;
            mSendThread = new SendThread();
            mSendThread.start();
            mReadThread = new ReadThread();
            mReadThread.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        final GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage.Builder showinfobuilder = GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage.newBuilder();
        showinfobuilder.setPlateNumber(lostPlateNum);
        showinfobuilder.setSession(PreferenceData.loadSession(this));
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_POLICE_GET_LOSTBIKEINFO_BY_PLATE_NUMBER, showinfobuilder.build().toByteArray());
            }
        }.start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            //丢失车辆列表
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_POLICE_GET_LOSTBIKEINFO_BY_PLATE_NUMBER) {
                final GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage getBikeAndBikeUserResponseMessage = GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.parseFrom(eventPackage.getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (getBikeAndBikeUserResponseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(ShowLostBikeInfoActivity.this, getBikeAndBikeUserResponseMessage.getErrorMsg().getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (getBikeAndBikeUserResponseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    if (picUrlList != null) {
                        picUrlList.clear();
                    }
                    boolean isFinish = false;

                    if (!isFinish) {
                        //显示车辆正面图片并添加进url图片list
                        if (getBikeAndBikeUserResponseMessage.getBike().getFrontPicPath() != null) {
                            showPictureByUrl(getBikeAndBikeUserResponseMessage.getBike().getFrontPicPath(), bike_info_iv1);
                            picUrlList.add(getBikeAndBikeUserResponseMessage.getBike().getFrontPicPath());
                            isFinish = true;
                        }
                    }

                    if (isFinish) {
                        //显示车辆左侧面图片并添加进url图片list
                        if (getBikeAndBikeUserResponseMessage.getBike().getBackPicPath() != null) {
                            isFinish = false;
                            showPictureByUrl(getBikeAndBikeUserResponseMessage.getBike().getBackPicPath(), bike_info_iv2);
                            picUrlList.add(getBikeAndBikeUserResponseMessage.getBike().getBackPicPath());
                            isFinish = true;
                        }
                    }


                    if (isFinish) {
                        //显示车辆左侧面图片并添加进url图片list
                        if (getBikeAndBikeUserResponseMessage.getBike().getSidePicPath() != null) {
                            isFinish = false;
                            showPictureByUrl(getBikeAndBikeUserResponseMessage.getBike().getSidePicPath(), bike_info_iv3);
                            picUrlList.add(getBikeAndBikeUserResponseMessage.getBike().getSidePicPath());
                            isFinish = true;
                        }
                    }

                    if (isFinish) {
                        //显示车辆车牌图片并添加进url图片list
                        if (getBikeAndBikeUserResponseMessage.getBike().getTagPicPath() != null) {
                            isFinish = false;
                            showPictureByUrl(getBikeAndBikeUserResponseMessage.getBike().getTagPicPath(), bike_info_iv4);
                            picUrlList.add(getBikeAndBikeUserResponseMessage.getBike().getTagPicPath());
                            isFinish = true;
                        }
                    }

                    if (isFinish) {
                        //显示正面身份证图片并添加进url图片list
                        if (getBikeAndBikeUserResponseMessage.getBikeUser().getIdFrontPicPath() != null) {
                            isFinish = false;
                            showPictureByUrl(getBikeAndBikeUserResponseMessage.getBikeUser().getIdFrontPicPath(), bike_user_iv1);
                            picUrlList.add(getBikeAndBikeUserResponseMessage.getBikeUser().getIdFrontPicPath());
                            isFinish = true;
                        }
                    }

                    if (isFinish) {
                        //显示反面身份证图片并添加进url图片list
                        if (getBikeAndBikeUserResponseMessage.getBikeUser().getIdBackPicPath() != null) {
                            showPictureByUrl(getBikeAndBikeUserResponseMessage.getBikeUser().getIdBackPicPath(), bike_user_iv2);
                            picUrlList.add(getBikeAndBikeUserResponseMessage.getBikeUser().getIdBackPicPath());
                        }
                    }


                }
            }


        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void showPictureByUrl(String url, ImageView iv) {
        Glide.with(context)
                .load(url)
                .crossFade(500)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.pic_load_err)
                .thumbnail(0.1f)
                .into(iv);
    }


    @Override
    public void onClickLeftIcon() {
        finish();
    }

    @Override
    public void onClickRightText() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (picUrlList != null) {
            picUrlList.clear();
        }

        if (mReadThread != null) {
            mReadThread.interrupt();
            closeSerialPort();
//            mSerialPort = null;
        }
        if (mSendThread != null) {
            mSendThread.interrupt();
        }

        if (soundPool != null) {
            soundPool.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doTrace();
    }

    @Override
    protected void onPause() {
        super.onPause();
        needRead = false;
        if (mReadThread != null) {
            mReadThread.interrupt();
            closeSerialPort();
        }
        if (mSendThread != null) {
            mSendThread.interrupt();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (needRead) {
                if (mInputStream == null)
                    return;
                try {
                    int count = 4;
                    byte[] buffer = new byte[count];//4位帧控制,第4位是帧长度，
//                    int readCount = -1; // 已经成功读取的字节的个数
                    if (mInputStream.available() >= 4) {
                        mInputStream.read(buffer, 0, 4);
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
                        if (mInputStream.available() >= nContnetCount) {
                            mInputStream.read(bufferContent, 0, nContnetCount);
//                            readCountContent += mInputStream.read(bufferContent, readCountContent, nContnetCount - readCountContent);
                        } else {
                            continue;
                        }
                        onDataReceived(bufferContent, nContnetCount);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    void onDataReceived(final byte[] buffer, final int size) {
        // StringBuffer out = new StringBuffer();
        runOnUiThread(new Runnable() {
            public void run() {
//                String str = FormatTransfer.BytesToString(buffer, size);
                byte[] byrssi = new byte[]{buffer[2]};
                searchRssiNum = Integer.parseInt(FormatTransfer.BytesToString(byrssi).trim(), 16);
                //   logMessage(i1 + "");

                byte[] bytes = new byte[]{buffer[4], buffer[5], buffer[6], buffer[7]};
                long cardNo = FormatTransfer.bytesToLong(bytes);
                if (intentCardNo.equals(String.valueOf(cardNo))) {
                    lastScanTime = DateUtils.getLongCurrentDateTime();
                    Message message = Message.obtain();
                    message.what = MSG_UPDATE_RSSI_VALUE;
                    message.arg1 = searchRssiNum;
                    refreshHandler.sendMessage(message);

//                    tv_distance_value.setText(searchRssiNum + "");
//                    lostinfo_progressBar.setProgress(searchRssiNum);
                }

            }
        });
    }

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


}
