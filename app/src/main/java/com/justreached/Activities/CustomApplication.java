package com.justreached.Activities;

import android.app.Application;
import android.content.Intent;
import android.location.Location;

import com.justreached.Services.MyLocationService;

/**
 * Created by Zaks on 5/16/2017.
 */

public class CustomApplication extends Application {

    public static Location srcLocation = null;
    public static Location destnLocation = null;
    public static boolean isMapReadyCalled = false;
    public static boolean isDestionationReached = true;



    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent i = new Intent(CustomApplication.this,MyLocationService.class);
        stopService(i);
    }
}
