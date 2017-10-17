package com.yzh.rfidbike_police.activity;

import android.support.v7.widget.RecyclerView;

import com.yzh.rfid.app.response.Bike;
import com.yzh.rfidbike_police.R;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;



/**
 * Created by Administrator on 2016/12/28.
 */

public class DeviceAdapter extends BGARecyclerViewAdapter<Bike.BikeMessage> {


    public DeviceAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_device);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, Bike.BikeMessage
            model) {
        helper.setText(R.id.tv_name, model.getPlateNumber());

    }

}
