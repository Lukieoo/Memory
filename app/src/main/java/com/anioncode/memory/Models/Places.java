package com.anioncode.memory.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Places {

    private String username;
    private String name;
    private String position1;
    private String position2;
    private String description;
    private String places_id;
    private String timestamp;
    private String user_id;

    public Places() {

    }

    public Places(String username, String name, String position1, String position2, String description, String places_id,String timestamp,String user_id) {
        this.username = username;
        this.name = name;
        this.position1 = position1;
        this.position2 = position2;
        this.description = description;
        this.places_id = places_id;
        this.timestamp = timestamp;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPlaces_id() {
        return places_id;
    }

    public void setPlaces_id(String places_id) {
        this.places_id = places_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition1() {
        return position1;
    }

    public void setPosition1(String position1) {
        this.position1 = position1;
    }

    public String getPosition2() {
        return position2;
    }

    public void setPosition2(String position2) {
        this.position2 = position2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
