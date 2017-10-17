package com.yzh.rfidbike_police.socket;

import android.annotation.SuppressLint;

import org.apache.mina.core.session.IoSession;

@SuppressLint("DefaultLocale")
public class SocketAppPacket {
    // 最新版本检查
    public static final int COMMAND_ID_GET_POLICE_APP_VERSION = 0x81032;
    // 用户登录
    public static final int COMMAND_ID_USER_LOGIIN = 0x81012;
    public static final int COMMAND_ID_BIKE_INSURE = 0x83042;
    public static final int COMMAND_ID_BIKE_INSURE_LIST = 0x83032;
    public static final int COMMAND_ID_BIKE_BY_BIKE_NO = 0x83022;
    public static final int COMMAND_ID_BIKE_BY_IDCARD_NO = 0x83012;

    // 退出登录
    public static final int COMMAND_ID_LOGIINOUT = 0x81042;

    //修改密码
    public static final int COMMAND_ID_POLICE_UPDATE_PASSWORD = 0x81022;

    //获取公司列表
    public static final int GET_COMPANY_LIST =0x83052;

    //获取公安系统消息
    public static final int COMMAND_ID_POLICE_SYSTEM_MSG = 0x84012;

    //公安报警消息
    public static final int COMMAND_ID_POLICE_ALART_MSG = 0x84022;

    //通过id删除公安报警信息  0x84032
    public static final int COMMAND_ID_POLICE_DELETE_ALART_MSG = 0x84032;

    //获取丢失车辆列表
    public static final int COMMAND_ID_GET_LOST_BIKELIST = 0x82012;
    //按车牌号查询车辆信息
    public static final int COMMAND_ID_POLICE_GET_BIKE_BY_PLATE_NUMBER = 0x83022;
    //按卡号查询车辆列表
    public static final int COMMAND_ID_GET_LOST_BIKELIST_BYCARDNO = 0x83062;
    //按车牌查询丢失车辆信息
    public static final int COMMAND_ID_POLICE_GET_LOSTBIKEINFO_BY_PLATE_NUMBER = 0x83072;
    //按用户查询异常车辆信息
    public static final int COMMAND_ID_POLICE_GET_UNNORMAL_BIKE_BY_USER = 0x83082;

    private IoSession fromClient = null;

    /**
     * @return the fromClient
     */
    public IoSession getFromClient() {
        return fromClient;
    }

    public SocketAppPacket(IoSession channel) {
        this.fromClient = channel;
    }

    String packetType;

    public String getPacketType() {
        return packetType;
    }

    /**
     * @param packetType the packetType to set
     */
    private void setPacketType(String packetType) {
        this.packetType = packetType;
    }


    private int commandId = 0;

    /**
     * @return the commandId
     */
    public int getCommandId() {
        return commandId;
    }

    /**
     * @param commandId the commandId to set
     */
    public void setCommandId(int commandId) {
        this.commandId = commandId;

        String typeString = "0x" + Integer.toHexString(commandId).toUpperCase() + "_";
        switch (commandId) {
            // 最新版本检查
            case SocketAppPacket.COMMAND_ID_GET_POLICE_APP_VERSION:
                typeString += "GET_APP_VERSION";
                break;
            // 用户登录
            case SocketAppPacket.COMMAND_ID_USER_LOGIIN:
                typeString += "USER_LOGIN";
                break;
            // 获取公司列表
            case SocketAppPacket.GET_COMPANY_LIST:
                typeString += "GET_COMPANY_LIST";
                break;
            default:
                typeString += "UNKNOWN";
                break;
        }

        setPacketType(typeString);
    }

    private byte[] commandData = null;

    /**
     * @return the commandData
     */
    public byte[] getCommandData() {
        return commandData;
    }

    /**
     * @param commandData the commandData to set
     */
    public void setCommandData(byte[] commandData) {
        this.commandData = commandData;
    }

    long receiveTime = 0;

    /**
     * @return the receiveTime
     */
    public long getReceiveTime() {
        return receiveTime;
    }

    /**
     * @param receiveTime the receiveTime to set
     */
    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

}
