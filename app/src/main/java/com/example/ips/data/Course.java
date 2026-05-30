package com.example.ips.data;

import org.json.JSONObject;

public class Course {
    private String name;
    private String room;
    private String time;

    public Course(){}
    public Course(String name, String room, String time){
        this.name = name;
        this.room = room;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }
    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public JSONObject toJSON() {
        JSONObject cl = new JSONObject();
        try {
            cl.put("name", name);
            cl.put("room", room);
            cl.put("time", time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cl;
    }

}
