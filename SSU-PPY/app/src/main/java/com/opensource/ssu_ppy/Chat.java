package com.opensource.ssu_ppy;

import java.util.Date;

public class Chat {
    private String Name;
    private String Message;
    private String Uid;
    private Date Timestamp;

    public Chat() {
    }

    public Chat(String name, String message, String uid, Date timestamp) {
        Name = name;
        Message = message;
        Uid = uid;
        Timestamp = timestamp;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public Date getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Date timestamp) {
        Timestamp = timestamp;
    }
}
