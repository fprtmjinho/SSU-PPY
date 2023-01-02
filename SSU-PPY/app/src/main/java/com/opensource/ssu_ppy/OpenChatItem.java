package com.opensource.ssu_ppy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OpenChatItem implements Serializable {
    String name;
    String hobby;
    String roomNum;
    ArrayList<String> uidList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public ArrayList<String> getUidList() {
        return uidList;
    }

    public void setUidList(ArrayList<String> uidList) {
        this.uidList = uidList;
    }

    public void addUidList(String uid) {
        this.uidList.add(uid);
    }
}
