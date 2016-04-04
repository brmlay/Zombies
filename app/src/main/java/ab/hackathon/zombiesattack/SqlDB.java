package ab.hackathon.zombiesattack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.*;
import android.util.Log;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static java.security.AccessController.getContext;

/**
 * Created by Lay on 4/2/2016.
 */
public class SqlDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "LocationInfo.db";
    public static final int DATABASE_VERSION = 4;

    public static final String TABLE_NAME = " Locations";
    public static final String TABLE_INDEX = " Id";
    public static final String COLUMN_NAME_LOCATION = " LocIdentifier";
    public static final String COLUMN_NAME_INFECTED = " InfectedZone";//For Location = USER, maintains survivor state
    public static final String COLUMN_NAME_LATITUDE = " Latitude";
    public static final String COLUMN_NAME_RADIUS = " Radius";
    public static final String COLUMN_NAME_LONGTITUDE = " Longitude";
    public static final String COLUMN_NAME_RISKLEVEL = " RiskPer";//lower value is higher risk based on timeout of geofence

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String PRIMARY = " PRIMARY KEY";
    private static final String AUTOINC = " AUTOINCREMENT";

    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_INDEX + INT_TYPE + PRIMARY + COMMA_SEP +
                    COLUMN_NAME_INFECTED + INT_TYPE + COMMA_SEP +
                    COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
                    COLUMN_NAME_LONGTITUDE + REAL_TYPE + COMMA_SEP +
                    COLUMN_NAME_RADIUS + INT_TYPE + COMMA_SEP +
                    COLUMN_NAME_RISKLEVEL + INT_TYPE +
            " );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Integer Count = 0;
    public Boolean isEmpty = true;

    public SqlDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void GetCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        //db.execSQL(SQL_DELETE_ENTRIES);
        Count = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        isEmpty = Count == 0;}

    public boolean InsertUpdate(Integer Insert, Integer Zone, String LocIdent, Double Lat, Double Long, Integer RiskLev, Integer Radius){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_INFECTED, Zone);
        contentValues.put(COLUMN_NAME_LOCATION, LocIdent);
        contentValues.put(COLUMN_NAME_LATITUDE, Lat);
        contentValues.put(COLUMN_NAME_LONGTITUDE, Long);
        contentValues.put(COLUMN_NAME_RADIUS, Radius);
        contentValues.put(COLUMN_NAME_RISKLEVEL, RiskLev);
        if(Insert == 0) {
            if (db.insert(TABLE_NAME, null, contentValues) != -1) {
                Count++;
                return true;
            } else
                return false;
        }else{
            if (db.update(TABLE_NAME, contentValues, "where "+TABLE_INDEX+" = ?", new String[]{Insert.toString()}) != -1) {
                return true;
            } else
                return false;
        }
    }

    public ContentValues GetRow(int index){
        ContentValues contentValues = new ContentValues();
        /*switch(index){
            default:
                Log.d("GetRow", "Index doesn't exist");
                break;
            case 1:
                contentValues.put(COLUMN_NAME_INFECTED, 0);
                contentValues.put(COLUMN_NAME_LOCATION, "USER");
                contentValues.put(COLUMN_NAME_LATITUDE, 41.078188);
                contentValues.put(COLUMN_NAME_LONGTITUDE, -85.120684);
                contentValues.put(COLUMN_NAME_RADIUS, 50);
                contentValues.put(COLUMN_NAME_RISKLEVEL, 0);
                break;
            case 2:
                contentValues.put(COLUMN_NAME_INFECTED, 1);
                contentValues.put(COLUMN_NAME_LOCATION, "IndianaTech");
                contentValues.put(COLUMN_NAME_LATITUDE, 41.0777421);
                contentValues.put(COLUMN_NAME_LONGTITUDE, -85.116988);
                contentValues.put(COLUMN_NAME_RADIUS, 250);
                contentValues.put(COLUMN_NAME_RISKLEVEL, 40);
                break;
            case 3:
                contentValues.put(COLUMN_NAME_INFECTED, 1);
                contentValues.put(COLUMN_NAME_LOCATION, "Lutheran");
                contentValues.put(COLUMN_NAME_LATITUDE, 41.0777421);
                contentValues.put(COLUMN_NAME_LONGTITUDE, -85.149494);
                contentValues.put(COLUMN_NAME_RADIUS, 250);
                contentValues.put(COLUMN_NAME_RISKLEVEL, 30);
                break;
            case 4:
                contentValues.put(COLUMN_NAME_INFECTED, 1);
                contentValues.put(COLUMN_NAME_LOCATION, "Neighborhood");
                contentValues.put(COLUMN_NAME_LATITUDE, 41.1164838);
                contentValues.put(COLUMN_NAME_LONGTITUDE, -85.1350291);
                contentValues.put(COLUMN_NAME_RADIUS, 250);
                contentValues.put(COLUMN_NAME_RISKLEVEL, 10);
                break;
            case 5:
                contentValues.put(COLUMN_NAME_INFECTED, 1);
                contentValues.put(COLUMN_NAME_LOCATION, "Home");
                contentValues.put(COLUMN_NAME_LATITUDE, 41.231201);
                contentValues.put(COLUMN_NAME_LONGTITUDE, -85.155945);
                contentValues.put(COLUMN_NAME_RADIUS, 500);
                contentValues.put(COLUMN_NAME_RISKLEVEL, 15);
                break;
        }*/
        //return contentValues;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor ret = null;
                ret = db.query(TABLE_NAME, new String[] { COLUMN_NAME_LOCATION, COLUMN_NAME_RADIUS, COLUMN_NAME_INFECTED,
                                COLUMN_NAME_LATITUDE, COLUMN_NAME_LONGTITUDE, COLUMN_NAME_RISKLEVEL }, TABLE_INDEX + "=?",
                        new String[] { String.valueOf(index) }, null, null, null, null);
        try {
            if (ret != null) {
                if (ret.getCount() > 0) {
                    if (ret.moveToFirst()) {
                        contentValues = new ContentValues(ret.getCount());
                        contentValues.put(COLUMN_NAME_LONGTITUDE, ret.getDouble(ret.getColumnIndex(COLUMN_NAME_LONGTITUDE)));
                        contentValues.put(COLUMN_NAME_LATITUDE, ret.getDouble(ret.getColumnIndex(COLUMN_NAME_LATITUDE)));
                        contentValues.put(COLUMN_NAME_LOCATION, ret.getDouble(ret.getColumnIndex(COLUMN_NAME_LOCATION)));
                        contentValues.put(COLUMN_NAME_INFECTED, ret.getDouble(ret.getColumnIndex(COLUMN_NAME_INFECTED)));
                        contentValues.put(COLUMN_NAME_RISKLEVEL, ret.getDouble(ret.getColumnIndex(COLUMN_NAME_RISKLEVEL)));
                        contentValues.put(COLUMN_NAME_RADIUS, ret.getDouble(ret.getColumnIndex(COLUMN_NAME_RADIUS)));
                    }
                }
            }
        }catch (Exception e){
            Log.d("DB", e.getMessage());
        }finally {
            ret.close();
        }

        return contentValues;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
