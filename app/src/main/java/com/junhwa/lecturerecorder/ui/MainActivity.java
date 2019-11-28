package com.junhwa.lecturerecorder.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.junhwa.lecturerecorder.ui.fragment.ListFragment;
import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.ui.fragment.RecordFragment;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    final int REQUEST_PERMISSIONS = 100;
    Fragment recordFragment, listFragment;
    private DrawerLayout drawerLayout;

    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recordFragment = new RecordFragment();
        listFragment = new ListFragment();

        try {
            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.container, recordFragment).commit();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_nomedia:
                        Toast.makeText(getApplicationContext(), "nomedia", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.navigation_item_test:
                        Toast.makeText(getApplicationContext(), "Test mic", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = null;

                switch (tab.getPosition()) {
                    case 0:
                        selected = recordFragment;
                        break;
                    case 1:
                        selected = listFragment;
                        break;
                }
                manager.beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        AutoPermissions.Companion.loadAllPermissions(this, REQUEST_PERMISSIONS);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDenied(int i, String[] strings) {
        Toast.makeText(getApplicationContext(), "권한을 허용해주세요.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onGranted(int i, String[] strings) {

    }
}
