package com.yzh.rfidbike_police.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yzh.rfid.app.request.GetBikeByPlateNumberRequest;
import com.yzh.rfid.app.response.Bike;
import com.yzh.rfid.app.response.FileInfo;
import com.yzh.rfid.app.response.GetBikesResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.base.BaseApplication;
import com.yzh.rfidbike_police.base.BaseFragment;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.PhotoUtils;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.util.string.DateUtils;
import com.yzh.rfidbike_police.view.widgets.InputView;
import com.yzh.rfidbike_police.view.widgets.SelectView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import cn.qqtheme.framework.picker.DatePicker;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;




/**
 * Created by Administrator on 2017/1/10.
 */

public class BikeInfoFragment extends BaseFragment implements BGASortableNinePhotoLayout
        .Delegate, InputView.onInputContentChangeListener {
    @BindView(R.id.input_bike_num)
    InputView mInputBikeNum;
    @BindView(R.id.input_rfid_no)
    InputView mInputRfidNO;
    @BindView(R.id.input_bike_type)
    InputView mInputBikeType;
    @BindView(R.id.input_bike_color)
    InputView mInputBikeColor;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.bga_photo_bike_front)
    BGASortableNinePhotoLayout frontBgaPhotoBike;
    @BindView(R.id.bga_photo_bike_back)
    BGASortableNinePhotoLayout backBgaPhotoBike;
    @BindView(R.id.bga_photo_bike_side)
    BGASortableNinePhotoLayout sideBgaPhotoBike;
    @BindView(R.id.bga_photo_bike_cardno)
    BGASortableNinePhotoLayout cardNoBgaPhotoBike;
    @BindView(R.id.select_date_buy)
    SelectView mSelectDateBuy;
    @BindView(R.id.input_price_buy)
    InputView mInputPriceBuy;
    @BindView(R.id.tv_scan)
    TextView mTvScan;


    //    @BindView(R.id.input_usage_type)
//    InputView mInputUsageType;
//    @BindView(R.id.input_people_number)
//    InputView mInputPeopleNumber;
    @BindView(R.id.et_remark)
    EditText mEtRemark;
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 0x1131;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 0x1132;
    private static final int REQUEST_CODE_CHOOSE_BIKE_PHOTO_FRONT = 0x6691;
    private static final int REQUEST_CODE_CHOOSE_BIKE_PHOTO_BACK = 0x6692;
    private static final int REQUEST_CODE_CHOOSE_BIKE_PHOTO_SIDE = 0x6693;
    private static final int REQUEST_CODE_CHOOSE_BIKE_PHOTO_CARDNO = 0x6694;
    private static final int REQUEST_CODE_SCAN_RFID_CARD_NO = 0x6695;
    private boolean mBikeNumExist;

    public netCallBack mNetCallBack;
    private Bike.BikeMessage.Builder mBikeMessageBuilder;
    private Context mContext;
    private String mCurrentBikeNum = "";

    @Override
    protected int getContentView() {
        return R.layout.fragment_bike_info;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        ZXingLibrary.initDisplayOpinion(this.getActivity());
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mBikeMessageBuilder = Bike.BikeMessage.newBuilder();
        frontBgaPhotoBike.setDelegate(this);
        backBgaPhotoBike.setDelegate(this);
        sideBgaPhotoBike.setDelegate(this);
        cardNoBgaPhotoBike.setDelegate(this);
        mInputPriceBuy.setonInputContentChangeListener(this);

    }


    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void chooseFrontBgaPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "DCIM");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, takePhotoDir,
                    frontBgaPhotoBike.getMaxItemCount() - frontBgaPhotoBike
                            .getItemCount(), null, false), REQUEST_CODE_CHOOSE_BIKE_PHOTO_FRONT);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void chooseBackBgaPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "DCIM");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, takePhotoDir,
                    backBgaPhotoBike.getMaxItemCount() - backBgaPhotoBike
                            .getItemCount(), null, false), REQUEST_CODE_CHOOSE_BIKE_PHOTO_BACK);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void chooseSideBgaPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "DCIM");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, takePhotoDir,
                    sideBgaPhotoBike.getMaxItemCount() - sideBgaPhotoBike
                            .getItemCount(), null, false), REQUEST_CODE_CHOOSE_BIKE_PHOTO_SIDE);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void chooseCardnoBgaPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "DCIM");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, takePhotoDir,
                    sideBgaPhotoBike.getMaxItemCount() - sideBgaPhotoBike
                            .getItemCount(), null, false), REQUEST_CODE_CHOOSE_BIKE_PHOTO_CARDNO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View
            view, int position, ArrayList<String> models) {
        switch (sortableNinePhotoLayout.getId()) {
            case R.id.bga_photo_bike_front:
                chooseFrontBgaPhoto();
                break;
            case R.id.bga_photo_bike_back:
                chooseBackBgaPhoto();
                break;
            case R.id.bga_photo_bike_side:
                chooseSideBgaPhoto();
                break;
            case R.id.bga_photo_bike_cardno:
                chooseCardnoBgaPhoto();
                break;
        }
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout,
                                           View view, int position, String model,
                                           ArrayList<String> models) {

        switch (sortableNinePhotoLayout.getId()) {
            case R.id.bga_photo_bike_front:
                frontBgaPhotoBike.removeItem(position);
                break;

            case R.id.bga_photo_bike_back:
                backBgaPhotoBike.removeItem(position);
                break;

            case R.id.bga_photo_bike_side:
                sideBgaPhotoBike.removeItem(position);
                break;

            case R.id.bga_photo_bike_cardno:
                cardNoBgaPhotoBike.removeItem(position);
                break;

        }
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View
            view, int position, String model, ArrayList<String> models) {
        switch (sortableNinePhotoLayout.getId()) {
            case R.id.bga_photo_bike_front:
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent
                                (mContext, frontBgaPhotoBike
                                        .getMaxItemCount(), models, models, position, false),
                        REQUEST_CODE_PHOTO_PREVIEW);
                break;
            case R.id.bga_photo_bike_back:
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent
                                (mContext, backBgaPhotoBike
                                        .getMaxItemCount(), models, models, position, false),
                        REQUEST_CODE_PHOTO_PREVIEW);
                break;
            case R.id.bga_photo_bike_side:
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(mContext, sideBgaPhotoBike
                                        .getMaxItemCount(), models, models, position, false),
                        REQUEST_CODE_PHOTO_PREVIEW);
                break;
            case R.id.bga_photo_bike_cardno:
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(mContext, cardNoBgaPhotoBike
                                        .getMaxItemCount(), models, models, position, false),
                        REQUEST_CODE_PHOTO_PREVIEW);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_BIKE_PHOTO_FRONT:
                frontBgaPhotoBike.setData(BGAPhotoPickerActivity.getSelectedImages(data));
                break;
            case REQUEST_CODE_CHOOSE_BIKE_PHOTO_BACK:
                backBgaPhotoBike.setData(BGAPhotoPickerActivity.getSelectedImages(data));
                break;
            case REQUEST_CODE_CHOOSE_BIKE_PHOTO_SIDE:
                sideBgaPhotoBike.setData(BGAPhotoPickerActivity.getSelectedImages(data));
                break;
            case REQUEST_CODE_CHOOSE_BIKE_PHOTO_CARDNO:
                cardNoBgaPhotoBike.setData(BGAPhotoPickerActivity.getSelectedImages(data));
                break;
            case REQUEST_CODE_SCAN_RFID_CARD_NO:
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        mInputRfidNO.setContent( result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(BikeInfoFragment.this.getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    /**
     * 检查bike是否输入完整
     */
    public boolean setBikeMessage() {
        if (mInputBikeNum.getContent().isEmpty() || mInputBikeNum.getContent().equals("")) {
            ToastUtils.showTextToast(mContext, "请输入车牌号");
            return false;
        } else if (mCurrentBikeNum != null) {
            if (mCurrentBikeNum.equals(mInputBikeNum.getContent()) &&
                    mBikeNumExist) {
                ToastUtils.showTextToast(mContext, "请勿填写已登记的车牌");
                return false;
            } else {
                mBikeMessageBuilder.setPlateNumber(mInputBikeNum.getContent());
            }
        }
        if (mInputRfidNO.getContent().isEmpty() || mInputRfidNO.getContent().equals("")) {
            ToastUtils.showTextToast(mContext, "请输入RFID编号");
            return false;
        } else {
            mBikeMessageBuilder.setCardNo(Long.valueOf(mInputRfidNO.getContent()));
        }
        if (mInputBikeType.getContent().isEmpty() || mInputBikeType.getContent().equals("")) {
            ToastUtils.showTextToast(mContext, "请输入品牌型号");
            return false;
        } else {
            mBikeMessageBuilder.setBrandModel(mInputBikeType.getContent());

        }
        if (mInputBikeColor.getContent().isEmpty() || mInputBikeColor.getContent().equals("")) {
            ToastUtils.showTextToast(mContext, "请输入颜色");
            return false;
        } else {
            mBikeMessageBuilder.setColor(mInputBikeColor.getContent());
        }

        if (frontBgaPhotoBike.getData().size() == 0) {
            ToastUtils.showTextToast(mContext, "请上传车辆正面照片");
            return false;
        }else {
            buildFrontPhoto(frontBgaPhotoBike);
        }

        if (backBgaPhotoBike.getData().size() == 0) {
            ToastUtils.showTextToast(mContext, "请上传车辆后面照片");
            return false;
        }else {
            buildLeftSidePhoto(backBgaPhotoBike);
        }

        if (sideBgaPhotoBike.getData().size() == 0) {
            ToastUtils.showTextToast(mContext, "请上传车辆侧面照片");
            return false;
        }else {
            buildRightSidePhoto(sideBgaPhotoBike);
        }

        if (cardNoBgaPhotoBike.getData().size() == 0) {
            ToastUtils.showTextToast(mContext, "请上传车牌照片");
            return false;
        }else {
            buildCardNoSidePhoto(cardNoBgaPhotoBike);
        }


        if (!mInputPriceBuy.getContent().isEmpty()) {
            mBikeMessageBuilder.setPurchasePrice(Double.valueOf(mInputPriceBuy
                    .getContent()));
        }
//        if (!mInputUsageType.getContent().isEmpty()) {
//            mBikeMessageBuilder.setUsageNature(mInputUsageType.getContent());
//        }
//        if (!mInputPeopleNumber.getContent().isEmpty()) {
//            mBikeMessageBuilder.setLoadNumber(Integer.valueOf(mInputPeopleNumber
//                    .getContent()));
//        }
        if (!mEtRemark.getText().toString().isEmpty()) {
            mBikeMessageBuilder.setRemarks(mEtRemark.getText().toString());
        }

        return true;
    }

//    private void buildPhoto(BGASortableNinePhotoLayout bgaPhoto) {
//        String path = bgaPhoto.getData().get(0);
//        try {
//            byte[] photoByte = PhotoUtils.toByteArray
//                    (path);
//            ByteString byteString = ByteString.copyFrom(photoByte);
//            FileInfo.FileInfoMessage.Builder fileInfoMessage = FileInfo.FileInfoMessage
//                    .newBuilder();
//            fileInfoMessage.setFileBytes(byteString);
//            int dotIndex = path.indexOf(".");
//            fileInfoMessage.setExtension(path.substring(dotIndex + 1));
//            mBikeMessageBuilder.setPhotoFileInfo(fileInfoMessage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void buildFrontPhoto(BGASortableNinePhotoLayout bgaPhoto) {
        String path = bgaPhoto.getData().get(0);
        try {
            byte[] photoByte = PhotoUtils.toByteArray
                    (path);
            ByteString byteString = ByteString.copyFrom(photoByte);
            FileInfo.FileInfoMessage.Builder fileInfoMessage = FileInfo.FileInfoMessage
                    .newBuilder();
            fileInfoMessage.setFileBytes(byteString);
            int dotIndex = path.indexOf(".");
            fileInfoMessage.setExtension(path.substring(dotIndex + 1));
//            mBikeMessageBuilder.setPhotoFileInfo(fileInfoMessage);
            mBikeMessageBuilder.setFrontPicFileInfo(fileInfoMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void buildLeftSidePhoto(BGASortableNinePhotoLayout bgaPhoto) {
        String path = bgaPhoto.getData().get(0);
        try {
            byte[] photoByte = PhotoUtils.toByteArray
                    (path);
            ByteString byteString = ByteString.copyFrom(photoByte);
            FileInfo.FileInfoMessage.Builder fileInfoMessage = FileInfo.FileInfoMessage
                    .newBuilder();
            fileInfoMessage.setFileBytes(byteString);
            int dotIndex = path.indexOf(".");
            fileInfoMessage.setExtension(path.substring(dotIndex + 1));
            mBikeMessageBuilder.setBackPicFileInfo(fileInfoMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildRightSidePhoto(BGASortableNinePhotoLayout bgaPhoto) {
        String path = bgaPhoto.getData().get(0);
        try {
            byte[] photoByte = PhotoUtils.toByteArray
                    (path);
            ByteString byteString = ByteString.copyFrom(photoByte);
            FileInfo.FileInfoMessage.Builder fileInfoMessage = FileInfo.FileInfoMessage
                    .newBuilder();
            fileInfoMessage.setFileBytes(byteString);
            int dotIndex = path.indexOf(".");
            fileInfoMessage.setExtension(path.substring(dotIndex + 1));
            mBikeMessageBuilder.setSidePicFileInfo(fileInfoMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void buildCardNoSidePhoto(BGASortableNinePhotoLayout bgaPhoto) {
        String path = bgaPhoto.getData().get(0);
        try {
            byte[] photoByte = PhotoUtils.toByteArray
                    (path);
            ByteString byteString = ByteString.copyFrom(photoByte);
            FileInfo.FileInfoMessage.Builder fileInfoMessage = FileInfo.FileInfoMessage
                    .newBuilder();
            fileInfoMessage.setFileBytes(byteString);
            int dotIndex = path.indexOf(".");
            fileInfoMessage.setExtension(path.substring(dotIndex + 1));
            mBikeMessageBuilder.setTagPicFileInfo(fileInfoMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.select_date_buy, R.id.tv_query,R.id.ll_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_date_buy:
                final DatePicker datePicker = new DatePicker((Activity) mContext);

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                datePicker.setRangeEnd(year,month,day);
                datePicker.setSelectedItem(year,month,day);
                datePicker.show();
                datePicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        String date = String.format("%s-%s-%s", year, month, day);
                        mSelectDateBuy.setContent(date);
                        try {
                            long time = DateUtils.getLongFromDate(DateUtils.formatDate(date));
                            mBikeMessageBuilder.setPurchaseDate(time);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
            case R.id.tv_query:
                //车牌号不为空,则开启搜索
                if (!TextUtils.isEmpty(mInputBikeNum.getContent())) {
                    doSocket();
                } else {
                    ToastUtils.showTextToast(mContext, "车牌号不能为空");
                }
                break;
            case R.id.ll_scan:
                Intent intent = new Intent(BikeInfoFragment.this.getActivity(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN_RFID_CARD_NO);
                break;
        }

    }

    public Bike.BikeMessage build() {
        return mBikeMessageBuilder.build();
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        BaseApplication.getInstance().currentFragment = "BikeInfoFragment";
        dlgWaiting.setCancelable(false);
        mCurrentBikeNum = mInputBikeNum.getContent();
        final
        GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage.Builder
                requestMessage = GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage
                .newBuilder();
        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        if (companyUser != null) {
            requestMessage.setSession(companyUser.getSession());
        }
        requestMessage.setPlateNumber(mInputBikeNum.getContent());
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_BIKE_BY_BIKE_NO, requestMessage.build()
                        .toByteArray());
            }

        }.start();

    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_BIKE_BY_BIKE_NO && BaseApplication.getInstance().currentFragment.equals("BikeInfoFragment")) {
                GetBikesResponse.GetBikesResponseMessage bikesResponseMessage = GetBikesResponse
                        .GetBikesResponseMessage
                        .parseFrom(eventPackage.getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (bikesResponseMessage.getErrorMsg().getErrorCode() == 0) {
                    ToastUtils.showTextToast(mContext, "请勿填写已登记的车牌");
                    mBikeNumExist = true;
                    if (mNetCallBack != null) {
                        mNetCallBack.netFinish(true);
                    }
                } else {
                    ToastUtils.showTextToast(mContext, bikesResponseMessage.getErrorMsg()
                            .getErrorMsg());
                    if (bikesResponseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    mBikeNumExist = false;

                    if (mNetCallBack != null) {
                        mNetCallBack.netFinish(false);
                    }
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void FocusChange(InputView view, boolean hasFocus) {
//        switch (view.getId()) {
//            case R.id.input_bike_num:
//                if (!hasFocus && !mInputBikeNum.getContent().isEmpty() &&
//                        !mCurrentBikeNum.equals(mInputBikeNum.getContent())) {
//                    doSocket();
//
//                }
//                break;
//        }
//    }

    @Override
    public void ContentChange(InputView inputView, String content) {
        switch (inputView.getId()) {
            case R.id.input_price_buy:
                if (content.isEmpty()) break;
                if (content.contains(".")) {
                    if (content.indexOf(".") + 3 < content.length()) {
                        inputView.setContent(content.subSequence(0, content.toString().indexOf("" +
                                ".") + 3).toString());
                    }
                }
                if (content.isEmpty() && Double.valueOf(content) > 1000000) {
                    inputView.setContent("1000000");
                }
                inputView.setSelectionEnd();
                break;
        }
    }

    public interface netCallBack {
        void netFinish(boolean exist);

    }

    public void checkBikeNumber(netCallBack netCallBack) {
        mNetCallBack = netCallBack;
        if (mCurrentBikeNum.equals(mInputBikeNum.getContent())) {
            mNetCallBack.netFinish(mBikeNumExist);
        } else {
            doSocket();
        }
    }

    public void unCheckBikeNumber() {
        mNetCallBack = null;
    }

    /**
     * 清除所有数据
     */
    public void clearAllBikeInfo() {
        setPhotoEnable(true);
        clearBikeUser();
        setAllEnable(true);
    }

    /**
     * 设置图片是否可以选择
     */
    private void setPhotoEnable(boolean enable) {
        if (enable) {
            frontBgaPhotoBike.setVisibility(View.VISIBLE);
            frontBgaPhotoBike.setData(null);
            backBgaPhotoBike.setVisibility(View.VISIBLE);
            backBgaPhotoBike.setData(null);
            sideBgaPhotoBike.setVisibility(View.VISIBLE);
            sideBgaPhotoBike.setData(null);
            cardNoBgaPhotoBike.setVisibility(View.VISIBLE);
            cardNoBgaPhotoBike.setData(null);

        } else {
            frontBgaPhotoBike.setVisibility(View.GONE);
            backBgaPhotoBike.setVisibility(View.GONE);
            sideBgaPhotoBike.setVisibility(View.GONE);
            cardNoBgaPhotoBike.setVisibility(View.GONE);
        }

    }


    private void clearBikeUser() {
        mBikeMessageBuilder.clear();
        mInputBikeNum.setContent("");
        mInputRfidNO.setContent("");
        mInputBikeType.setContent("");
        mInputBikeColor.setContent("");
        mInputPriceBuy.setContent("");
        mSelectDateBuy.setContent("");
        mEtRemark.setText("");
    }

    private void setAllEnable(boolean enable) {
        mInputBikeNum.setInputEnable(enable);
        mInputRfidNO.setInputEnable(enable);
        mInputBikeType.setInputEnable(enable);
        mInputBikeColor.setInputEnable(enable);
        mInputPriceBuy.setInputEnable(enable);
        mSelectDateBuy.setEnabled(enable);
        mEtRemark.setEnabled(enable);
        frontBgaPhotoBike.setPlusEnable(enable);
        backBgaPhotoBike.setPlusEnable(enable);
        sideBgaPhotoBike.setPlusEnable(enable);
        cardNoBgaPhotoBike.setPlusEnable(enable);

    }

}

