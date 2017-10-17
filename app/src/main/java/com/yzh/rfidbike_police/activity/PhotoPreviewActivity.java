package com.yzh.rfidbike_police.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yzh.rfidbike_police.R;

public class PhotoPreviewActivity extends Activity {

    ImageView mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        mPreview = (ImageView) findViewById(R.id.iv_preview);
        String url = getIntent().getStringExtra("url");
        Glide.with(this)
                .load(url).error(R.mipmap.pic_load_err)
                .into(mPreview);
    }

    public void finish(View view) {
        finish();
    }
}
