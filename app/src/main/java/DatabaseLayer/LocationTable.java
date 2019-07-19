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
import BusinessObjectLayer.Departure;
import BusinessObjectLayer.Location;

public class LocationTable {
    public static final String TABLE_NAME = "Location";
    public static final String LOCATION_ID = "Location_ID"; // INT IDENTITY PRIMARY KEY
    public static final String AIRPORT = "Airport_ID"; // VARCHAR(6) NOT NULL,
    public static final String AIRPORT_NAME = "Airport_Name"; // VARCHAR(45),
    public static final String CITY = "City"; // VARCHAR(45),
    public static final String STATE_NAME = "State_Name"; // VARCHAR(45),
    public static final String COUNTRY = "Country"; // VARCHAR(45),

    public static void onCreate(SQLiteDatabase db){
        String CREATE_LOCATIONTABLE = "CREATE TABLE " + TABLE_NAME +
                "("+ LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                AIRPORT + " STRING, "+
                AIRPORT_NAME + " STRING, "+
                CITY + " STRING, " +
                STATE_NAME + " STRING, " +
                COUNTRY + " STRING" +
                ")";
        db.execSQL(CREATE_LOCATIONTABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Gets the size of the table, equivalent to the total number of records in the table
    public static long getTableCount(SQLiteDatabase db){
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    private static Location getLocation(SQLiteDatabase db, Location locData){
        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            Location item    = new Location();
            while(!items.isAfterLast()) {
                item.setLocationID(items.getLong(items.getColumnIndex(LOCATION_ID)));
                item.setAirportID(items.getString(items.getColumnIndex(AIRPORT)));
                item.setAirportName(items.getString(items.getColumnIndex(AIRPORT_NAME)));
                item.setCity(items.getString(items.getColumnIndex(CITY)));
                item.setStateName(items.getString(items.getColumnIndex(STATE_NAME)));
                item.setCountry(items.getString(items.getColumnIndex(COUNTRY)));
                //put record in unit and compare to target unit before adding
                if (item.isSameAs(locData)) {
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

    /**
     * Method that allows a new data (New table row) to be added to the table
     * @Parameters: Database (db), LocationData (lD)
     * @return: True: For successful insertion
     *          False: For failed insertion
     */
    public static boolean insertItem(SQLiteDatabase db, Location lD){
        try {
            Location loc = new Location();
            loc = getLocation(db, lD);
            if(loc == null) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(AIRPORT, lD.getAirportID());
                contentValues.put(AIRPORT_NAME, lD.getAirportName());
                contentValues.put(CITY, lD.getCity());
                contentValues.put(STATE_NAME, lD.getStateName());
                contentValues.put(COUNTRY, lD.getCountry());

                db.insertOrThrow(TABLE_NAME, null, contentValues);
                lD.setLocationID(getLocation(db, lD).getLocationID());
            }
            else
                lD.setLocationID(loc.getLocationID());
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
     *@Parameters: Database (db), LocationData (lD)
     *@return: True: For successful delete
     *         False: For failed delete
     */
    public static boolean deleteItem(SQLiteDatabase db, Location lD){
        String whereStatement = LOCATION_ID + " = ?"; /*AIRPORT + " = ? AND" + AIRPORT_NAME + " = ? AND " +
                CITY + " = ? AND " + STATE_NAME + " = ? AND " + COUNTRY +
                " = ?";*/

        String[] args = {String.valueOf(lD.getLocationID())}; /*{lD.getAirportID(), lD.getAirportName(),
                lD.getCity(), lD.getStateName(), lD.getCountry()};*/
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
            int rowsUpdated = db.update(TABLE_NAME, cV, LOCATION_ID + " = ?", new String[] {tID});

            if (rowsUpdated > 0) updated = true;

            return updated;
        }
        catch(SQLiteConstraintException e){
            Log.e("Update record",e.toString());
            return false;
        }
    }

    public static List<Location> getItems(SQLiteDatabase db){
        Cursor items = getAll(db);
        List<Location> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                Location item = new Location();
                item.setLocationID(items.getLong(items.getColumnIndex(LOCATION_ID)));
                item.setAirportID(items.getString(items.getColumnIndex(AIRPORT)));
                item.setAirportName(items.getString(items.getColumnIndex(AIRPORT_NAME)));
                item.setCity(items.getString(items.getColumnIndex(CITY)));
                item.setStateName(items.getString(items.getColumnIndex(STATE_NAME)));
                item.setCountry(items.getString(items.getColumnIndex(COUNTRY)));

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
