package com.anioncode.memory.Models;

public class Friend {
    private String friend_id;

    private String Friend_Doc_id;

    public Friend() {

    }

    public Friend(String friend_id , String friend_Doc_id) {

        this.friend_id = friend_id;

        Friend_Doc_id = friend_Doc_id;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public String getFriend_Doc_id() {
        return Friend_Doc_id;
    }

    public void setFriend_Doc_id(String friend_Doc_id) {
        Friend_Doc_id = friend_Doc_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }


}
