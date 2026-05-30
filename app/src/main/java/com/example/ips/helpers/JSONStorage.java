package com.example.ips.helpers;

import android.content.Context;

import com.example.ips.data.AP;
import com.example.ips.data.Room;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import com.example.ips.data.Course;

public class JSONStorage {

    private static final String FILE_NAME_ROOMS = "rooms.json";
    private static final String FILE_NAME_CLASSES = "classes.json";

    public static void saveAllRooms(Context context, List<Room> rooms) {
        try {
            JSONArray array = new JSONArray();
            for (Room room : rooms) {
                array.put(room.toJSON());
            }
            File file = new File(context.getExternalFilesDir(null), FILE_NAME_ROOMS);
            FileWriter writer = new FileWriter(file, false);
            writer.write(array.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Room> loadRooms(Context context) {
        List<Room> rooms = new ArrayList<>();
        try {
            File file = new File(context.getExternalFilesDir(null), FILE_NAME_ROOMS);
            if (!file.exists()) return rooms;

            JSONArray array = new JSONArray(readJSON(file));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Room room = new Room();
                room.setName(obj.getString("name"));
                JSONArray aps = obj.getJSONArray("fingerprint");
                for (int j = 0; j < aps.length(); j++) {
                    JSONObject apObj = aps.getJSONObject(j);
                    AP ap = new AP();
                    ap.setBssid(apObj.getString("bssid"));
                    ap.setSumSamples(apObj.getInt("sumSamples"));
                    ap.setNSamples(apObj.getInt("nSamples"));
                    room.getRouters().add(ap);
                }
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static void saveClasses(Context context, String day, List<Course> classes){
        try {
            File file = new File(context.getExternalFilesDir(null), FILE_NAME_CLASSES);
            JSONObject classesOfDay = new JSONObject();
            if (file.exists()) {
                try {
                    classesOfDay = new JSONObject(readJSON(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            JSONArray array = new JSONArray();
            for (Course cl : classes) {
                array.put(cl.toJSON());
            }
            classesOfDay.put(day, array);
            FileWriter writer = new FileWriter(file, false);
            writer.write(classesOfDay.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Course> loadClasses(Context context, String day){
        List<Course> classes = new ArrayList<>();
        try {
            File file = new File(context.getExternalFilesDir(null), FILE_NAME_CLASSES);
            if (!file.exists()) return classes;

            JSONObject classesOfDay = new JSONObject(readJSON(file));
            if (!classesOfDay.has(day)) {
                return classes;
            }
            JSONArray array = classesOfDay.getJSONArray(day);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                    Course c = new Course();
                    c.setName(obj.getString("name"));
                    c.setRoom(obj.getString("room"));
                    c.setTime(obj.getString("time"));
                    classes.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static String readJSON(File file) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}