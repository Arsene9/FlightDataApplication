package DatabaseLayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import BusinessObjectLayer.Departure;

public class DepartureTable {
    public static final String TABLE_NAME = "Departure";
    public static final String DEPARTURE_ID = "Departure_ID"; // INT IDENTITY PRIMARY KEY,
    public static final String LOCATION_ID = "Location_ID"; // INT, //CONSTRAINT FK_Dep_Loc FOREIGN KEY (Location_ID)//REFERENCES Location (Location_ID)
    public static final String GATE_PARKING = "GATE_Parking_Name"; // VARCHAR(5) NOT NULL,
    public static final String DEPARTURE_TIME = "Departure_Time"; // DATETIME

    public static void onCreate(SQLiteDatabase db){
        String CREATE_DEPARTURETABLE = "CREATE TABLE " + TABLE_NAME +
                "("+ DEPARTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                LOCATION_ID + " REAL, "+
                GATE_PARKING + " STRING, " +
                DEPARTURE_TIME + " DATETIME, " +
                "FOREIGN KEY( " + LOCATION_ID + " ) REFERENCES " + LocationTable.TABLE_NAME +
                " ( " + LocationTable.LOCATION_ID + " ) ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_DEPARTURETABLE);
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
     * @Parameters: Database (db), DepartureData (dD)
     * @return: True: For successful insertion
     *          False: For failed insertion
     */
    public static boolean insertItem(SQLiteDatabase db, Departure dD){
        try {
            Departure fp = new Departure();
            fp = getDeparture(db, dD);
            if(fp == null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LOCATION_ID, dD.getLocationID());
                contentValues.put(GATE_PARKING, dD.getGateParkingName());
                contentValues.put(DEPARTURE_TIME, dD.getDepartureTime());

                db.insertOrThrow(TABLE_NAME, null, contentValues);
                dD.setDepartureID(getDeparture(db, dD).getDepartureID());
            }
            else
                dD.setDepartureID(fp.getDepartureID());
            return true;
        }
        catch (SQLiteConstraintException e)
        {
            Log.e("Insert Departure Data",e.toString());
            return false;
        }
    }

    /**
     * Method that allows for an existing data to be deleted form the table
     * The method doesn't take into account dependencies between records
     *@Parameters: Database (db), DepartureData (dD)
     *@return: True: For successful delete
     *         False: For failed delete
     */
    public static boolean deleteItem(SQLiteDatabase db, Departure dD){
        String whereStatement = DEPARTURE_ID + " = ?"; /*LOCATION_ID + " = ? AND" + GATE_PARKING + " = ? AND " +
                DEPARTURE_TIME + " = ? ";*/

        String[] args = {String.valueOf(dD.getDepartureID())}; /*{String.valueOf(dD.getLocationID()), dD.getGateParkingName(),
                dD.getDepartureTime()};*/
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
            int rowsUpdated = db.update(TABLE_NAME, cV, DEPARTURE_ID + " = ?", new String[] {tID});

            if (rowsUpdated > 0) updated = true;

            return updated;
        }
        catch(SQLiteConstraintException e){
            Log.e("Update record",e.toString());
            return false;
        }
    }

    private static Departure getDeparture(SQLiteDatabase db, Departure depData){
        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            Departure item    = new Departure();
            while(!items.isAfterLast()) {
                item.setDepartureID(items.getLong(items.getColumnIndex(DEPARTURE_ID)));
                item.setLocationID(items.getLong(items.getColumnIndex(LOCATION_ID)));
                item.setGateParkingName(items.getString(items.getColumnIndex(GATE_PARKING)));
                item.setDepartureTime(items.getString(items.getColumnIndex(DEPARTURE_TIME)));

                //put record in unit and compare to target unit before adding
                if (item.isSameAs(depData)) {
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

    public static List<Departure> getItems(SQLiteDatabase db){
        Cursor items = getAll(db);
        List<Departure> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                Departure item = new Departure();
                item.setDepartureID(items.getLong(items.getColumnIndex(DEPARTURE_ID)));
                item.setLocationID(items.getLong(items.getColumnIndex(LOCATION_ID)));
                item.setGateParkingName(items.getString(items.getColumnIndex(GATE_PARKING)));
                item.setDepartureTime(items.getString(items.getColumnIndex(DEPARTURE_TIME)));

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
