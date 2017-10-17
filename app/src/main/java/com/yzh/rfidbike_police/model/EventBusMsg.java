package com.yzh.rfidbike_police.model;


import java.util.Map;

public class EventBusMsg {


    private MsgType mMsgType;
    private Map value;

    public MsgType getMsgType() {
        return mMsgType;
    }

    public void setMsgType(MsgType msgType) {
        mMsgType = msgType;
    }

    public Map getValue() {
        return value;
    }

    public void setValue(Map value) {
        this.value = value;
    }

    public enum MsgType {
        MSG_COMPANY_ID
    }


    private int listSizeNum;
    public int getListSize() {
        return listSizeNum;
    }

    public void setListSize(int listSizeNum) {
        this.listSizeNum = listSizeNum;
    }


    private MsgEmptyType emptyType;

    public MsgEmptyType getEmptyType() {
        return emptyType;
    }

    public void setEmptyType(MsgEmptyType emptyType) {
        this.emptyType = emptyType;
    }

    public enum MsgEmptyType {
        MSG_ALART_LIST_SIZE_CHANGE
    }
}
