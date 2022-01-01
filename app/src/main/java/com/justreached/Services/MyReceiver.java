package com.justreached.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.justreached.Activities.DestinationReachedActivity;

/**
 * Created by Syed.Zakriya on 18-06-2017.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context.getApplicationContext(),"OnReceive called....",Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.setClass(context.getApplicationContext(), DestinationReachedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
