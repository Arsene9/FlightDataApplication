package DatabaseLayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import BusinessObjectLayer.Arrival;

public class ArrivalTable {
    public static final String TABLE_NAME = "Arrival";
    public static final String ARRIVAL_ID = "Arrival_ID"; // INT IDENTITY PRIMARY KEY,
    public static final String LOCATION_ID = "Location_ID"; // INT, //CONSTRAINT FK_Dep_Loc FOREIGN KEY (Location_ID)//REFERENCES Location (Location_ID)
    public static final String GATE_PARKING = "GATE_Parking_Name"; // VARCHAR(5) NOT NULL,
    public static final String ARRIVAL_TIME = "Arrival_Time"; // DATETIME

    public static void onCreate(SQLiteDatabase db){
        String CREATE_ARRIVAL_TABLE = "CREATE TABLE " + TABLE_NAME +
                "("+ ARRIVAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                LOCATION_ID + " REAL, "+
                GATE_PARKING + " STRING, " +
                ARRIVAL_TIME + " DATETIME, " +
                "FOREIGN KEY( " + LOCATION_ID + " ) REFERENCES " + LocationTable.TABLE_NAME +
                " ( " + LocationTable.LOCATION_ID + " ) ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_ARRIVAL_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**Drops then recreates the table and repopulates the newly created table with the
     * data provides to it
     * @param db
     * @param arrivalList
     * @return true
     */
    protected static boolean restoreDB(SQLiteDatabase db, List<Arrival> arrivalList){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        for (Arrival data : arrivalList) {
            insertItem(db, data);
        }
        return true;
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
    public static boolean insertItem(SQLiteDatabase db, Arrival dD){
        try {
            Arrival fp = getArrival(db, dD);
            if(fp == null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LOCATION_ID, dD.getLocationID());
                contentValues.put(GATE_PARKING, dD.getGateParking());
                contentValues.put(ARRIVAL_TIME, dD.getArrivalTime());

                db.insertOrThrow(TABLE_NAME, null, contentValues);
                dD.setArrivalID(getArrival(db, dD).getArrivalID());
            }
            else
                dD.setArrivalID(fp.getArrivalID());
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
    public static boolean deleteItem(SQLiteDatabase db, Arrival dD){
        String whereStatement = ARRIVAL_ID + " = ?"; /*LOCATION_ID + " = ? AND" + GATE_PARKING + " = ? AND " +
                ARRIVAL_TIME + " = ? ";*/

        String[] args = {String.valueOf(dD.getArrivalID())}; /*{String.valueOf(dD.getLocationID()), dD.getGateParkingName(),
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
            int rowsUpdated = db.update(TABLE_NAME, cV, ARRIVAL_ID + " = ?", new String[] {tID});

            if (rowsUpdated > 0) updated = true;

            return updated;
        }
        catch(SQLiteConstraintException e){
            Log.e("Update record",e.toString());
            return false;
        }
    }

    private static Arrival getArrival(SQLiteDatabase db, Arrival arrData){
        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            Arrival item    = new Arrival();
            while(!items.isAfterLast()) {
                item.setArrivalID(items.getLong(items.getColumnIndex(ARRIVAL_ID)));
                item.setLocationID(items.getLong(items.getColumnIndex(LOCATION_ID)));
                item.setGateParking(items.getString(items.getColumnIndex(GATE_PARKING)));
                item.setArrivalTime(items.getString(items.getColumnIndex(ARRIVAL_TIME)));

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

    public static List<Arrival> getItems(SQLiteDatabase db){
        Cursor items = getAll(db);
        List<Arrival> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                Arrival item = new Arrival();
                item.setArrivalID(items.getLong(items.getColumnIndex(ARRIVAL_ID)));
                item.setLocationID(items.getLong(items.getColumnIndex(LOCATION_ID)));
                item.setGateParking(items.getString(items.getColumnIndex(GATE_PARKING)));
                item.setArrivalTime(items.getString(items.getColumnIndex(ARRIVAL_TIME)));

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
