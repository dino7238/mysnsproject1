package com.example.mysnsproject.FriendList;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public abstract class FriendListData implements Parcelable {
    private String my_id;
    private String friend_id;
    private String friend_name;
    private String idx;

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }
    public String getMy_id() {
        return my_id;
    }

    public void setMy_id(String my_id) {
        this.my_id = my_id;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

}

