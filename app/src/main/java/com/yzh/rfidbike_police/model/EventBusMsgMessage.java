package com.yzh.rfidbike_police.model;


public class EventBusMsgMessage {


    public static final int MSG_Message_Alarm_Delete = 1998;
    private int MsgType;

    public int getMsgType() {
        return MsgType;
    }

    public void setMsgType(int msgType) {
        MsgType = msgType;
    }


}
