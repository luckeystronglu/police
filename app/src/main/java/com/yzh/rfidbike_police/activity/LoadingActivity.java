package com.yzh.rfidbike_police.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.base.PreferenceData;
import com.yzh.rfidbike_police.util.pinfo.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;



public class LoadingActivity extends Activity {

    @BindView(R.id.tv_version)
    TextView mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        String version = AppUtils.getVersionName(this);
        final long loadLoginAccount = PreferenceData.loadLoginAccount(this);
        mTvVersion.setText(String.format("当前版本号:%s", version));
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1 * 1000);
                    if (loadLoginAccount <= 0) {
                        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoadingActivity.this, MainNewActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
