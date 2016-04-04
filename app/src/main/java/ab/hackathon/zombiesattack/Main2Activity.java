package ab.hackathon.zombiesattack;

/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.Manifest;
        import android.app.PendingIntent;
        import android.content.ContentValues;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
        import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
        import com.google.android.gms.common.api.ResultCallback;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.Geofence;
        import com.google.android.gms.location.GeofencingRequest;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.location.GeofencingApi;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptor;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.CircleOptions;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.PolygonOptions;

        import java.util.ArrayList;
        import java.util.Map;

/**
 * Demonstrates how to create and remove geofences using the GeofencingApi. Uses an IntentService
 * to monitor geofence transitions and creates notifications whenever a device enters or exits
 * a geofence.
 *
 * This sample requires a device's Location settings to be turned on. It also requires
 * the ACCESS_FINE_LOCATION permission, as specified in AndroidManifest.xml.
 *
 * Note that this Activity implements ResultCallback<Status>, requiring that
 * {@code onResult} must be defined. The {@code onResult} runs when the result of calling
 * {@link GeofencingApi#addGeofences(GoogleApiClient, GeofencingRequest, PendingIntent)}  addGeofences()} or
 * {@link com.google.android.gms.location.GeofencingApi#removeGeofences(GoogleApiClient, java.util.List)}  removeGeofences()}
 * becomes available.
 */
public class Main2Activity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>,
        OnMapReadyCallback, LocationListener {

    protected static final String TAG = "MainActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected GoogleMap mMap;
    protected FloatingActionButton Bite;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;

    LocationRequest locationRequest;

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        locationRequest = new LocationRequest();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            }
        }
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, "My location moved");
        //mMap.isMyLocationEnabled()
        //mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).title("Me"));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .zoom(15.5f)
                .bearing(0)
                .tilt(25)
                .build()));
        if(CheckInfectedStatus()){
            Bite.show();
            Bite.setClickable(true);
        }
        //onMyLocationButtonClick();
        //check for GeoFences
    }

    protected boolean CheckInfectedStatus(){
        SqlDB CheckInfected = new SqlDB(this);
        ContentValues UserStatus = CheckInfected.GetRow(1);
        return (UserStatus.getAsInteger(CheckInfected.COLUMN_NAME_INFECTED) == 1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bite = (FloatingActionButton) findViewById(R.id.Bite);
        Bite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: Infect close people
            }
        });
        if(CheckInfectedStatus()){
            Bite.show();
            Bite.setClickable(true);
        }else{
            Bite.hide();
        }

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Get the geofences used. Geofence data is hard coded in this sample.

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
        //mGoogleApiClient.connect();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    MarkerOptions myLocationMarker;
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            }
        }
        populateGeofenceList();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mCurrentLocation != null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position((new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude())));
            myLocationMarker.title("User");//todo: username
            mMap.addMarker(myLocationMarker);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build()));
            //enableMyLocation();
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
        Log.i(TAG, "Connected to GoogleApiClient");
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                // The GeofenceRequest object.
                getGeofencingRequest(),
                // A pending intent that that is reused when calling removeGeofences(). This
                // pending intent is used to generate an intent when a matched geofence
                // transition is observed.
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;

            Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.accept :
                            R.string.decline),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            Integer errorMessage = status.getStatusCode();
            Log.e(TAG, errorMessage.toString());
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, RuntimeDetails.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {
        SqlDB mydb = new SqlDB(this);
        mydb.GetCount();
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        //mGeofenceList = new ArrayList<Geofence>();
        for (Integer i = 1; i<=mydb.Count; i++) {
            ContentValues results = null;
            results = mydb.GetRow(i);
            if (results == null || (results.getAsInteger(mydb.COLUMN_NAME_INFECTED) == 0)) {
                continue;
            }
            CircleOptions fenceOptions = new CircleOptions();
            LatLng LatLong = new LatLng(
                    results.getAsDouble(mydb.COLUMN_NAME_LATITUDE),
                    results.getAsDouble(mydb.COLUMN_NAME_LONGTITUDE));
            mMap.addMarker(new MarkerOptions().position(LatLong).title("Infected "+ results.getAsString(mydb.COLUMN_NAME_LOCATION)));
            /*fenceOptions = fenceOptions.center(LatLong);
            fenceOptions = fenceOptions.radius(results.getAsFloat(mydb.COLUMN_NAME_RADIUS));
            fenceOptions = fenceOptions.strokeColor(204);
            fenceOptions = fenceOptions.strokeWidth(100);
            fenceOptions = fenceOptions.fillColor(204);
            mMap.addCircle(fenceOptions);*/
            mGeofenceList.add(new Geofence.Builder()
                    .setCircularRegion(
                            results.getAsDouble(mydb.COLUMN_NAME_LATITUDE),
                            results.getAsDouble(mydb.COLUMN_NAME_LONGTITUDE),
                            results.getAsFloat(mydb.COLUMN_NAME_RADIUS))
                    .setLoiteringDelay(results.getAsInteger(mydb.COLUMN_NAME_RISKLEVEL) * 1000)
                    .setNotificationResponsiveness(10000)
                    .setTransitionTypes(
                            Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT |
                                    Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setRequestId(results.getAsString(mydb.COLUMN_NAME_LOCATION))
                    .build());
        }
    }
}