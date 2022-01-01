package com.justreached.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.justreached.R;
import com.justreached.Services.MyLocationService;
//import com.crashlytics.android.Crashlytics;
//
//import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    String shareBody = "https://play.google.com/store/apps/details?id=com.justreached&hl=en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(MainActivity.this,MyLocationService.class);
        startService(i);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Fabric.with(this, new Crashlytics());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new AppSettings());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Notification");

        navigationView = (NavigationView) findViewById(R.id.navigationview);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.home:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new AppSettings()).addToBackStack(null).commit();
                        getSupportActionBar().setTitle("Notification");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.contacts:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new RZContactPicker()).addToBackStack(null).commit();
                        getSupportActionBar().setTitle("Contacts");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;


                  case R.id.share:
                       /* fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new ShareFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Share");
                        drawerLayout.closeDrawers();
                        item.setChecked(true); */
                      drawerLayout.closeDrawers();
                      Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                      sharingIntent.setType("text/plain");
                      sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Notification ");

                      sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                      startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        break;


                    case R.id.settings:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new SettingsFragment()).addToBackStack(null).commit();
                        getSupportActionBar().setTitle("Settings");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.exit:
                        Intent i = new Intent(MainActivity.this,MyLocationService.class);
                        stopService(i);
                        finish();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

   @Override
    public void onBackPressed() {
       if (getFragmentManager().getBackStackEntryCount() > 0) {
           getFragmentManager().popBackStack();
        } else {
           super.onBackPressed();
           }
    }
}
