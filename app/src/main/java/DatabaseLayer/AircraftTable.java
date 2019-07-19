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
import BusinessObjectLayer.Aircraft;

public class AircraftTable {
    public static final String TABLE_NAME = "Aircraft";
    public static final String AIRCRAFT_ID = "Aircraft_ID"; // INT IDENTITY PRIMARY KEY,
    public static final String AIRLINE_NAME = "Airline_Name"; // VARCHAR(30) NOT NULL,
    public static final String AIRCRAFT_TYPE = "Aircraft_Type"; // VARCHAR(9) NOT NULL,
    public static final String MANUFACTURER = "Manufacturer"; // VARCHAR(20) NOT NULL,
    public static final String TAIL_NUMBER = "Tail_Num"; // VARCHAR(10) NOT NULL,
    public static final String CALL_SIGN = "Call_Sign"; // VARCHAR(10),
    public static final String FLIGHT_NUMBER = "Flight_Num"; // VARCHAR(15)

    public static void onCreate(SQLiteDatabase db){
        String CREATE_TODOTABLE = "CREATE TABLE " + TABLE_NAME +
                "("+ AIRCRAFT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                AIRLINE_NAME + " STRING NOT NULL, "+
                AIRCRAFT_TYPE + " STRING NOT NULL, "+
                MANUFACTURER + " STRING NOT NULL, " +
                TAIL_NUMBER + " STRING NOT NULL, " +
                CALL_SIGN + " STRING, " +
                FLIGHT_NUMBER + " STRING" +
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
     * @Parameters: Database (db), AircraftData (aD)
     * @return: True: For successful insertion
     *          False: For failed insertion
     */
    public static boolean insertItem(SQLiteDatabase db, Aircraft aD){
        try {
            Aircraft loc = new Aircraft();
            loc = getAircraft(db, aD);
            if(loc == null) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(AIRLINE_NAME, aD.getAirlineName());
                contentValues.put(AIRCRAFT_TYPE, aD.getAircraftType());
                contentValues.put(MANUFACTURER, aD.getManufacturer());
                contentValues.put(TAIL_NUMBER, aD.getTailNum());
                contentValues.put(CALL_SIGN, aD.getCallSign());
                contentValues.put(FLIGHT_NUMBER, aD.getFlightNum());

                db.insertOrThrow(TABLE_NAME, null, contentValues);
                aD.setAircraftID(getAircraft(db, aD).getAircraftID());
            }
            else
                aD.setAircraftID(loc.getAircraftID());
            return true;
        }
        catch (SQLiteConstraintException e)
        {
            Log.e("Insert Aircraft Data",e.toString());
            return false;
        }
    }

    /**
     * Method that allows for an existing data to be deleted form the table
     * The method doesn't take into account dependencies between records
     *@Parameters: Database (db), AircraftData (aD)
     *@return: True: For successful delete
     *         False: For failed delete
     */
    public static boolean deleteItem(SQLiteDatabase db, Aircraft aD){
        String whereStatement = AIRCRAFT_ID + " = ?"; /*AIRLINE_NAME + " = ? AND" + AIRCRAFT_TYPE + " = ? AND " +
                MANUFACTURER + " = ? AND " + TAIL_NUMBER + " = ? AND " + CALL_SIGN +
                " = ? AND " + FLIGHT_NUMBER + " = ? ";*/

        String[] args = {String.valueOf(aD.getAircraftID())}; /*{aD.getAirlineName(), aD.getAircraftType(),
                aD.getManufacturer(), aD.getTailNum(),
                aD.getCallSign(), aD.getFlightNum()};*/
        try {
            boolean deleted = db.delete(TABLE_NAME, whereStatement, args) > 0;
            if(deleted && getItems(db).isEmpty()) {
                //Resets the auto increment value back to 0
                db.delete("SQLITE_SEQUENCE", "NAME = ?", new String[]{TABLE_NAME});
            }

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
            cV.put(colName, nVal);
            int rowsUpdated = db.update(TABLE_NAME, cV, AIRCRAFT_ID + " = ?", new String[] {tID});

            if (rowsUpdated > 0) updated = true;

            return updated;
        }
        catch(SQLiteConstraintException e){
            Log.e("Update record",e.toString());
            return false;
        }
    }

    private static Aircraft getAircraft(SQLiteDatabase db, Aircraft airData){
        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            Aircraft item    = new Aircraft();
            while(!items.isAfterLast()) {
                item.setAircraftID(items.getLong(items.getColumnIndex(AIRCRAFT_ID)));
                item.setAirlineName(items.getString(items.getColumnIndex(AIRLINE_NAME)));
                item.setAircraftType(items.getString(items.getColumnIndex(AIRCRAFT_TYPE)));
                item.setManufacturer(items.getString(items.getColumnIndex(MANUFACTURER)));
                item.setTailNum(items.getString(items.getColumnIndex(TAIL_NUMBER)));
                item.setCallSign(items.getString(items.getColumnIndex(CALL_SIGN)));
                item.setFlightNum(items.getString(items.getColumnIndex(FLIGHT_NUMBER)));

                //put record in unit and compare to target unit before adding
                if (item.isSameAs(airData)) {
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

    public static List<Aircraft> getItems(SQLiteDatabase db){
        Cursor items = getAll(db);
        List<Aircraft> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                Aircraft item = new Aircraft();
                item.setAircraftID(items.getLong(items.getColumnIndex(AIRCRAFT_ID)));
                item.setAirlineName(items.getString(items.getColumnIndex(AIRLINE_NAME)));
                item.setAircraftType(items.getString(items.getColumnIndex(AIRCRAFT_TYPE)));
                item.setManufacturer(items.getString(items.getColumnIndex(MANUFACTURER)));
                item.setTailNum(items.getString(items.getColumnIndex(TAIL_NUMBER)));
                item.setCallSign(items.getString(items.getColumnIndex(CALL_SIGN)));
                item.setFlightNum(items.getString(items.getColumnIndex(FLIGHT_NUMBER)));
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
