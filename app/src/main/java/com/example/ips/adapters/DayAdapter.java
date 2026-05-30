package com.example.ips.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ips.R;
import com.example.ips.fragments.DayClasses;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private final FragmentActivity activity;
    public DayAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timetable_days, parent, false);
        return new DayAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DayAdapter.ViewHolder viewHolder, int position) {
        viewHolder.day.setText(days[position]);
        viewHolder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("day", days[position]);

            DayClasses fragment = new DayClasses();
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return days.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        public ViewHolder(View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
        }
    }
}
