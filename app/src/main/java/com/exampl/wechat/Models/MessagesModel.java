package com.exampl.wechat.Models;

import java.util.Comparator;

public class MessagesModel {
    String uId , message,messageId,ImageUrl;
    String timestamp;
    int Feeling=-1;

    public MessagesModel(String uId, String message, String timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public MessagesModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }


    public int getFeeling() {
        return Feeling;
    }

    public void setFeeling(int feeling) {
        Feeling = feeling;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public MessagesModel(){}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
