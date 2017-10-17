package com.yzh.rfidbike_police.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzh.rfid.app.response.BikeLost;
import com.yzh.rfidbike_police.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by appadmin on 2016/12/24.
 */


public class AdapterGetReportBike extends BaseAdapter {


    private List<BikeLost.BikeLostMessage> datas;
    private Context context;

    public AdapterGetReportBike(Context context) {
        super();
        this.context = context;
        this.datas = new ArrayList();
    }

    public void setDatas(List<BikeLost.BikeLostMessage> datas){
        this.datas = datas;
        this.notifyDataSetChanged();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lost_bikelist, null);
            holder.tv_lost_bike_cardid = (TextView) convertView.findViewById(R.id.tv_lost_bike_cardid);
            holder.tv_lost_bike_status = (TextView) convertView.findViewById(R.id.tv_lost_bike_status);
            holder.iv_lost_bike_logo = (ImageView) convertView.findViewById(R.id.iv_lost_bike_logo);
            convertView.setTag(holder);
        }

        if (datas.get(position).getStatus().equals("1")) {
            holder.tv_lost_bike_cardid.setText(datas.get(position).getPlateNumber());
            holder.tv_lost_bike_status.setText("已报案");
            holder.tv_lost_bike_status.setTextColor(Color.parseColor("#FF6E64FE"));
            holder.iv_lost_bike_logo.setImageResource(R.drawable.ico_ba);
        }

        else if (datas.get(position).getStatus().equals("4")){
            holder.tv_lost_bike_cardid.setText(datas.get(position).getPlateNumber());
            holder.tv_lost_bike_status.setText("已丢失");
            holder.tv_lost_bike_status.setTextColor(Color.parseColor("#FFD9372C"));
            holder.iv_lost_bike_logo.setImageResource(R.drawable.ico_ds);
        }

        return convertView;
    }

    class ViewHolder{
        TextView tv_lost_bike_cardid,tv_lost_bike_status;
        ImageView iv_lost_bike_logo;

    }





}
