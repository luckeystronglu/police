package com.yzh.rfidbike_police.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.GetInsuranceRequest;
import com.yzh.rfid.app.response.BikeInsurance;
import com.yzh.rfid.app.response.GetInsuranceResponse;
import com.yzh.rfid.app.response.Insurance;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.base.BaseFragment;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.view.widgets.InputView;
import com.yzh.rfidbike_police.view.widgets.SelectView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by Administrator on 2017/1/10.
 */

public class InsureFragment extends BaseFragment {
    @BindView(R.id.select_insure_class)
    SelectView mSelectInsureClass;
    @BindView(R.id.input_insure_number)
    InputView mInputInsureNumber;
    @BindView(R.id.input_insure_money)
    InputView mInputInsureMoney;
    @BindView(R.id.input_insure_money_max)
    InputView mInputInsureMoneyMax;
    @BindView(R.id.input_insure_company)
    InputView mInputInsureCompany;
    @BindView(R.id.input_insure_month_valid)
    InputView mInputInsureMonthValid;
    @BindView(R.id.tv_remark)
    TextView mTvRemark;
    private BikeInsurance.BikeInsuranceMessage.Builder mBuilder;
    private Context mContext;
    private List<Insurance.InsuranceMessage> mInsuranceMessageList;
    private List<String> mInsureStringList;

    @Override
    protected int getContentView() {
        return R.layout.fragment_insure;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mBuilder = BikeInsurance.BikeInsuranceMessage.newBuilder();
        mInsureStringList = new ArrayList<>();
    }


    @OnClick(R.id.select_insure_class)
    public void onClick() {
        Insurance.InsuranceMessage.Builder builder = Insurance.InsuranceMessage.newBuilder();
        doSocket();
    }



    public boolean setInsureMessage() {

        if (!mSelectInsureClass.getContent().isEmpty() && mInputInsureNumber.getContent().isEmpty
                ()) {
            ToastUtils.showTextToast(mContext, "请填写保险单号");
            return false;
        } else if (mSelectInsureClass.getContent().isEmpty() && !mInputInsureNumber.getContent()
                .isEmpty
                        ()) {
            ToastUtils.showTextToast(mContext, "请选择保险种类");
            return false;
        } else {
            mBuilder.setInsuranceNo(mInputInsureNumber.getContent());
        }
        return true;
    }

    public BikeInsurance.BikeInsuranceMessage build() {
        return mBuilder.build();
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        final GetInsuranceRequest.GetInsuranceRequestMessage.Builder requestMessage =
                GetInsuranceRequest.GetInsuranceRequestMessage.newBuilder();
        Insurance.InsuranceMessage insuranceMessage = Insurance.InsuranceMessage.newBuilder()
                .build();
        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        requestMessage.setSession(companyUser.getSession());
        requestMessage.setInsuranceMessage(insuranceMessage);
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_BIKE_INSURE_LIST, requestMessage.build()
                        .toByteArray());
            }

        }.start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_BIKE_INSURE_LIST) {
                GetInsuranceResponse.GetInsuranceResponseMessage responseMessage =
                        GetInsuranceResponse.GetInsuranceResponseMessage
                                .parseFrom(eventPackage
                                        .getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (responseMessage.getErrorMsg().getErrorCode() != 0) {
                    ToastUtils.showTextToast(mContext, responseMessage.getErrorMsg().getErrorMsg());
                    if (responseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    mInsureStringList.clear();
                    mInsuranceMessageList = responseMessage.getInsuranceMessageList();
                    for (Insurance.InsuranceMessage message : mInsuranceMessageList) {
                        mInsureStringList.add(message.getInsuranceType());
                    }
                    OptionPicker optionPicker = new OptionPicker((Activity) mContext,
                            mInsureStringList);
                    optionPicker.setCycleDisable(true);
                    optionPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                        @Override
                        public void onOptionPicked(int index, String item) {
                            mSelectInsureClass.setContent(item);
                            Insurance.InsuranceMessage message = mInsuranceMessageList.get(index);
                            mInputInsureMoney.setContent(message.getInsuranceAmount() + "");
                            mInputInsureMoneyMax.setContent(message.getCompensationAmount() + "");
                            mInputInsureCompany.setContent(message.getInsuranceCompany());
                            mInputInsureMonthValid.setContent(message.getValidMonth() + "");
                            mTvRemark.setText(message.getRemarks());

                            mBuilder.setInsuranceType(message.getInsuranceType());
                            mBuilder.setInsuranceAmount(message.getInsuranceAmount());
                            mBuilder.setCompensationAmount(message.getCompensationAmount());
                            mBuilder.setInsuranceCompany(message.getInsuranceCompany());
                            mBuilder.setValidMonth(message.getValidMonth());
                            mBuilder.setRemarks(message.getRemarks());
                        }
                    });
                    optionPicker.show();
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除所有数据
     */
    public void clearAllInsureInfo() {
        clearBikeInsure();
        setAllEnable(true);
    }



    /**
     SelectView mSelectInsureClass;
     InputView mInputInsureNumber;
     InputView mInputInsureMoney;
     InputView mInputInsureMoneyMax;
     InputView mInputInsureCompany;
     InputView mInputInsureMonthValid;
     TextView mTvRemark;
     */
    private void clearBikeInsure() {
        mBuilder.clear();
        mInputInsureNumber.setContent("");
        mInputInsureMoney.setContent("");
        mInputInsureMoneyMax.setContent("");
        mInputInsureCompany.setContent("");
        mInputInsureMonthValid.setContent("");
        mSelectInsureClass.setContent("");
        mTvRemark.setText("");
    }

    private void setAllEnable(boolean enable) {
        mInputInsureNumber.setInputEnable(enable);
        mInputInsureMoney.setInputEnable(enable);
        mInputInsureMoneyMax.setInputEnable(enable);
        mInputInsureCompany.setInputEnable(enable);
        mInputInsureMonthValid.setInputEnable(enable);
        mSelectInsureClass.setEnabled(enable);
        mTvRemark.setEnabled(enable);
    }


}
