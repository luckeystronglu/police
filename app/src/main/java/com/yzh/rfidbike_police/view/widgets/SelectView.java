package com.yzh.rfidbike_police.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzh.rfidbike_police.R;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Created by Administrator on 2017/1/11.
 */

public class SelectView extends LinearLayout {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_mark)
    ImageView mIvMark;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.iv_indicator)
    ImageView mIvIndicator;


    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_select, this);
        ButterKnife.bind(view);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SelectView);
        String title = array.getString(R.styleable.SelectView_SelectViewTitle);
        String hint = array.getString(R.styleable.SelectView_SelectViewHint);
        int indicatorImg = array.getResourceId(R.styleable.SelectView_SelectViewIndicatorImg, R.drawable
                .bg_date);
        int markVisible = array.getInt(R.styleable.SelectView_SelectViewMarkVisible, 2);
        array.recycle();
        mIvIndicator.setImageResource(indicatorImg);
        mTvTitle.setText(title);
        mTvContent.setHint(hint);
        switch (markVisible) {
            case 0:
                mIvMark.setVisibility(VISIBLE);
                break;

            case 1:
                mIvMark.setVisibility(INVISIBLE);

                break;
            case 2:
                mIvMark.setVisibility(GONE);

                break;
        }

    }

    public String getContent() {
        return mTvContent.getText().toString();
    }

    public void setContent(String s) {
        mTvContent.setText(s);
    }

}
