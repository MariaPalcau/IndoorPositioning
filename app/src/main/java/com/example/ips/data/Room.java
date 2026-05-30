package com.example.ips.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private List<AP> routers = new ArrayList<>();
    public Room() {}
    public Room(String name) {
        this.name = name;
    }
    public void addSample2Room(AP accessPoint) {
        for (AP router : routers) {
            if (router.getBssid().equals(accessPoint.getBssid())) {
                router.addSample(accessPoint.getSumSamples());
                return;
            }
        }
        routers.add(accessPoint);
    }
    public String getName() {
        return name;
    }
    public List<AP> getRouters() {
        return routers;
    }
    public void setName(String name) {
        this.name = name;
    }
    public JSONObject toJSON() {
        JSONObject room = new JSONObject();
        try {
                room.put("name", name);
                JSONArray arr = new JSONArray();
                for (AP ap : routers)
                    arr.put(ap.toJSON());
                room.put("fingerprint", arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return room;
    }
}