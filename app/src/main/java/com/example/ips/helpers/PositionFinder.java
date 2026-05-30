package com.example.ips.helpers;

import com.example.ips.data.AP;
import com.example.ips.data.Room;

import java.util.List;

public class PositionFinder {

    public static Room findRoom(List<Room> rooms, List<AP> liveMap) {
        if (rooms == null || rooms.isEmpty()) {
            return null;
        }
        Room pbRoom = null;
        double pbScore = Double.MAX_VALUE;
        for (Room room : rooms) {
            double totalDiff = 0;
            int matches = 0;
            for (AP liveAP : liveMap) {
                String bssid = liveAP.getBssid();
                if (bssid == null)
                    continue;
                for (AP savedAP : room.getRouters()) {
                    if (savedAP.getBssid() == null)
                        continue;
                    if (savedAP.getBssid().equals(bssid)) {
                        totalDiff += Math.abs(savedAP.getMean() - liveAP.getMean());
                        matches++;
                    }
                }
            }
            if (matches == 0)
                continue;
            double avgDiff = totalDiff / matches;
            if (avgDiff < pbScore) {
                pbScore = avgDiff;
                pbRoom = room;
            }
        }
        return pbRoom;
    }

    public static boolean shouldNotify(Room detected, String expectedRoom) {
        if (expectedRoom == null || expectedRoom.isEmpty()) {
            return false;
        }
        if (detected == null) {
            return true;
        }
        return !detected.getName().equalsIgnoreCase(expectedRoom);
    }
}