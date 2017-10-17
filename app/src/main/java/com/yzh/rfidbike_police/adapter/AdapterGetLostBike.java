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

import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.model.MyBikeLostMessage;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by appadmin on 2016/12/24.
 */

public class AdapterGetLostBike extends BaseAdapter {


    private List<MyBikeLostMessage> datas;
    private Context context;

    public AdapterGetLostBike(Context context) {
        super();
        this.context = context;
        this.datas = new ArrayList();
    }

    public void setDatas(List<MyBikeLostMessage> datas){
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lost_bike, null);
            holder.tv_lost_bike_cardid = (TextView) convertView.findViewById(R.id.tv_lost_bike_cardid);
            holder.tv_lost_bike_status = (TextView) convertView.findViewById(R.id.tv_lost_bike_status);
            holder.iv_lost_bike_logo = (ImageView) convertView.findViewById(R.id.iv_lost_bike_logo);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.distance_value = (TextView) convertView.findViewById(R.id.distance_value);
            convertView.setTag(holder);
        }


        holder.tv_lost_bike_cardid.setText(datas.get(position).getCardNo());
        holder.progressBar.setMax(100);
       int progressValue= datas.get(position).getRssinum();
        if(progressValue <= 45)
        {
            progressValue=0;
        }else if(progressValue>=90)
        {
            progressValue=100;
        }else
        {
            progressValue=(progressValue-45)*100/(90-45);
        }


        holder.progressBar.setProgress(progressValue);
        holder.distance_value.setText(datas.get(position).getRssinum()+"");

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


        return convertView;
    }

    class ViewHolder{
        TextView tv_lost_bike_cardid,tv_lost_bike_status,distance_value;
        ImageView iv_lost_bike_logo;
        ProgressBar progressBar;
    }





}
