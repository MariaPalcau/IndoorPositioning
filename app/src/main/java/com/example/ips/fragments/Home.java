package com.example.ips.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ips.R;
import com.example.ips.data.Room;
import com.example.ips.helpers.JSONStorage;

import java.util.List;

public class Home extends Fragment {
    private TextView tvRoomCount, tvNotifStatus;
    private EditText timeNotif;

    public Home() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvRoomCount = view.findViewById(R.id.tvRoomCount);
        tvNotifStatus = view.findViewById(R.id.tvNotifStatus);
        timeNotif = view.findViewById(R.id.timeNotif);
        Button btnSave = view.findViewById(R.id.btnSaveNotification);
        loadSavedPreference();
        btnSave.setOnClickListener(v -> savePreference());
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Room> rooms = JSONStorage.loadRooms(requireContext());
        tvRoomCount.setText(String.valueOf(rooms.size()));
    }

    private void savePreference() {
        String input = timeNotif.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(requireContext(), "Enter minutes", Toast.LENGTH_SHORT).show();
            return;
        }
        int minutes = Integer.parseInt(input);
        if (minutes <= 0) {
            Toast.makeText(requireContext(), "Enter a number greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("ips_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("notify_minutes", minutes);
        editor.apply();

        tvNotifStatus.setText("You'll be notified " + minutes + " min before class");
        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    private void loadSavedPreference() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("ips_prefs", Context.MODE_PRIVATE);
        int saved_min = sharedPref.getInt("notify_minutes", 0);
        if (saved_min == 0) return;
        timeNotif.setText(String.valueOf(saved_min));
        tvNotifStatus.setText("You'll be notified " + saved_min + " min before class");
    }
}