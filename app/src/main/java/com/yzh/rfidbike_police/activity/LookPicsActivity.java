package com.yzh.rfidbike_police.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.view.widgets.Header;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by lenovo on 2016/10/23.
 */
public class LookPicsActivity extends BaseActivity{

    TextView tv_num_now, tv_num_total;
    private ViewPager picvp;
    private PicsVpAdapter vpAdapter;
//    private List<String> contents;
    private ArrayList<String> urls;
    private int position;
    private Header header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookpics);
        init();
    }

    protected void init() {
        header = findViewByIds(R.id.pic_look_header);
        picvp = findViewByIds(R.id.pic_viewpager);
        tv_num_now = findViewByIds(R.id.pic_num_now);
        tv_num_total = findViewByIds(R.id.pic_num_total);
        Intent intent = getIntent();
        urls = intent.getStringArrayListExtra("urls");
        position = intent.getIntExtra("position", -1);

        tv_num_now.setText((position + 1) + "");
        tv_num_total.setText(urls.size() + "");
        vpAdapter = new PicsVpAdapter(this, urls);
        picvp.setAdapter(vpAdapter);
        picvp.setCurrentItem(position);
        picvp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_num_now.setText((position + 1) + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        header.setListener(new Header.headerListener() {
            @Override
            public void onClickLeftIcon() {
                finish();
            }

            @Override
            public void onClickRightText() {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        urls.clear();
    }

    public static class PicsVpAdapter extends PagerAdapter {
        private Context context;
        private List<String> piclist;

        public PicsVpAdapter(Context context,ArrayList<String> piclist) {
            this.context = context;
            this.piclist = piclist;
        }

        @Override
        public int getCount() {
            return piclist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(context);
            photoView.enable();//可缩放
            photoView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(context)
                    .load(piclist.get(position))
                    .crossFade(500)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.pic_load_err)
                    .thumbnail(0.1f)
                    .into(photoView);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}

