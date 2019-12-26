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
import BusinessObjectLayer.FlightPlan;

public class FlightPlanTable {
    public static final String TABLE_NAME = "Flight_Plan";
    public static final String FLIGHT_PLAN_ID = "Flight_Plan_ID"; // INT IDENTITY PRIMARY KEY,
    public static final String ESTIMATE_TRIP_TIME = "Trip_Duration_Estimate"; // TIME(2) NOT NULL,
    public static final String DEPARTURE_FUEL = "Fuel_Taken"; // FLOAT,
    public static final String AIRCRAFT = "Aircraft_ID"; // INT, //CONSTRAINT FK_FP_AirID FOREIGN KEY (Aircraft_ID)//REFERENCES Aircraft (Aircraft_ID)
    public static final String DEPARTURE = "Departure_ID"; // INT, //CONSTRAINT FK_FP_DepID FOREIGN KEY (Departure_ID)//REFERENCES Departure (Departure_ID),
    public static final String ARRIVAL = "Arrival_ID"; // INT, //CONSTRAINT FK_FP_ArrID FOREIGN KEY (Arrival_ID)//REFERENCES Arrival (Arrival_ID),
    public static final String ACTUAL_NUMBERS = "Actual_ID"; // INT, //CONSTRAINT FK_FP_ActID FOREIGN KEY (Actual_ID)//REFERENCES Actual (Actual_ID)
    public static final String ESTIMATE_TRIP_DISTANCE = "Trip_Distance_Estimate"; // FLOAT,
    public static final String CLIMB_SPEED = "Climb_Speed"; // INT,
    public static final String CRUISE_SPPED = "Cruise_Speed"; // INT,
    public static final String CRUISE_ALTITUDE = "Cruise_Altitude"; // INT,
    public static final String PAYLOAD_WEIGHT = "Payload_Weight"; // FLOAT,
    public static final String FUEL_WEIGHT = "Fuel_Weight"; // FLOAT,
    public static final String GROSS_WEIGHT = "Gross_Weight"; // FLOAT,
    public static final String FLIGHT_PLAN_STATUS = "FP_Status"; // VARCHAR(10),

    public static void onCreate(SQLiteDatabase db){
        String CREATE_TODOTABLE = "CREATE TABLE " + TABLE_NAME +
                "("+ FLIGHT_PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ESTIMATE_TRIP_TIME + " DATETIME, "+
                DEPARTURE_FUEL + " DATETIME, " +
                AIRCRAFT + " REAL, " +
                DEPARTURE + " REAL, " +
                ARRIVAL + " REAL, " +
                ACTUAL_NUMBERS + " REAL, " +
                ESTIMATE_TRIP_DISTANCE + " FLOAT, " +
                CLIMB_SPEED + " REAL, " +
                CRUISE_SPPED + " REAL, " +
                CRUISE_ALTITUDE + " REAL, " +
                PAYLOAD_WEIGHT + " FLOAT, " +
                FUEL_WEIGHT + " FLOAT, " +
                GROSS_WEIGHT + " FLOAT, " +
                FLIGHT_PLAN_STATUS + " STRING, " +
                "FOREIGN KEY( " + AIRCRAFT + " ) REFERENCES " + AircraftTable.TABLE_NAME +
                " ( " + AircraftTable.AIRCRAFT_ID + " ) ON DELETE CASCADE, " +
                "FOREIGN KEY( " + DEPARTURE + " ) REFERENCES " + DepartureTable.TABLE_NAME +
                " ( " + DepartureTable.DEPARTURE_ID + " ) ON DELETE CASCADE, " +
                "FOREIGN KEY( " + ARRIVAL + " ) REFERENCES " + ArrivalTable.TABLE_NAME +
                " ( " + ArrivalTable.ARRIVAL_ID + " ) ON DELETE CASCADE, " +
                "FOREIGN KEY( " + ACTUAL_NUMBERS + " ) REFERENCES " + ActualTable.TABLE_NAME +
                " ( " + ActualTable.ACTUAL_ID + " ) ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_TODOTABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**Drops then recreates the table and repopulates the newly created table with the
     * data provides to it
     * @param db
     * @param flightPlanList
     * @return true
     */
    protected static boolean restoreDB(SQLiteDatabase db, List<FlightPlan> flightPlanList){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        for (FlightPlan data : flightPlanList) {
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
     * @Parameters: Database (db), FlightPlanData (fD)
     * @return: True: For successful insertion
     *          False: For failed insertion
     */
    public static boolean insertItem(SQLiteDatabase db, FlightPlan fD){
        try {
            FlightPlan fp = new FlightPlan();
            fp = getFlightPlan(db, fD);
            if(fp == null) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(ESTIMATE_TRIP_TIME, fD.getTripDurationEstimate());
                contentValues.put(DEPARTURE_FUEL, fD.getFuelTaken());
                contentValues.put(AIRCRAFT, fD.getAircraftID());
                contentValues.put(DEPARTURE, fD.getDepartureID());
                contentValues.put(ARRIVAL, fD.getArrivalID());
                contentValues.put(ACTUAL_NUMBERS, fD.getActualID());
                contentValues.put(ESTIMATE_TRIP_DISTANCE, fD.getTripDistanceEstimate());
                contentValues.put(CLIMB_SPEED, fD.getClimbSpeed());
                contentValues.put(CRUISE_SPPED, fD.getCruiseSpeed());
                contentValues.put(CRUISE_ALTITUDE, fD.getCruiseAltitude());
                contentValues.put(PAYLOAD_WEIGHT, fD.getPayloadWeight());
                contentValues.put(FUEL_WEIGHT, fD.getFuelWeight());
                contentValues.put(GROSS_WEIGHT, fD.getGrossWeight());
                contentValues.put(FLIGHT_PLAN_STATUS, fD.getfPStatus());

                db.insertOrThrow(TABLE_NAME, null, contentValues);
                fD.setFlightPlanID(getFlightPlan(db, fD).getFlightPlanID());
            }
            else
                fD.setFlightPlanID(fp.getFlightPlanID());
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
     *@Parameters: Database (db), FlightPlanData (fD)
     *@return: True: For successful delete
     *         False: For failed delete
     */
    public static boolean deleteItem(SQLiteDatabase db, FlightPlan fD){
        String whereStatement = FLIGHT_PLAN_ID + " = ?"; /*ESTIMATE_TRIP_TIME + " = ? AND" + DEPARTURE_FUEL + " = ? AND " +
                AIRCRAFT + " = ? AND " + DEPARTURE + " = ? AND " + ARRIVAL +
                " = ? AND " + ACTUAL_NUMBERS + " = ? AND " + ESTIMATE_TRIP_DISTANCE + " = ? " +
                CLIMB_SPEED + " = ? AND" + CRUISE_SPPED + " = ? AND " +
                CRUISE_ALTITUDE + " = ? AND " + PAYLOAD_WEIGHT + " = ? AND " + FUEL_WEIGHT +
                " = ? AND " + GROSS_WEIGHT + " = ? AND " + FLIGHT_PLAN_STATUS + " = ?";*/

        String[] args = {String.valueOf(fD.getFlightPlanID())}; /*{fD.getTripDurationEstimate(), String.valueOf(fD.getFuelTaken()),
                String.valueOf(fD.getAircraftID()), String.valueOf(fD.getDepartureID()),
                String.valueOf(fD.getArrivalID()), String.valueOf(fD.getActualID()),
                String.valueOf(fD.getTripDistanceEstimate()), String.valueOf(fD.getClimbSpeed()),
                String.valueOf(fD.getCruiseSpeed()), String.valueOf(fD.getCruiseAltitude()),
                String.valueOf(fD.getPayloadWeight()), String.valueOf(fD.getFuelWeight()),
                String.valueOf(fD.getGrossWeight()), fD.getfPStatus()};*/
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
            if(colName.equals(ESTIMATE_TRIP_DISTANCE) || colName.equals(PAYLOAD_WEIGHT) ||
                    colName.equals(FUEL_WEIGHT) || colName.equals(GROSS_WEIGHT))
                cV.put(colName, Float.valueOf(nVal));
            else if(colName.equals(FLIGHT_PLAN_STATUS) || colName.equals(AIRCRAFT) ||
                    colName.equals(DEPARTURE_FUEL) || colName.equals(ESTIMATE_TRIP_TIME))
                cV.put(colName, nVal);
            else
                cV.put(colName, Integer.valueOf(nVal));
            int rowsUpdated = db.update(TABLE_NAME, cV, FLIGHT_PLAN_ID + " = ?", new String[] {tID});

            if (rowsUpdated > 0) updated = true;

            return updated;
        }
        catch(SQLiteConstraintException e){
            Log.e("Update record",e.toString());
            return false;
        }
    }

    private static FlightPlan getFlightPlan(SQLiteDatabase db, FlightPlan airData){
        Cursor items = db.rawQuery("SELECT * FROM "+ TABLE_NAME,null);

        try {
            items.moveToFirst();
            FlightPlan item    = new FlightPlan();
            while(!items.isAfterLast()) {
                item.setFlightPlanID(items.getLong(items.getColumnIndex(FLIGHT_PLAN_ID)));
                item.setTripDurationEstimate(items.getString(items.getColumnIndex(ESTIMATE_TRIP_TIME)));
                item.setFuelTaken(items.getFloat(items.getColumnIndex(DEPARTURE_FUEL)));
                item.setAircraftID(items.getLong(items.getColumnIndex(AIRCRAFT)));
                item.setDepartureID(items.getLong(items.getColumnIndex(DEPARTURE)));
                item.setArrivalID(items.getLong(items.getColumnIndex(ARRIVAL)));
                item.setActualID(items.getLong(items.getColumnIndex(ACTUAL_NUMBERS)));
                item.setTripDistanceEstimate(items.getFloat(items.getColumnIndex(ESTIMATE_TRIP_DISTANCE)));
                item.setClimbSpeed(items.getInt(items.getColumnIndex(CLIMB_SPEED)));
                item.setCruiseSpeed(items.getInt(items.getColumnIndex(CRUISE_SPPED)));
                item.setCruiseAltitude(items.getInt(items.getColumnIndex(CRUISE_ALTITUDE)));
                item.setPayloadWeight(items.getFloat(items.getColumnIndex(PAYLOAD_WEIGHT)));
                item.setFuelWeight(items.getFloat(items.getColumnIndex(FUEL_WEIGHT)));
                item.setGrossWeight(items.getFloat(items.getColumnIndex(GROSS_WEIGHT)));
                item.setfPStatus(items.getString(items.getColumnIndex(FLIGHT_PLAN_STATUS)));

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

    public static List<FlightPlan> getItems(SQLiteDatabase db){
        Cursor items = getAll(db);
        List<FlightPlan> dataList = new ArrayList<>();

        try{
            items.moveToFirst();
            while(!items.isAfterLast()){
                FlightPlan item = new FlightPlan();
                item.setFlightPlanID(items.getLong(items.getColumnIndex(FLIGHT_PLAN_ID)));
                item.setTripDurationEstimate(items.getString(items.getColumnIndex(ESTIMATE_TRIP_TIME)));
                item.setFuelTaken(items.getFloat(items.getColumnIndex(DEPARTURE_FUEL)));
                item.setAircraftID(items.getLong(items.getColumnIndex(AIRCRAFT)));
                item.setDepartureID(items.getLong(items.getColumnIndex(DEPARTURE)));
                item.setArrivalID(items.getLong(items.getColumnIndex(ARRIVAL)));
                item.setActualID(items.getLong(items.getColumnIndex(ACTUAL_NUMBERS)));
                item.setTripDistanceEstimate(items.getFloat(items.getColumnIndex(ESTIMATE_TRIP_DISTANCE)));
                item.setClimbSpeed(items.getInt(items.getColumnIndex(CLIMB_SPEED)));
                item.setCruiseSpeed(items.getInt(items.getColumnIndex(CRUISE_SPPED)));
                item.setCruiseAltitude(items.getInt(items.getColumnIndex(CRUISE_ALTITUDE)));
                item.setPayloadWeight(items.getFloat(items.getColumnIndex(PAYLOAD_WEIGHT)));
                item.setFuelWeight(items.getFloat(items.getColumnIndex(FUEL_WEIGHT)));
                item.setGrossWeight(items.getFloat(items.getColumnIndex(GROSS_WEIGHT)));
                item.setfPStatus(items.getString(items.getColumnIndex(FLIGHT_PLAN_STATUS)));

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
