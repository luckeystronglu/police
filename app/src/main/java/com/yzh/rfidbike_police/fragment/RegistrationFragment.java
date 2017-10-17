package com.yzh.rfidbike_police.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.ApplyBikePlateNumberRequest;
import com.yzh.rfid.app.response.Bike;
import com.yzh.rfid.app.response.BikeInsurance;
import com.yzh.rfid.app.response.BikeUser;
import com.yzh.rfid.app.response.CommonResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.base.BaseFragment;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.view.widgets.Header;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/3/16.
 * 上牌登记Fragment
 */

public class RegistrationFragment extends BaseFragment {
    @BindView(R.id.btn_back)
    Button mBtnBack;
    @BindView(R.id.btn_next)
    Button mBtnNext;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.header)
    Header mHeader;
    @BindView(R.id.container)
    LinearLayout mContainer;
    private BikeInfoFragment mBikeInfoFragment;
    private BikeUserInfoFragment mBikeUserInfoFragment;
    private InsureFragment mInsureFragment;
    private Bike.BikeMessage mBikeMessage;
    private BikeUser.BikeUserMessage mBikeUserMessage;
    private BikeInsurance.BikeInsuranceMessage mBikeInsuranceMessage;
    private boolean mIsNetting;//是否可以请求网络
    private Context mContext;
    FragmentManager fragmentManager;
    private int backLocation = -1; //记录当前Fragment的下标,BikeInfoFragment(车辆信息) 为1, BikeUserInfoFragment(车主信息)为2,InsureFragment(保险信息)为3

    @Override
    protected int getContentView() {
        return R.layout.fragment_registration;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if (mBikeInfoFragment == null) {
            mBikeInfoFragment = new BikeInfoFragment();
        }

        if (mBikeUserInfoFragment == null) {
            mBikeUserInfoFragment = new BikeUserInfoFragment();
        }
        if (mInsureFragment == null) {
            mInsureFragment = new InsureFragment();
        }

        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void init(View view) {
        super.init(view);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        fragmentManager(R.id.container, mBikeInfoFragment, "fragBikeInfo");
        backLocation = 1;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick({R.id.btn_back, R.id.btn_next, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                BackPressed();
                break;
            case R.id.btn_next:
                if (backLocation == 1) {
                    if (!mBikeInfoFragment.setBikeMessage()) {
                        mBtnBack.setVisibility(View.GONE);
                        mBtnNext.setVisibility(View.VISIBLE);
                        mBtnCommit.setVisibility(View.GONE);
                        return;
                    }else {
                        mIsNetting = true;
                        mBikeInfoFragment.checkBikeNumber(new BikeInfoFragment.netCallBack() {
                            @Override
                            public void netFinish(boolean exist) {
                                mIsNetting = false;
                                if (!exist) {
                                    fragmentManager(R.id.container, mBikeUserInfoFragment, "fragUserInfo");
                                    backLocation = 2;
                                    mBikeInfoFragment.mNetCallBack=null;

                                    mBtnBack.setVisibility(View.VISIBLE);
                                    mBtnNext.setVisibility(View.VISIBLE);
                                    mBtnCommit.setVisibility(View.GONE);

                                } else {
                                    ToastUtils.showTextToast(mContext, "请勿填写已登记的车牌");
                                    mBtnBack.setVisibility(View.GONE);
                                    mBtnNext.setVisibility(View.VISIBLE);
                                    mBtnCommit.setVisibility(View.GONE);
                                }


                            }
                        });
                    }
                }else if (backLocation == 2){
                    if (!mBikeUserInfoFragment.setBikeUserMessage()) {
                        mBtnBack.setVisibility(View.VISIBLE);
                        mBtnNext.setVisibility(View.VISIBLE);
                        mBtnCommit.setVisibility(View.GONE);
                        return;
                    } else {
                        fragmentManager(R.id.container, mInsureFragment, "fragInsure");
                        backLocation = 3;
                        mBtnBack.setVisibility(View.VISIBLE);
                        mBtnNext.setVisibility(View.GONE);
                        mBtnCommit.setVisibility(View.VISIBLE);
                    }
                }


                break;
            case R.id.btn_commit:
                if (!mInsureFragment.setInsureMessage()) {
                    return;
                }
                mBikeMessage = mBikeInfoFragment.build();
                mBikeUserMessage = mBikeUserInfoFragment.build();
                mBikeInsuranceMessage = mInsureFragment.build();
                doSocket();
                break;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void BackPressed() {
        if (backLocation == 2) {
            fragmentManager(R.id.container, mBikeInfoFragment, "fragBikeInfo");
            mBikeInfoFragment.unCheckBikeNumber();
            mBtnBack.setVisibility(View.GONE);
            mBtnNext.setVisibility(View.VISIBLE);
            mBtnCommit.setVisibility(View.GONE);
            backLocation = 1;
        }else if (backLocation == 3){
            fragmentManager(R.id.container, mBikeUserInfoFragment,"fragUserInfo");
            mBikeUserInfoFragment.unCheckIdCardNumber();
            mBtnBack.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            mBtnCommit.setVisibility(View.GONE);
            backLocation = 2;
        }else if (backLocation == 1){
            fragmentManager(R.id.container, mBikeInfoFragment, "fragBikeInfo");
            mBikeInfoFragment.unCheckBikeNumber();
            mBtnBack.setVisibility(View.GONE);
            mBtnNext.setVisibility(View.VISIBLE);
            mBtnCommit.setVisibility(View.GONE);
            backLocation = 1;
        }
    }


    @Override
    protected void doSocket() {
        super.doSocket();
        final ApplyBikePlateNumberRequest.ApplyBikePlateNumberRequestMessage.Builder
                requestMessage = ApplyBikePlateNumberRequest
                .ApplyBikePlateNumberRequestMessage.newBuilder();
        requestMessage.setBikeMessage(mBikeMessage);
        requestMessage.setBikeUserMessage(mBikeUserMessage);
        requestMessage.setBikeInsuranceMessage(mBikeInsuranceMessage);
        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        requestMessage.setSession(companyUser.getSession());
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_BIKE_INSURE, requestMessage.build()
                        .toByteArray());
            }

        }.start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_BIKE_INSURE) {
                CommonResponse.CommonResponseMessage companyMessage = CommonResponse
                        .CommonResponseMessage.
                                parseFrom(eventPackage.getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (companyMessage.getErrorMsg().getErrorCode() != 0) {
                    ToastUtils.showTextToast(mContext, companyMessage.getErrorMsg()
                            .getErrorMsg());
                    if (companyMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    ToastUtils.showTextToast(mContext, "上牌信息提交成功");

                    mBikeInfoFragment.clearAllBikeInfo();
                    mBikeUserInfoFragment.clearAll();
                    mInsureFragment.clearAllInsureInfo();
                    fragmentManager(R.id.container, mBikeInfoFragment, "fragBikeInfo");
                    backLocation = 1;
                    mBtnBack.setVisibility(View.GONE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    mBtnCommit.setVisibility(View.GONE);

                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mBikeInfoFragment = null;
        mBikeUserInfoFragment = null;
        mInsureFragment = null;
    }


}
