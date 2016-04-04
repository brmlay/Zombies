package ab.hackathon.zombiesattack;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import android.database.sqlite.*;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StartupDetails extends Service {

    SqlDB mydb;

    public void InitializeSQL(){
        //todo: Reachout to Heroku for list of geofences.
        mydb = new SqlDB(this);
        mydb.GetCount();
        if(mydb.isEmpty) {
            Integer Insert = 0;
            mydb.InsertUpdate(Insert, 0, "USER", 41.078188, -85.120684, 0, 0);//Default to Indiana Tech until location is verified
            mydb.InsertUpdate(Insert, 1, "IndianaTech", 41.077801, -85.116988, 250, 40);
            mydb.InsertUpdate(Insert, 1, "Lutheran", 41.0777421, -85.149494, 250, 30);
            mydb.InsertUpdate(Insert, 1, "Neighborhood", 41.109804, -85.086535, 250, 60);
            mydb.InsertUpdate(Insert, 1, "Home", 41.231201, -85.155945, 250, 15);
            mydb.InsertUpdate(Insert, 1, "Walgreens", 41.192348, -85.166815, 500, 120);
        }else{
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        boolean retVal = checkPermission("android.permission.ACCESS_FINE_LOCATION",1,1) == PackageManager.PERMISSION_GRANTED;
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        InitializeSQL();
    }

    public StartupDetails() {
        super();
    }
}
