package com.anioncode.memory.Models;

import android.app.Application;

import com.anioncode.memory.Models.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
