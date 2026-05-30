package com.example.ips.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ips.data.Course;

import java.util.Calendar;
import java.util.List;

public class AlarmScheduler {
    public static void scheduleNextCheck(Context context) {
        long delay = getDelayUntilNextClass(context);

        if (delay < 0) {
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 1);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            delay = tomorrow.getTimeInMillis() - System.currentTimeMillis();
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pending);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pending);
    }

    private static long getDelayUntilNextClass(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ips_prefs", Context.MODE_PRIVATE);
        int minutesBefore = prefs.getInt("notif_minutes", 5);

        Calendar now = Calendar.getInstance();
        int nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        String day = getDay(now);
        if (day == null)
            return -1;

        List<Course> classes = JSONStorage.loadClasses(context, day);
        int nextNotifyAt = Integer.MAX_VALUE;
        for (Course c : classes) {
            String time = c.getTime();
            if (time == null || time.isEmpty()) continue;
            String[] parts = time.split(":");
            int classMinutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            int notifyAt = classMinutes - minutesBefore;
            if (notifyAt > nowMinutes && notifyAt < nextNotifyAt) {
                nextNotifyAt = notifyAt;
            }
        }

        if (nextNotifyAt == Integer.MAX_VALUE)
            return -1;
        long delay = (long)(nextNotifyAt - nowMinutes) * 60 * 1000;
        if (delay <= 0)
            return -1;
        return delay;
    }

    public static Course getCls(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ips_prefs", Context.MODE_PRIVATE);
        int minutesBefore = prefs.getInt("notif_minutes", 5);

        Calendar now = Calendar.getInstance();
        int nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        String day = getDay(now);
        if (day == null)
            return null;
        List<Course> classes = JSONStorage.loadClasses(context, day);
        for (Course c : classes) {
            String time = c.getTime();
            if (time == null || time.isEmpty())
                continue;
            String[] parts = time.split(":");
            int classMinutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            int diff = classMinutes - nowMinutes;
            if (diff >= 0 && diff <= minutesBefore)
                return c;
        }
        return null;
    }

    public static String getDay(Calendar cal) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int index = day - 2;
        if (index < 0 || index >= days.length)
            return null;
        return days[index];
    }
}
