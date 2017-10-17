/**
 * 
 */
package com.yzh.rfidbike_police.base;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yzh.rfidbike_police.R;
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
 * 
 */
public abstract class BaseV4Fragment extends Fragment implements FinalDb.DbUpdateListener {
	/** 当前页的布局 */
	protected View view;
	protected FinalDb db;
	protected FinalBitmap fBitmap;
	protected FinalHttp fHttp;
	protected DialogUtils dlgWaiting;
	protected Context mcontext;
	protected static final int MSG_cannt_get_data = 2000;
	protected Handler mDlgWaitingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {

					case MSG_cannt_get_data:
						if(dlgWaiting.isShowing())
						{
							dlgWaiting.dismiss();
							ToastUtils.showTextToast(getContext(), getResources().getString(R.string.network_error));
						}
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
		mcontext = getActivity();
        dlgWaiting = DialogUtils.createDialog(getContext(), DialogUtils.REFRESH);
        dlgWaiting.setCanceledOnTouchOutside(false);
        //		registerNetworkChange();
        EventBus.getDefault().register(this);
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
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(getContentView(),container,false);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		init(view);
		loadDatas();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getBundle(getArguments());
	}

	//获得bundle数据
	protected void getBundle(Bundle bundle){

	}

	protected abstract int getContentView();
	//初始化
	protected void init(View view) {

	}
	//加载数据
	protected void loadDatas() {
	}


	//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		String msg = "onEventMainThread收到了消息：" + eventPackage.getCommandId() + eventPackage.getCommandData();
		LogUtils.logMessage("harvic", msg);

	}

	public void onEventMainThread(SocketMsg eventPackage) {

		String msg = "onEventMainThread收到了消息：" + eventPackage.getCommandId() + eventPackage.getSocketMsg();
		LogUtils.logMessage("harvic", msg);

	}

	public void onEventMainThread(EventBusMsgMessage eventPackage) {

		//		String msg = "onEventMainThread收到了消息：" + eventPackage.getCommandId()+eventPackage.getCommandData();
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

	protected void doSocket()
	{
		dlgWaiting.show();
		mDlgWaitingHandler.sendEmptyMessageDelayed(MSG_cannt_get_data, App.WAITTING_SECOND);
	}

}
