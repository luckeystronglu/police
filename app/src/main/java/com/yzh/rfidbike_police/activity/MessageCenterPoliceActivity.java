package com.yzh.rfidbike_police.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.adapter.MsgViewPagerAdapter;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.base.BaseApplication;
import com.yzh.rfidbike_police.fragment.AlertMsgFragment;
import com.yzh.rfidbike_police.fragment.SystemMsgFragment;
import com.yzh.rfidbike_police.model.EventBusMsg;
import com.yzh.rfidbike_police.model.EventBusMsgMessage;
import com.yzh.rfidbike_police.model.EventBusMsgPush;
import com.yzh.rfidbike_police.view.widgets.Header;

import de.greenrobot.event.EventBus;


/**
 * Created by appadmin on 2016/1/13.
 * 运维系统消息
 */

public class MessageCenterPoliceActivity extends BaseActivity implements Header.headerListener, TabLayout.OnTabSelectedListener {

    private Header header;
    private ViewPager msgvp;
    private TabLayout tabLayout;
    private String[] tabs = {"报警消息", "系统消息"};


    private TextView delete_all_tv;
    private AlertDialog deleteAlertDialog2;
    private TextView tv_cancel, tv_sure;
    private ImageView iv_cancel;

    private int pushmsglistsize = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_msg_center);
        initView();
//        initDialog1();
        initDialog2();
    }


    @Override
    protected void onResume() {
        super.onResume();
        selectTab();
    }


    //头部标题栏返回键的点击接口回调
    @Override
    public void onClickLeftIcon() {
        finish();
    }

    @Override
    public void onClickRightText() {

    }


    private void initView() {
        delete_all_tv = findViewByIds(R.id.delete_all_tv);
        header = findViewByIds(R.id.readcard_header);
        header.setTitle("消息中心");
        header.setListener(this);

        msgvp = (ViewPager) findViewById(R.id.msg_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.msg_tablayout);
        initViewpager(msgvp);
        tabLayout.setOnTabSelectedListener(this);
//        msgvp.setOffscreenPageLimit(1);
        //此处的方法是为了页面和标题联动
        tabLayout.setupWithViewPager(msgvp);
//        tabLayout.getTabAt(0).select();

        //消息来到，当前界面不是消息界面会触发
//        Intent intent=new Intent();
//        EventBusMsgPush msgPush= (EventBusMsgPush) intent.getSerializableExtra("EventBusMsgPush");
//        if(intent!=null&&msgPush!=null)
//        {
//            tabLayout.getTabAt(msgPush.getMsgType()-1).select();
//        }else
//        {
//            tabLayout.getTabAt(0).select();
//        }

    }

    private void selectTab() {
        //通知点击后，会触发或是消息来到，当前界面不是消息界面会触发
        if (BaseApplication.getInstance().needGoToMessageCenter) {
            BaseApplication.getInstance().needGoToMessageCenter = false;
            tabLayout.getTabAt(BaseApplication.getInstance().MessageType - 1).select();
        }
    }



    private void initViewpager(ViewPager msgvp) {
        MsgViewPagerAdapter adapter = new MsgViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AlertMsgFragment(), tabs[0]);
        adapter.addFragment(new SystemMsgFragment(), tabs[1]);
//        adapter.addFragment(new AlertMsgFragment(), tabs[2]);
        msgvp.setAdapter(adapter);
    }


//    private void initDialog1() {
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.dialog_layout_delete_one, null);
//        deleteAlertDialog = new AlertDialog.Builder(this).setView(layout).create();
//    }

    private void initDialog2() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_layout_delete_all, null);
        deleteAlertDialog2 = new AlertDialog.Builder(this).setView(layout).create();
    }


    public void deleteAllClick(View view) {
        //点击显示AlertDialog
        //设置dialog的样式属性
        Window dialogView = deleteAlertDialog2.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams lp = dialogView.getAttributes();
        dialogView.setBackgroundDrawable(new BitmapDrawable());
        lp.width = width - 160;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //        lp.height = height / 3;
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
        //lp.x = 100;
//        lp.y = -300;
        dialogView.setAttributes(lp);
        //点击显示AlertDialog
        deleteAlertDialog2.show();
        iv_cancel = (ImageView) dialogView.findViewById(R.id.delete);
        tv_cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        tv_sure = (TextView) dialogView.findViewById(R.id.tv_sure);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlertDialog2.cancel();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlertDialog2.cancel();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                deleteAlertDialog2.cancel();
//                delete_all_tv.setVisibility(View.GONE);

                EventBusMsgMessage msgMessage = new EventBusMsgMessage();
                msgMessage.setMsgType(EventBusMsgMessage.MSG_Message_Alarm_Delete);
                EventBus.getDefault().post(msgMessage);
                deleteAlertDialog2.dismiss();

            }
        });
    }


    //接收EventBus的方法
    @Override
    public void onEventMainThread(EventBusMsg eventPackage) {
        super.onEventMainThread(eventPackage);
        if (eventPackage.getEmptyType().equals(EventBusMsg.MsgEmptyType.MSG_ALART_LIST_SIZE_CHANGE)) {
            pushmsglistsize = eventPackage.getListSize();
        }

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                if (pushmsglistsize != -1) {
                    delete_all_tv.setVisibility(View.GONE);
                } else {
                    delete_all_tv.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                delete_all_tv.setVisibility(View.GONE);

                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //对推送消息的特殊处理:
// 1.当前页面是消息中心，tabView跳转到响应的界面，添加一条记录
// 2. 当前页面不是消息中心，弹出对话框，让用户选择是否跳转，跳转后的处理逻辑与1同
//    edit by tom
    @Override
    public void onEventMainThread(final EventBusMsgPush eventPackage) {
        if (isActivityTop()) {
            tabLayout.getTabAt(eventPackage.getMsgType()-1).select();
        }
    }

    protected boolean isActivityTop() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(this.getClass().getName());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        EventBusMsgPush msgPush = (EventBusMsgPush) intent.getSerializableExtra("EventBusMsgPush");
        if (intent != null && msgPush != null) {
            tabLayout.getTabAt(msgPush.getMsgType() - 1).select();
        } else {
            tabLayout.getTabAt(0).select();
        }
        if (BaseApplication.getInstance().needGoToMessageCenter) {
            BaseApplication.getInstance().needGoToMessageCenter = false;
            tabLayout.getTabAt(BaseApplication.getInstance().MessageType - 1).select();
        }
    }
}
