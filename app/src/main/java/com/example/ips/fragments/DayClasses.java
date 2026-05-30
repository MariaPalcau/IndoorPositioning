package com.example.ips.fragments;

import static com.example.ips.helpers.AlarmScheduler.scheduleNextCheck;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ips.R;
import com.example.ips.adapters.ClassAdapter;
import com.example.ips.helpers.JSONStorage;
import com.example.ips.data.Course;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.ips.data.ClassClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DayClasses extends Fragment {
    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private FloatingActionButton btnAdd;
    private List<Course> classes;
    private String day;

    public DayClasses() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_classes, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnAdd = view.findViewById(R.id.btnAdd);
        TextView txtDay = view.findViewById(R.id.txtDay);
        txtDay.setText(day);
        classes = JSONStorage.loadClasses(requireContext(), day);
        if (classes == null) {
            classes = new ArrayList<>();
        }
        adapter = new ClassAdapter(classes, new ClassClickListener() {
            @Override
            public void classShortClick(int position) {
                editClass(position);
            }

            @Override
            public void classLongClick(int position) {
                deleteClass(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getString("day");
        }
    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        TextView titleN = new TextView(requireContext());
        titleN.setText("Name: ");
        TextView titleR = new TextView(requireContext());
        titleR.setText("Room: ");
        TextView titleT = new TextView(requireContext());
        titleT.setText("Time: ");

        EditText name = new EditText(requireContext());
        name.setEms(6);
        EditText room = new EditText(requireContext());
        room.setEms(6);
        EditText time = new EditText(requireContext());
        time.setEms(3);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(time);
            }
        });

        builder.setTitle("Insert a new class");

        LinearLayout layoutParent = new LinearLayout(requireContext());
        layoutParent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layoutName = new LinearLayout(requireContext());
        layoutName.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout layoutRoom = new LinearLayout(requireContext());
        layoutRoom.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout layoutTime = new LinearLayout(requireContext());
        layoutTime.setOrientation(LinearLayout.HORIZONTAL);

        layoutName.addView(titleN);
        layoutName.addView(name);
        layoutParent.addView(layoutName);

        layoutRoom.addView(titleR);
        layoutRoom.addView(room);
        layoutParent.addView(layoutRoom);

        layoutTime.addView(titleT);
        layoutTime.addView(time);
        layoutParent.addView(layoutTime);

        builder.setView(layoutParent);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String className = name.getText().toString();
                String classRoom = room.getText().toString();
                String classTime = time.getText().toString();
                if (className.isEmpty() || classRoom.isEmpty() || classTime.isEmpty()) return;
                Course newCourse = new Course(className, classRoom, classTime);
                classes.add(newCourse);
                JSONStorage.saveClasses(requireContext(), day, classes);
                adapter.updateClasses(classes);
                scheduleNextCheck(requireContext());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void editClass(int position) {
        Course course = classes.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Class");
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText name = new EditText(requireContext());
        name.setText(course.getName());
        EditText room = new EditText(requireContext());
        room.setText(course.getRoom());
        EditText time = new EditText(requireContext());
        time.setText(course.getTime());

        time.setOnClickListener(v -> showTimePicker(time));

        layout.addView(name);
        layout.addView(room);
        layout.addView(time);

        builder.setView(layout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                course.setName(name.getText().toString());
                course.setRoom(room.getText().toString());
                course.setTime(time.getText().toString());
                JSONStorage.saveClasses(requireContext(), day, classes);
                adapter.notifyItemChanged(position);
                scheduleNextCheck(requireContext());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void deleteClass(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete")
                .setMessage("Delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    classes.remove(position);
                    JSONStorage.saveClasses(requireContext(), day, classes);
                    adapter.notifyItemRemoved(position);
                    scheduleNextCheck(requireContext());
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void showTimePicker(EditText target) {
        Calendar now = Calendar.getInstance();
        new TimePickerDialog(
                requireContext(),
                (view, hour, minute) ->
                        target.setText(String.format("%02d:%02d", hour, minute)),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true).show();
    }

}
