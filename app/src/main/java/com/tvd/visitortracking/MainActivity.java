package com.tvd.visitortracking;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.tvd.visitortracking.fragments.AllVisitorsData;
import com.tvd.visitortracking.fragments.Visitor_checkOut;
import com.tvd.visitortracking.fragments.Visitor_details;
import com.tvd.visitortracking.services.BluetoothService;
import com.tvd.visitortracking.values.GetSetValues;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RequestPermissionCode = 1;
    FragmentTransaction fragmentTransaction;
    GetSetValues getSetValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSetValues = new GetSetValues();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissionsMandAbove();
            }
        }, 1000);

        startup(new Visitor_details());
    }

    public GetSetValues getSetValues() {
        return getSetValues;
    }

    public void startup(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    public void addOnstartup(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_visitor_details:
                startup(new Visitor_details());
                break;

            case R.id.menu_visitors:
                startup(new AllVisitorsData());
                break;

            case R.id.menu_visitors_checkout:
                startup(new Visitor_checkOut());
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(23)
    private void checkPermissionsMandAbove() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            if (!checkPermission()) {
                requestPermission();
            } else startService();
        } else startService();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        ACCESS_FINE_LOCATION,
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean ReadLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadCameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (ReadLocationPermission && ReadCameraPermission && ReadStoragePermission) {
                        startService();
                    } else {
                        finish();
                    }
                }
                break;
        }
    }

    private void startService() {
        Intent intent = new Intent(MainActivity.this, BluetoothService.class);
        startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(MainActivity.this, BluetoothService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }
}
