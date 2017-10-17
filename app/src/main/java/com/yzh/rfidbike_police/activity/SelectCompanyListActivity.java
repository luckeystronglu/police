package com.yzh.rfidbike_police.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yzh.rfid.app.request.GetCompanyListRequest;
import com.yzh.rfid.app.response.Company;
import com.yzh.rfid.app.response.GetCompanyListResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.adapter.CompanyAdapter;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.model.EventBusMsg;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.ActivityManager;
import com.yzh.rfidbike_police.view.widgets.Header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class SelectCompanyListActivity extends BaseActivity implements Header.headerListener {

    @BindView(R.id.rv_subCompany)
    RecyclerView mRvCompany;
    @BindView(R.id.refresh_layout)
    PtrClassicFrameLayout mRefreshLayout;
    @BindView(R.id.header)
    Header mHeader;
    private List<Company.CompanyMessage> mCompanyList;
    private CompanyAdapter mCompanyAdapter;
    private String mSession;
    private long mCompanyId;
    private int mUserId;
    private Context mContext = this;
    private boolean mPullToFresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_company);
        ButterKnife.bind(this);
        ActivityManager.getInstance().addActivity(this);

        mHeader.setListener(this);
        Intent intent = getIntent();
        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        mSession = companyUser.getSession();
        mUserId = companyUser.getUserId();
        mCompanyId = intent.getLongExtra("companyId", 0);

        mRvCompany.setLayoutManager(new LinearLayoutManager(this));
        mCompanyAdapter = new CompanyAdapter(mRvCompany);
        mRvCompany.setAdapter(mCompanyAdapter);
        doSocket();
        mRefreshLayout.setPtrHandler(
                new PtrHandler() {
                    @Override
                    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View
                            header) {

                        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content,
                                header);
                    }

                    @Override
                    public void onRefreshBegin(PtrFrameLayout frame) {
                        mPullToFresh = true;
                        doSocket();
                    }
                }
        );
        mCompanyAdapter.setOnRVItemClickListener(new BGAOnRVItemClickListener() {
            @Override
            public void onRVItemClick(ViewGroup parent, View itemView, int position) {
                if (mCompanyAdapter.getItem(position).getHasChild().equals("0")) {
                    EventBusMsg msg = new EventBusMsg();
                    msg.setMsgType(EventBusMsg.MsgType.MSG_COMPANY_ID);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("companyId", mCompanyAdapter.getItem(position).getId() + "");
                    map.put("companyName", mCompanyAdapter.getItem(position).getName());
                    msg.setValue(map);
                    EventBus.getDefault().post(msg);
                    ActivityManager.getInstance().finishActivities(SelectCompanyListActivity
                            .class);

                }
                if (mCompanyAdapter.getItem(position).getHasChild().equals("1")) {
                    Intent intent = new Intent();
                    CompanyUser companyUser = CompanyUser.getCurUser(mContext);

                    intent.putExtra("userId", companyUser.getUserId());
                    intent.putExtra("session", companyUser.getSession());
                    intent.putExtra("companyId", mCompanyAdapter.getItem(position).getId());
                    intent.setClass(mContext, SelectCompanyListActivity.class);
                    startActivity(intent);
                }
            }

        });
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        if (mPullToFresh && dlgWaiting.isShowing()) {
            dlgWaiting.dismiss();
        }
        final GetCompanyListRequest.GetCompanyListRequestMessage.Builder requestMessage =
                GetCompanyListRequest
                        .GetCompanyListRequestMessage.newBuilder();
        requestMessage.setSession(mSession);
        requestMessage.setCompanyId(mCompanyId);
        requestMessage.setUserId(mUserId);
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.GET_COMPANY_LIST, requestMessage.build()
                        .toByteArray());
            }

        }.start();

    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            if (eventPackage.getCommandId() == SocketAppPacket.GET_COMPANY_LIST) {
                GetCompanyListResponse.GetCompanyListResponseMessage responseMessage =
                        GetCompanyListResponse
                                .GetCompanyListResponseMessage.parseFrom(eventPackage
                                .getCommandData());
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.refreshComplete();
                }
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (responseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(SelectCompanyListActivity.this, responseMessage.getErrorMsg()
                            .getErrorMsg(), Toast.LENGTH_LONG).show();
                    if (responseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    mCompanyList = responseMessage.getCompanyMessageList();
                    mCompanyAdapter.setData(mCompanyList);
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        mPullToFresh = false;

    }

    @Override
    protected void getDataErr() {
        super.getDataErr();
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.refreshComplete();
        }
    }

    @Override
    public void onClickLeftIcon() {
        super.onBackPressed();
    }

    @Override
    public void onClickRightText() {

    }
    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        logMessage("--->onDestroy()...");
        super.onDestroy();
        mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
        App.activityList.remove(this);
    }

}
