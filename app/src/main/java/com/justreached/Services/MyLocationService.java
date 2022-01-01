package com.justreached.Services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.justreached.Activities.CustomApplication;
import com.justreached.Activities.HomeFragment;
import com.justreached.Activities.MainActivity;
import com.justreached.Models.Contact;
import com.justreached.Others.HelperMethods;
import com.justreached.R;
import com.justreached.Sqlite.DBHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;


import static com.justreached.Activities.HomeFragment.PHONE_KEY;
import static com.justreached.Activities.HomeFragment.SMS_KEY;

/**
 * Created by Syed.Zakriya on 15-05-2017.
 */

public class MyLocationService extends Service {

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    public static int NOTIFICATION_ID = 90898;
    //TextToSpeech t1;
    Intent intent;
    int counter = 0;
    public ArrayList<Contact> contactsInSqlite = null;
    public DBHandler dbHandler = null;
    String selectedContact = null;
    public Handler myHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*t1 = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });*/
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Just Reached!!!")
                .setContentText("Using Location Service")
                .setContentIntent(pendingIntent).build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(getBaseContext(), "Service started...", Toast.LENGTH_LONG).show();
        myHandler = new Handler();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        //Toast.makeText(getBaseContext(),"1",Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= 23) {
            if (getBaseContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    getBaseContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return START_STICKY;
            }
        }
        //Toast.makeText(getBaseContext(),"2",Toast.LENGTH_LONG).show();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        //Toast.makeText(getBaseContext(),"3",Toast.LENGTH_LONG).show();
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent i = new Intent(getBaseContext(),MyLocationService.class);
        stopService(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.v("STOP_SERVICE", "DONE");
        //Toast.makeText(getBaseContext(), "Service destroyed...", Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= 23) {
            if (getBaseContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    getBaseContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.removeUpdates(listener);
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getBaseContext().getSystemService(ns);
        nMgr.cancel(NOTIFICATION_ID);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
       // Toast.makeText(getBaseContext(), "isBetterLocation", Toast.LENGTH_LONG).show();
        if (currentBestLocation == null) {
            previousBestLocation = location;
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            previousBestLocation = location;
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            previousBestLocation = location;
            return true;
        } else if (isNewer && !isLessAccurate) {
            previousBestLocation = location;
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            previousBestLocation = location;
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public void broadcastIntent(double lat, double longitude){
        Intent intent = new Intent();
        intent.putExtra("myLat",lat);
        intent.putExtra("myLong",longitude);
        intent.setAction("com.myService.DESTINATION_REACHED");
        sendBroadcast(intent);
    }



    public class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(final Location location) {
            //Toast.makeText(getBaseContext(), "Inside onlocationChanged", Toast.LENGTH_LONG).show();
            if (isBetterLocation(location, previousBestLocation)) {
                CustomApplication.srcLocation = location;
                //Toast.makeText(getBaseContext(), "Latitude:" + location.getLatitude() + "  ,  Longitude:" + location.getLongitude(), Toast.LENGTH_LONG).show();

                if(HomeFragment.mapIsVisible) {
                    final double mLat = location.getLatitude();
                    final double mLon = location.getLongitude();
                    //broadcastIntent(mLat, mLon);
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            /*LatLng latLng = new LatLng(mLat, mLon);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                            if (HomeFragment.mMap!=null)
                                HomeFragment.mMap.animateCamera(cameraUpdate);*/

                            if (HomeFragment.mMap!=null)
                                updateCameraBearing(HomeFragment.mMap,location.getBearing());
                        }
                    });
                }

                Location dest_location = new Location("");
                Float radius = 100f;
                Float distance = 0f;

                if (CustomApplication.destnLocation != null) {
                    dest_location.setLatitude(CustomApplication.destnLocation.getLatitude());
                    dest_location.setLongitude(CustomApplication.destnLocation.getLongitude());
                    distance = (Float) location.distanceTo(dest_location);

                    if (!CustomApplication.isDestionationReached) {
                        if (distance < radius) {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CustomApplication.isDestionationReached = true;
                                    //Toast.makeText(getBaseContext(), "You are in the radius of your destination", Toast.LENGTH_LONG).show();
                                   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        t1.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null, null);
                                    } else {
                                        t1.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null);
                                    }*/

                                    boolean smsEnabled = HelperMethods.fetchBoolFromSharedPreferences(getBaseContext(), SMS_KEY);
                                    boolean phoneEnabled = HelperMethods.fetchBoolFromSharedPreferences(getBaseContext(), PHONE_KEY);
                                    if (smsEnabled) {
                                        HelperMethods.sendSMS(getBaseContext());

                                    }
                                    if (phoneEnabled) {
                                        //fetchDataToNotify();
                                        broadcastIntent(location.getLatitude(),location.getLongitude());
                                    }
                                    CustomApplication.isDestionationReached =true;
                                    Intent i = new Intent(getBaseContext(),MyLocationService.class);
                                    stopService(i);
                                }
                            });
                        }
                    }
                }
            }
        }



        public Vibrator vibration() {

            Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);

            long[] pattern = {0, 3000, 0};

            v.vibrate(pattern, -1);
            return v;

        }

        public void fetchDataToNotify() {
            //Initialize the Sqlite DBHandler
            dbHandler = new DBHandler(getBaseContext());

            // get all contacts data from database
            contactsInSqlite = new ArrayList<Contact>();
            contactsInSqlite = dbHandler.getAllContacts();

            String[] phNum = new String[contactsInSqlite.size()];

            boolean phoneEnabled = HelperMethods.fetchBoolFromSharedPreferences(getBaseContext(), PHONE_KEY);

            vibration();
            Log.d("my_app", "phone enabled");
            Log.d("my_app", String.valueOf(contactsInSqlite.size()));

            for (int i = 0; i < contactsInSqlite.size(); i++) {
                Log.d("my_app", "inside loop");
                Contact contact = contactsInSqlite.get(i);
                String phoneNumber = contact.getName() + " " + "-" + " " + contact.getNumber();
                Log.d("my_app", phoneNumber);

                phNum[i] = phoneNumber;
            }

            alertDialogView(phNum);
        }

        private void alertDialogView(final String[] phNum) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());//ERROR ShowDialog cannot be resolved to a type
            builder.setTitle("I want to call...");

            builder.setSingleChoiceItems(phNum, -1,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            selectedContact = phNum[item];
                            /*Toast.makeText(getBaseContext(), phNum[item],
                                    Toast.LENGTH_SHORT).show();*/
                        }
                    });

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();

                    try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        //Toast.makeText(getBaseContext(), "Selected phone number: " + selectedContact, Toast.LENGTH_SHORT).show();
                        String selectedPhoneNum[] = selectedContact.split("-");
                        callIntent.setData(Uri.parse("tel:" + selectedPhoneNum[1]));
                        String uri = "tel:" + selectedPhoneNum[1];
                        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(dialIntent);
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Your call has failed...",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(getBaseContext(), "Fail", Toast.LENGTH_SHORT)
                            .show();
                    //getActivity().finish();
                    System.exit(0);
                }
            });

            AlertDialog alert = builder.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.show();
        }
        private void updateCameraBearing(GoogleMap googleMap, float bearing) {
            if ( googleMap == null) return;
            CameraPosition camPos = CameraPosition
                    .builder(
                            googleMap.getCameraPosition() // current Camera
                    )
                    .bearing(bearing)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
