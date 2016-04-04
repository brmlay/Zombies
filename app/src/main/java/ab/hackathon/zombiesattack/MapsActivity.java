package ab.hackathon.zombiesattack;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private int myStatus = 0;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        /*SqlDB mySQL = new SqlDB(this);
        ContentValues results = new ContentValues();
        mySQL.GetRow("select * from " + mySQL.TABLE_NAME + " where " + mySQL.COLUMN_NAME_LOCATION + " = \"USER\"", results);*/

        FloatingActionButton Bite = (FloatingActionButton) findViewById(R.id.Bite);
        Bite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: Infect close people
            }
        });

        if (myStatus == 1) {
            Bite.show();
            Bite.setClickable(true);
        }else{
            Bite.hide();
            Bite.setClickable(false);
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    GoogleApiClient LocReqAPI;
    LocationRequest locationRequest;

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        locationRequest = new LocationRequest();
        LocReqAPI = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            }
        }
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        enableMyLocation();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                LocReqAPI, locationRequest, this);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            onMyLocationButtonClick();//Zoom to our location
           /* mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(new LatLng(-33.87365, 151.20689))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build()));*/
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mCurrentLocation = mMap.getMyLocation();
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    Location mCurrentLocation;

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        onMyLocationButtonClick();
        Intent myIntent = new Intent(this, Main2Activity.class);
        //check for GeoFences
    }

    /*public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;

        protected Location mapLocation;
        SqlDB mydb;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         *
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mydb = new SqlDB(this);
            String Query = "select * from " + mydb.TABLE_NAME + " where " +
                    mydb.COLUMN_NAME_LOCATION + " = \'USER\'";
            ContentValues results = new ContentValues();
            mydb.GetRow(Query, results);
            mapLocation.setLatitude(results.getAsDouble(mydb.COLUMN_NAME_LATITUDE));
            mapLocation.setLongitude(results.getAsDouble(mydb.COLUMN_NAME_LONGTITUDE));

            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title(results.getAsString(mydb.COLUMN_NAME_LOCATION)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }*/
    public static final String TAG = "debug";

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                }
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(LocReqAPI);
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        LocReqAPI.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}
