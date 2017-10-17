/**
 *
 */
package com.yzh.rfidbike_police.base;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.model.EventBusMsg;
import com.yzh.rfidbike_police.model.EventBusMsgMessage;
import com.yzh.rfidbike_police.socket.SocketAppPacket;
import com.yzh.rfidbike_police.socket.SocketMsg;
import com.yzh.rfidbike_police.util.DialogUtils;
import com.yzh.rfidbike_police.util.logort.LogUtils;
import com.yzh.rfidbike_police.util.logort.ToastUtils;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;

import de.greenrobot.event.EventBus;


/**
 * @author WT00111
 */
public abstract class BaseFragment extends Fragment implements FinalDb.DbUpdateListener {

    private FragmentManager fragmentManager;
    private Fragment showFragment;
    /**
     * 当前页的布局
     */
    protected View view;
    protected FinalDb db;
    protected FinalBitmap fBitmap;
    protected FinalHttp fHttp;
    protected DialogUtils dlgWaiting;
    protected static final int MSG_cannt_get_data = 2000;
    private static final String TAG = BaseFragment.class.getName();
    protected Handler mDlgWaitingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {

                    case MSG_cannt_get_data:
                        if (dlgWaiting.isShowing()) {
                            dlgWaiting.dismiss();
                            ToastUtils.showTextToast(getActivity(), getResources().getString(R
                                    .string.network_error));
                        }
                        getDataErr();

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化FragmentManager对象
        fragmentManager = getActivity().getSupportFragmentManager();
        dlgWaiting = DialogUtils.createDialog(getActivity(), DialogUtils.REFRESH);
        dlgWaiting.setCanceledOnTouchOutside(false);
        //		registerNetworkChange();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db = FinalDb.create(activity);
        fBitmap = FinalBitmap.create(activity);
        fHttp = new FinalHttp();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
        loadDatas();
    }
    protected void logMessage(String log) {
        LogUtils.logMessage(getLogTAG(), log);
    }
    protected String getLogTAG() {
        return TAG;
    }


    //管理fragment的显示与隐藏的方法
    protected void fragmentManager(int resId, Fragment fragment, String tag){
        //开始事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //隐藏正在显示的fragment对象
        if (showFragment != null){
            fragmentTransaction.hide(showFragment);
        }
        Fragment loadFragment = fragmentManager.findFragmentByTag(tag);
        if (loadFragment != null){
            fragmentTransaction.show(loadFragment);
        }else {
            loadFragment = fragment;
            fragmentTransaction.add(resId,fragment,tag);
        }
        showFragment = loadFragment;
        fragmentTransaction.commit();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBundle(getArguments());
    }

    //获得bundle数据
    protected void getBundle(Bundle bundle) {

    }

    protected void getDataErr() {

    }


    protected abstract int getContentView();

    /**
     * 查找页面布局ID所对应的控件对象，不用强制转换
     * @param resId
     * @param <T>
     * @return
     */
    protected  <T> T findViewByIds(int resId){
        return (T) getActivity().findViewById(resId);
    }

    //初始化
    protected void init(View view) {

    }

    //加载数据
    protected void loadDatas() {
    }


    //	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
// savedInstanceState) {
//		initView();
//		initData();
//		initEvent();
//		return view;
//	}

//	public int setContentView(int layoutId) {
//		view = LayoutInflater.from(getActivity()).inflate(layoutId, null);
//		return layoutId;
//	}

    public View findViewById(int id) {
        return view.findViewById(id);
    }


    public void onEventMainThread(SocketAppPacket eventPackage) {
        String msg = "onEventMainThread收到了消息：" + eventPackage.getCommandId() + eventPackage
                .getCommandData();
        LogUtils.logMessage("harvic", msg);

    }

    public void onEventMainThread(SocketMsg eventPackage) {

        String msg = "onEventMainThread收到了消息：" + eventPackage.getCommandId() + eventPackage
                .getSocketMsg();
        LogUtils.logMessage("harvic", msg);

    }

    public void onEventMainThread(EventBusMsg eventPackage) {

    }

    public void onEventMainThread(EventBusMsgMessage eventPackage) {

        //		String msg = "onEventMainThread收到了消息：" + eventPackage.getCommandId()+eventPackage
        // .getCommandData();
        //		LogUtils.logMessage("harvic", msg);

    }

    public void log(String str) {
        LogUtils.logMessage("Mine", str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        try {

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    protected void doSocket() {
        dlgWaiting.show();
        mDlgWaitingHandler.sendEmptyMessageDelayed(MSG_cannt_get_data, App.WAITTING_SECOND);
    }

}
