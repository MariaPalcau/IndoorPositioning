package com.example.ips.helpers;
import static com.example.ips.helpers.AlarmScheduler.getCls;
import static com.example.ips.helpers.AlarmScheduler.scheduleNextCheck;
import static com.example.ips.helpers.Permissions.hasPermissions;
import static com.example.ips.helpers.Permissions.isWifiEnabled;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.ips.MainActivity;
import com.example.ips.data.AP;
import com.example.ips.data.Course;
import com.example.ips.data.Room;
import com.example.ips.fragments.ScanRoom;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "ips_channel";
    private static final int NOTIF_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "IPS", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(mChannel);
        }

        if (!isWifiEnabled(context)) {
            scheduleNextCheck(context);
            return;
        }
        if (!hasPermissions(context)) {
            scheduleNextCheck(context);
            return;
        }

        Course upcoming = getCls(context);
        if (upcoming == null) {
            scheduleNextCheck(context);
            return;
        }

        List<ScanResult> results = scan(context);

        List<AP> live = ScanRoom.filterAPs(results);
        List<Room> rooms = JSONStorage.loadRooms(context);
        Room detected = PositionFinder.findRoom(rooms, live);

        if (PositionFinder.shouldNotify(detected, upcoming.getRoom())) {
            String message;
            if(detected != null)
                message = "You should be in " + upcoming.getRoom();
            else
                message = "Head to " + upcoming.getRoom();
            sendNotification(context, nm, message);
        } else {
            nm.cancel(NOTIF_ID);
        }
        scheduleNextCheck(context);
    }

    private List<ScanResult> scan(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        return new ArrayList<>(wifiManager.getScanResults());
    }

    private void sendNotification(Context context, NotificationManager nm, String message) {
        Intent pint = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, pint,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification n = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Class Starting Soon")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        nm.notify(NOTIF_ID, n);
    }
}
