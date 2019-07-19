package DatabaseLayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import BusinessObjectLayer.ActualData;

import static android.app.DownloadManager.COLUMN_STATUS;

public class ActualTable {
    public static final String TABLE_NAME = "Actual";
    public static final String ACTUAL_ID = "Actual_ID"; // INT IDENTITY PRIMARY KEY,
    public static final String DEPARTURE_TIME = "Departure_Time"; // TIME(2),
    public static final String ARRIVAL_TIME = "Arrival_Time"; // TIME(2),
    public static final String FUEL_BALANCE = "Fuel_Balance"; // FLOAT,
    public static final String FUEL_USED = "Fuel_Used"; // FLOAT,
    public static final String TOTAL_TRIP_TIME = "Total_Trip_Duration"; // TIME(2),
    public static final String TOTAL_TRIP_DISTANCE = "Total_Trip_Distance"; // FLOAT,
    public static final String ARRIVAL_GATE_PARKING = "Gate_Parking_Name"; // VARCHAR(5)

    public static void onCreate(SQLiteDatabase db){
        String CREATE_TODOTABLE = "CREATE TABLE " + TABLE_NAME +
                "("+ ACTUAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                DEPARTURE_TIME + " DATETIME, "+
                ARRIVAL_TIME + " DATETIME, "+
                FUEL_BALANCE + " FLOAT, " +
                FUEL_USED + " FLOAT, " +
                TOTAL_TRIP_TIME + " DATETIME, " +
                TOTAL_TRIP_DISTANCE + " FLOAT, " +
                ARRIVAL_GATE_PARKING + " STRING" +
                ")";
        db.execSQL(CREATE_TODOTABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Gets the size of the table, equivalent to the total number of records in the table
    public static long getTableCount(SQLiteDatabase db){
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    /**
     * Method that allows a new data (New table row) to be added to the table
     * @Parameters: Database (db), ActualData (aD)
     * @return: True: For successful insertion
     *          False: For failed insertion
     */
    public static boolean insertItem(SQLiteDatabase db, ActualData aD){
        try {
            ActualData fp = new ActualData();
            fp = getActual(db, aD);
            if(fp == null) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(DEPARTURE_TIME, aD.getDepartureTime());
                contentValues.put(ARRIVAL_TIME, aD.getArrivalTime());
                contentValues.put(FUEL_BALANCE, aD.getFuelBalance());
                contentValues.put(FUEL_USED, aD.getFuelUsed());
                contentValues.put(TOTAL_TRIP_TIME, aD.getTotalTripDuration());
                contentValues.put(TOTAL_TRIP_DISTANCE, aD.getTotalTripDistance());
                contentValues.put(ARRIVAL_GATE_PARKING, aD.getGateParkingName());

                db.insertOrThrow(TABLE_NAME, null, contentValues);
                aD.setActual_ID(getActual(db, aD).getActual_ID());
            }
            else
                aD.setActual_ID(fp.getActual_ID());
            return true;
        }
        catch (SQLiteConstraintException e)
        {
            Log.e("Insert Actual Data",e.toString());
            return false;
        }
    }

    /**
     * Method that allows for an existing data to be deleted form the table
     * The method doesn't take into account dependencies between records
     *@Parameters: Database (db), ActualData (aD)
     *@return: True: For successful delete
     *         False: For failed delete
     */
    public static boolean deleteItem(SQLiteDatabase db, ActualData aD){
        String whereStatement = ACTUAL_ID + " = ?"; /*DEPARTURE_TIME + " = ? AND" + ARRIVAL_TIME + " = ? AND " +
                FUEL_BALANCE + " = ? AND " + FUEL_USED + " = ? AND " + TOTAL_TRIP_TIME +
                " = ? AND " + TOTAL_TRIP_DISTANCE + " = ? AND " + ARRIVAL_GATE_PARKING + " = ?";*/

        String[] args = {String.valueOf(aD.getActual_ID())}; /*{aD.getDepartureTime(), aD.getArrivalTime(),
                String.valueOf(aD.getFuelBalance()), String.valueOf(aD.getFuelUsed()),
                aD.getTotalTripDuration(), String.valueOf(aD.getTotalTripDistance()),
                aD.getGateParkingName()};*/
        try {
            boolean deleted = db.delete(TABLE_NAME, whereStatement, args) > 0;
            return deleted; //returns true if deleted, return false if no row deleted because no such user id exists
        }
        catch(SQLiteConstraintException e)
        {
            Log.e("Delete Item",e.toString());
            return false;
        }
    }

    private static Cursor getAll(SQLiteDatabase db){
        Cursor allData = db.rawQuery("SELECT * FROM " + TABLE_NAME, null );
        return allData;
    }

    /**
     *Updates the content of a record (row) in the table
     * @Parameters: Database (db), Table'sID (tID), ColumnName (colName), NewValue (nVal)
     * @return: True: For successful Update
     *          False: For failed Update
     */
    public static boolean updateItem(SQLiteDatabase db, String tID, String nVal, String colName){
        try{
            boolean updated = false;
            ContentValues cV = new ContentValues();
            if(colName.equals(FUEL_BALANCE) || colName.equals(FUEL_USED) ||
                    colName.equals(TOTAL_TRIP_DISTANCE))
                cV.put(colName, Float.valueOf(nVal));
            else
                cV.put(colName, nVal);
            int rowsUpdated = db.update(TABLE_NAME, cV, ACTUAL_ID + " = ?", new String[] {tID});

            if (rowsUpdated > 0) updated = true;

            return updated;
        }
        catch(SQLiteConstraintException e){
            Log.e("Update record",e.toString());
            return false;
        }
    }

    private static ActualData getActual(SQLiteDatabase db, ActualData arrData){
        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            ActualData item    = new ActualData();
            while(!items.isAfterLast()) {
                item.setActual_ID(items.getLong(items.getColumnIndex(ACTUAL_ID)));
                item.setDepartureTime(items.getString(items.getColumnIndex(DEPARTURE_TIME)));
                item.setArrivalTime(items.getString(items.getColumnIndex(ARRIVAL_TIME)));
                item.setFuelBalance(items.getFloat(items.getColumnIndex(FUEL_BALANCE)));
                item.setFuelUsed(items.getFloat(items.getColumnIndex(FUEL_USED)));
                item.setTotalTripDuration(items.getString(items.getColumnIndex(TOTAL_TRIP_TIME)));
                item.setTotalTripDistance(items.getFloat(items.getColumnIndex(TOTAL_TRIP_DISTANCE)));
                item.setGateParkingName(items.getString(items.getColumnIndex(ARRIVAL_GATE_PARKING)));

                //put record in unit and compare to target unit before adding
                if (item.isSameAs(arrData)) {
                    return item;
                }
                items.moveToNext();
            }
        }
        finally {
            items.close();
        }
        return null;
    }

    public static List<ActualData> getItems(SQLiteDatabase db){
        Cursor items = getAll(db);
        List<ActualData> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                ActualData item = new ActualData();
                item.setActual_ID(items.getLong(items.getColumnIndex(ACTUAL_ID)));
                item.setDepartureTime(items.getString(items.getColumnIndex(DEPARTURE_TIME)));
                item.setArrivalTime(items.getString(items.getColumnIndex(ARRIVAL_TIME)));
                item.setFuelBalance(items.getFloat(items.getColumnIndex(FUEL_BALANCE)));
                item.setFuelUsed(items.getFloat(items.getColumnIndex(FUEL_USED)));
                item.setTotalTripDuration(items.getString(items.getColumnIndex(TOTAL_TRIP_TIME)));
                item.setTotalTripDistance(items.getFloat(items.getColumnIndex(TOTAL_TRIP_DISTANCE)));
                item.setGateParkingName(items.getString(items.getColumnIndex(ARRIVAL_GATE_PARKING)));

                dataList.add(item);
                items.moveToNext();
            }

            return dataList;
        }
        finally {
            items.close();
        }
    }
}
