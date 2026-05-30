package com.example.ips.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ips.R;
import com.example.ips.data.RoomSavedListener;
import com.example.ips.adapters.RoomAdapter;
import com.example.ips.data.Room;
import com.example.ips.helpers.JSONStorage;

import java.util.List;

public class Rooms extends Fragment implements RoomSavedListener {
    protected RecyclerView recyclerView;
    protected RoomAdapter adapter;

    public Rooms() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Room> rooms = JSONStorage.loadRooms(requireContext());
        adapter = new RoomAdapter(requireContext(), rooms);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.scan_room_container, new ScanRoom())
                    .commit();
        }
    }
    @Override
    public void roomSaved() {
        List<Room> updatedRooms = JSONStorage.loadRooms(requireContext());
        adapter.updateRooms(updatedRooms);
    }
}