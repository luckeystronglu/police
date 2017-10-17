package com.yzh.rfidbike_police.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.yzh.rfid.app.request.CheckUpdateRequest;
import com.yzh.rfid.app.request.LogoutRequest;
import com.yzh.rfid.app.request.PlatformTypes;
import com.yzh.rfid.app.response.CheckUpdateResponse;
import com.yzh.rfid.app.response.CommonResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.activity.MessageCenterPoliceActivity;
import com.yzh.rfidbike_police.activity.setting.UpdatePasswordActivity;
import com.yzh.rfidbike_police.adapter.SettingAdapter;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseApplication;
import com.yzh.rfidbike_police.base.BaseFragment;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.ToolsUtils;
import com.yzh.rfidbike_police.util.file.FileUtils;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.view.widgets.Header;

import java.io.File;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Created by Administrator on 2017/3/17.
 */

public class SettingFragment extends BaseFragment {
    @BindView(R.id.lv_setting)
    ListView mLvSetting;
    @BindView(R.id.header)
    Header mHeader;
    private Context mContext;
    private SoundPool soundPool;
    private int hitOkSfx;
    private int playResource=0;
    private static final int MSG_cannt_get_data = 1000;

    private Timer timer1,timer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_cannt_get_data:
                        if (dlgWaiting.isShowing()) {
                            dlgWaiting.dismiss();
                            Toast.makeText(getActivity(), R.string.network_error, Toast
                                    .LENGTH_SHORT).show();
                        }
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
        return R.layout.fragment_settings;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        initView();
    }



    protected void initView() {
        soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM,5);
        hitOkSfx = soundPool.load(mContext, R.raw.a9, 0);

        mLvSetting.setAdapter(new SettingAdapter(mContext));
        mLvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {

                    case 0:
                        startActivity(new Intent(mContext, UpdatePasswordActivity.class));
                        break;

                    case 1:
                        startActivity(new Intent(mContext, MessageCenterPoliceActivity.class));
                        break;

                    case 2:
                        doVersionUpdate(); //版本更新
                        break;

                    case 3:
                        //退出登陆
                        doLoginOutSocket();

//                        ToastUtils.showTextToast(getContext(),"Alarm报警声");
//                        soundPool.stop(playResource);
//
//                        if(timer1!=null) {
//                            timer1.cancel();
//                        }
//                        timer1=new Timer();
//                        TimerTask task=new TimerTask() {
//                            @Override
//                            public void run() {
//                                playResource= soundPool.play(hitOkSfx, (float)0.15, (float)0.15, 1, 0, 1);
//                            }
//                        };
//                        timer1.schedule(task,0,70);

                        break;


                }
            }
        });
    }


    private void doLoginOutSocket() {
        doSocket();
        final LogoutRequest.LogoutRequestMessage.Builder logoutbuilder = LogoutRequest
                .LogoutRequestMessage.newBuilder();
        logoutbuilder.setUserId(PreferenceData.loadLoginAccount(mContext));

        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_LOGIINOUT, logoutbuilder.build()
                        .toByteArray());
            }

        }.start();
    }

    //升级版本
    private void doVersionUpdate() {
        doSocket();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                SocketClient client = SocketClient.getInstance();
                CheckUpdateRequest.CheckUpdateRequestMessage.Builder requestMessage =
                        CheckUpdateRequest.CheckUpdateRequestMessage.newBuilder();
                requestMessage.setAppVersion(ToolsUtils
                        .getAppVersionName(mContext
                                .getApplicationContext()));
                requestMessage.setPlatformType(PlatformTypes.PlatformType.AndroidPhone);
                client.send(SocketAppPacket.COMMAND_ID_GET_POLICE_APP_VERSION, requestMessage
                        .build().toByteArray());
            }
        }).start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            mHandler.removeMessages(MSG_cannt_get_data);
            if (dlgWaiting.isShowing()) {
                dlgWaiting.dismiss();
            }
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_GET_POLICE_APP_VERSION) {
                if (BaseApplication.getInstance().isChangeLanguage) {
                    return;
                }
                final CheckUpdateResponse.CheckUpdateResponseMessage responseMessage;

                responseMessage = CheckUpdateResponse.CheckUpdateResponseMessage.parseFrom
                        (eventPackage.getCommandData());

                if (responseMessage.getErrorMsg().getErrorCode() == 0) {
                    if (responseMessage.getVersion().length() > 0) {
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        View layout = inflater.inflate(R.layout.dlg_version_update, null);
                        final Dialog dlgVersionUpdate = new AlertDialog.Builder(mContext).setView
                                (layout).create();
                        dlgVersionUpdate.show();

                        TextView tv_cancel = (TextView) dlgVersionUpdate.findViewById(R.id
                                .tv_cancel);
                        TextView tv_sure = (TextView) dlgVersionUpdate.findViewById(R.id.tv_sure);
                        TextView tv_content = (TextView) dlgVersionUpdate.findViewById(R.id
                                .tv_content);
                        tv_content.setText(this.getResources().getString(R.string.last_version) +
                                responseMessage.getVersion() + "\n" + responseMessage
                                .getUpdateLog());
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dlgVersionUpdate.dismiss();
                            }
                        });
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dlgVersionUpdate.dismiss();
                                downloadFile(responseMessage.getUpdateURL(),
                                        getString(R.string.app_name),
                                        getString(R.string.home_dlg_download_title));
                            }
                            //App.APK_DOWNLOAD_ADDRESS
                        });
                    } else {
                        ToastUtils.showTextToast(mContext, mContext.getResources
                                ().getString(R.string.lastest_version), 2000);
                    }
                }
            }

            //退出登陆
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_LOGIINOUT) {
                CommonResponse.CommonResponseMessage commonResponseMessage = CommonResponse
                        .CommonResponseMessage.parseFrom(eventPackage.getCommandData());
                if (commonResponseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(mContext, commonResponseMessage.getErrorMsg()
                            .getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (commonResponseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    //退出登陆success
//                    Toast.makeText(SettingActivity.this, "退出登陆success", Toast
//                            .LENGTH_SHORT).show();
//                    PushManager.stopWork(SettingActivity.this);

                    PreferenceData.saveLoginAccount(mContext, -1);
                    Intent in = new Intent(mContext, LoginActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent
                            .FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    /**
     * APK文件下载
     *
     * @param url
     * @param fileName
     */
    private void downloadFile(String url, String fileName, String title) {
        // 创建ProgressDialog对象
        final ProgressDialog dlg = new ProgressDialog(mContext);
        // 设置进度条风格，风格为圆形，旋转的
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 标题
        // dlg.setTitle(R.string.home_dlg_download_title);
        dlg.setTitle(title);
        dlg.setIcon(R.drawable.logo);
        dlg.setMax(100);
        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
        dlg.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回键取消
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);

        File target = null;
        if (FileUtils.isSDCardMounted()) {
            target = Environment
                    .getExternalStoragePublicDirectory(App.IMAGE_CACHE_PATH
                            + fileName + ".apk");
            // target = new File (App.IMAGE_CACHE_PATH + fileName + ".apk");
        }
        AQuery homeAq = new AQuery(mContext);
        homeAq.progress(dlg).download(url, target, new AjaxCallback<File>() {
            public void callback(String url, File file, AjaxStatus status) {
                if (status.getCode() == 200 && file != null) {
                    logMessage("Http-->File:" + file.length() + ":" + file);
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri,
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                } else {
                    logMessage("Http-->Failed");
                    Toast.makeText(mContext,
                            R.string.home_dlg_download_update_fail,
                            Toast.LENGTH_SHORT).show();
                }
                dlg.cancel();
            }
        });
        // 让ProgressDialog显示
        dlg.show();

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//
//        if (soundPool != null) {
//            soundPool.release();
//        }
//    }
}
