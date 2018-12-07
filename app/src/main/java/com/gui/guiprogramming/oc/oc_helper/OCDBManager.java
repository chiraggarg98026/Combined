package com.gui.guiprogramming.oc.oc_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gui.guiprogramming.oc.oc_model.OCDBMap;
import com.gui.guiprogramming.oc.oc_model.Trip;

import java.util.ArrayList;

/**
 * OCDBManager class will help to store/update data to local SQLite database
 * OCDBManager is a helper class which provides functions/methods to
 * perform certain operation as per requirement
 */
public class OCDBManager {

    //current object of a class
    private DBOpenHelper dbOpenHelper;
    //context required to work with SQLite
    private Context context;

    private SQLiteDatabase database;

    /**
     * @param context - context of a current activity,
     *                used to establish SQLite database connection
     */
    public OCDBManager(Context context) {
        this.context = context;
    }

    /**
     * This method helps you to establish connection SQLite database, and
     * to store/update current data
     */
    public void open() throws SQLException {
        dbOpenHelper = new DBOpenHelper(context);
        database = dbOpenHelper.getWritableDatabase();
    }

    /**
     * This method closes the connection to SQLite database
     */
    public void close() {
        dbOpenHelper.close();
    }

    class DBOpenHelper extends SQLiteOpenHelper {

        //Database name
        static final String DATABASE_NAME = "GUI_OCTRANSPO_DB";
        //table name, which will be created later
        static final String TABLE_OC_TRANSPO = "OCTRANSPO";

        //Declaration of column names of OCTRANSPO table
        static final String OC_STOP_NO = "stopNo";
        static final String OC_STOP_NAME = "stopName";
        static final String OC_DIRECTION = "direction";
        static final String OC_ROUTE_LABEL = "routeLabel";
        static final String OC_ROUTE_NO = "routeNo";
        static final String OC_REQ_PROC_TIME = "requestProcessTime";
        static final String OC_DELAY = "delay";
        static final String OC_SPEED = "gpsSpeed";
        static final String OC_LAT = "latitude";
        static final String OC_LONG = "longitude";
        static final String OC_DESTINATION = "destination";
        static final String OC_START_TIME = "startTime";
        //Declaration of column names of OCTRANSPO table ends here

        /**
         * SQLiteDatabase open helper class constructor
         *
         * @param context required to handle SQLite database connection
         */
        DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        /**
         * onCreate() will be called once
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL = "CREATE TABLE " + TABLE_OC_TRANSPO + " (" +
                    OC_STOP_NO + " INTEGER," +
                    OC_STOP_NAME + " TEXT," +
                    OC_DIRECTION + " TEXT," +
                    OC_ROUTE_LABEL + " TEXT," +
                    OC_ROUTE_NO + " INTEGER," +
                    OC_REQ_PROC_TIME + " TEXT," +
                    OC_DELAY + " TEXT," +
                    OC_SPEED + " REAL," +
                    OC_LAT + " REAL," +
                    OC_LONG + " REAL," +
                    OC_DESTINATION + " TEXT," +
                    OC_START_TIME + " TEXT" +
                    ")";
            db.execSQL(SQL);
        }

        /**
         * onUpgrade() will be called if we increment
         * the current database version number
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OC_TRANSPO);
            onCreate(db);
        }
    }

    /**
     * <p>This method checks detects current data is exists or not,
     * can be used before inserting or deleting the row.
     * OCDBManager#open() method should be called before using this method</p>
     * @return true is record already exists, otherwise false
     * @see OCDBManager#open()
     * */
    public boolean isExists(int stopNo, String direction, int routeNo, String destination, String startTime) {
        String[] columns = new String[]{DBOpenHelper.OC_STOP_NO,
                DBOpenHelper.OC_DIRECTION, DBOpenHelper.OC_ROUTE_NO,
                DBOpenHelper.OC_DESTINATION, DBOpenHelper.OC_START_TIME};
        Cursor cursor = database.query(DBOpenHelper.TABLE_OC_TRANSPO,
                columns,
                DBOpenHelper.OC_STOP_NO + " = ? AND " +
                        DBOpenHelper.OC_DIRECTION + " = ? AND " +
                        DBOpenHelper.OC_ROUTE_NO + " = ? AND " +
                        DBOpenHelper.OC_DESTINATION + " = ? AND " +
                        DBOpenHelper.OC_START_TIME + " = ?",
                new String[]{String.valueOf(stopNo), direction, String.valueOf(routeNo), destination, startTime},
                null,
                null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /**
     * <p>This method inserts a row into a table.
     * Used to save a trip information into local database.
     * Sqlite database has to be opened as writable database to use this.</p>
     * @return column index if record inserted successfully, otherwise -1
     * @see SQLiteDatabase#insert(String, String, ContentValues)
     * @see OCDBManager#open()
     * */
    public long saveTrip(int stopNo, String stopName, String direction, String routeLabel,
                         int routeNo, String reqProcTime, Trip trip) {
        ContentValues cv = new ContentValues();
        cv.put(DBOpenHelper.OC_STOP_NO, stopNo);
        cv.put(DBOpenHelper.OC_STOP_NAME, stopName);
        cv.put(DBOpenHelper.OC_DIRECTION, direction);
        cv.put(DBOpenHelper.OC_ROUTE_LABEL, routeLabel);
        cv.put(DBOpenHelper.OC_ROUTE_NO, routeNo);
        cv.put(DBOpenHelper.OC_REQ_PROC_TIME, reqProcTime);
        cv.put(DBOpenHelper.OC_DELAY, trip.getAdjustedScheduleTime());
        cv.put(DBOpenHelper.OC_SPEED, trip.getGPSSpeed());
        cv.put(DBOpenHelper.OC_LAT, trip.getLatitude());
        cv.put(DBOpenHelper.OC_LONG, trip.getLongitude());
        cv.put(DBOpenHelper.OC_DESTINATION, trip.getTripDestination());
        cv.put(DBOpenHelper.OC_START_TIME, trip.getTripStartTime());
        return database.insert(DBOpenHelper.TABLE_OC_TRANSPO, null, cv);
    }

    /**
     * <p>This method deletes a row into a table.
     * Used to save a trip information into local database.
     * Sqlite database has to be opened as writable database to use this.</p>
     * @see SQLiteDatabase#delete(String, String, String[])
     * @see OCDBManager#open()
     * */
    public boolean removeTrip(int stopNo, String direction, int routeNo, String destination, String startTime) {
        return database.delete(DBOpenHelper.TABLE_OC_TRANSPO,
                DBOpenHelper.OC_STOP_NO + " = ? AND " +
                        DBOpenHelper.OC_DIRECTION + " = ? AND " +
                        DBOpenHelper.OC_ROUTE_NO + " = ? AND " +
                        DBOpenHelper.OC_DESTINATION + " = ? AND " +
                        DBOpenHelper.OC_START_TIME + " = ?",
                new String[]{String.valueOf(stopNo),
                        direction, String.valueOf(routeNo), destination, startTime}) > 0;
    }

    /**
     * <p>This method returns all the rows stored in table.
     * Used to display the record as SAVED TRIPS</p>
     * @return OCDBMap ArrayList
     * */
    public ArrayList<OCDBMap> getTrips() {
        ArrayList<OCDBMap> maps = new ArrayList<>();
        String[] columns = new String[]{DBOpenHelper.OC_STOP_NO,
                DBOpenHelper.OC_STOP_NAME, DBOpenHelper.OC_DIRECTION,
                DBOpenHelper.OC_ROUTE_LABEL, DBOpenHelper.OC_ROUTE_NO,
                DBOpenHelper.OC_REQ_PROC_TIME, DBOpenHelper.OC_DELAY,
                DBOpenHelper.OC_SPEED, DBOpenHelper.OC_LAT, DBOpenHelper.OC_LONG,
                DBOpenHelper.OC_DESTINATION, DBOpenHelper.OC_START_TIME};
        Cursor cursor = database.query(DBOpenHelper.TABLE_OC_TRANSPO,
                columns,
                null, null,
                null,
                null, null);
        if (cursor.moveToFirst()) {
            do {
                int stopNo = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.OC_STOP_NO));
                String stopName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.OC_STOP_NAME));
                String direction = cursor.getString(cursor.getColumnIndex(DBOpenHelper.OC_DIRECTION));
                String routeLabel = cursor.getString(cursor.getColumnIndex(DBOpenHelper.OC_ROUTE_LABEL));
                int routeNo = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.OC_ROUTE_NO));
                String reqProcessingTime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.OC_REQ_PROC_TIME));
                String delay = cursor.getString(cursor.getColumnIndex(DBOpenHelper.OC_DELAY));
                float speed = cursor.getFloat(cursor.getColumnIndex(DBOpenHelper.OC_SPEED));
                double lat = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.OC_LAT));
                double lon = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.OC_LONG));
                String destination = cursor.getString(cursor.getColumnIndex(DBOpenHelper.OC_DESTINATION));
                String tripStartTime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.OC_START_TIME));
                OCDBMap map = new OCDBMap();
                map.setStopNo(stopNo);
                map.setStopName(stopName);
                map.setStopNo(stopNo);
                map.setDirection(direction);
                map.setRouteLabel(routeLabel);
                map.setRouteNo(routeNo);
                map.setReqProcessingTime(reqProcessingTime);
                Trip trip = new Trip();
                trip.setGPSSpeed(speed);
                trip.setLongitude(lon);
                trip.setLatitude(lat);
                trip.setAdjustedScheduleTime(delay);
                trip.setTripDestination(destination);
                trip.setTripStartTime(tripStartTime);
                map.setTrip(trip);
                maps.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return maps;
    }

    /**
     * @return no. of records stored in database, returns 0 if no data is stored in DB
     */
    public int getCount() {
        int count = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " +
                DBOpenHelper.TABLE_OC_TRANSPO, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * @return Minimum delay value
     */
    public double getMinDelay() {
        double min = 0;
        Cursor cursor = database.rawQuery("SELECT min(" + DBOpenHelper.OC_DELAY + ") FROM "
                + DBOpenHelper.TABLE_OC_TRANSPO, null);
        if (cursor.moveToFirst()) {
            min = cursor.getDouble(0);
        }
        cursor.close();
        return min;
    }

    /**
     * @return Average delay value
     */
    public double getAvgDelay() {
        double avg = 0;
        Cursor cursor = database.rawQuery("SELECT avg(" + DBOpenHelper.OC_DELAY + ") FROM "
                + DBOpenHelper.TABLE_OC_TRANSPO, null);
        if (cursor.moveToFirst()) {
            avg = cursor.getDouble(0);
        }
        cursor.close();
        return avg;
    }

    /**
     * @return Maximum delay value
     */
    public double getMaxDelay() {
        double max = 0;
        Cursor cursor = database.rawQuery("SELECT max(" + DBOpenHelper.OC_DELAY + ") FROM "
                + DBOpenHelper.TABLE_OC_TRANSPO, null);
        if (cursor.moveToFirst()) {
            max = cursor.getDouble(0);
        }
        cursor.close();
        return max;
    }
}
