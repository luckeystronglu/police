package com.yzh.rfidbike_police.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.base.App;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.base.BaseApplication;
import com.yzh.rfidbike_police.fragment.RegistrationFragment;
import com.yzh.rfidbike_police.fragment.LocationFragment;
import com.yzh.rfidbike_police.fragment.SearchNearbyBikeFragment;
import com.yzh.rfidbike_police.fragment.SettingFragment;


public class MainNewActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        init();

    }

    private void init() {
        radioGroup = findViewByIds(R.id.rg_police_tab);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.getChildAt(0).performClick();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (BaseApplication.getInstance().needGoToMessageCenter) {
            Intent intentNew = new Intent();
            intentNew.setClass(context.getApplicationContext(), MessageCenterPoliceActivity.class);
            intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intentNew);
        }
    }

    //连续按键两次退出app
    private long presstime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
/**
 * web.canGoBack()判断webview是否有可以返回的页面
 */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - presstime < 2000) {
                App.exitSystem(this);
            } else {
                Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
                presstime = System.currentTimeMillis();
            }
        }
        return true;
    }

    protected boolean isMessageCenterTop() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(MessageCenterPoliceActivity.class);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_frag_location:
                //点击"首页"
                fragmentManager(R.id.fl_fragment, new LocationFragment(), "fragLocation");
                break;

            case R.id.rb_frag_search:
                //点击"数据"
                fragmentManager(R.id.fl_fragment, new SearchNearbyBikeFragment(), "fragSearch");
                break;

            case R.id.rb_frag_bike_register:
                //点击"发现"
                fragmentManager(R.id.fl_fragment, new RegistrationFragment(), "fragRegister");
                break;

            case R.id.rb_frag_setting:
                //点击"我"
                fragmentManager(R.id.fl_fragment, new SettingFragment(), "fragSetting");
                break;
        }
    }
}
