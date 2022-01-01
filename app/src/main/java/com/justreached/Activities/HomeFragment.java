package com.justreached.Activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.justreached.Models.Contact;
import com.justreached.Others.HelperMethods;
import com.justreached.R;
import com.justreached.Others.MapPathDrawer;
import com.justreached.Others.MyJSONParser;
import com.justreached.Sqlite.DBHandler;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, PlaceSelectionListener {

    public static GoogleMap mMap;
    EditText editText;
    Toolbar toolbar;
    TextToSpeech t1;
    Button button;
    public EditText searchEditText = null;
    public static boolean mapIsVisible = false;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    LocationManager locationManager;
    SharedPreferences sharedpreferences;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    List<Address> addressList = null;
    int GPSoff = 0;
    boolean networkConnectionEnabled = false;

    //public static Location sourceLocation = null;
    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;
    private static final int REQUEST_SELECT_PLACE = 1234;
    String selectedContact = null;


    public static final String SMS_KEY = "IsSMSEnabled";
    public static final String PHONE_KEY = "IsPhoneCallEnabled";
    public static final String SAVED_MSG_KEY = "MyMessage";
    public static final String WHATSAPP_PKG_NAME = "com.whatsapp";

    public ArrayList<Contact> contactsInSqlite = null;
    public DBHandler dbHandler = null;
    public String Name = null;
    public String PhoneNumber = null;
    public String email = null;

    public ProgressDialog progress;
    //PowerManager.WakeLock wakeLock;

    public HomeFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_home, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapIsVisible = true;
        mapFragment.getMapAsync(this);
        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        AdView adView3 = (AdView) fragment_view.findViewById(R.id.adView3);
        AdRequest adRequest3 = new AdRequest.Builder().build();
        adView3.loadAd(adRequest3);


        setHasOptionsMenu(true);
        //Code to launch auto fill-------DO NOT DELETE

        // initialise the wake lock
        //PowerManager mgr = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        //wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");

        searchEditText = (EditText) fragment_view.findViewById(R.id.mySearchEditText);

        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    Log.d("MY_APP", "*****Search edit text Touched...");
                    progress = ProgressDialog.show(getActivity(), "Please wait",
                            "Loading...", true);


                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder
                                (PlaceAutocomplete.MODE_FULLSCREEN)
                                //.setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                                .build(getActivity());
                        startActivityForResult(intent, REQUEST_SELECT_PLACE);
                    } catch (GooglePlayServicesRepairableException |
                            GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });


        return fragment_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //hide the loading progress dialog
        new Thread(new Runnable() {
            @Override
            public void run() {
                // do the thing that takes a long time

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
            }
        }).start();
        selectedContact = null;
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == getActivity().RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        sharedpreferences = getActivity().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (!checkNetworkConnection(getActivity())) {
            showNetDisabledAlertToUser(getActivity());
        }
        enableLocation(getContext());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("my_app", "no permissions");
            return;
        }

        mMap.setMyLocationEnabled(true);
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        if (!CustomApplication.isMapReadyCalled)
            getCurrentLatitudeAndLongitude();
    }

    public void locationZoom(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void getCurrentLatitudeAndLongitude() {
        CustomApplication.isMapReadyCalled = true;
        //Get user current location
        float latitude = 0;
        float longitude = 0;
        Location location = null;

        // LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            //Network or gps is not enabled
            latitude = 0.0f;
            longitude = 0.0f;

            LatLng latLng_src = new LatLng(latitude, longitude);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_src));
            //locationZoom(latitude, longitude, 17);
            //mMap.getUiSettings().setMapToolbarEnabled(true);

            //store this in shared preferences
            setLocationData("SRC_LATITUDE", Float.toString(latitude));
            setLocationData("SRC_LONGITUDE", Float.toString(longitude));
        }

        if (isGPSEnabled) {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                if (isGPSEnabled) {
                    //Toast.makeText(getActivity(), "gps is enabled", Toast.LENGTH_LONG).show();
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else if (isNetworkEnabled) {
                    //Toast.makeText(getActivity(), "network is enabled", Toast.LENGTH_LONG).show();
                    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (location == null)
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

              /* if(location == null)
                   location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);*/
                if (location != null) {
                    CustomApplication.srcLocation = location;
                    //Toast.makeText(getActivity(), "Location is not null\nLatitude: " + location.getLatitude() + "\nLongitude:" + location.getLongitude(), Toast.LENGTH_LONG).show();
                    latitude = (float) location.getLatitude();
                    longitude = (float) location.getLongitude();

                    LatLng latLng_src = new LatLng(latitude, longitude);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_src));
                    locationZoom(latitude, longitude, 17);
                   // mMap.getUiSettings().setMapToolbarEnabled(true);
                    //sourceLocation = location;
                    //store this in shared preferences
                    setLocationData("SRC_LATITUDE", Float.toString(latitude));
                    setLocationData("SRC_LONGITUDE", Float.toString(longitude));
                } else {
                   // Toast.makeText(getActivity(), "Location is NULL ******", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void setLocationData(String key, String value) {
        //   SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void finishTheActivity()
    {
        getActivity().finish();
    }

    public String getLocationData(String key) {

        String str = sharedpreferences.getString(key, null);
        return str;
    }
/*
    public void onLocationChanged(Location location) {
//        Toast.makeText(getActivity(),"in location changed",Toast.LENGTH_LONG).show();
        //sourceLocation = location;
        CustomApplication.srcLocation = location;
        double latitude = 0.0;
        double longitude = 0.0;
        Location cur_location = location;
        Location dest_location = null;
        Float radius = 100f;
        Float distance = 0f;
        setLocationData("SRC_LATITUDE", Double.toString(location.getLatitude()));
        setLocationData("SRC_LONGITUDE", Double.toString(location.getLongitude()));
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);

        //   locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        dest_location = new Location(locationManager.GPS_PROVIDER);
        String destnLat = getLocationData("DEST_LATITUDE");
        String destnLong = getLocationData("DEST_LONGITUDE");

        if (destnLat != null && destnLong != null) {
            dest_location.setLatitude(Double.parseDouble(getLocationData("DEST_LATITUDE")));
            dest_location.setLongitude(Double.parseDouble(getLocationData("DEST_LONGITUDE")));
            distance = (Float) cur_location.distanceTo(dest_location);

            if (!CustomApplication.isDestionationReached) {
                if (distance < radius) {
                    CustomApplication.isDestionationReached = true;
                    Toast.makeText(getActivity(), "You are in the radius of your destination", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        t1.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        t1.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null);
                    }


                    boolean smsEnabled = HelperMethods.fetchBoolFromSharedPreferences((MainActivity) getActivity(), SMS_KEY);
                    boolean phoneEnabled = HelperMethods.fetchBoolFromSharedPreferences((MainActivity) getActivity(), PHONE_KEY);
                    if (smsEnabled && !phoneEnabled) {
                        HelperMethods.sendSMS(getActivity());

                    }
                    if (phoneEnabled) {
                        fetchDataToNotify();
                    }
                }
            }
        }
    }*/

    @Override
    public void onPlaceSelected(Place place) {
        Log.d("MY_APP", "***A place has been selected. Selected place is: " + place.getAddress() + place.getName());
        searchEditText.setText("");
        searchEditText.setText(place.getAddress());
        LatLng latLang = place.getLatLng();
        Log.d("MY_APP", "Selected Latitude:" + latLang.latitude);
        Log.d("MY_APP", "Selected Longitude:" + latLang.longitude);

        setDestination(place);

        /*if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }*/
    }

    @Override
    public void onStop() {
        mapIsVisible = false;
        super.onStop();

    }

    @Override
    public void onError(Status status) {
        Log.d("MY_APP", "***Inside on error. Error status:" + status.getStatusMessage());
    }


    public void setDestination(Place destinationPlace) {
        float destLat = 0.0f;
        float destLong = 0.0f;
        /*if (CustomApplication.srcLocation != null)
            Toast.makeText(getActivity(), "SourceLocation is:\n Lat:" + CustomApplication.srcLocation.getLatitude() + "\nLong:" + CustomApplication.srcLocation.getLongitude(), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), "SourceLocation is null", Toast.LENGTH_LONG).show();*/

        if (destinationPlace != null) {
            destLat = (float) destinationPlace.getLatLng().latitude;
            destLong = (float) destinationPlace.getLatLng().longitude;
        }
        if (destLat != 0 && destLong != 0) {

            CustomApplication.isDestionationReached = false;
            setLocationData("DEST_LATITUDE", Double.toString(destLat));
            setLocationData("DEST_LONGITUDE", Double.toString(destLong));
            Location destnLocation = new Location("");
            destnLocation.setLatitude(destLat);
            destnLocation.setLongitude(destLong);
            CustomApplication.destnLocation = destnLocation;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(destinationPlace.getLatLng()).title("Place is" + destnLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(destinationPlace.getLatLng()));
            locationZoom(destLat, destLong, 16);
            mMap.getUiSettings().setMapToolbarEnabled(true);


            //Intent intent = new Intent(getActivity(),ContactsPickerActivity.class);
            //startActivity(intent);


            //String requestURL = MapPathDrawer.createGoogleMapsServiceRequestURL(Float.parseFloat(getLocationData("SRC_LATITUDE")), Float.parseFloat(getLocationData("SRC_LONGITUDE")), destLat, destLong);
            String requestURL = MapPathDrawer.createGoogleMapsServiceRequestURL(CustomApplication.srcLocation != null ? CustomApplication.srcLocation.getLatitude() : Float.parseFloat(getLocationData("SRC_LATITUDE")),
                    CustomApplication.srcLocation != null ? CustomApplication.srcLocation.getLongitude() : Float.parseFloat(getLocationData("SRC_LONGITUDE")),
                    destLat, destLong);
            HomeFragment.AddPathOnMapextends addPathAsync = new HomeFragment.AddPathOnMapextends(requestURL);
            addPathAsync.execute();
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //  LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
           // wakeLock.acquire();


        } else {
            Log.d("my_app", "in else");
            Toast.makeText(getActivity(), "No results found for entered location", Toast.LENGTH_LONG).show();

        }
    }

    class AddPathOnMapextends extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        AddPathOnMapextends(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);

            if (progressDialog != null)
                try {
                    progressDialog.show();
                } catch (Exception e) {
                    Log.d("MY_APP", "EXCEPTION: " + e.getStackTrace().toString());
                }
        }

        @Override
        protected String doInBackground(Void... params) {
            MyJSONParser jParser = new MyJSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                MapPathDrawer.drawPath(result, mMap);
            }
        }

    }

    public void onPause() {
        //Toast.makeText(getActivity(), "on pause method called", Toast.LENGTH_LONG).show();
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
        //getActivity().onBackPressed();
    }

    public boolean checkNetworkConnection(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifiNetwork = cm.getActiveNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        if (wifiNetwork != null && wifiNetwork.isConnected()) {
//            return true;
//        }
//        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if (mobileNetwork != null && mobileNetwork.isConnected()) {
//            return true;
//        }
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (activeNetwork != null && activeNetwork.isConnected()) {
//            return true;
//        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
                return true;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                return true;
            }
            if (networkInfo.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public static void showNetDisabledAlertToUser(final Context context) {
        Log.d("my_app", "showNetDisabledAlertToUser");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Would you like to enable it?")
                .setTitle("No Internet Connection")
                .setPositiveButton(" Enable Internet ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(dialogIntent);
                    }
                });

        alertDialogBuilder.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void enableLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "Enable either GPS or any other location"
                    + " service to find current location.  Click OK to go to"
                    + " location services settings to let you do so.";

            builder.setMessage(message)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    startActivity(new Intent(action));
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                }
                            });
            builder.create();
            builder.show();
        }
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.maptype_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return true;
    }

    public Vibrator vibration() {

        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        long[] pattern = {0, 3000, 0};

        v.vibrate(pattern, -1);
        return v;

    }

    public void fetchDataToNotify() {
        //Initialize the Sqlite DBHandler
        dbHandler = new DBHandler(getActivity());

        // get all contacts data from database
        contactsInSqlite = new ArrayList<Contact>();
        contactsInSqlite = dbHandler.getAllContacts();

        String[] phNum = new String[contactsInSqlite.size()];

        boolean phoneEnabled = HelperMethods.fetchBoolFromSharedPreferences((MainActivity) getActivity(), PHONE_KEY);

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("I want to call...");

        builder.setSingleChoiceItems(phNum, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        selectedContact = phNum[item];
                        /*Toast.makeText(getActivity(), phNum[item],
                                Toast.LENGTH_SHORT).show();*/
                    }
                });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    //Toast.makeText(getActivity(), "Selected phone number: " + selectedContact, Toast.LENGTH_SHORT).show();
                    String selectedPhoneNum[] = selectedContact.split("-");
                    callIntent.setData(Uri.parse("tel:" + selectedPhoneNum[1]));
                    String uri = "tel:" + selectedPhoneNum[1];
                    Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                    getActivity().finish();
                    startActivity(dialIntent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Your call has failed...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                System.exit(0);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //locationManager.removeUpdates(this);
        super.onDestroy();
    }



}




