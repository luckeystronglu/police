/**
 *
 */
package com.yzh.rfidbike_police.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.UpdatePasswordRequest;
import com.yzh.rfid.app.response.CommonResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.view.widgets.Header;

import net.tsz.afinal.FinalDb;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author WT00111
 */
public class UpdatePasswordActivity extends BaseActivity implements Header.headerListener {

    @BindView(R.id.header)
    Header mHeader;
    /* (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
    private String mUserOldPassword = "";
    private String mUserNewPassword = "";
    private String mUserNewPassword2 = "";

    // UI references.
    private EditText mUserOldpwView;
    private EditText mUserNewpwView;
    private EditText mUserNewpwView2;
    private Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepassword);
        ButterKnife.bind(this);
        initView();
        mHeader.setListener(this);
    }


    protected void initView() {
        mUserOldpwView = (EditText) findViewById(R.id.originalPassword);
        mUserNewpwView = (EditText) findViewById(R.id.newPassWord);
        mUserNewpwView2 = (EditText) findViewById(R.id.confirmPassword);
        saveBtn = (Button) findViewById(R.id.savebtn);

    }

    public void onClick(View v) {
        getPassword();
    }

    private void getPassword() {
        mUserOldPassword = mUserOldpwView.getText().toString();
        mUserNewPassword = mUserNewpwView.getText().toString();
        mUserNewPassword2 = mUserNewpwView2.getText().toString();
        boolean cancel = false;
        View focusView = null;
        //判断输入的旧密码是否为空
        if (TextUtils.isEmpty(mUserOldPassword)) {
//            mUserOldpwView.setError(getString(R.string.error_field_required));
            ToastUtils.showTextToast(UpdatePasswordActivity.this, "旧密码不能为空");
            focusView = mUserOldpwView;
            cancel = true;
            return;
        }
        //判断输入的新密码是否为空
        if (TextUtils.isEmpty(mUserNewPassword)) {
//            mUserNewpwView.setError(getString(R.string.error_field_required));
            ToastUtils.showTextToast(UpdatePasswordActivity.this, "新密码不能为空");
            focusView = mUserNewpwView;
            cancel = true;
            return;
        }

        //判断输入的新密码是否为6-12位
        if (mUserNewPassword.length() < 6) {
            ToastUtils.showTextToast(UpdatePasswordActivity.this,"密码位数不能少于6位");
            focusView = mUserNewpwView;
            cancel = true;
            return;
        }

        //判断再次输入的新密码是否为空
        if (TextUtils.isEmpty(mUserNewPassword2)) {
//            mUserNewpwView2.setError(getString(R.string.error_field_required));
            ToastUtils.showTextToast(UpdatePasswordActivity.this, "确认密码不能为空");
            focusView = mUserNewpwView2;
            cancel = true;
            return;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //判断新密码与确认密码是否一致
            if (mUserNewPassword.equals(mUserNewPassword2)) {
                doSocket();
            } else {
                Toast.makeText(this, "新密码与确认密码不一致", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        final UpdatePasswordRequest.UpdatePasswordRequestMessage.Builder requestMessage =
                UpdatePasswordRequest.UpdatePasswordRequestMessage.newBuilder();
        requestMessage.setSession(PreferenceData.loadSession(this));
        requestMessage.setPassword(mUserOldPassword);
        requestMessage.setNewPassword(mUserNewPassword2);
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_POLICE_UPDATE_PASSWORD, requestMessage.build()
                        .toByteArray());
            }

        }.start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            CommonResponse.CommonResponseMessage changePwMessage = CommonResponse
                    .CommonResponseMessage.parseFrom(eventPackage.getCommandData());

            if (dlgWaiting.isShowing()) {
                dlgWaiting.dismiss();
            }
            mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
            if (changePwMessage.getErrorMsg().getErrorCode() != 0) {
                Toast.makeText(this, changePwMessage.getErrorMsg().getErrorMsg(), Toast
                        .LENGTH_LONG).show();
                if (changePwMessage.getErrorMsg().getErrorCode() == 20003) {
                    Intent intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            } else {
//				BikeUser bikeuser = new BikeUser();
//				bikeuser.setId(changePwMessage.get);
//				bikeuser.setName(loginResponseMessage.getName());
//				bikeuser.setIdBackPicPath(loginResponseMessage.getIdBackPicPath());

                final FinalDb mDb = FinalDb.create(this, App.DB_NAME, true, App.DB_VERSION, this);
                //判断是否存在，如存在就修改，不存在直接保存
                List<CompanyUser> list = mDb.findAllByWhere(CompanyUser.class, "userId=" + String
                        .valueOf
                        (PreferenceData.loadLoginAccount(this)));

                CompanyUser bikeuser = list.get(0);

                mDb.update(bikeuser);

                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                finish();


            }

//			startActivity(new Intent(this,MainActivity.class));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onClickLeftIcon() {
        super.onBackPressed();
    }

    @Override
    public void onClickRightText() {

    }

}
