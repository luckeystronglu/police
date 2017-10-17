package com.yzh.rfidbike_police.model;

/**
 * Created by appadmin on 2017/1/5.
 */

public class ReadCardMsgEntity {
    private String title;
    private long dateTime;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ReadCardMsgEntity() {
    }

    public ReadCardMsgEntity(String title, long dateTime, String content) {
        this.title = title;
        this.dateTime = dateTime;
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReadCardMsgEntity{" +
                "title='" + title + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
