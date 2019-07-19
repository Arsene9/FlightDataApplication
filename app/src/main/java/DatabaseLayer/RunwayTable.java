package DatabaseLayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import BusinessObjectLayer.Runway;

public class RunwayTable {
    public static final String TABLE_NAME = "Runway";
    public static final String RUNWAY_ID = "RWY_ID"; // INT IDENTITY PRIMARY KEY,
    public static final String RUNWAY_NAME = "Name"; // VARCHAR(6),
    public static final String RUNWAY_LENGTH = "Length"; // INT,
    public static final String RUNWAY_SURFACE = "Surface"; // VARCHAR(20),
    public static final String RUNWAY_HEADING = "HDG"; // INT,
    public static final String RUNWAY_ILS_ID = "ILS_ID"; // VARCHAR(10),
    public static final String ILS_FREQUENCY = "ILS_Freq"; // FLOAT,
    public static final String LOCATION = "Location_ID"; // INT, //CONSTRAINT FK_RWY_Loc FOREIGN KEY (Location_ID)//REFERENCES Location (Location_ID)

    public static void onCreate(SQLiteDatabase db){
        String CREATE_TODOTABLE = "CREATE TABLE " + TABLE_NAME +
                "("+ RUNWAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                RUNWAY_NAME + " STRING, "+
                RUNWAY_LENGTH + " REAL, " +
                RUNWAY_SURFACE + " STRING, " +
                RUNWAY_HEADING + " REAL, "+
                RUNWAY_ILS_ID + " STRING, " +
                ILS_FREQUENCY + " FLOAT, " +
                LOCATION + " REAL, " +
                "FOREIGN KEY( " + LOCATION + " ) REFERENCES " + LocationTable.TABLE_NAME +
                " ( " + LocationTable.LOCATION_ID + " ) ON DELETE CASCADE" +
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

    public static boolean runwayExists(SQLiteDatabase db, Runway data){
        boolean exists = false;

        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            Runway item    = new Runway();
            while(!items.isAfterLast()) {
                item.setRunwayID(items.getLong(items.getColumnIndex(RUNWAY_ID)));
                item.setName(items.getString(items.getColumnIndex(RUNWAY_NAME)));
                item.setLength(items.getInt(items.getColumnIndex(RUNWAY_LENGTH)));
                item.setSurface(items.getString(items.getColumnIndex(RUNWAY_SURFACE)));
                item.setHdg(items.getInt(items.getColumnIndex(RUNWAY_HEADING)));
                item.setILS_ID(items.getString(items.getColumnIndex(RUNWAY_ILS_ID)));
                item.setILS_Freq(items.getFloat(items.getColumnIndex(ILS_FREQUENCY)));
                item.setLocation_ID(items.getLong(items.getColumnIndex(LOCATION)));
                //data.setRunwayID(item.getRunwayID());
                //put record in unit and compare to target unit before adding
                if (item.isSameAs(data)) {
                    exists = true;
                    break;
                }
                items.moveToNext();
            }
        }
        finally {
            items.close();
        }

        return exists;
    }

    /**
     * Method that allows a new data (New table row) to be added to the table
     * @Parameters: Database (db), RunwayData (rD)
     * @return: True: For successful insertion
     *          False: For failed insertion
     */
    public static boolean insertItem(SQLiteDatabase db, Runway rD){
        try {
            Runway loc = new Runway();
            loc = getRunway(db, rD);
            if(loc == null) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(RUNWAY_NAME, rD.getName());
                contentValues.put(RUNWAY_LENGTH, rD.getLength());
                contentValues.put(RUNWAY_SURFACE, rD.getSurface());
                contentValues.put(RUNWAY_HEADING, rD.getHdg());
                contentValues.put(RUNWAY_ILS_ID, rD.getILS_ID());
                contentValues.put(ILS_FREQUENCY, rD.getILS_Freq());
                contentValues.put(LOCATION, rD.getLocation_ID());

                db.insertOrThrow(TABLE_NAME, null, contentValues);
                List<Runway> rwyList = new ArrayList<>();
                rwyList.addAll(getItems(db, rD.getLocation_ID()));
                rD.setRunwayID(rwyList.get(rwyList.size()-1).getRunwayID());
            }
            else
                rD.setRunwayID(loc.getRunwayID());
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
     *@Parameters: Database (db), RunwayData (rD)
     *@return: True: For successful delete
     *         False: For failed delete
     */
    public static boolean deleteItem(SQLiteDatabase db, Runway rD){
        String whereStatement = RUNWAY_ID + " = ?";/*RUNWAY_NAME + " = ? AND" + RUNWAY_LENGTH + " = ? AND " +
                RUNWAY_SURFACE + " = ? AND " + RUNWAY_HEADING + " = ? AND " + RUNWAY_ILS_ID +
                " = ? AND " + ILS_FREQUENCY + " = ? AND " + LOCATION + " = ?";*/

        String[] args = {String.valueOf(rD.getRunwayID())}; /*{rD.getName(), String.valueOf(rD.getLength()),
                String.valueOf(rD.getSurface()), String.valueOf(rD.getHdg()),
                rD.getILS_ID(), String.valueOf(rD.getILS_Freq()),
                String.valueOf(rD.getLocation_ID())};*/
        try {
            boolean deleted = db.delete(TABLE_NAME, whereStatement, args) > 0;
            if(deleted && getItems(db).isEmpty()) {
                //Resets the auto increment value back to 0
                db.delete("SQLITE_SEQUENCE", "NAME = ?", new String[]{TABLE_NAME});
            }

            return deleted; //returns true if deleted, return false if no row deleted because no such id exists
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
            if(colName.equals(ILS_FREQUENCY))
                cV.put(colName, Float.valueOf(nVal));
            else if(colName.equals(LOCATION) || colName.equals(RUNWAY_HEADING) ||
                    colName.equals(RUNWAY_LENGTH))
                cV.put(colName, Integer.valueOf(nVal));
            else
                cV.put(colName, nVal);
            int rowsUpdated = db.update(TABLE_NAME, cV, RUNWAY_ID + " = ?", new String[] {tID});

            if (rowsUpdated > 0) updated = true;

            return updated;
        }
        catch(SQLiteConstraintException e){
            Log.e("Update record",e.toString());
            return false;
        }
    }

    private static Runway getRunway(SQLiteDatabase db, Runway locData){
        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            Runway item    = new Runway();
            while(!items.isAfterLast()) {
                item.setRunwayID(items.getLong(items.getColumnIndex(RUNWAY_ID)));
                item.setName(items.getString(items.getColumnIndex(RUNWAY_NAME)));
                item.setLength(items.getInt(items.getColumnIndex(RUNWAY_LENGTH)));
                item.setSurface(items.getString(items.getColumnIndex(RUNWAY_SURFACE)));
                item.setHdg(items.getInt(items.getColumnIndex(RUNWAY_HEADING)));
                item.setILS_ID(items.getString(items.getColumnIndex(RUNWAY_ILS_ID)));
                item.setILS_Freq(items.getFloat(items.getColumnIndex(ILS_FREQUENCY)));
                item.setLocation_ID(items.getLong(items.getColumnIndex(LOCATION)));
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

    private static List<Runway> getItems(SQLiteDatabase db){
        Cursor items = getAll(db);
        List<Runway> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                Runway item = new Runway();
                item.setRunwayID(items.getLong(items.getColumnIndex(RUNWAY_ID)));
                item.setName(items.getString(items.getColumnIndex(RUNWAY_NAME)));
                item.setLength(items.getInt(items.getColumnIndex(RUNWAY_LENGTH)));
                item.setSurface(items.getString(items.getColumnIndex(RUNWAY_SURFACE)));
                item.setHdg(items.getInt(items.getColumnIndex(RUNWAY_HEADING)));
                item.setILS_ID(items.getString(items.getColumnIndex(RUNWAY_ILS_ID)));
                item.setILS_Freq(items.getFloat(items.getColumnIndex(ILS_FREQUENCY)));
                item.setLocation_ID(items.getLong(items.getColumnIndex(LOCATION)));

                dataList.add(item);
                items.moveToNext();
            }

            return dataList;
        }
        finally {
            items.close();
        }
    }

    public static List<Runway> getItems(SQLiteDatabase db, Long locID){
        Cursor items = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + LOCATION + " = "
                + locID, null );
//        Cursor items = getAll(db);
        List<Runway> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                    Runway item = new Runway();
                    item.setRunwayID(items.getLong(items.getColumnIndex(RUNWAY_ID)));
                    item.setName(items.getString(items.getColumnIndex(RUNWAY_NAME)));
                    item.setLength(items.getInt(items.getColumnIndex(RUNWAY_LENGTH)));
                    item.setSurface(items.getString(items.getColumnIndex(RUNWAY_SURFACE)));
                    item.setHdg(items.getInt(items.getColumnIndex(RUNWAY_HEADING)));
                    item.setILS_ID(items.getString(items.getColumnIndex(RUNWAY_ILS_ID)));
                    item.setILS_Freq(items.getFloat(items.getColumnIndex(ILS_FREQUENCY)));
                    item.setLocation_ID(items.getLong(items.getColumnIndex(LOCATION)));
                    //deleteItem(db, item);

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
