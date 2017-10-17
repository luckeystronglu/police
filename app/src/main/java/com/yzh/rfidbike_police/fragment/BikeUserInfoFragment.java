package com.yzh.rfidbike_police.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.GetBikeUserRequest;
import com.yzh.rfid.app.response.BikeUser;
import com.yzh.rfid.app.response.FileInfo;
import com.yzh.rfid.app.response.GetBikUserResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.activity.PhotoPreviewActivity;
import com.yzh.rfidbike_police.activity.SelectCompanyListActivity;
import com.yzh.rfidbike_police.base.BaseFragment;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.model.EventBusMsg;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.PhotoUtils;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.util.string.StringUtils;
import com.yzh.rfidbike_police.view.widgets.InputView;
import com.yzh.rfidbike_police.view.widgets.SelectView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import cn.qqtheme.framework.picker.OptionPicker;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Administrator on 2017/1/10.
 */

public class BikeUserInfoFragment extends BaseFragment implements BGASortableNinePhotoLayout
        .Delegate, InputView.onInputContentChangeListener {
    @BindView(R.id.input_idCard_number)
    InputView mInputIdCardNumber;
    @BindView(R.id.select_company)
    SelectView mSelectCompany;
    @BindView(R.id.input_name)
    InputView mInputName;
    @BindView(R.id.input_password)
    InputView mInputPassword;
    @BindView(R.id.bga_idCard_front)
    BGASortableNinePhotoLayout mBgaIdCardFront;
    @BindView(R.id.bga_idCard_back)
    BGASortableNinePhotoLayout mBgaIdCardBack;
    @BindView(R.id.input_email)
    InputView mInputEmail;
    @BindView(R.id.input_phone)
    InputView mInputPhone;
    @BindView(R.id.input_mobile)
    InputView mInputMobile;
    @BindView(R.id.input_address)
    InputView mInputAddress;
    @BindView(R.id.select_gender)
    SelectView mSelectGender;
    @BindView(R.id.input_age)
    InputView mInputAge;
    @BindView(R.id.bga_photo_people)
    BGASortableNinePhotoLayout mBgaPhotoPeople;
    @BindView(R.id.et_remark)
    EditText mEtRemark;
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 2;
    private static final int REQUEST_CODE_CHOOSE_PHOTO_FRONT = 1;
    private static final int REQUEST_CODE_CHOOSE_PHOTO_BACK = 2;
    private static final int REQUEST_CODE_CHOOSE_PHOTO_PEOPLE = 3;
    @BindView(R.id.iv_idCard_front)
    ImageView mIvIdCardFront;
    @BindView(R.id.iv_idCard_back)
    ImageView mIvIdCardBack;
    @BindView(R.id.iv_photo_people)
    ImageView mIvPhotoPeople;

    private BikeUser.BikeUserMessage.Builder mBikeUserMessageBuilder;
    private Context mContext;
    private String mCurrentCardNum = "";
    private boolean mCardNumExist;
    private netCallBack mNetCallBack;
    private String mFrontPicUrl = "";
    private String mBackPicUrl = "";
    private String mPeoplePicUrl = "";

    @Override

    protected int getContentView() {
        return R.layout.fragment_bike_user_info;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mBikeUserMessageBuilder = BikeUser.BikeUserMessage.newBuilder();
        mBgaIdCardFront.setDelegate(this);
        mBgaIdCardBack.setDelegate(this);
        mBgaPhotoPeople.setDelegate(this);
        mInputIdCardNumber.setonInputContentChangeListener(this);
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View
            view, int position, ArrayList<String> models) {
        switch (sortableNinePhotoLayout.getId()) {
            case R.id.bga_idCard_front:
                chooseFrontPhoto();
                break;
            case R.id.bga_idCard_back:
                chooseBackPhoto();
                break;
            case R.id.bga_photo_people:
                choosePeoplePhoto();
                break;
        }
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout,
                                           View view, int position, String model,
                                           ArrayList<String> models) {

        switch (sortableNinePhotoLayout.getId()) {
            case R.id.bga_idCard_front:
                mBgaIdCardFront.removeItem(position);

                break;
            case R.id.bga_idCard_back:
                mBgaIdCardBack.removeItem(position);

                break;
            case R.id.bga_photo_people:
                mBgaPhotoPeople.removeItem(position);

                break;
        }
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View
            view, int position, String model, ArrayList<String> models) {
        switch (sortableNinePhotoLayout.getId()) {
            case R.id.bga_idCard_front:
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent
                                (mContext, mBgaIdCardFront
                                        .getMaxItemCount(), models, models, position, false),
                        REQUEST_CODE_PHOTO_PREVIEW);
                break;
            case R.id.bga_idCard_back:
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent
                                (mContext, mBgaIdCardBack
                                        .getMaxItemCount(), models, models, position, false),
                        REQUEST_CODE_PHOTO_PREVIEW);
                break;
            case R.id.bga_photo_people:
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent
                                (mContext, mBgaPhotoPeople
                                        .getMaxItemCount(), models, models, position, false),
                        REQUEST_CODE_PHOTO_PREVIEW);
                break;
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void chooseFrontPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "DCIM");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, takePhotoDir,
                    mBgaIdCardFront.getMaxItemCount() - mBgaIdCardFront
                            .getItemCount(), null, false), REQUEST_CODE_CHOOSE_PHOTO_FRONT);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void chooseBackPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "DCIM");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, takePhotoDir,
                    mBgaIdCardBack.getMaxItemCount() - mBgaIdCardBack
                            .getItemCount(), null, false), REQUEST_CODE_CHOOSE_PHOTO_BACK);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void choosePeoplePhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "DCIM");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(mContext, takePhotoDir,
                    mBgaPhotoPeople.getMaxItemCount() - mBgaPhotoPeople
                            .getItemCount(), null, false), REQUEST_CODE_CHOOSE_PHOTO_PEOPLE);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照",
                    REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PHOTO_FRONT:
                mBgaIdCardFront.setData(BGAPhotoPickerActivity.getSelectedImages(data));
                break;
            case REQUEST_CODE_CHOOSE_PHOTO_BACK:
                mBgaIdCardBack.setData(BGAPhotoPickerActivity.getSelectedImages(data));
                break;
            case REQUEST_CODE_CHOOSE_PHOTO_PEOPLE:
                mBgaPhotoPeople.setData(BGAPhotoPickerActivity.getSelectedImages(data));
                break;

        }
    }


    public boolean setBikeUserMessage() {
        if (mInputIdCardNumber.getContent().isEmpty()) {
            ToastUtils.showTextToast(mContext, "请输入您的身份证号");
            return false;
        } else if (!StringUtils.identity(mInputIdCardNumber.getContent())) {
            ToastUtils.showTextToast(mContext, "身份证号格式不正确");
            return false;
//        } else if (mCurrentCardNum != null) {
//            if (mCurrentCardNum.equals(mInputIdCardNumber.getContent()) &&
//                    mCardNumExist) {
//                ToastUtils.showTextToast(mContext, "车主已登记，信息不可修改");
//                return false;
//            }
        } else {
            mBikeUserMessageBuilder.setIdCard(mInputIdCardNumber.getContent());
        }
        if (mSelectCompany.getContent().isEmpty()) {
            ToastUtils.showTextToast(mContext, "请选择公司");
            return false;
        }
        if (mInputName.getContent().isEmpty()) {
            ToastUtils.showTextToast(mContext, "请输姓名");
            return false;
        } else {
            mBikeUserMessageBuilder.setName(mInputName.getContent());
        }
        if (mInputPassword.getContent().isEmpty()) {
            ToastUtils.showTextToast(mContext, "请输入您的密码");
            return false;
        } else if (mInputPassword.getContent().length() < 6) {
            ToastUtils.showTextToast(mContext, "密码长度为6-12位");
            return false;
        } else {
            mBikeUserMessageBuilder.setPassword(mInputPassword.getContent());
        }

        if (mBgaIdCardFront.getData().size() != 0) {
            buildFrontPhoto(mBgaIdCardFront);

        }
        if (mBgaIdCardBack.getData().size() != 0) {
            buildBackPhoto(mBgaIdCardBack);

        }
        if (!mInputEmail.getContent().isEmpty()) {
            if (!StringUtils.isEmail(mInputEmail.getContent())) {
                ToastUtils.showTextToast(mContext, "邮箱格式不正确");
                return false;
            } else {
                mBikeUserMessageBuilder.setEmail(mInputEmail.getContent());
            }
        }
        if (!mInputPhone.getContent().isEmpty()) {
            if (!StringUtils.isPhoneNumber(mInputPhone.getContent())) {
                ToastUtils.showTextToast(mContext, "电话格式不正确,示例075512345678，也可以不填写区号");
                return false;
            } else {
                mBikeUserMessageBuilder.setPhone(mInputPhone.getContent());
            }
        }
        if (!mInputMobile.getContent().isEmpty()) {
            if (!StringUtils.isMobileNumber(mInputMobile.getContent())) {
                ToastUtils.showTextToast(mContext, "手机格式不正确");
                return false;
            } else {
                mBikeUserMessageBuilder.setMobile(mInputMobile.getContent());
            }
        }
        if (!mInputAddress.getContent().isEmpty()) {
            mBikeUserMessageBuilder.setAddress(mInputAddress.getContent());
        }
        if (!mSelectGender.getContent().isEmpty()) {
            if (mSelectGender.getContent().equals("女")) {
                mBikeUserMessageBuilder.setSex("0");
            }
            if (mSelectGender.getContent().equals("男")) {
                mBikeUserMessageBuilder.setSex("1");
            }
        }
        if (!mInputAge.getContent().isEmpty()) {
            mBikeUserMessageBuilder.setAge(Double.valueOf(mInputAge.getContent()));
        }
        if (mBgaPhotoPeople.getData().size() != 0) {
            buildPeoplePhoto(mBgaPhotoPeople);
        }
        if (!mEtRemark.getText().toString().isEmpty()) {
            mBikeUserMessageBuilder.setRemarks(mEtRemark.getText().toString());
        }

        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        if (companyUser != null) {
            mBikeUserMessageBuilder.setId(companyUser.getUserId());
        }
        return true;
    }

    @OnClick({R.id.select_gender, R.id.select_company, R.id.iv_idCard_front, R.id.iv_idCard_back,
            R.id.iv_photo_people, R.id.tv_query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_gender:
                String[] gender = {"女", "男"};

                final OptionPicker genderPicker = new OptionPicker((Activity) mContext, gender);
                genderPicker.setCycleDisable(true);
                genderPicker.show();
                genderPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
                        mSelectGender.setContent(item);
                        mBikeUserMessageBuilder.setSex(index + "");
                    }
                });
                break;
            case R.id.select_company:
                startActivity(new Intent(mContext, SelectCompanyListActivity.class));
                break;
            case R.id.iv_idCard_front:
                startPhotoPreview(mFrontPicUrl);
                break;
            case R.id.iv_idCard_back:
                startPhotoPreview(mBackPicUrl);
                break;
            case R.id.iv_photo_people:
                startPhotoPreview(mPeoplePicUrl);
                break;
            case R.id.tv_query:
                if (!checkIdCardNoFormat()) {
                    return;
                }
                doSocket();
                break;
        }


    }

    private boolean checkIdCardNoFormat() {
        if (mInputIdCardNumber.getContent().isEmpty()) {
            ToastUtils.showTextToast(mContext, "请输入您的身份证号");
            return false;
        } else if (!StringUtils.identity(mInputIdCardNumber.getContent())) {
            ToastUtils.showTextToast(mContext, "身份证号格式不正确");
            return false;

        }
        return true;
    }

    private void startPhotoPreview(String url) {
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.setClass(mContext, PhotoPreviewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        dlgWaiting.setCancelable(false);
        mCurrentCardNum = mInputIdCardNumber.getContent();
        final
        GetBikeUserRequest.GetBikeUserRequestMessage.Builder
                requestMessage = GetBikeUserRequest.GetBikeUserRequestMessage
                .newBuilder();
        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        if (companyUser != null) {
            requestMessage.setSession(companyUser.getSession());
        }
        BikeUser.BikeUserMessage bikeUserMessage = BikeUser.BikeUserMessage.newBuilder().setIdCard
                (mInputIdCardNumber.getContent()).build();
        requestMessage.setBikeUserMessage(bikeUserMessage);
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_BIKE_BY_IDCARD_NO, requestMessage.build()
                        .toByteArray());
            }

        }.start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.COMMAND_ID_BIKE_BY_IDCARD_NO) {
                GetBikUserResponse.GetBikeUserResponseMessage responseMessage = GetBikUserResponse
                        .GetBikeUserResponseMessage
                        .parseFrom(eventPackage.getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (responseMessage.getErrorMsg().getErrorCode() == 0) {
                    showIdCardNumExistDialog();
                    mCardNumExist = true;
                    setAllEnable(false);

                    BikeUser.BikeUserMessage bikeUserMessage = responseMessage.getBikeUserMessage
                            (0);

                    mSelectCompany.setContent(bikeUserMessage.getCompanyName());
                    mBikeUserMessageBuilder.setCompanyId(bikeUserMessage.getCompanyId());
                    mInputName.setContent(bikeUserMessage.getName());
                    mInputPassword.setContent("111111");

                    if (hasPhoto(bikeUserMessage.getIdFrontPicPath())) {
                        mFrontPicUrl = bikeUserMessage.getIdFrontPicPath();
                        mIvIdCardFront.setVisibility(View.VISIBLE);
                        mBgaIdCardFront.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(mFrontPicUrl)
                                .error(R.mipmap.pic_load_err)
                                .into(mIvIdCardFront);
                    } else {
                        mIvIdCardFront.setVisibility(View.GONE);
                        mBgaIdCardFront.setVisibility(View.VISIBLE);
                    }


                    if (hasPhoto(bikeUserMessage.getIdBackPicPath())) {
                        mBackPicUrl = bikeUserMessage.getIdBackPicPath();
                        mIvIdCardBack.setVisibility(View.VISIBLE);
                        mBgaIdCardBack.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(mBackPicUrl)
                                .error(R.mipmap.pic_load_err)
                                .into(mIvIdCardBack);
                    } else {
                        mIvIdCardBack.setVisibility(View.GONE);
                        mBgaIdCardBack.setVisibility(View.VISIBLE);
                    }


                    if (hasPhoto(bikeUserMessage.getPhoto())) {
                        mPeoplePicUrl = bikeUserMessage.getPhoto();
                        mIvPhotoPeople.setVisibility(View.VISIBLE);
                        mBgaPhotoPeople.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(mPeoplePicUrl)
                                .error(R.mipmap.pic_load_err)
                                .into(mIvPhotoPeople);
                    } else {
                        mIvPhotoPeople.setVisibility(View.GONE);
                        mBgaPhotoPeople.setVisibility(View.VISIBLE);
                    }

                    mInputEmail.setContent(bikeUserMessage.getEmail());
                    mInputPhone.setContent(bikeUserMessage.getPhone());
                    mInputMobile.setContent(bikeUserMessage.getMobile());
                    mInputAddress.setContent(bikeUserMessage.getAddress());
                    if (bikeUserMessage.getSex().equals("0")) {
                        mSelectGender.setContent("女");

                    }
                    if (bikeUserMessage.getSex().equals("1")) {
                        mSelectGender.setContent("男");

                    }
                    if (bikeUserMessage.getAge() != 0) {
                        mInputAge.setContent(bikeUserMessage.getAge() + "");
                    }
                    mEtRemark.setText(bikeUserMessage.getRemarks());

                } else {
                    if (responseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    clearAllExceptIdCad();
                    ToastUtils.showTextToast(mContext,"身份证号尚未登记");

                    mCardNumExist = false;
                    if (mNetCallBack != null) {
                        mNetCallBack.netFinish(false);
                    }
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除所有数据
     */
    public void clearAll() {
        setPhotoEnable(true);
        clearBikeUser();
        setAllEnable(true);
    }

    /**
     * 清除身份证以外所有数据
     */
    public void clearAllExceptIdCad() {
        setPhotoEnable(true);
        clearBikeUserExceptIdCard();
        setAllEnable(true);
    }

    /**
     * 设置图片是否可以选择
     */
    private void setPhotoEnable(boolean enable) {
        if (enable) {
            mIvIdCardBack.setVisibility(View.GONE);
            mBgaIdCardBack.setVisibility(View.VISIBLE);
            mIvIdCardFront.setVisibility(View.GONE);
            mBgaIdCardFront.setVisibility(View.VISIBLE);
            mIvPhotoPeople.setVisibility(View.GONE);
            mBgaPhotoPeople.setVisibility(View.VISIBLE);
            mBgaPhotoPeople.setData(null);
            mBgaIdCardFront.setData(null);
            mBgaIdCardBack.setData(null);
        } else {
            mIvIdCardBack.setVisibility(View.VISIBLE);
            mBgaIdCardBack.setVisibility(View.GONE);
            mIvIdCardFront.setVisibility(View.VISIBLE);
            mBgaIdCardFront.setVisibility(View.GONE);
            mIvPhotoPeople.setVisibility(View.VISIBLE);
            mBgaPhotoPeople.setVisibility(View.GONE);
        }

    }

    private void clearBikeUser() {
        mBikeUserMessageBuilder.clear();
        mInputIdCardNumber.setContent("");
        mSelectCompany.setContent("");
        mInputName.setContent("");
        mInputPassword.setContent("");
        mInputEmail.setContent("");
        mInputPhone.setContent("");
        mInputMobile.setContent("");
        mInputAddress.setContent("");
        mSelectGender.setContent("");
        mInputAge.setContent("");
        mEtRemark.setText("");
    }

    private void clearBikeUserExceptIdCard() {
        mBikeUserMessageBuilder.clear();
        mSelectCompany.setContent("");
        mInputName.setContent("");
        mInputPassword.setContent("");
        mInputEmail.setContent("");
        mInputPhone.setContent("");
        mInputMobile.setContent("");
        mInputAddress.setContent("");
        mSelectGender.setContent("");
        mInputAge.setContent("");
        mEtRemark.setText("");
    }

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
            mBikeUserMessageBuilder.setIdFrontPicFileInfo(fileInfoMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildBackPhoto(BGASortableNinePhotoLayout bgaPhoto) {
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
            mBikeUserMessageBuilder.setIdBackPicFileInfo(fileInfoMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildPeoplePhoto(BGASortableNinePhotoLayout bgaPhoto) {
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
            mBikeUserMessageBuilder.setPhotoFileInfo(fileInfoMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEventMainThread(EventBusMsg eventPackage) {
        super.onEventMainThread(eventPackage);
        if (eventPackage.getMsgType().equals(EventBusMsg.MsgType.MSG_COMPANY_ID)) {
            Map<String, String> map = eventPackage.getValue();
            String companyId = map.get("companyId");
            String companyName = map.get("companyName");
            mSelectCompany.setContent(companyName);
            mBikeUserMessageBuilder.setCompanyId(Long.valueOf(companyId));
        }
    }

    private boolean hasPhoto(String url) {

        return !url.endsWith("/");
    }

    public BikeUser.BikeUserMessage build() {
        return mBikeUserMessageBuilder.build();
    }

    private void setAllEnable(boolean enable) {
        mSelectCompany.setEnabled(enable);
        mInputName.setInputEnable(enable);
        mInputPassword.setInputEnable(enable);
        mInputPassword.setContent("");
        mInputEmail.setInputEnable(enable);
        mInputPhone.setInputEnable(enable);
        mInputMobile.setInputEnable(enable);
        mInputAddress.setInputEnable(enable);
        mSelectGender.setEnabled(enable);
        mInputAge.setInputEnable(enable);
        mEtRemark.setEnabled(enable);
        mBgaIdCardFront.setPlusEnable(enable);
        mBgaIdCardBack.setPlusEnable(enable);
        mBgaPhotoPeople.setPlusEnable(enable);

    }

    @Override
    public void ContentChange(InputView inputView, String content) {
        switch (inputView.getId()) {
            case R.id.input_idCard_number:
                if (content.length() == 18) {
                    if (!StringUtils.identity(content)) {
                        ToastUtils.showTextToast(mContext, "身份证号格式不正确");
                    } else {
                        doSocket();
                    }

                }
                break;
        }
    }

    public interface netCallBack {
        void netFinish(boolean exist);

    }

    public void checkIdCardNumber(netCallBack netCallBack) {
        if (!checkIdCardNoFormat()) {
            return;
        }
        mNetCallBack = netCallBack;
        if (mCurrentCardNum.equals(mInputIdCardNumber.getContent())) {
            mNetCallBack.netFinish(mCardNumExist);
        } else {
            doSocket();
        }
    }

    public void unCheckIdCardNumber() {
        mNetCallBack = null;
    }

    private void showIdCardNumExistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("车主已登记，信息不可修改");
        builder.setTitle("提示");
        builder.setPositiveButton("我知道了", new DialogInterface
                .OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("重新填写身份证号码", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearAll();
            }
        });
        builder.create().show();
    }
}
