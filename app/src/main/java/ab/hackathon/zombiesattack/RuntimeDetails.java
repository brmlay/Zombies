package ab.hackathon.zombiesattack;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingEvent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RuntimeDetails extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "ab.hackathon.zombiesattack.action.FOO";
    private static final String ACTION_BAZ = "ab.hackathon.zombiesattack.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "ab.hackathon.zombiesattack.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "ab.hackathon.zombiesattack.extra.PARAM2";



    public RuntimeDetails() {
        super("RuntimeDetails");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, RuntimeDetails.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, RuntimeDetails.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
            Integer errorMessage = geofencingEvent.getErrorCode();
            Log.e("Geofence Error", errorMessage.toString());
            return;
        }
            switch (geofencingEvent.getGeofenceTransition()){
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    //todo: random chance
                    if(true) {
                        Toast.makeText(this, R.string.Infected, Toast.LENGTH_LONG).show();
                        Log.d("GEOFENCE", "INFECTED from location");
                        SqlDB myDB = new SqlDB(this);
                        myDB.InsertUpdate(1, 1, "USER", 41.11, -85.11, 100, 125);
                    }else {
                        Toast.makeText(this, R.string.Evade_StillInZone, Toast.LENGTH_LONG).show();
                        Log.d("GEOFENCE", "Missed INFECTION from location");
                    }
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Toast.makeText(this, R.string.Escape, Toast.LENGTH_LONG).show();
                    Log.d("GEOFENCE", "Exited INFECTED location");
                    break;
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    Toast.makeText(this, R.string.Beware, Toast.LENGTH_LONG).show();
                    Log.d("GEOFENCE", "Entered INFECTED location");
                    break;
                default:
                    break;
            }
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
