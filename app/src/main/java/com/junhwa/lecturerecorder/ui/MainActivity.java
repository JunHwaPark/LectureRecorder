package com.junhwa.lecturerecorder.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.junhwa.lecturerecorder.player.PlayerService;
import com.junhwa.lecturerecorder.recorder.RecordService;
import com.junhwa.lecturerecorder.ui.fragment.ListFragment;
import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.ui.fragment.RecordFragment;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.File;

import static com.junhwa.lecturerecorder.recorder.RecordService.STOP_RECORD;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    final int REQUEST_PERMISSIONS = 100;
    Fragment recordFragment, listFragment;
    private DrawerLayout drawerLayout;
    public TabLayout tabs = null;

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
                    case R.id.navigation_item_directory_management:
                        Intent intent = new Intent(getApplicationContext(), LectureManagementActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_nomedia:
                        File nomedia = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()
                                + "/.nomedia");
                        if(nomedia.exists()){
                            nomedia.delete();
                            Toast.makeText(getApplicationContext(), ".nomedia deleted", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                nomedia.createNewFile();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), ".nomedia created", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.nav_sub_menu_item01:
                        startActivity(new Intent(getApplicationContext(), OssLicensesMenuActivity.class));
                        break;
                }
                return true;
            }
        });

        tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent = new Intent(getApplicationContext(), PlayerService.class);
                stopService(intent);
                intent = new Intent(getApplicationContext(), RecordService.class);
                intent.putExtra("COMMAND", STOP_RECORD);
                startService(intent);
                stopService(intent);
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
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
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
