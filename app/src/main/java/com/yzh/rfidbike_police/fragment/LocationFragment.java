package com.yzh.rfidbike_police.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yzh.rfid.app.request.GetBikeByPlateNumberRequest;
import com.yzh.rfid.app.response.Bike;
import com.yzh.rfid.app.response.GetBikesResponse;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.BaseStationMapActivity;
import com.yzh.rfidbike_police.activity.DeviceAdapter;
import com.yzh.rfidbike_police.activity.LoginActivity;
import com.yzh.rfidbike_police.activity.MessageCenterPoliceActivity;
import com.yzh.rfidbike_police.activity.SearchDevice;
import com.yzh.rfidbike_police.activity.SearchDeviceAdapter;
import com.yzh.rfidbike_police.adapter.AdapterAbnormalBike;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseApplication;
import com.yzh.rfidbike_police.base.BaseFragment;
import com.yzh.rfidbike_police.model.CompanyUser;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketClient;
import com.yzh.rfidbike_police.util.logort.ToastUtils;
import com.yzh.rfidbike_police.view.widgets.Header;

import net.tsz.afinal.FinalDb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;

/**
 * Created by Administrator on 2017/3/16.
 * 实时定位Fragment
 */

public class LocationFragment extends BaseFragment {

    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.header)
    Header mHeader;
    @BindView(R.id.rv_tip)
    RecyclerView mRvTip;
    @BindView(R.id.rv_device)
    RecyclerView mRvDevice;
    @BindView(R.id.ll_abnormal_bike_prompt)
    LinearLayout ll_abnormal_bike_prompt;
    @BindView(R.id.tv_no_abnormal_prompt)
    TextView tv_no_abnormal_prompt;
    @BindView(R.id.btn_abnormal)
    Button btnAbnormal;


//    private DeviceAdapter mAbnormalBikeAdapter;
    private SearchDeviceAdapter mSearchDeviceAdapter;


    @BindView(R.id.lv_abnormal_bike)
    PullToRefreshListView lvAbnormal;


    private AdapterAbnormalBike mAbnormalBikeAdapter;
    List<Bike.BikeMessage> abnormalBikeList;

    private Context mContext;
    private boolean isFirst = false;
    private int requestType=0;
    //0:其他方式,1:下拉刷新

    private Handler handler = new Handler() {
        @SuppressWarnings({"unused", "unchecked"})
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_cannt_get_data:
                        ToastUtils.showTextToast(getContext(), getResources().getString(R.string.get_data_error));
                        lvAbnormal.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                lvAbnormal.onRefreshComplete();
                            }
                        }, 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //		super.handleMessage(msg);
        }
    };




    @Override
    protected int getContentView() {
        return R.layout.fragment_local;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void init(View view) {
        super.init(view);
        mContext = getActivity();
        ButterKnife.bind(this, view);
//        mAbnormalBikeAdapter = new DeviceAdapter(mRvDevice);
//        mRvDevice.setLayoutManager(new LinearLayoutManager(mContext));
//        mRvDevice.setAdapter(mAbnormalBikeAdapter);

        mSearchDeviceAdapter = new SearchDeviceAdapter(mContext, mRvTip);
        mRvTip.setLayoutManager(new LinearLayoutManager(mContext));
        mRvTip.setAdapter(mSearchDeviceAdapter);

        abnormalBikeList =new ArrayList<Bike.BikeMessage>();
        mAbnormalBikeAdapter=new AdapterAbnormalBike(mContext,abnormalBikeList);
        lvAbnormal.setAdapter(mAbnormalBikeAdapter);
        lvAbnormal.setMode(PullToRefreshBase.Mode.PULL_FROM_START);// 同时支持上拉下拉
        requestType=0;
        getAbnormalBikeList();


        tv_no_abnormal_prompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestType=0;
                getAbnormalBikeList();
            }
        });
        final List<SearchDevice> deviceList = getSearchInfoList();
        mSearchDeviceAdapter.setData(deviceList);


        mSearchDeviceAdapter.setOnRVItemClickListener(new BGAOnRVItemClickListener() {
            @Override
            public void onRVItemClick(ViewGroup parent, View itemView, int position) {
                if (position != 0) {
                    mEtSearch.setText(String.valueOf(mSearchDeviceAdapter.getItem(position)
                            .getDeviceIdDecimal()));
                    doSocket();
                }
            }
        });

        mSearchDeviceAdapter.setDeleteListener(new SearchDeviceAdapter.OnDeleteListener() {
            @Override
            public void onDelete(SearchDevice device, int position) {
                deleteSearchInfo(device);

            }

            @Override
            public void onDeleteAll() {
                deleteAllSearchInfo();

            }
        });

        lvAbnormal.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                setUpdateTime(refreshView);
                mEtSearch.setText("");
                requestType=1;
                getAbnormalBikeList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (isLastPage) {
//                    ToastUtils.showTextToast(getContext(), getResources().getString(R.string.this_is_last_page));
//                    lv_alertmsg.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            lv_alertmsg.onRefreshComplete();
//                        }
//                    }, 1000);
//                    return;
//                }
//                setUpdateTime(refreshView);
//                dataFleshType = 2;
//                pageNum++;
//
//                lv_alertmsg.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        lv_alertmsg.onRefreshComplete();
//                    }
//                }, 1000);
//
//                //获取listView列表数据
//                doRefreshSocket();

            }
        });
        initListViewTipText();
        btnAbnormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.clearFocus();
                requestType=0;
                getAbnormalBikeList();
                mEtSearch.setText("");
            }
        });

//lvAbnormal.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        requestType=0;
//        getAbnormalBikeList();
//    }
//});
    lvAbnormal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=1) {
                    Bike.BikeMessage message = (Bike.BikeMessage) mAbnormalBikeAdapter.getItem(position - 1);
                    Intent intent = new Intent(getActivity(), BaseStationMapActivity.class);
                    intent.putExtra("DeviceMessage", message);
                    startActivity(intent);
                }
            }
        });


        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mEtSearch.getText().length() > 0) {
                    doSocket();
                    return true;
                }
                return false;
            }
        });
        mEtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    setFocus();
                }else
                {
                    setUnFocus();
                }
            }
        });


        if (BaseApplication.getInstance().needGoToMessageCenter) {
            Intent intentNew = new Intent();
            intentNew.setClass(mContext.getApplicationContext(), MessageCenterPoliceActivity.class);
            intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.getApplicationContext().startActivity(intentNew);
        }


    }


    /**
     * 初始化列表刷新时的提示文本
     */
    private void initListViewTipText() {
        // 设置上拉刷新文本
        ILoadingLayout startLabels = lvAbnormal.getLoadingLayoutProxy(true,
                false);
        startLabels.setPullLabel(getResources().getString(R.string.pull_down_refresh));
        startLabels.setReleaseLabel(getResources().getString(R.string.release_refresh));
        startLabels.setRefreshingLabel(getResources().getString(R.string.refreshing));

        // 设置下拉刷新文本
        ILoadingLayout endLabels = lvAbnormal.getLoadingLayoutProxy(false, true);
//        if (isLastPage) {
//            endLabels.setPullLabel(getResources().getString(R.string.pull_up_load_more));
//            endLabels.setReleaseLabel(getResources().getString(R.string.release_load_more));
//            endLabels.setRefreshingLabel(getResources().getString(R.string.last_page));
//        } else {
            endLabels.setPullLabel(getResources().getString(R.string.pull_up_load_more));
            endLabels.setReleaseLabel(getResources().getString(R.string.release_load_more));
            endLabels.setRefreshingLabel(getResources().getString(R.string.loading_more));
//        }

    }


    /**
     * 设置更新时间
     *
     * @param refreshView
     */
    private void setUpdateTime(PullToRefreshBase refreshView) {
        String label = getStringDateNow();
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }


    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDateNow() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    @OnClick({R.id.ll_search})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_search:
                BaseApplication.getInstance().currentFragment = "LocationFragment";
                doSocket();
                break;
        }
    }

       void  getAbnormalBikeList()
    {
      if(requestType==0)
      {
          super.doSocket();
      }else
      {
          handler.sendEmptyMessageDelayed(MSG_cannt_get_data, App.WAITTING_SECOND);
      }
        BaseApplication.getInstance().currentFragment = "LocationFragment";
        final GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage
                .Builder requestMessage =
                GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage.newBuilder();
        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        requestMessage.setSession(companyUser.getSession());
        requestMessage.setPlateNumber(mEtSearch.getText().toString());
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_POLICE_GET_UNNORMAL_BIKE_BY_USER,
                        requestMessage.build()
                                .toByteArray());
            }

        }.start();
    }

    @Override
    protected void doSocket() {
        super.doSocket();
        final GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage
                .Builder requestMessage =
                GetBikeByPlateNumberRequest.GetBikeByPlateNumberRequestMessage.newBuilder();
        CompanyUser companyUser = CompanyUser.getCurUser(mContext);
        requestMessage.setSession(companyUser.getSession());
        requestMessage.setPlateNumber(mEtSearch.getText().toString());
        new Thread() {
            public void run() {
                SocketClient client;
                client = SocketClient.getInstance();
                client.send(SocketAppPacket.COMMAND_ID_POLICE_GET_BIKE_BY_PLATE_NUMBER,
                        requestMessage.build()
                .toByteArray());
            }

        }.start();
    }

    @Override
    public void onEventMainThread(SocketAppPacket eventPackage) {
        try {
            super.onEventMainThread(eventPackage);
            btnAbnormal.setVisibility(View.GONE);
            if (eventPackage.getCommandId() == SocketAppPacket
                    .COMMAND_ID_POLICE_GET_BIKE_BY_PLATE_NUMBER && BaseApplication.getInstance().currentFragment.equals("LocationFragment")) {
                GetBikesResponse.GetBikesResponseMessage responseMessage =
                        GetBikesResponse.GetBikesResponseMessage.parseFrom(eventPackage
                                .getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }

                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (responseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(getContext(), responseMessage.getErrorMsg().getErrorMsg(), Toast.LENGTH_SHORT).show();
                    if (responseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                } else {
                    setUnFocus();
                        if (responseMessage.getBikeCount() > 0) {
                            abnormalBikeList.clear();
                            abnormalBikeList.addAll(responseMessage.getBikeList());
                            mAbnormalBikeAdapter.notifyDataSetChanged();
                            ll_abnormal_bike_prompt.setVisibility(View.GONE);
                            lvAbnormal.setVisibility(View.VISIBLE);
                            saveSearchInfo();
                        }else {
                            ll_abnormal_bike_prompt.setVisibility(View.VISIBLE);
                            lvAbnormal.setVisibility(View.GONE);
                        }
                }




            }else if(eventPackage.getCommandId() == SocketAppPacket
                    .COMMAND_ID_POLICE_GET_UNNORMAL_BIKE_BY_USER && BaseApplication.getInstance().currentFragment.equals("LocationFragment"))
            {
                GetBikesResponse.GetBikesResponseMessage responseMessage =
                        GetBikesResponse.GetBikesResponseMessage.parseFrom(eventPackage
                                .getCommandData());
                if (dlgWaiting.isShowing()) {
                    dlgWaiting.dismiss();
                }
                lvAbnormal.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvAbnormal.onRefreshComplete();
                    }
                }, 1000);
                mDlgWaitingHandler.removeMessages(MSG_cannt_get_data);
                if (responseMessage.getErrorMsg().getErrorCode() != 0) {
                    Toast.makeText(getContext(), responseMessage.getErrorMsg().getErrorMsg(), Toast.LENGTH_SHORT).show();
                    if (responseMessage.getErrorMsg().getErrorCode() == 20003) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                .FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    if (responseMessage.getBikeCount() > 0) {
                        abnormalBikeList.clear();
                        abnormalBikeList.addAll(responseMessage.getBikeList());
                        mAbnormalBikeAdapter.notifyDataSetChanged();
                        ll_abnormal_bike_prompt.setVisibility(View.GONE);
                        lvAbnormal.setVisibility(View.VISIBLE);
                    }else {
                        ll_abnormal_bike_prompt.setVisibility(View.VISIBLE);
                        lvAbnormal.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSearchInfo(final SearchDevice device) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FinalDb db = FinalDb.create(mContext);
                    db.delete(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteAllSearchInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FinalDb db = FinalDb.create(mContext);
                    db.deleteAll(SearchDevice.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setUnFocus() {
        mEtSearch.clearFocus();
        mRvTip.setVisibility(View.GONE);
        lvAbnormal.setVisibility(View.VISIBLE);
    }

    private void saveSearchInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FinalDb db = FinalDb.create(mContext);
                SearchDevice device = new SearchDevice();
                device.setDeviceIdDecimal(mEtSearch.getEditableText().toString());
                device.setTime(System.currentTimeMillis());
                try {
                    List<SearchDevice> list = db.findAllByWhere(SearchDevice.class,
                            "deviceIdDecimal='" + device.getDeviceIdDecimal() + "'");
                    if (list.size() > 0) {
                        return;
                    } else {
                        db.save(device);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setFocus() {
        mEtSearch.requestFocus();
        lvAbnormal.setVisibility(View.GONE);
        ll_abnormal_bike_prompt.setVisibility(View.GONE);
        btnAbnormal.setVisibility(View.VISIBLE);
        mRvTip.setVisibility(View.VISIBLE);
        mSearchDeviceAdapter.notifyDataSetChanged();
    }

    private List<SearchDevice> getSearchInfoList(String keyWord) {

        FinalDb db = FinalDb.create(mContext);
        List<SearchDevice> devices = new ArrayList<>();
        List<SearchDevice> deviceList = db.findAll(SearchDevice.class, "time DESC");
        for (SearchDevice searchDevice : deviceList) {
            if (searchDevice.getDeviceIdDecimal().contains(keyWord)) {
                devices.add(searchDevice);
            }
        }
        return devices;
    }

    private List<SearchDevice> getSearchInfoList() {
        FinalDb db = FinalDb.create(mContext);
        List<SearchDevice> deviceList = db.findAll(SearchDevice.class, "time DESC");
        return deviceList;
    }
}
