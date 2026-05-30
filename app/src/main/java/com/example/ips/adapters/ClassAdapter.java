package com.example.ips.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ips.R;
import com.example.ips.data.ClassClickListener;
import com.example.ips.data.Course;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    private List<Course> classes;
    private static ClassClickListener listener;

    public ClassAdapter(List<Course> classes, ClassClickListener listener) {
        this.classes = classes;
        ClassAdapter.listener = listener;
    }

    @NonNull
    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_row, parent, false);
        return new ClassAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ClassAdapter.ViewHolder viewHolder, int position) {
        Course classItem = classes.get(position);
        viewHolder.roomName.setText(classItem.getName());
        viewHolder.classRoom.setText(classItem.getRoom());
        viewHolder.classTime.setText(classItem.getTime());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, classRoom, classTime;
        public ViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.className);
            classRoom = itemView.findViewById(R.id.classRoom);
            classTime = itemView.findViewById(R.id.classTime);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.classShortClick(pos);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.classLongClick(pos);
                }
                return true;
            });
        }
    }
    public void updateClasses(List<Course> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }
}
