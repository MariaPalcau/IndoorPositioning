package com.example.ips;

import static com.example.ips.helpers.AlarmScheduler.scheduleNextCheck;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ips.fragments.Home;
import com.example.ips.fragments.Rooms;
import com.example.ips.fragments.Timetable;
import com.example.ips.helpers.Permissions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
        setupBottomNav();
        if (savedInstanceState == null) {
            navigateTo(new Home());
        }
        if (Permissions.permissionsGranted(this)) {
            scheduleNextCheck(this);
        }
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return onNavItemSelected(menuItem);
            }
        });
    }

    private boolean onNavItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            navigateTo(new Home());
        } else if (id == R.id.menu_rooms) {
            navigateTo(new Rooms());
        } else if (id == R.id.menu_timetable) {
            navigateTo(new Timetable());
        } else {
            return false;
        }
        return true;
    }

    private void navigateTo(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}