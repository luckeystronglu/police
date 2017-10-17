package com.yzh.rfidbike_police.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yzh.rfid.app.response.Bike;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.activity.SearchDevice;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/12/28.
 */

public class AdapterAbnormalBike extends BaseAdapter {

    private List<Bike.BikeMessage> datas;
    private Context context;

    public AdapterAbnormalBike(Context context, List<Bike.BikeMessage> list) {
        super();
        this.context = context;
        this.datas = list;
//        this.cacheMap = new HashMap<Integer, String>();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView!=null){
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device, null);
            holder.tv_lost_bike_cardid = (TextView) convertView.findViewById(R.id.tv_card);
            holder.tv_lost_bike_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.iv_lost_bike_logo = (ImageView) convertView.findViewById(R.id.img_status_logo);
            holder.tv_lost_bike_plate = (TextView) convertView.findViewById(R.id.tv_plate);
            convertView.setTag(holder);
        }



        if (datas.get(position).getStatus().equals("1")) {
            holder.tv_lost_bike_status.setText("已报案");
            holder.tv_lost_bike_status.setTextColor(Color.parseColor("#FF6E64FE"));
            holder.iv_lost_bike_logo.setImageResource(R.drawable.ico_ba);
        }else if (datas.get(position).getStatus().equals("2")){
            holder.tv_lost_bike_status.setText("处理中");
            holder.tv_lost_bike_status.setTextColor(Color.parseColor("#FFD9372C"));
            holder.iv_lost_bike_logo.setImageResource(R.drawable.ico_ds);
        }else if (datas.get(position).getStatus().equals("0")){
            holder.tv_lost_bike_status.setText("正    常");
            holder.tv_lost_bike_status.setTextColor(Color.parseColor("#FF00A5E3"));
            holder.iv_lost_bike_logo.setImageResource(R.drawable.icon_bike_normal);
        }
        else if (datas.get(position).getStatus().equals("3")){
            holder.tv_lost_bike_status.setText("已丢失");
            holder.tv_lost_bike_status.setTextColor(Color.parseColor("#FF09BA07"));
            holder.iv_lost_bike_logo.setImageResource(R.drawable.icon_status_over);
        }else
        {
            holder.tv_lost_bike_status.setText("查询中");
            holder.tv_lost_bike_status.setTextColor(Color.parseColor("#FF09BA07"));
            holder.iv_lost_bike_logo.setImageResource(R.drawable.refresh_animate);
        }
        holder.tv_lost_bike_cardid.setText(String.valueOf(datas.get(position).getCardNo()));
        holder.tv_lost_bike_plate.setText(datas.get(position).getPlateNumber());
        return convertView;
    }

    class ViewHolder{
        TextView tv_lost_bike_cardid,tv_lost_bike_status,tv_lost_bike_plate;
        ImageView iv_lost_bike_logo;
    }

}
