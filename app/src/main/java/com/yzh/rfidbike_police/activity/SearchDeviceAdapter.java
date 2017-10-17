package com.yzh.rfidbike_police.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzh.rfidbike_police.R;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;


/**
 * Created by Administrator on 2017/1/5.
 */

public class SearchDeviceAdapter extends BGARecyclerViewAdapter<SearchDevice> {
    private Context mContext;
    private String mKeyWord;
    private OnDeleteListener mDeleteListener;
    private int dataSize = 0;

    public SearchDeviceAdapter(Context context, RecyclerView recyclerView) {
        super(recyclerView);
        mContext = context;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.item_search_device_header;
        } else {
            return R.layout.item_search_device;
        }
    }

    @Override
    public void setData(List<SearchDevice> data) {
        super.setData(data);
        dataSize = data.size();
        if (data.size() != 0) {
            data.add(0, new SearchDevice());
        }

    }

    @Override
    public void clear() {
        super.clear();
//        getData().add(0, new SearchDevice());
    }

    @Override
    protected void fillData(final BGAViewHolderHelper helper, final int position, final SearchDevice
            model) {
        if (position == 0) {
            TextView tvDeleteAll = helper.getTextView(R.id.tv_deleteAll);
            tvDeleteAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDeleteListener.onDeleteAll();
                    clear();
                }
            });

        } else {
            String deviceId = model.getDeviceIdDecimal();
            helper.setText(R.id.tv_device, deviceId);
            if (mKeyWord != null && deviceId.contains(mKeyWord)) {
                int start = deviceId.indexOf(mKeyWord);
                int end = start + mKeyWord.length();
                SpannableString spannableString = new SpannableString(model.getDeviceIdDecimal());
                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor
                        (R.color
                                .actionsheet_blue)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                helper.setText(R.id.tv_device, spannableString);

            }
            LinearLayout ll_deleteOne = helper.getView(R.id.ll_delete_one);
            ll_deleteOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeleteListener.onDelete(model, helper.getPosition());
//                    final int position1 = helper.getPosition();
                    if (helper.getPosition() != 1) {
                        removeItem(helper.getPosition());
                        dataSize--;
                    }else if(helper.getPosition() == 1 && dataSize > 1){
                        removeItem(helper.getPosition());
                        dataSize--;
                    }
                    else if(helper.getPosition() == 1 && dataSize == 1){
                        mDeleteListener.onDeleteAll();
                        clear();
                    }
                }
            });
            
//            ImageView ivDelete = helper.getImageView(R.id.tv_delete);
//            ivDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mDeleteListener.onDelete(model, helper.getPosition());
//                    if (helper.getPosition() != 1) {
//                        removeItem(helper.getPosition());
//                    }else {
//                        mDeleteListener.onDeleteAll();
//                        clear();
//                    }
//                }
//            });
        }

    }

    public void setData(List<SearchDevice> data, String keyWord) {
        super.setData(data);
        dataSize = data.size();
        if (data.size() != 0) {
            data.add(0, new SearchDevice());
            mKeyWord = keyWord;
        }

    }



    public void setDeleteListener(OnDeleteListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public interface OnDeleteListener {
        void onDelete(SearchDevice device, int position);

        void onDeleteAll();
    }


}
