package com.yzh.rfidbike_police.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yzh.rfidbike_police.R;


/**
 * Created by Administrator on 2016/12/27.
 */

public class Header extends RelativeLayout implements View.OnClickListener {
    private headerListener mListener;
    private final ImageView mIvLeft;
    private final TextView mTvRight;
    private final TextView mTvTitle;

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Header);
        int leftIcon = array.getResourceId(R.styleable.Header_leftIcon, R.mipmap.ico_setting);
        String RightText = array.getString(R.styleable.Header_rightText);
        String title = array.getString(R.styleable.Header_headerTitle);
        int leftVisible = array.getInt(R.styleable.Header_leftIconVisible, 0);
        int rightVisible = array.getInt(R.styleable.Header_rightTextVisible, 0);
        array.recycle();
        View v = LayoutInflater.from(context).inflate(R.layout.header, this);
        mIvLeft = (ImageView) v.findViewById(R.id.iv_left);
        mTvRight = (TextView) v.findViewById(R.id.tv_right);
        mTvTitle = (TextView) v.findViewById(R.id.tv_title);
        mIvLeft.setImageResource(leftIcon);
        mTvRight.setText(RightText);
        mTvTitle.setText(title);
        mIvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        switch (leftVisible) {
            case 0:
                mIvLeft.setVisibility(VISIBLE);
                break;

            case 1:
                mIvLeft.setVisibility(INVISIBLE);

                break;
            case 2:
                mIvLeft.setVisibility(GONE);

                break;
        }
        switch (rightVisible) {
            case 0:
                mTvRight.setVisibility(VISIBLE);
                break;
            case 1:
                mTvRight.setVisibility(INVISIBLE);

                break;
            case 2:
                mTvRight.setVisibility(GONE);

                break;
        }
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setTitle(int title) {
        mTvTitle.setText(title);
    }

    public void setListener(headerListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                if (mListener != null) {
                    mListener.onClickLeftIcon();
                }
                break;
            case R.id.tv_right:
                if (mListener != null) {
                    mListener.onClickRightText();
                }
                break;
            default:
                break;
        }

    }

    public interface headerListener {
        void onClickLeftIcon();

        void onClickRightText();
    }


}
