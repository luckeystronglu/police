package com.yzh.rfidbike_police.activity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.android.pushservice.BasicPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.LoginRequest;
import com.yzh.rfid.app.response.LoginResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.base.BaseApplication;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.BaiduPushUtils;
import com.yzh.rfidbike_police.util.logort.ToastUtils;

import net.tsz.afinal.FinalDb;

import java.util.List;
import java.util.regex.Pattern;



public class LoginActivity extends BaseActivity {


    private String mLoginId = "";
    private String mPassword = "";

    private EditText mUserNameView;
    private EditText mPasswordView;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mUserNameView = (EditText) findViewById(R.id.et_name);
        mPasswordView = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        initBaiduChannelId();

        CompanyUser companyUser = CompanyUser.getCurUser(this);
        if (companyUser != null) {
            mUserNameView.setText(companyUser.getLoginId());
        }
    }


    public void attemptLogin() {

        mLoginId = mUserNameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(mLoginId)) {
            ToastUtils.showTextToast(LoginActivity.this, "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(mPassword)) {
            ToastUtils.showTextToast(LoginActivity.this, "密码不能为空");
            return;
        }

        doSocket();
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        final LoginRequest.LoginRequestMessage.Builder requestMessage = LoginRequest
                .LoginRequestMessage.newBuilder();
        requestMessage.setLoginName(mUserNameView.getText().toString());
        requestMessage.setPassword(mPasswordView.getText().toString());
        String channelId = BaiduPushUtils.getBaiduPushChannelId(context);
        requestMessage.setPushChannelId(channelId);
        requestMessage.setPhoneType(LoginRequest.LoginRequestMessage.PhoneType.AndroidPhone);
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_USER_LOGIIN, requestMessage.build()
                        .toByteArray());
            }

        }.start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_USER_LOGIIN) {
                LoginResponse.LoginResponseMessage loginResponseMessage = LoginResponse
                        .LoginResponseMessage.parseFrom(eventPackage.getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (loginResponseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(LoginActivity.this, loginResponseMessage.getErrorMsg()
                            .getErrorMsg(), Toast.LENGTH_LONG).show();
                } else {
                    CompanyUser companyUser = new CompanyUser();
                    companyUser.setLoginId(mLoginId);
                    companyUser.setCompanyId(String.valueOf(loginResponseMessage.getCompanyId()));
                    companyUser.setUserId((int) loginResponseMessage.getId());
                    companyUser.setName(loginResponseMessage.getName());
                    companyUser.setPassword(loginResponseMessage.getPassword());
                    companyUser.setSession(loginResponseMessage.getSession());
                    companyUser.setAddress(loginResponseMessage.getAddress());
                    companyUser.setEmail(loginResponseMessage.getEmail());
                    companyUser.setPhone(loginResponseMessage.getPhone());
                    FinalDb mDb = FinalDb.create(getApplicationContext(), App.DB_NAME, true, App
                            .DB_VERSION, BaseApplication.getInstance());

                    //判断是否存在，如存在就修改，不存在直接保存
                    List<CompanyUser> list = mDb.findAllByWhere(CompanyUser.class, "loginid='" +
                            mLoginId+"'");
                    if (list.size() > 0) {
                        mDb.update(companyUser);
                    } else {
                        mDb.save(companyUser);
                    }
                    //删除先前登录过的用户
                    mDb.deleteByWhere(CompanyUser.class, "loginid <>'" +
                            mLoginId+"'");
                    PreferenceData.saveLoginAccount(this, loginResponseMessage.getId());
                    PreferenceData.saveSession(this, loginResponseMessage.getSession());
                    PreferenceData.saveLoginName(this, loginResponseMessage.getIdCard());
                    startActivity(new Intent(this, MainNewActivity.class));
                    finish();
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是身份证，
     *
     * @param str
     * @return
     */
    public static boolean identity(String str) {
        Pattern pattern = Pattern
                .compile("^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])" +
                        "(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$");
        return pattern.matcher(str).matches();
    }

    private void initBaiduChannelId() {
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                App.BAIDU_PUSH_API_KEY);

        BasicPushNotificationBuilder basicbuilder = new BasicPushNotificationBuilder();
        basicbuilder.setStatusbarIcon(R.drawable.police);
        basicbuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        PushManager.setDefaultNotificationBuilder(LoginActivity.this,basicbuilder);

    }



}