package com.yzh.rfidbike_police.model;

import net.tsz.afinal.annotation.sqlite.Id;

/**
 * Created by appadmin on 2017/1/10.
 */

public class MyBikeLostMessage  {

    @Id(column="id")
    private long id;
    private long  bikeId;
    private String plateNumber;
    private String status;
    private String cardNo;
    private int rssinum;
    private RequestStatus requestStatus;
    private long scanTime;//用来判断长时间接受不到数据后，需要从列表里删除



    public enum  RequestStatus{Found,WaitingForRequest,NoExistInDB,ExistInDB};
    public int getRssinum() {
        return rssinum;
    }

    public void setRssinum(int rssinum) {
        this.rssinum = rssinum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public long getScanTime() {
        return scanTime;
    }

    public void setScanTime(long scanTime) {
        this.scanTime = scanTime;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }


}
