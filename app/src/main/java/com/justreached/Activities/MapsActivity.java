package com.justreached.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.justreached.Others.MapPathDrawer;
import com.justreached.Others.MyJSONParser;
import com.justreached.R;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, PlaceSelectionListener {

    private GoogleMap mMap;
    EditText editText;
    Toolbar toolbar;
    TextToSpeech t1;
    public EditText searchEditText = null;

   DrawerLayout drawerLayout;
//    RecyclerViewAdapter recyclerViewAdapter;
//    RecyclerView recyclerView;
//    RecyclerView.LayoutManager layoutManager;
//    ArrayList<String> arrayList = new ArrayList<String>();
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    List<Address> addressList = null;
    int GPSoff = 0;
    boolean networkConnectionEnabled = false;

    private static final int REQUEST_SELECT_PLACE = 1234;
    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Fabric.with(this, new Crashlytics());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Log.d("my_app","in oncreate");
        mapFragment.getMapAsync(MapsActivity.this);
        Log.d("my_app","after getmapasync");
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });



        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    Log.d("MY_APP","*****Search edit text Touched...");
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder
                                (PlaceAutocomplete.MODE_FULLSCREEN)
                                //.setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                                .build(MapsActivity.this);
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
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MY_APP","*****Search edit text clicked...");
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_FULLSCREEN)
                            //.setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, REQUEST_SELECT_PLACE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

       // drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//
//        String[] names = getResources().getStringArray(R.array.array_names);
//        for(String name:names){
//            arrayList.add(name);
//        }
//
//        recyclerViewAdapter = new RecyclerViewAdapter(arrayList);
//        recyclerView.setAdapter(recyclerViewAdapter);
//
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
//        drawerLayout.setDrawerListener(actionBarDrawerToggle);

//        navigationView = (NavigationView) findViewById(R.id.navigationview);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.home:
//                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.main_container,new HomeFragment());
//                        fragmentTransaction.commit();
//                        getSupportActionBar().setTitle("Home");
//                        item.setChecked(true);
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case R.id.contacts:
//                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.main_container,new ContactsFragment());
//                        fragmentTransaction.commit();
//                        getSupportActionBar().setTitle("Contacts");
//                        item.setChecked(true);
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case R.id.share:
//                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.main_container,new ShareFragment());
//                        fragmentTransaction.commit();
//                        getSupportActionBar().setTitle("Share");
//                        drawerLayout.closeDrawers();
//                        item.setChecked(true);
//                        break;
//
//                    case R.id.settings:
//                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.main_container,new SettingsFragment());
//                        fragmentTransaction.commit();
//                        getSupportActionBar().setTitle("Settings");
//                        item.setChecked(true);
//                        drawerLayout.closeDrawers();
//                        break;
//
//                }
//                return true;
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        actionBarDrawerToggle.syncState();
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("my_app","map type " + String.valueOf(mMap.getMapType()));



        if (!checkNetworkConnection(MapsActivity.this)) {
            showNetDisabledAlertToUser(MapsActivity.this);
        }
        enableLocation(MapsActivity.this);

        //Initialize Google Play Services
//        LatLng sydney = new LatLng(0, 0);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));




        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("my_app", "no permissions");

//            AlertDialog.Builder builder =
//                    new AlertDialog.Builder(MapsActivity.this);
//            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
//            final String message = "Enable either GPS or any other location"
//                    + " service to find current location.  Click OK to go to"
//                    + " location services settings to let you do so.";
//
//            builder.setMessage(message)
//                    .setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface d, int id) {
//                                    startActivity(new Intent(action));
//                                    d.dismiss();
//
//                                }
//                            })
//                    .setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface d, int id) {
//                                    d.cancel();
//                                }
//                            });
//            builder.create();
//            builder.show();
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getCurrentLatitudeAndLongitude();
    }
/*
    public void Search(View view) {

        editText = (EditText) findViewById(R.id.searchEditText);
        String location = editText.getText().toString();
        t1.speak("You will reach your destination in some time.", TextToSpeech.QUEUE_FLUSH, null);

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                Log.d("my_app", addressList.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("my_app", "error:" + e);
//                AlertDialog.Builder builder =
//                        new AlertDialog.Builder(MapsActivity.this);
//                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
//                final String message = "Enable either GPS or any other location"
//                        + " service to find current location.  Click OK to go to"
//                        + " location services settings to let you do so.";
//
//                builder.setMessage(message)
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface d, int id) {
//                                        startActivity(new Intent(action));
//                                        d.dismiss();
//                                    }
//                                })
//                        .setNegativeButton("Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface d, int id) {
//                                        d.cancel();
//                                    }
//                                });
//                builder.create();
//                builder.show();
                return;
            }
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng_dest = new LatLng(address.getLatitude(), address.getLongitude());
                setLocationData("DEST_LATITUDE", Double.toString(latLng_dest.latitude));
                setLocationData("DEST_LONGITUDE", Double.toString(latLng_dest.longitude));


                mMap.addMarker(new MarkerOptions().position(latLng_dest).title("Place is" + location));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_dest));
                locationZoom(address.getLatitude(), address.getLongitude(), 17);
                mMap.getUiSettings().setMapToolbarEnabled(true);

                //Intent intent = new Intent(getApplicationContext(),ContactSettings.class);
                //startActivity(intent);

                String requestURL = MapPathDrawer.createGoogleMapsServiceRequestURL(Float.parseFloat(getLocationData("SRC_LATITUDE")), Float.parseFloat(getLocationData("SRC_LONGITUDE")), address.getLatitude(), address.getLongitude());
                AddPathOnMapextends addPathAsync = new AddPathOnMapextends(requestURL);
                addPathAsync.execute();
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationManager locationManager = (LocationManager) (MapsActivity.this).getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            } else {
                Log.d("my_app", "in else");
                Toast.makeText(getApplicationContext(), "no results found for " + location, Toast.LENGTH_LONG).show();

            }
        }
    }
*/

    public void locationZoom(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void getCurrentLatitudeAndLongitude() {
        //Get user current location
        float latitude = 0;
        float longitude = 0;
        Location location = null;

        LocationManager locationManager = (LocationManager) (MapsActivity.this).getSystemService(Context.LOCATION_SERVICE);
        //getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            //Network or gps is not enabled
            latitude = 0.0f;
            longitude = 0.0f;

            LatLng latLng_src = new LatLng(latitude,longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_src));
            locationZoom(latitude,longitude, 17);
            mMap.getUiSettings().setMapToolbarEnabled(true);

            //store this in shared preferences
            setLocationData("SRC_LATITUDE", Float.toString(latitude));
            setLocationData("SRC_LONGITUDE", Float.toString(longitude));
        }

        if (isGPSEnabled) {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location == null)
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

              /* if(location == null)
                   location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);*/
                if (location != null) {
                    latitude = (float) location.getLatitude();
                    longitude = (float) location.getLongitude();

                    LatLng latLng_src = new LatLng(latitude,longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_src));
                    locationZoom(latitude,longitude, 17);
                    mMap.getUiSettings().setMapToolbarEnabled(true);

                    //store this in shared preferences
                    setLocationData("SRC_LATITUDE", Float.toString(latitude));
                    setLocationData("SRC_LONGITUDE", Float.toString(longitude));
                }
            }
        }
    }

    public void setLocationData(String key, String value) {
        SharedPreferences sharedpreferences = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getLocationData(String key) {
        SharedPreferences sharedpreferences = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        String str = sharedpreferences.getString(key, null);
        return str;
    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = 0.0;
        double longitude = 0.0;
        Location cur_location = location;
        Location dest_location = null;
        Float radius = 10f;
        Float distance = 0f;

        LocationManager locationManager = (LocationManager) (MapsActivity.this).getSystemService(Context.LOCATION_SERVICE);
        dest_location = new Location(locationManager.GPS_PROVIDER);
        dest_location.setLatitude(Double.parseDouble(getLocationData("DEST_LATITUDE")));
        dest_location.setLongitude(Double.parseDouble(getLocationData("DEST_LONGITUDE")));
        distance = (Float) cur_location.distanceTo(dest_location);
        if (distance < radius) {
            //Toast.makeText(getApplicationContext(), "You are in the radius of your destination", Toast.LENGTH_LONG).show();
            t1.speak("You will reach your destination in some time", TextToSpeech.QUEUE_FLUSH, null);
            sendMessage();

        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.d("MY_APP","***A place has been selected. Selected place is: "+place.getAddress()+place.getName());

        searchEditText.setText(getString(R.string.formatted_place_data, place
                .getName(), place.getAddress(), place.getPhoneNumber(), place
                .getWebsiteUri(), place.getRating(), place.getId()));
        /*if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }*/
    }

    @Override
    public void onError(Status status) {
        Log.d("MY_APP","***Inside on error. Error status:"+status.getStatusMessage());
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
            progressDialog = new ProgressDialog(MapsActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
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
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    public boolean checkNetworkConnection(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }


    public static void showNetDisabledAlertToUser(final Context context) {
        Log.d("my_app","showNetDisabledAlertToUser");
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

    public void enableLocation(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MapsActivity.this);
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

    public void sendMessage(){

    }
}