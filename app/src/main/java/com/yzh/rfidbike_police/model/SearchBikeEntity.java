package com.yzh.rfidbike_police.model;

/**
 * Created by appadmin on 2017/1/10.
 */

public class SearchBikeEntity {


    private String findCardNo;
    private String requestStatus;

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String findStatus) {
        this.requestStatus = findStatus;
    }

    private int findRssinum;

    public String getFindCardNo() {
        return findCardNo;
    }

    public void setFindCardNo(String findCardNo) {
        this.findCardNo = findCardNo;
    }

    public int getFindRssinum() {
        return findRssinum;
    }

    public void setFindRssinum(int findRssinum) {
        this.findRssinum = findRssinum;
    }

    public SearchBikeEntity(String findCardNo, int findRssinum) {
        this.findCardNo = findCardNo;
        this.findRssinum = findRssinum;
    }

    public SearchBikeEntity() {
    }
}
