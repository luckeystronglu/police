package com.yzh.rfidbike_police.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by Administrator on 2017/1/5.
 */
@Table(name = "SearchDevice")
public class SearchDevice {

    @Id(column = "deviceId")
    private String deviceId;
    private long time;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public int hashCode() {
        return deviceId.hashCode();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
