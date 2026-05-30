package com.example.ips.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.ips.helpers.Permissions;
import com.example.ips.helpers.PositionFinder;
import com.example.ips.R;
import com.example.ips.data.RoomSavedListener;
import com.example.ips.data.AP;
import com.example.ips.data.Room;
import com.example.ips.helpers.JSONStorage;

import java.util.ArrayList;
import java.util.List;

public class ScanRoom extends Fragment {
    private EditText etRoomName;
    private TextView tvStatus;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiReceiver;
    private final int MAX_SCANS = 5;
    private int scanCount = 0;
    private boolean isChecking = false;
    private RoomSavedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_room, container, false);
        etRoomName = view.findViewById(R.id.editRoom);
        tvStatus   = view.findViewById(R.id.processing);
        wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCollecting();
            }
        });
        view.findViewById(R.id.buttonCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChecking();
            }
        });
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!isVisible() || !isResumed()) return;
                List<ScanResult> results = wifiManager.getScanResults();
                if (isChecking)
                    checkPosition(results);
                else
                    collectData(results);
            }
        };
        return view;
    }

    private void startCollecting() {
        if (etRoomName.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter a room name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Permissions.permissionsGranted(requireActivity())) return;
        isChecking = false;
        scanCount  = 0;
        tvStatus.setText("Scanning 1/" + MAX_SCANS);
        wifiManager.startScan();
    }

    private void startChecking() {
        if (!Permissions.permissionsGranted(requireActivity())) return;
        isChecking = true;
        tvStatus.setText("Detecting position…");
        wifiManager.startScan();
    }

    private void collectData(List<ScanResult> results) {
        String room = etRoomName.getText().toString();
        saveRoomScan(room, results);
        scanCount++;
        if (scanCount < MAX_SCANS) {
            tvStatus.setText("Scanning " + (scanCount + 1) + "/" + MAX_SCANS);
            wifiManager.startScan();
        } else {
            scanCount = 0;
            tvStatus.setText("Done: " + MAX_SCANS + " scans saved for " + room);
        }
    }

    private void checkPosition(List<ScanResult> results) {
        List<Room> rooms = JSONStorage.loadRooms(requireContext());
        Room detected = PositionFinder.findRoom(rooms, filterAPs(results));
        if(detected != null)
            tvStatus.setText("You are in: " + detected.getName());
        else
            tvStatus.setText("No matching");
    }

    private void saveRoomScan(String roomName, List<ScanResult> results) {
        List<Room> rooms = JSONStorage.loadRooms(requireContext());
        Room x = null;
        for (Room r : rooms) {
            if (r.getName().equals(roomName)) {
                x = r;
                break;
            }
        }
        if (x == null) {
            x = new Room(roomName);
            rooms.add(x);
        }
        List<AP> aps = filterAPs(results);
        for (AP ap : aps)
            x.addSample2Room(ap);

        JSONStorage.saveAllRooms(requireContext(), rooms);
        if (listener != null)
            listener.roomSaved();
    }

    public static List<AP> filterAPs(List<ScanResult> results) {
        List<AP> filt = new ArrayList<>();
        for (ScanResult r : results) {
            if (r.level >= -85)
                filt.add(new AP(r.BSSID, r.level));
        }
        return filt;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent instanceof RoomSavedListener)
            listener = (RoomSavedListener) parent;
    }

    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            requireContext().unregisterReceiver(wifiReceiver);
        } catch (Exception ignored) {}
    }
}